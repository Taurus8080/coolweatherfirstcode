package com.ye.coolweatherfirstcode.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Taurus on 18.2.1.
 *
 * "now":{
 *     "temp":"29",
 *     "cond":{
 *         "text":"阵雨"
 *     }
 * }
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
