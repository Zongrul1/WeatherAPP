package com.example.weather.Service;

import com.example.weather.gson.Weather;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import rx.Observable;

public interface RxWeatherService {
    @GET("weather")
    Call<ResponseBody> getMessage(@Query("cityid") String cityid);
}
