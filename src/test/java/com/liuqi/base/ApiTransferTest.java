package com.liuqi.base;

import cn.hutool.http.HttpRequest;
import com.liuqi.utils.MD5Util;
import com.liuqi.utils.MapSort;
import org.redisson.misc.Hash;

import java.util.HashMap;
import java.util.Map;

public class ApiTransferTest {

    public static void main(String[] args){
        HttpRequest request=HttpRequest.post("http://127.0.0.1:8082/api/transfer/publish");
        String key="a0368eeb46ba464185ee73c23421d71b";
        Map<String,Object> params=new HashMap<>();
        params.put("num",System.currentTimeMillis());//唯一编码
        params.put("name","SYS");//名称
        params.put("userName","18674006013");//交易所名称
        params.put("currencyName","USDT");//转入币种
        params.put("quantity","80");//数量
        params.put("type","1");//类型 0可用 1锁仓
        params.put("transferName","123");//转入人

        String waitSign=MapSort.toStringMap(params);
        System.out.println("-->"+waitSign);
        String sign= MD5Util.MD5Encode(waitSign+key);
        params.put("sign",sign);//签名
        System.out.println(sign);
        String str=request.form(params).execute().body();
        System.out.println(str);
    }
}

