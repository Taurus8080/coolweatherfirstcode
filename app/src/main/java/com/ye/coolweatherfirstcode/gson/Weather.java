package com.ye.coolweatherfirstcode.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Taurus on 18.2.1.
 *
 * “和风天气”返回的数据非常多，作者筛选一些比较重要的数据进行解析。
 * 返回数据的大致格式：
 * {
 *     "HeWeather":[
 *          {
 *         "status":"ok",
 *         "basic":{},
 *         "aqi":{},
 *         "now":{},
 *         "suggestion":{},
 *         "daily_forecast":[]
 *         }
 *     ]
 * }
 * 其中basic、aqi、now、suggestion 和 dialy_forecast的内部
 * 都会有具体的内容，那么我们就将这5个部分定义成5个实体类。
 * status数据，成功返回ok，失败则会返回具体的原因。
 *
 * 总的实例类，引用各个实体类。
 */

public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
