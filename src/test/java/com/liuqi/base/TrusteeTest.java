package com.liuqi.base;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class TrusteeTest{

    public static final int count=100;
    public static CountDownLatch countDownLatch=new CountDownLatch(count);
    public static String[] user={"a96bc759d248a2d08f860da12a22a496","5960dc33da2354914eab9e1fd5669e67"};
    public static void main(String[] args){
       /* for(int i=0;i<count;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    countDownLatch.countDown();
                    int index=RandomUtil.randomInt(1);
                    int index1= Math.abs(1-index);
                    try {
                        countDownLatch.await();
                        buy(user[index]);
                        sell(user[index1]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        try {
            Thread.sleep(100000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
       //String token="14ecbe530c1efb02d5d6a0dd4f9038e7";
       //String token="c14a9bae8fc005ba8f996653dbe594cf";
       String token="8c7f462031b60a6e440c4037f60c420e";
       sell(token);
        token="14ecbe530c1efb02d5d6a0dd4f9038e7";
        buy(token);
    }

    public static void buy(String userToken){
        System.out.println(Thread.currentThread().getName()+"buy--start->"+ com.liuqi.utils.DateTimeUtils.currentDate("HH:mm:ss:SSS"));
        HttpRequest request= HttpUtil.createPost("http://127.0.0.1:8081/front/trade/publish");
        Map<String,Object> params=new HashMap<>();
        params.put("token",userToken);
        params.put("tradePassword","111111");
        params.put("tradeId",1L);
        params.put("tradeType",0);
        params.put("quantity", 10);
        //params.put("quantity", "10");
        params.put("price",1);
        request.form(params);
        String result=request.execute().body();
        System.out.println(result);
        System.out.println(Thread.currentThread().getName()+"buy--end->"+ com.liuqi.utils.DateTimeUtils.currentDate("HH:mm:ss:SSS"));
    }
    public static void sell(String userToken){
        System.out.println(Thread.currentThread().getName()+"sell--start->"+ com.liuqi.utils.DateTimeUtils.currentDate("HH:mm:ss:SSS"));
        HttpRequest request= HttpUtil.createPost("http://127.0.0.1:8081/front/trade/publish");
        Map<String,Object> params=new HashMap<>();
        params.put("token",userToken);
        params.put("tradePassword","111111");
        params.put("tradeId",1L);
        params.put("tradeType",1);
        params.put("quantity", 10);
        //params.put("quantity", "10");
        params.put("price",1);
        request.form(params);
        String result=request.execute().body();
        System.out.println(result);
        System.out.println(Thread.currentThread().getName()+"sell--end->"+ com.liuqi.utils.DateTimeUtils.currentDate("HH:mm:ss:SSS"));
    }

}
