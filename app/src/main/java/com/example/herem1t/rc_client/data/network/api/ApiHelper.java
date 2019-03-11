package com.example.herem1t.rc_client.data.network.api;

import com.example.herem1t.rc_client.data.network.api.model.Coordinates;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.DriveResourceClient;
import io.reactivex.Observable;

public interface ApiHelper {

    DriveResourceClient getDriveResourceClient(GoogleSignInAccount account);
    Observable<Coordinates> getCoordinatesApiCall(String ip);

}
