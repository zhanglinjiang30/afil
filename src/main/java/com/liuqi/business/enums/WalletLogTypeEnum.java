package com.liuqi.business.enums;

import com.liuqi.business.dto.SelectDto;

import java.util.ArrayList;
import java.util.List;

public enum WalletLogTypeEnum {

    SYS("系统", 1,true),
    TRANSFER("转账", 2,false),
    RECHARGE("充值", 3,true),
    EXTRACT("提现", 4,true),
    TRADE_BUY("交易买", 5,true),
    TRADE_SELL("交易卖", 6,true),
    FINANCING("融资融币", 7,false),
    AIR_DROP_SUB_USDT("空投扣除USDT", 8,true),
    AIR_DROP_ADD_SUC("空投增加AFIL", 9,true),
    SUPERNODE("超级节点", 10,true),
    RELEASE("锁仓释放", 11,true),
    OUT_TRANSFER("外部转入", 12,true),
    CHARGE_AWARD("手续费分红", 13,true),
    TRUST_GATEWAY("转入锁仓", 14,true),
    MINING("挖矿收益", 15,true),
    INVITE_MINING("推广收益", 16,true),
    BUY_CROWDFUND("投票扣费", 17,true),
    CROWDFUND_SERVICE_CHARGE("投票手续费", 18,true),
    CROWDFUND_REFUND("投票本金返还", 19,true),
    CROWDFUND_DIRECT_REWARD("投票直推奖励", 20,true),
    CROWDFUND_TEAM_REWARD("投票团队奖励", 20,true),
    CROWDFUND_STATIC_PROFIT("投票静态收益", 21,true),
    CROWDFUND_REFUND_AFIL("投票本金返还AFIL", 22,true),
    POOL_DIRECT_PROFIT("挖矿直推奖励", 23,true),
    INPUT("转入", 24, true),
    OUTPUT("转出", 25, true),
    PUBLIC_OFFER_BUY("公募购买", 26, true),
    PUBLIC_OFFER_PROFIT("公募奖励", 27, true),
    CROWDFUND_AFIL_RELEASE("投票AFIL释放", 28, true),
    CROWDFUND_BUY_AFIL("投票认购AFIL", 29, true),
    OTC("otc", 30, true),
    OUT_SERVICE_CHARGE("转账手续费", 31, true),
    ;

    private String name;
    private Integer code;
    private boolean using;//是否使用

    WalletLogTypeEnum(String name, Integer code,boolean using) {
        this.name = name;
        this.code = code;
        this.using = using;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public static String getName(Integer code) {
        for (WalletLogTypeEnum e : WalletLogTypeEnum.values()) {
            if (e.getCode().equals(code)) {
                return e.getName();
            }
        }
        return "";
    }

    public static List<SelectDto> getList() {
        List<SelectDto> list = new ArrayList<SelectDto>();
        for (WalletLogTypeEnum e : WalletLogTypeEnum.values()) {
            if(e.using) {
                list.add(new SelectDto(e.getCode(), e.getName()));
            }
        }
        return list;
    }
}
