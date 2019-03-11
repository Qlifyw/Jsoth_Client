package com.example.herem1t.rc_client.data.network.api.geo;

import com.example.herem1t.rc_client.data.network.api.model.Coordinates;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GeoClient {
    @GET("json/{ipAddress}")
    Observable<Coordinates> getServerCoodinatesByIp(@Path("ipAddress") String ip);
}
