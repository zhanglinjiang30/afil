package com.liuqi.business.constant;


public class KeyConstant {
    public final static String KEY_TOKEN="key:base:token:";
    public final static String KEY_USER_ID="key:user:id:";
    public final static String KEY_USER_AUTHINFO="key:user:authinfo:";
    public final static String KEY_USER_LEVEL="key:user:level:";

    public final static String KEY_ROBOT_ID="key:robot:id:";

    public final static String KEY_CURRENCY_ID="key:currency:id:";//币种id
    public final static String KEY_CURRENCY_NAME="key:currency:name:";//币种名称
    public final static String KEY_CURRENCY_CONFIG_ID="key:currencyconfig:name:";//币种配置id
    public final static String KEY_CURRENCY_DATA_ID="key:currencydata:name:";//币种配置id
    public final static String KEY_CURRENCY_AREA="key:currency:area";//币种区域
    public final static String KEY_CURRENCYTRADE_AREAID="key:currencytrade:areaId:";//区域交易对
    public final static String KEY_CURRENCYTRADE_ID="key:currencytrade:id";//交易对id
    public final static String KEY_CONFIG_NAME="key:config:name:";//缓存配置文件key
    public final static String KEY_USER_AUTH="key:user:auth:";//用户验证
    public final static String KEY_USER_VERIFY="key:user:verify:";//用户验证
    public static final String KEY_EXTRACT_QUANTITY="key:extract:quantity:";

    public final static String KEY_SMS_CONFIG="key:sms:config:";//短信配置

    public final static String KEY_APITRANSFER_CONFIG="key:apitransfer:config:";//api转入

    public final static String KEY_SLIDE="key:slide";//轮播图
    public static final String KEY_CONTENT_INFO="key:content:info";//公告

    public final static String KEY_HELPTYPE_ID="key:helptype:id";//帮助类型id
    public final static String KEY_HELP_ID="key:help:id";//帮助类型id

    public static final String KEY_CURRENCY_PIC_NAME="key:currency:pic:name:";
    public static final String KEY_CURRENCY_PIC_ID="key:currency:pic:id:";

    public static final String KEY_FINANCING_INF="key:financing:inf:config:";

    public static final String KEY_CTC_PRICE="key:ctc:price:";
    public static final String KEY_CTC_NUM="key:ctc:num:";
    public static final String KEY_OTC_NUM="key:otc:num:";
    public static final String KEY_WORK_NUM="key:work:num:";
    public static final String KEY_SUPER_NUM="key:super:num:";

    public static final String KEY_INFO_LIST="key:info:list:";

    //撮合交易
    public static final String KEY_ALL_PRICE="key:all:price:";
    public static final String KEY_TRADE_ERROR_TIME="key:trade:error:";
    public static final String KEY_TRADEINFO_ID="key:tradeinfo:id:";

    public static final String KEY_RECORD_ERROR_TIME="key:record:error:";
    public static final String KEY_RECORD_RUN_TIME="key:record:run:";

    public static final int KEY_TRADE_LIST_NUM=20;//交易
    public static final String KEY_TRADERECORD_LIST="key_traderecord_list_";//成交记录列表
    public static final String KEY_TRUSTEE_BUY_LIST="key:trustee:buy:list:";//买列表
    public static final String KEY_TRUSTEE_SELL_LIST="key:trustee:sell:list:";//卖列表

    public static final String KEY_TRADE_SWITCH="key:trade:switch";//上次撮合开关
    public static final String KEY_TRADE_LASTTIME="key:trade:lasttime";//上次撮合交易时间  标志着撮合在运行着

    public static final String KEY_K="key_k_";//K线
    public static final String KEY_CUR_K="key:cur:k:";//K线

    public static final String KEY_ZB_PRICE="key:zb:price";//中币价格

    public final static String KEY_WORKTYPE="key:worktype";//工单类型
    public final static String KEY_WORKTYPE_Id="key:worktype:id:";//工单类型
    public final static String KEY_ZONE="key:zone";//区号
    public final static String KEY_SUPERNODE_CONFIG="key:supernode:config:";//超级节点配置

    public final static String KEY_LOCK_CONFIG_CURRENCYID="key:lock:config:currencyId:";
    public final static String KEY_LOCK_CONFIG_TRADEID="key:lock:config:tradeId";


    public static final String KEY_LOCK_RELEASE_SELL="key:lock:sell:total:";//锁仓释放数量
    public static final String KEY_LOCK_RELEASE_TRUESEE_SELL="key:lock:sell:trusteeids:";//锁仓释放单据ids
    public static final String KEY_LOCK_RELEASE_BUY="key:lock:buy:total:";//锁仓释放数量
    public static final String KEY_LOCK_RELEASE_TRUESEE_BUY="key:lock:buy:trusteeids:";//锁仓释放单据ids


    public final static String KEY_CHARGEAWARD_CONFIG="key:chargeaward:config:";//分红手续费配置
    public final static String KEY_LOCK_TRANSFER_CONFIG="key:locktransfer:config:";//分红手续费配置

    public final static String KEY_VERSION_CONFIG="key:version:config:";//版本
    public final static String KEY_LOGIN_ERROR="key:login:error:";
    public final static String KEY_WEBSOCKET_COUNT="key:websocket:count:";

    public final static String KEY_BLOCK="key:block:";
    public final static String KEY_BLOCK_ERROR="key:block:error:";
    public final static String KEY_RECHARGE="key:recharge:";
    public final static String KEY_RE_CONFIG="key:config:re";

    public static final String KEY_RECHARGE_SEARCH_STOP= "key:search:stop";
    public static final String KEY_ADMIN_AUTH_ERROR= "key:admin:auth";

    public static final String KEY_MAIN_ADDRESS = "key:device:main:address:";
    public static final String KEY_DISPLAY_ADDRESS = "key:device:display:address:";

    public static final String KEY_ADDRESS_RECORD_ID = "key:address:record:id:";
    public static final String KEY_ADDRESS_RECORD_ADDRESS = "key:address:record:address:";

    public static final String KEY_CROWDFUND_PROFIT_JOB = "key:crowdfund:profit:job:";
}
