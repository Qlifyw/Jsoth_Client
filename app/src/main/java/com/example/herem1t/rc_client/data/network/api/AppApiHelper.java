package com.example.herem1t.rc_client.data.network.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.herem1t.rc_client.data.network.api.geo.GeoClient;
import com.example.herem1t.rc_client.data.network.api.geo.GeoServiceGenerator;
import com.example.herem1t.rc_client.data.network.api.model.Coordinates;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveResourceClient;

import io.reactivex.Observable;

public class AppApiHelper implements ApiHelper {

    private Context context;

    public AppApiHelper(Context context) {
        this.context = context;
    }

    @Override
    public DriveResourceClient getDriveResourceClient(GoogleSignInAccount account) {
        return Drive.getDriveResourceClient(context, account);
    }

    @Override
    public Observable<Coordinates> getCoordinatesApiCall(String ip) {
        return GeoServiceGenerator.createService(GeoClient.class).getServerCoodinatesByIp(ip);
    }


}
