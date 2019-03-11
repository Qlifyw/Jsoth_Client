package com.example.herem1t.rc_client.ui.screens.location;

import com.example.herem1t.rc_client.data.sqlite.model.Server;
import com.google.android.gms.maps.model.LatLng;

public interface ServersMapMvpView {

    void addMarkerOnMap(Server server);

}
