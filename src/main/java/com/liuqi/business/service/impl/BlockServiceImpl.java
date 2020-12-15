package com.liuqi.business.service.impl;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.enums.TableIdNameEnum;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.business.websocket.BlockPushHandle;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.mq.BlockProducer;
import com.liuqi.mq.TradeTopic;
import com.liuqi.redis.RedisRepository;
import com.liuqi.response.ReturnResponse;
import com.liuqi.utils.BitcoinAddressUtils;
import com.liuqi.utils.DateUtil;
import com.liuqi.utils.MathUtil;
import com.liuqi.utils.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;


import com.liuqi.business.mapper.BlockMapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class BlockServiceImpl extends BaseServiceImpl<BlockModel,BlockModelDto> implements BlockService{

	@Autowired
	private BlockMapper blockMapper;
	@Autowired
	private TableIdService tableIdService;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private BlockTransactionUnconfirmService blockTransactionUnconfirmService;
	@Autowired
	private BlockTransactionService blockTransactionService;
	@Autowired
	private TransferRecordService transferRecordService;
	@Autowired
	private BlockProducer blockProducer;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private AddressRecordService addressRecordService;

	@Override
	public BaseMapper<BlockModel,BlockModelDto> getBaseMapper() {
		return this.blockMapper;
	}

	@Override
	protected void doMode(BlockModelDto dto) {
		dto.setTimeString(DateUtil.formatDate(dto.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
	}

	@Override
	@Transactional
	public void insert(BlockModel t) {
		t.setId(tableIdService.getNextId(TableIdNameEnum.BLOCK));
		if(t.getCreateTime()==null){
			t.setCreateTime(new Date());
		}
		if(t.getUpdateTime()==null){
			t.setUpdateTime(new Date());
		}
		if(t.getVersion()==null){
			t.setVersion(0);
		}
		this.getBaseMapper().insert(t);
	}

	@Override
	@Transactional
	public void generateBlock() {
		Long lastHeight = redisRepository.hasKey("lastHeight") ? redisRepository.getLong("lastHeight")
				: -1L;
		Long height = lastHeight + 1;
		BigDecimal totalSuc = redisRepository.getBigDecimal(ConfigConstant.CONFIG_TOTAL_SUC);
		if (totalSuc.compareTo(BigDecimal.ZERO) <= 0) {
			totalSuc = BigDecimal.valueOf(141300000);
		}
		// 抓取所有待确认交易
		List<BlockTransactionUnconfirmModelDto> unconfirmModelDtos = blockTransactionUnconfirmService.getAllUnconfrimTransaction();
		int blockReward = RandomUtil.randomInt(1900000, 2100000);
		BigDecimal reward = MathUtil.div(BigDecimal.valueOf(blockReward), BigDecimal.valueOf(1000000));
		Integer count = 0;
		if (unconfirmModelDtos != null && !unconfirmModelDtos.isEmpty()) {
			for (BlockTransactionUnconfirmModelDto d : unconfirmModelDtos) {
				reward = MathUtil.add(reward, d.getFee());
				count++;
				// 添加到区块交易表中
				blockTransactionService.addTransaction(height, d.getFromAddress(), d.getToAddress(), d.getCurrencyId(), d.getAmount(), d.getFee(), d.getTxHash());
				// 待确认交易变更为已确认
				blockTransactionUnconfirmService.modifyToConfirmed(d.getId());
				// 转账记录修改为已确认
				transferRecordService.modifyToConfirm(d.getExId(), height);
			}
		}
		// 生成区块
		BlockModelDto blockModel = new BlockModelDto();
		blockModel.setHeight(height);
		blockModel.setReward(reward);
		blockModel.setTransactionCount(count);
		String content = "" + System.currentTimeMillis() + height;
		blockModel.setHash(BitcoinAddressUtils.getHash(content));
		blockModel.setTimeString(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
		insert(blockModel);

		redisRepository.set(ConfigConstant.CONFIG_TOTAL_SUC, MathUtil.sub(totalSuc, reward));
		BigDecimal hasBlock = redisRepository.getBigDecimal(ConfigConstant.CONFIG_BLOCK_SUC);
		redisRepository.set(ConfigConstant.CONFIG_BLOCK_SUC, MathUtil.add(hasBlock, reward));
		redisRepository.set("lastHeight", height);

		blockProducer.sendMessage(blockModel);
	}

	@Override
	public ReturnResponse doContentSearch(String content) {
		Map result = new HashMap();
		if (ValidatorUtil.isDigital(content)) {
			result.put("type", 1);
			result.put("result", blockMapper.getByHeight(Long.parseLong(content)));
		} else if (content.startsWith("S")) {
			AddressRecordModelDto a = addressRecordService.getByAddress(content);
			if (a == null) {
				return ReturnResponse.backFail(MessageSourceHolder.getMessage("message58"));
			}
			BlockTransactionModelDto params = new BlockTransactionModelDto();
			params.setSearchAddress(content);
			List<BlockTransactionModelDto> list = blockTransactionService.queryListByDto(params, true);
			result.put("type", 2);
			result.put("result", list);

			List<UserWalletModelDto> assets = userWalletService.getByUserId(a.getId());
			result.put("assets", assets);
		} else {
			result.put("type", 3);
			result.put("result", blockTransactionService.getByTxHash(content));
		}
		return ReturnResponse.backSuccess(result);
	}
}
