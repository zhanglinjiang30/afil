package com.liuqi.business.constant;


public class ConfigConstant {
    /****配置文件*****************************************************************/
    public final static String CONFIG_NAME_DEFAULT_PWD="config.default.pwd";//默认密码
    public static final String CONFIGNAME_USDT="config.usdt.currencyId";//usdt
    public static final String CONFIGNAME_BASE="config.base.currencyId";//基准币种
    public static final String CONFIGNAME_PT="config.pt.currencyId";//平台币种
    public static final String CONFIGNAME_REGISTER_INVITECODE="config.register.invitecode";//注册是否必填邀请码 0关 1开
    public static final String CONFIGNAME_REGISTER_SWITCH="config.register.switch";//注册开关 0关 1开
    public static final String CONFIGNAME_TRADE_SHOW="config.index.trade.show";//首页交易显示
    public static final String CONFIGNAME_CTC_CANCEL="config.ctc.cancel";//ctc自动取消

    public final static String CONFIG_ADDRESS = "config.default.address";
    public final static String CONFIG_PROJECTNAME = "config.default.project";

    public final static String CONFIG_PRICE = "config.price.";// 价格

    public final static String CONFIG_TOTAL_SUC = "config.total.afil";// AFIL剩余总量
    public final static String CONFIG_BLOCK_SUC = "config.block.afil";// AFIL爆出总量
    public final static String CONFIG_SERVICE_CHARGE = "config.service.charge";// 手续费


    public final static String CONFIG_GATEWAY_ADDRESS = "config.gateway.adress";// 全局网关地址

    public final static String CONFIG_TRANSER_MINER_FEE = "config.transfer.miner.fee";// 转账矿工费
    public final static String CONFIG_EXTRACT_MINER_FEE = "config.extract.miner.fee";// 提现矿工费
    public final static String CONFIG_ACTIVE_GATEWAY_FEE = "config.active.gateway.fee";// 激活网关矿工费

    //投票手续费
    public final static String CONFIG_CROWDFUND_CHARGE_AFIL_RATE = "config:crowdfund:charge:afil:rate";// 投票手续费比例

    public final static String CONFIG_PRICE_AFIL ="config.price.afil"; //AFIL 价格

    public final static String CONFIG_CROWDFUND_NO_FINISH_REFUND_RATE ="config:crowdfund:no:finish:refund:rate"; //投票未完成，返回本金比例

    public final static String CONFIG_CROWDFUND_NO_FINISH_REFUND_AFIL_RATE ="config:crowdfung:no:finish:refund:afil:rate"; //投票未完成，返回AFIL 扩大比例
    public final static String CONFIG_CROWDFUND_PROFIT_COUNT ="config:crowdfund:profit:count"; //投票计算收益，3期一轮

    public final static String CONFIG_POOL_DIRECT_PROFIT_RATE ="config:pool:direct:profit:rate"; //矿池收益直推奖励

    public final static String CONFIG_POOL_STATIC_COIN_DAYS ="config:pool:static:coin:days"; //计算每天静态矿池总量天数

    public final static String CONFIG_POOL_STATIC_COIN_AMOUNT_RATE ="config:pool:static:coin:amount:rate"; //计算每天静态矿池总量比例

    public final static String CONFIG_POOL_DYNAMIC_COIN_RATE ="config:pool:dynamic:coin:rate"; //计算每天动态矿池总量比例 静态矿池数量*1.5

    public final static String CONFIG_CROWDFUND_AFIL_RELEASE_RATE ="config:crowdfund:afil:release:rate"; //投票返还AFIL币释放比例




}
