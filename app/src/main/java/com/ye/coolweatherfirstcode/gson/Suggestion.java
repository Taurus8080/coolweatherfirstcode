package com.ye.coolweatherfirstcode.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Taurus on 18.2.1.
 *
 * "suggestion":{
 *     "comf":{
 *        "txt":"白天天气较热，虽然有雨，但仍然无法削弱较高气温给人们带来的署意，
 *              这种天气会让您感到不舒适。"
 *     },
 *     "cw":{
 *         "txt":"不以洗车。。。。。"
 *     },
 *     "sport":{
 *         "txt":"有降水。。。。。"
 *     }
 * }
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }
}
