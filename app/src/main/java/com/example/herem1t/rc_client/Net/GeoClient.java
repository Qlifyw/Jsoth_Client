package com.example.herem1t.rc_client.Net;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Herem1t on 10.05.2018.
 */

public interface GeoClient {
    @GET("json/{ipAddress}")
    Observable<Coordinates> serverCoordinates(@Path("ipAddress") String ip);
}
