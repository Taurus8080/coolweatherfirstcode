package com.ye.coolweatherfirstcode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ye.coolweatherfirstcode.gson.Forecast;
import com.ye.coolweatherfirstcode.gson.Weather;
import com.ye.coolweatherfirstcode.service.AutoUpdateService;
import com.ye.coolweatherfirstcode.util.HttpUtil;
import com.ye.coolweatherfirstcode.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 请求天气数据，并将天气数据展示到界面
 */

public class WeatherActivity extends AppCompatActivity {

    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ScrollView weatherLayou;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipRefresh;
    // 滑动菜单
    public DrawerLayout drawerLayout;
    private Button navButton; // 调出滑动菜单的按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 实现“沉浸式菜单”的另一种方法
        if (Build.VERSION.SDK_INT >= 21) {  // 5.0以上支持
            View decorView = getWindow().getDecorView(); // 得到当前活动的DecorView
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE); // 两个值表示活动的布局会显示在状态栏上
            getWindow().setStatusBarColor(Color.TRANSPARENT); // 状态栏设置成透明
        }
        setContentView(R.layout.activity_weather);
        initView();
        // 下拉刷新. 设置刷新进度条的颜色
        swipRefresh.setColorSchemeResources(R.color.colorPrimary);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);

        final String weatherId;
        if (weatherString != null) {
            // 有缓存时，直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            // 无缓存时，去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            // 请求数据时将ScrollView隐藏，以免空数据的界面看起来奇怪
            weatherLayou.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
        // 下拉刷新
        swipRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        // 来自于必应的每日一图
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }

        // 点击“home”按钮调出滑动菜单
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
                // 此时只打开滑动菜单而已，还需要处理切换城市后的逻辑才行。
                // 这个工作必须要在ChooseAreaFragment中进行，
                // 因为之前选中某个城市后是跳转到WeatherActivity的，
                // 而现在由于我们本来就是在WeatherActivity中，因此并不需要跳转，
                // 只是去请求选择城市的天气信息就可以了。
            }
        });
    }

    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this)
                        .edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this)
                                .load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 根据天气id请求城市天气信息
     *
     * @param weatherId
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid="
                + weatherId + "&key=a33f4da395b44e38a85a7a1fc9e2ec5b";

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();

                Log.d("WeatherActivity", responseText); //-----------------------------

                final Weather weather = Utility.handleWeatherResponse(responseText);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this,
                                    "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipRefresh.setRefreshing(false); // 刷新事件结束
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,
                                "从服务器获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipRefresh.setRefreshing(false); // 刷新事件结束
                    }
                });
            }
        });
        // 添加加载背景图片的方法。这样在每次请求天气信息的时候同时也会刷新背景图片。
        loadBingPic();
    }

    /**
     * 处理并显示Weather实体类中的数据
     *
     * @param weather
     */
    private void showWeatherInfo(Weather weather) {
        if(weather != null && "ok".equals(weather.status)) {
            String cityName = weather.basic.cityName;
            String updateTime = weather.basic.update.updateTime.split(" ")[1];
            String degree = weather.now.temperature + "℃";
            String weatherInfo = weather.now.more.info;
            titleCity.setText(cityName);
            titleUpdateTime.setText(updateTime);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);

            forecastLayout.removeAllViews();
            List<Forecast> forecastList = weather.forecastList;
            for (Forecast forecast : forecastList) {
                Log.d("forecast", "ok");
                View view = LayoutInflater.from(this)
                        .inflate(R.layout.forecast_item, forecastLayout, false);
                TextView dateText = view.findViewById(R.id.date_text);
                TextView infoText = view.findViewById(R.id.info_text);
                TextView maxText = view.findViewById(R.id.max_text);
                TextView minText = view.findViewById(R.id.min_text);
                Log.d("dateText", "ok");
                dateText.setText(forecast.date);
                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temperature.max);
                minText.setText(forecast.temperature.min);
                forecastLayout.addView(view);
            }
            if (weather.aqi != null) {
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
            }
            String comfort = "舒适度：" + weather.suggestion.comfort.info;
            String carWash = "洗车指数：" + weather.suggestion.carWash.info;
            String sport = "运动建议：" + weather.suggestion.sport.info;
            comfortText.setText(comfort);
            carWashText.setText(carWash);
            sportText.setText(sport);
            weatherLayou.setVisibility(View.VISIBLE);

            // 启动后台刷新天气的服务
            Intent intent = new Intent(this, AutoUpdateService.class);
            startService(intent);
        } else {
            Toast.makeText(this, "获取天气失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        weatherLayou = findViewById(R.id.weather_layou);
        bingPicImg = findViewById(R.id.bing_pic_img);
        swipRefresh = (SwipeRefreshLayout) findViewById(R.id.swip_refresh);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
    }
}
