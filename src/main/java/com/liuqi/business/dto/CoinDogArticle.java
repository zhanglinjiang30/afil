package com.liuqi.business.dto;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.model.CoinArticleModelDto;
import com.liuqi.utils.MD5Util;

import java.util.List;

public class CoinDogArticle {

    private static final String access_key = "897f0397c26e506bf65a0d0b99388f87";

    private static final String secret_key = "7d592e6d914cb04f";

    public static List<CoinArticleModelDto> getCoinArticle(String last_id){

        String date= System.currentTimeMillis()/1000+"";

        String params="access_key="+access_key+"&date="+date+"&last_id="+last_id;
        String sign= MD5Util.MD5Encode(params+"&secret_key="+secret_key).toLowerCase();

        params=params+"&sign="+sign;
        String url="http://api.coindog.com/topic/list?"+params;
        HttpRequest request= HttpUtil.createGet(url);
        String result=request.execute().body();
        return JSONObject.parseArray(result, CoinArticleModelDto.class);
    }

    public static void main(String[] args) {
        List<CoinArticleModelDto> coinArticle = CoinDogArticle.getCoinArticle("591579");

        for (CoinArticleModelDto coinArticleDto : coinArticle) {

        }
    }
}
