package com.ye.coolweatherfirstcode.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Taurus on 18.2.1.
 *
 * 根据JSON格式数据创建相应的实体类
 * city表示城市名
 * id表示城市对应的天气id
 * update中的loc表示天气的更新时间
 *
 * JSON格式数据例如：
 *
 * "basic":{
 *     "city":"苏州",
 *     "id":"CN10090401",
 *     "update":{
 *         "loc":"2016-08-08 21:58"
 *     }
 * }
 *
 * 由于JSON中的一些字段可能不太合适直接作为Java字段来命名，
 * 因此这里使用了@SerializedName注解的方式来让JSON字段和Java字段
 * 之间建立映射关系。
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {

        @SerializedName("loc")
        public String updateTime;
    }
}

