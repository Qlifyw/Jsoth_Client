package com.example.herem1t.rc_client.data.network.api.geo;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Herem1t on 15.05.2018.
 */

public class GeoServiceGenerator {
    private static String BASE_URL = "http://ip-api.com/";

    private static OkHttpClient.Builder client = new OkHttpClient.Builder();
    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    private static Retrofit retrofit;

    public static <S> S createService(Class<S> serviceClass) {
        retrofit = builder.build();
        return retrofit.create(serviceClass);
    }
}
