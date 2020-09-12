package com.example.weather;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weather.Service.RxWeatherService;
import com.example.weather.gson.Forecast;
import com.example.weather.gson.Weather;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.Utility;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherActivity extends AppCompatActivity {

    //控件
    public DrawerLayout drawerLayout;//侧拉

    public SwipeRefreshLayout swipeRefresh;

    private ScrollView weatherLayout;

    private Button navButton;

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

    private ImageView bingPicImg;

    private String mWeatherId;

    private static final String TAG = "weather";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        navButton = (Button) findViewById(R.id.nav_button);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        //缓存
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String weatherString = prefs.getString("weather", null);
//        Log.d("TAG",weatherString); //查询缓存
//        if (weatherString != null) {
//            // 有缓存时直接解析天气数据
//            Weather weather = Utility.handleWeatherResponse(weatherString);
//            mWeatherId = weather.basic.weatherId;
//            showWeatherInfo(weather);
//        } else {
            // 无缓存时去服务器查询天气
        mWeatherId = getIntent().getStringExtra("weather_id");
        weatherLayout.setVisibility(View.INVISIBLE);
        requestWeather(mWeatherId);
//        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            new AlertDialog.Builder(WeatherActivity.this).setTitle("退出程序").setMessage("是否退出程序？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    }) .show();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }

    public void requestWeather(final String weatherId) {
        /***
        //okhttp
         ***/
//        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=bc0418b57b2d4918819d3974ac1285d9";
//        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                final String responseText = response.body().string();
//                final Weather weather = Utility.handleWeatherResponse(responseText);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (weather != null && "ok".equals(weather.status)) {
//                            //存进缓存
//                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
//                            editor.putString("weather", responseText);
//                            editor.apply();
//                            mWeatherId = weather.basic.weatherId;
//                            showWeatherInfo(weather);
//                        } else {
//                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
//                        }
//                        swipeRefresh.setRefreshing(false);
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
//                        swipeRefresh.setRefreshing(false);
//                    }
//                });
//            }
//        });
        //loadBingPic();

         /***
         //retrofit
         ***/
        doRequestByRxRetrofit("http://guolin.tech/api/",weatherId);
    }

    /**
     * 处理并展示Weather实体类中的数据。
     */
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
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
        String sport = "运行建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
        //Intent intent = new Intent(this, AutoUpdateService.class);
        //startService(intent);
    }

    public void doRequestByRxRetrofit(String address,String county) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(address)//基础URL 建议以 / 结尾
                .addConverterFactory(GsonConverterFactory.create())//设置 Json 转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//RxJava 适配器
                .build();
        RxWeatherService rxjavaService = retrofit.create(RxWeatherService.class);
        retrofit2.Call<ResponseBody> call = rxjavaService.getMessage("weather?cityid=" + county);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    final Weather weather = Utility.handleWeatherResponse(response.body().string());
                    Log.d("TAG", weather.status);
                    runOnUiThread(new Runnable() {
                    @Override
                        public void run() {
                            if (weather != null && "ok".equals(weather.status)) {
                                //存进缓存
                                mWeatherId = weather.basic.weatherId;
                                showWeatherInfo(weather);
                            } else {
                                Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                            }
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
