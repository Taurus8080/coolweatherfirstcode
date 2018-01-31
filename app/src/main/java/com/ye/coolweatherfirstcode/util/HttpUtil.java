package com.ye.coolweatherfirstcode.util;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Taurus on 18.1.31.
 * 和服务器交互，获取数据
 *
 */

public class HttpUtil {

    /**
     * 用此方法发起一条HTTP请求
     * @param address   传入的请求地址
     * @param callback  回调，处理服务器的响应
     */
    public static void sendOkHttpRequest(String address, Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
