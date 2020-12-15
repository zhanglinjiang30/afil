package com.liuqi.third.zb;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.liuqi.business.constant.ConfigConstant;
import com.liuqi.business.constant.KeyConstant;
import com.liuqi.business.service.ConfigService;
import com.liuqi.redis.RedisRepository;
import com.liuqi.utils.MathUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 查询zb交易对价格
 */
@Component
public class SearchPrice {

    private static String URL = "http://api.zb.live/data/v1/ticker";
    //private static String URL = "https://www.okex.com/api/spot/v3/instruments/";

    @Autowired
    private RedisRepository redisRepository;
    @Autowired
    private ConfigService configService;

    /**
     * 获取单个价格
     * @param name  获取名称   比如：ethqc
     * @return
     */
    public SearchPriceDto getPriceInfo(String name) {
        String key = KeyConstant.KEY_ZB_PRICE + name;
        System.out.println(name);
        SearchPriceDto price = new SearchPriceDto();//redisRepository.getModel(key);
//        if (price == null) {
////            HttpRequest request = HttpUtil.createGet(ApiUrlConstant.ZB_PRICE_URL + "?market=" + name);
//                HttpRequest request = HttpUtil.createGet(URL + "?market=" + name);
//                String result = request.execute().body();
//                System.out.println("getPriceInfo,"+ result);
//                if (StringUtils.isNotEmpty(result)) {
////                JSONObject obj = JSONObject.parseObject(result);
////                price = JSONObject.parseObject(obj.getString("ticker"), SearchPriceDto.class);
//                    price = JSONObject.parseObject(result, SearchPriceDto.class);
//                    redisRepository.set(key, price, 2L, TimeUnit.SECONDS);
//                }
//        }
        price.setBuy("0.5");
        price.setHigh("1.2");
        price.setLast("0.7");
        price.setLow("0.4");
        price.setVol("1");
        price.setSell("0.5");
        return price;
    }
    /**
     * 获取单个价格
     * @param name  获取名称   比如：ethqc
     * @return
     */
    public BigDecimal getPrice(String name) {
        String configPrice = configService.queryValueByName(ConfigConstant.CONFIG_PRICE + name.toLowerCase());
        if (StringUtils.isNotEmpty(configPrice)) {
            return new BigDecimal(configPrice);
        }
        if (StringUtils.equalsIgnoreCase(name, "USDT")) {
            return BigDecimal.ONE;
        }
        if(StringUtils.isEmpty(name)){
            return BigDecimal.ZERO;
        }
        if (!name.contains("-") && !name.contains("_")) {
            name = name.toUpperCase() + "-USDT";
        }
        SearchPriceDto dto = this.getPriceInfo(name);
        return dto!=null?new BigDecimal(dto.getLast()):BigDecimal.ZERO;
    }

    public BigDecimal getPrice2(String name) {

        if (StringUtils.equalsIgnoreCase(name, "AFIL")) {
            return MathUtil.div(BigDecimal.ONE, getUsdtQcPrice());
        }
        if (StringUtils.equalsIgnoreCase(name, "USDT")) {
            return BigDecimal.ONE;
        }
        if(StringUtils.isEmpty(name)){
            return BigDecimal.ZERO;
        }
        if (!name.contains("-") && !name.contains("_")) {
            name = name.toUpperCase() + "-USDT";
        }
        SearchPriceDto dto = this.getPriceInfo(name.toLowerCase() + "_usdt");
        return dto!=null?new BigDecimal(dto.getLast()):BigDecimal.ZERO;
    }


    /**
     * 获取usdt的人民币价格
     * @return
     */
    public BigDecimal getUsdtQcPrice() {
        //        SearchPriceDto dto = this.getPriceInfo("usdt_qc");
//        return dto!=null?new BigDecimal(dto.getLast()):BigDecimal.ZERO;
        JSONObject j = new JSONObject();
        j.put("webp", 1);
        j.put("code", "tether");
        j.put("token", "");
        try {
            String s = HttpUtil.createPost("https://dncapi.bqrank.net/api/coin/web-coininfo").body(j.toJSONString())
                    .execute().body();
            JSONObject result = JSONObject.parseObject(s);
            return result.getJSONObject("data").getBigDecimal("price_cny");
        } catch (Exception e) {
            return BigDecimal.valueOf(6.77);
        }
    }
}
