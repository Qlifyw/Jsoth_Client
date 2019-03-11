package com.example.herem1t.rc_client.ui.screens.location;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.data.AppDataManager;
import com.example.herem1t.rc_client.data.sqlite.AppDbHelper;
import com.example.herem1t.rc_client.data.sqlite.DbOpenHelper;
import com.example.herem1t.rc_client.data.sqlite.model.Server;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ServersMapActivity extends FragmentActivity implements ServersMapMvpView, OnMapReadyCallback {

    private GoogleMap googleMap;

    private ServersMapMvpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servers_map);

        AppDbHelper dbHelper = new AppDbHelper(new DbOpenHelper(this));
        AppDataManager dataManager = new AppDataManager(dbHelper ,null, null, null);
        presenter = new ServersMapPresenter(dataManager,this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        presenter.prepareMap();

    }

    @Override
    public void addMarkerOnMap(Server server) {
        double lat = server.getCoordinates().getLatitude();
        double lon = server.getCoordinates().getLongitude();
        LatLng location = new LatLng(lat, lon);
        googleMap.addMarker(new MarkerOptions().position(location).title(server.getExternalIP()));
    }
}
