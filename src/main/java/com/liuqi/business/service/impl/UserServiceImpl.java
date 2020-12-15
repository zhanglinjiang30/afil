package com.liuqi.business.service.impl;


import cn.hutool.core.util.RandomUtil;
import com.google.common.base.Preconditions;
import com.liuqi.base.BaseMapper;
import com.liuqi.base.BaseServiceImpl;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.dto.UserDto;
import com.liuqi.business.enums.UserStatusEnum;
import com.liuqi.business.enums.YesNoEnum;
import com.liuqi.business.mapper.UserMapper;
import com.liuqi.business.model.LoggerModelDto;
import com.liuqi.business.model.UserAuthModel;
import com.liuqi.business.model.UserModel;
import com.liuqi.business.model.UserModelDto;
import com.liuqi.business.service.*;
import com.liuqi.exception.BusinessException;
import com.liuqi.redis.CodeCache;
import com.liuqi.redis.RedisRepository;
import com.liuqi.token.RedisTokenManager;
import com.liuqi.utils.ShiroPasswdUtil;
import com.liuqi.utils.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl extends BaseServiceImpl<UserModel, UserModelDto> implements UserService {

	@Autowired
	private UserMapper userMapper;
	@Autowired
	private RedisRepository redisRepository;
	@Autowired
	private ConfigService configService;
	@Autowired
	private UserWalletService userWalletService;
	@Autowired
	private RedisTokenManager redisTokenManager;
	@Autowired
	private AutoRechargeService autoRechargeService;
	@Autowired
	private UserAuthService userAuthService;
	@Autowired
	private UserLevelService userLevelService;
	@Override
	public BaseMapper<UserModel, UserModelDto> getBaseMapper() {
		return this.userMapper;
	}

	@Override
	protected void doMode(UserModelDto dto) {
		super.doMode(dto);
		dto.setRealName(userAuthService.getNameByUserId(dto.getId()));
	}

	@Override
	public UserModelDto getById(Long id) {
		UserModelDto user = redisRepository.getModel(KeyConstant.KEY_USER_ID + id);
		if (user == null) {
			user = userMapper.getById(id);
			if (user != null) {
				this.doMode(user);
				//缓存一小时
				redisRepository.set(KeyConstant.KEY_USER_ID + id, user, 5L, TimeUnit.HOURS);
			}
		}
		return user;
	}

	@Override
	public UserModelDto getNotPwdById(Long id){
		UserModelDto dto=this.getById(id);
		dto.setPwd("");
		dto.setTradePwd("");
		return dto;
	}

	@Override
	public String getRealNameById(Long id) {
		String name="";
		UserModelDto user=this.getById(id);
		name=user!=null?user.getRealName():"";
		user=null;
		return name;
	}

	@Override
	public String getOtcNameById(Long id) {
		String name="";
		UserModelDto user=this.getById(id);
		name=user!=null?user.getOtcName():"";
		user=null;
		return name;
	}
	@Override
	public String getNameById(Long id) {
		String name="";
		UserModel user=this.getById(id);
		name=user!=null?user.getName():"";
		user=null;
		return name;
	}

	@Override
	public void cleanCacheByModel(UserModel userModel) {
		if(userModel!=null){
			redisRepository.del(KeyConstant.KEY_USER_ID + userModel.getId());
		}
	}

	@Override
	public UserModelDto queryByPhone(String phone) {
		if(StringUtils.isEmpty(phone)){
			return null;
		}
		UserModelDto search = new UserModelDto();
		search.setPhone(phone);
		return userMapper.queryUnique(search);
	}

	@Override
	public UserModelDto queryByEmail(String email) {
		if(StringUtils.isEmpty(email)){
			return null;
		}
		UserModelDto search = new UserModelDto();
		search.setEmail(email);
		return userMapper.queryUnique(search);
	}

	@Override
	public UserModelDto queryByName(String name) {
		if(StringUtils.isEmpty(name)){
			return null;
		}
		UserModelDto search = new UserModelDto();
		search.setName(name);
		return userMapper.queryUnique(search);
	}

	@Override
	public UserModelDto queryByInviteCode(String inviteCode) {
		if (StringUtils.isEmpty(inviteCode)) {
			return null;
		}
		UserModelDto search = new UserModelDto();
		search.setInviteCode(inviteCode);
		return userMapper.queryUnique(search);
	}

	@Override
	public Long queryIdByName(String name) {
		UserModelDto dto=this.queryByName(name);
		return dto!=null?dto.getId():0L;
	}

	@Override
	public List<Long> queryIdByLikeName(String name) {
		if(StringUtils.isEmpty(name)){
			return null;
		}
		return userMapper.queryIdByLikeName(name);
	}


	@Override
	public boolean whiteUser(Long userId) {
		UserModel user = this.getById(userId);
		return user.getWhiteIf().equals(YesNoEnum.YES.getCode());
	}

	@Override
	public int getTotal() {
		return userMapper.getTotal();
	}

	@Override
	@Transactional
	public Map<String, Object> login(String name, String password, HttpServletRequest request) {
		String key = KeyConstant.KEY_LOGIN_ERROR + name;
		Integer times = redisRepository.getInteger(key);
		if (times >= 3) {
			throw new BusinessException("密码错误3次，请10分钟后再试");
		}
		UserModel userModel = this.queryByName(name);
		Map<String,Object> map = new HashMap<String,Object>();
		if (userModel != null) {
			if (userModel.getStatus() == 2) {
				throw new BusinessException("用户被冻结，请联系管理员！");
			}
			if (StringUtils.equalsIgnoreCase(userModel.getPwd(),
					ShiroPasswdUtil.getUserPwd(password))) {
				//使用token登录
				String token=redisTokenManager.getToken(null, null);
				map.put("token",token);
				map.put("status", UserStatusEnum.USDING.getCode());
				if (this.update(userModel)) {
					map.put("name", userModel.getName());

					//清除登录错误信息
					redisRepository.del(key);
					return map;
				} else {
					throw new BusinessException("数据库异常！");
				}
			} else {
				times++;
				if (times == 1) {
					redisRepository.set(key, times, 10L, TimeUnit.MINUTES);
				} else {
					redisRepository.incrOne(key);
				}
				throw new BusinessException("账号密码错误");
			}
		} else {
			throw  new BusinessException("用户名不存在");
		}
	}

	@Override
	@Transactional
	public void forgetPassword(String name, String pwd, String code) {
		//验证验证码  失败抛异常
		CodeCache.verifyCode(name, code);

		UserModel userModel = this.queryByName(name);
		userModel.setPwd(ShiroPasswdUtil.getUserPwd(pwd));
		this.update(userModel);
	}



	@Override
	@Transactional
	public void register(UserModelDto user) {
		//设置基础信息
		this.userBase(user);
//		//初始化用户基本信息
//		this.initUserBase(user.getId(),user.getParentName());
	}

	private void userBase(UserModelDto user){
		if(StringUtils.isEmpty(user.getName())){
			user.setName(user.getPhoneEmail());
		}
		//判断是否存在
		UserModelDto temp=this.queryByName(user.getName());
		Preconditions.checkArgument(temp==null,"用户名已存在");

		user.setPhoneAuth(YesNoEnum.NO.getCode());
		user.setEmailAuth(YesNoEnum.NO.getCode());
		user.setStatus(UserStatusEnum.USDING.getCode());
		if(ValidatorUtil.isMobile(user.getPhoneEmail())){
			if(StringUtils.isEmpty(user.getZone())) {
				user.setZone("86");
			}
			user.setPhone(user.getPhoneEmail());
			user.setPhoneAuth(YesNoEnum.NO.getCode());
			user.setAuthType(UserModelDto.AUTHTYPE_PHONE);
		}else{
			user.setEmail(user.getPhoneEmail());
			user.setEmailAuth(YesNoEnum.NO.getCode());
			user.setAuthType(UserModelDto.AUTHTYPE_EMAIL);
		}
		String defaultPwd = configService.queryValueByName(ConfigConstant.CONFIG_NAME_DEFAULT_PWD);
		//设置密码强度
		if (StringUtils.isEmpty(user.getPwd())) {
			user.setPwd(defaultPwd);
		}
		if (StringUtils.isEmpty(user.getTradePwd())) {
			user.setTradePwd(defaultPwd);
		}
		//设置密码强度
		user.setPwdStrength(this.checkPassword(user.getPwd()));
		user.setPwd(ShiroPasswdUtil.getUserPwd(user.getPwd()));
		user.setTradePwd(ShiroPasswdUtil.getUserPwd(user.getTradePwd()));
		user.setRobot(YesNoEnum.YES.getCode());
		user.setWhiteIf(YesNoEnum.NO.getCode());
		user.setInviteCode(this.getInviteCode());
		user.setOtc(YesNoEnum.NO.getCode());
		user.setOtcName(user.getName());
		this.insert(user);
	}

	/**
	 * 初始化用户信息
	 * @param userId
	 * @param inviteCode
	 */
	private void initUserBase(Long userId,String inviteCode){
		//初始化用户层级信息
//		userLevelService.initLevel(userId,inviteCode);
		//初始化用户认证信息
		userAuthService.initAuth(userId);
		//初始化用户钱包信息
		userWalletService.insertUserWallet(userId);
	}

	private String getInviteCode(){
		String inviteCode= RandomUtil.randomString(6);
		int i=0;
		while(this.queryByInviteCode(inviteCode)!=null){
			i++;
			inviteCode= RandomUtil.randomString(6);
			if(i>20){
				throw new BusinessException("邀请码生成异常");
			}
		}
		return inviteCode;
	}


	@Override
	@Transactional
	public void changePwd(Long userId, String newPwd, String pwd,boolean checkCode,  String code) {
		UserModel user = this.getById(userId);
		if (user.getPwd().equalsIgnoreCase(ShiroPasswdUtil.getUserPwd(pwd))){
			if(checkCode){
				//验证验证码  失败抛异常
				CodeCache.verifyCode(user.getName(), code);
			}
			user.setPwd(ShiroPasswdUtil.getUserPwd(newPwd));
			this.update(user);
		}else{
			throw new BusinessException("原密码输入错误");
		}
	}
	@Override
	@Transactional
	public void changeTradePwd(String tradePassword,String newTradePassword,Long userId,boolean checkCode, String code){
		UserModel user = this.getById(userId);
		if (user.getTradePwd().equalsIgnoreCase(ShiroPasswdUtil.getUserPwd(tradePassword))){
			//验证验证码  失败抛异常
			if(checkCode) {
				CodeCache.verifyCode(user.getName(), code);
			}
			user.setTradePwd(ShiroPasswdUtil.getUserPwd(newTradePassword));
			this.update(user);
		}else{
			throw new BusinessException("原密码输入错误");
		}
	}

	@Override
	@Transactional
	public void findLoginPwd(String newPwd, String code, Long userId) {
		UserModel user = this.getById(userId);
		//验证验证码  失败抛异常
		CodeCache.verifyCode(user.getName(), code);

		user.setPwd(ShiroPasswdUtil.getUserPwd(newPwd));
		this.update(user);
	}

	@Override
	@Transactional
	public void findTradePwd(String newTradePwd, String code, Long userId) {
		UserModel user = this.getById(userId);
		//验证验证码  失败抛异常
		CodeCache.verifyCode(user.getName(), code);

		user.setTradePwd(ShiroPasswdUtil.getUserPwd(newTradePwd));
		this.update(user);
	}

	@Override
	@Transactional
	public void freeze(Long userId,Long adminId) {
		UserModel user=this.getById(userId);
		user.setStatus(UserStatusEnum.FREEZE.getCode());
		this.update(user);
		loggerService.insert(LoggerModelDto.TYPE_UPDATE, "冻结用户" + user.getName(), "用户管理", adminId);
	}


	@Override
	@Transactional
	public void unfreeze(Long userId,Long adminId) {
		UserModel user=this.getById(userId);
		user.setStatus(UserStatusEnum.USDING.getCode());
		this.update(user);
		loggerService.insert(LoggerModelDto.TYPE_UPDATE, "解冻用户" + user.getName(), "用户管理", adminId);
	}

	@Override
	@Transactional
	public void phoneAuth(Long userId, String phone, String zone) {
		UserModel user = this.getById(userId);
		user.setPhone(phone);
		user.setPhoneAuth(YesNoEnum.YES.getCode());
		user.setZone(zone);
		this.update(user);
	}

	@Override
	@Transactional
	public void emailAuth(Long userId, String email) {
		UserModel user = this.getById(userId);
		user.setEmail(email);
		user.setEmailAuth(YesNoEnum.YES.getCode());
		this.update(user);
	}

	@Override
	@Transactional
	public void robot(Long userId, Long adminId) {
		UserModel user = this.getById(userId);
		user.setRobot(YesNoEnum.YES.getCode().equals(user.getRobot())?YesNoEnum.NO.getCode():YesNoEnum.YES.getCode());
		this.update(user);
		loggerService.insert(LoggerModelDto.TYPE_UPDATE, "修改用户：" + user.getName()+",改为机器人状态:"+YesNoEnum.getName(user.getRobot()), "用户管理", adminId);
	}

	@Override
	@Transactional
	public void modifyPwd(Long userId, String pwd,Long adminId) {
		UserModel user = this.getById(userId);
		user.setPwd(ShiroPasswdUtil.getUserPwd(pwd));
		this.update(user);
		loggerService.insert(LoggerModelDto.TYPE_UPDATE, "修改用户密码：" + user.getName(), "用户管理", adminId);
	}

	@Override
	@Transactional
	public void modifyTradePwd(Long userId, String tradePwd,Long adminId) {
		UserModel user = this.getById(userId);
		user.setTradePwd(ShiroPasswdUtil.getUserPwd(tradePwd));
		this.update(user);
		loggerService.insert(LoggerModelDto.TYPE_UPDATE, "修改用户交易密码：" + user.getName(), "用户管理", adminId);
	}

    @Override
    public UserDto getBaseInfo(Long userId) {
		UserDto dto=new UserDto();
		UserModel user=this.getById(userId);
		if(user!=null){
			UserAuthModel auth=userAuthService.getByUserId(userId);
			dto.setId(userId);
			dto.setName(user.getName());
			dto.setRealName(auth!=null?auth.getRealName():"");
			dto.setPhone(user.getPhone());
			dto.setEmail(user.getEmail());
		}
        return dto;
    }

	/*  一、假定密码字符数范围6-16，除英文数字和字母外的字符都视为特殊字符：
        弱：^[0-9A-Za-z]{6,16}$
        中：^(?=.{6,16})[0-9A-Za-z]*[^0-9A-Za-z][0-9A-Za-z]*$
        强：^(?=.{6,16})([0-9A-Za-z]*[^0-9A-Za-z][0-9A-Za-z]*){2,}$
        二、假定密码字符数范围6-16，密码字符允许范围为ASCII码表字符：
        弱：^[0-9A-Za-z]{6,16}$
        中：^(?=.{6,16})[0-9A-Za-z]*[\x00-\x2f\x3A-\x40\x5B-\xFF][0-9A-Za-z]*$
        强：^(?=.{6,16})([0-9A-Za-z]*[\x00-\x2F\x3A-\x40\x5B-\xFF][0-9A-Za-z]*){2,}$*/
	private  int checkPassword(String passwordStr) {
		int strength = 1;
		String regexZ = "\\d*";
		String regexS = "[a-zA-Z]+";
		String regexT = "\\W+$";
		String regexZT = "\\D*";
		String regexST = "[\\d\\W]*";
		String regexZS = "\\w*";
		String regexZST = "[\\w\\W]*";

		if (passwordStr.matches(regexZ)) {
			return 1;
		}
		if (passwordStr.matches(regexS)) {
			return 1;
		}
		if (passwordStr.matches(regexT)) {
			return 1;
		}
		if (passwordStr.matches(regexZT)) {
			return 2;
		}
		if (passwordStr.matches(regexST)) {
			return 2;
		}
		if (passwordStr.matches(regexZS)) {
			return 2;
		}
		if (passwordStr.matches(regexZST)) {
			return 3;
		}
		return strength;
	}
}
