package com.ye.coolweatherfirstcode.gson;

/**
 * Created by Taurus on 18.2.1.
 *
 * “aqi”:{
 *     "city":{
 *         "aqi":"44",
 *         "pm25":"13"
 *     }
 * }
 */

public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
