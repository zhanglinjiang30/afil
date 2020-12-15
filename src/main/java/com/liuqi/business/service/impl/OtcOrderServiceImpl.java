package com.liuqi.business.service.impl;


import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.enums.*;
import com.liuqi.business.mapper.OtcOrderMapper;
import com.liuqi.business.model.*;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.message.MessageSourceHolder;
import com.liuqi.utils.MathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class OtcOrderServiceImpl extends BaseServiceImpl<OtcOrderModel,OtcOrderModelDto> implements OtcOrderService{

	@Autowired
	private OtcOrderMapper otcOrderMapper;
	@Autowired
	private OtcOrderRecordService otcOrderRecordService;
	@Autowired
	private CurrencyService currencyService;

	@Autowired
	private UserOtcWalletService userWalletService;
	@Autowired
	private UserOtcWalletLogService userWalletLogService;
	@Autowired
	private UserPayService userPayService;
	@Autowired
	private OtcStoreService otcStoreService;

	@Autowired
    private UserAuthService userAuthService;

	@Autowired
    private AddressHoldingRecordService addressHoldingRecordService;

	@Override
	public BaseMapper<OtcOrderModel,OtcOrderModelDto> getBaseMapper() {
		return this.otcOrderMapper;
	}


	@Override
	protected void doMode(OtcOrderModelDto dto) {
		super.doMode(dto);
		dto.setCurrencyName(currencyService.getNameById(dto.getCurrencyId()));
		String name = addressHoldingRecordService.getNameByAddressId(dto.getUserId());
		String realName = userAuthService.getByUserId(dto.getUserId()).getRealName();
		dto.setUserName(name);
		dto.setOtcName(name);
		dto.setRealName(realName);
		OtcStoreModel store=otcStoreService.getByUserId(dto.getUserId(),dto.getCurrencyId());
		if(store!=null){
			dto.setTotal(store.getTotal());
			dto.setSuccess(store.getSuccess());
		}
	}

	/**
	 * 发布
	 *
	 * @param order
	 */
	@Override
	@Transactional
	public void publish(OtcOrderModel order) {
		//输入的数量和价格取八位
		order.setQuantity(order.getQuantity().setScale(8, BigDecimal.ROUND_DOWN));
		order.setPrice(order.getPrice().setScale(8, BigDecimal.ROUND_DOWN));
		order.setStatus(OtcOrderStatusEnum.WAIT.getCode());
		order.setTradeQuantity(BigDecimal.ZERO);
		order.setCancel(OtcOrderCancelEnum.NORMAL.getCode());

		this.insert(order);
		//判断卖
		if (order.getType().equals(BuySellEnum.SELL.getCode())) {
			BigDecimal quantity=order.getQuantity();
			//可用- 冻结+
			BigDecimal changeUsing= MathUtil.zeroSub(quantity);
			BigDecimal changeFreeze=quantity;
			UserOtcWalletModel wallet = userWalletService.modifyWallet(order.getUserId(), order.getCurrencyId(), changeUsing, changeFreeze);
			userWalletLogService.addLog(order.getUserId(), order.getCurrencyId(), changeUsing, WalletLogTypeEnum.OTC.getCode(), order.getId() , "OTC卖扣除",  wallet);

		}

	}

	/**
	 * 交易
	 *
	 * @param userId
	 * @param orderId
	 * @param quantity
	 * @return
	 */
	@Override
	@Transactional
	public Long trade(Long userId, Long orderId, BigDecimal quantity) {
		quantity = quantity.setScale(8, BigDecimal.ROUND_DOWN);
		OtcOrderModelDto order = this.getById(orderId);
		if (order == null) {
			throw new BusinessException(MessageSourceHolder.getMessage("message71"));
		}
		if (order.getStatus().equals(OtcOrderStatusEnum.END.getCode())) {
			throw new BusinessException(MessageSourceHolder.getMessage("message81"));
		}
		if (order.getCancel().equals(OtcOrderCancelEnum.CANCEL.getCode())) {
			throw new BusinessException(MessageSourceHolder.getMessage("message82"));
		}

		if (order.getResidue().compareTo(quantity) < 0) {
			throw new BusinessException(MessageSourceHolder.getMessage("message83") + order.getResidue());
		}

		//判断自己是否还有未付款的买单
		int count = otcOrderRecordService.getMyBuyNoPay(userId);
		if (count > 0) {
			throw new BusinessException(MessageSourceHolder.getMessage("message84"));
		}


		//如果otc是买单   冻结卖家的币
		if (order.getType().equals(BuySellEnum.BUY.getCode())) {
			//判断是否有收款信息
			List<UserPayModelDto> payList=userPayService.getByUserId(userId);
			if(payList!=null){
				payList=payList.stream().filter(t-> UserPayStatusEnum.USING.getCode().equals(t.getStatus())).collect(Collectors.toList());
			}
			if(payList==null||payList.size()==0){
				throw new BusinessException(MessageSourceHolder.getMessage("message85"));
			}
			//可用- 冻结+
			BigDecimal changeUsing= MathUtil.zeroSub(quantity);
			BigDecimal changeFreeze=quantity;
			UserOtcWalletModel wallet = userWalletService.modifyWallet(userId, order.getCurrencyId(), changeUsing, changeFreeze);
			userWalletLogService.addLog(userId, order.getCurrencyId(), changeUsing, WalletLogTypeEnum.OTC.getCode(), order.getId() , "OTC卖扣除",  wallet);
		}

		//判断订单是否完结
		order.setTradeQuantity(MathUtil.add(order.getTradeQuantity(), quantity));
		if (order.getResidue().compareTo(BigDecimal.ZERO) == 0) {
			order.setStatus(OtcOrderStatusEnum.END.getCode());
		}
		this.update(order);

		//生成订单
		Long recordId = otcOrderRecordService.createRecord(userId,order,quantity);
		return recordId;
	}

	/**
	 * 取消
	 *
	 * @param orderId
	 * @param userId    当前用户
	 * @param checkUser 是否检查用户=userId
	 */
	@Override
	@Transactional
	public void cancel(Long orderId, Long userId, boolean checkUser) {
		OtcOrderModelDto order = this.getById(orderId);
		if (order == null){
			throw new BusinessException(MessageSourceHolder.getMessage("message71"));
		}
		if(checkUser && !userId.equals(order.getUserId())) {
			throw new BusinessException(MessageSourceHolder.getMessage("message86"));
		}
		if(order.getStatus().equals(OtcOrderStatusEnum.END.getCode())) {
			throw new BusinessException(MessageSourceHolder.getMessage("message81"));
		}

		boolean canCancel = otcOrderRecordService.canCancel(orderId);
		//查询出待支付或待收款的单子  申诉的单子
		if (!canCancel) {
			throw new BusinessException(MessageSourceHolder.getMessage("message87"));
		}

		//卖单
		if(order.getType().equals(BuySellEnum.SELL.getCode())) {
			//获取已交易的数量
			BigDecimal successQuantity=otcOrderRecordService.getSuccessQuantity(orderId);
			if(order.getTradeQuantity().compareTo(successQuantity)!=0){
				order.setTradeQuantity(successQuantity);
			}
			//可用+ 冻结-
			BigDecimal quantity=order.getResidue();
			BigDecimal changeUsing= quantity;
			BigDecimal changeFreeze=MathUtil.zeroSub(quantity);
			UserOtcWalletModel wallet = userWalletService.modifyWallet(order.getUserId(),order.getCurrencyId(), changeUsing,changeFreeze);
			userWalletLogService.addLog(order.getUserId(), order.getCurrencyId(), changeUsing, WalletLogTypeEnum.OTC.getCode(), order.getId() , "OTC取消返还",  wallet);
		}

		order.setStatus(OtcOrderStatusEnum.END.getCode());
		this.update(order);
	}

	/**
	 * 上下架修改
	 * @param orderId
	 * @param cancelStatus
	 */
	@Override
	@Transactional
	public void cancelStatus(Long orderId,Integer cancelStatus) {
		OtcOrderModelDto order = this.getById(orderId);
		if (order == null){
			throw new BusinessException(MessageSourceHolder.getMessage("message71"));
		}
		if(order.getStatus().equals(OtcOrderStatusEnum.END.getCode())) {
			throw new BusinessException(MessageSourceHolder.getMessage("message81"));
		}
		otcOrderMapper.updateCancelStatus(orderId,cancelStatus);
	}
}
