package com.example.herem1t.rc_client.ui.screens.location;

import com.example.herem1t.rc_client.data.DataManager;
import com.example.herem1t.rc_client.data.sqlite.model.Server;
import com.example.herem1t.rc_client.ui.base.BasePresenter;

import java.util.List;

public class ServersMapPresenter extends BasePresenter implements ServersMapMvpPresenter {

    private ServersMapMvpView view;

    public ServersMapPresenter(DataManager dataManager, ServersMapMvpView view) {
        super(dataManager);
        this.view = view;
    }

    @Override
    public void prepareMap() {

        List<Server> servers = getDataManager().getDbHelper().getLocationInfo();

        if(!servers.isEmpty()) {
            for (Server server: servers) {
                view.addMarkerOnMap(server);
            }
        }
    }



}
