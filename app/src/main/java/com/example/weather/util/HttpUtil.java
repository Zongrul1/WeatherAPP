package com.example.weather.util;

import android.util.Log;

import com.example.weather.Service.RxWeatherService;
import com.example.weather.gson.Weather;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HttpUtil {

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    public static void doRequestByRxRetrofit(String address,String county) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(address)//基础URL 建议以 / 结尾
                .addConverterFactory(GsonConverterFactory.create())//设置 Json 转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//RxJava 适配器
                .build();
        RxWeatherService rxjavaService = retrofit.create(RxWeatherService.class);
        Call<ResponseBody> call = rxjavaService.getMessage("weather?cityid=" + county);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Weather weather = Utility.handleWeatherResponse(response.body().string());
                    Log.d("TAG",weather.status);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
//        rxjavaService.getMessage("weather?cityid=" + county)
//                .subscribeOn(Schedulers.io())//IO线程加载数据
//                .observeOn(AndroidSchedulers.mainThread())//主线程显示数据
//                .subscribe(new Subscriber<Weather>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.d("TAG", "Finish");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("TAG", "ERROR");
//                    }
//                    @Override
//                    public void onNext(Weather weatherEntity) {
//
//                    }
//                });
    }

}
