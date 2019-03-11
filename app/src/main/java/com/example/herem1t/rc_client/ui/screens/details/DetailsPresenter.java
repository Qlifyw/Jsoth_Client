package com.example.herem1t.rc_client.ui.screens.details;

import com.example.herem1t.rc_client.data.DataManager;
import com.example.herem1t.rc_client.data.sqlite.model.Server;
import com.example.herem1t.rc_client.ui.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

public class DetailsPresenter extends BasePresenter implements DetailsMvpPresenter {

    private DetailsMvpView view;

    public DetailsPresenter(DataManager dataManager, DetailsMvpView view) {
        super(dataManager);
        this.view = view;
    }


    @Override
    public void onFavoriteClicked(String extIp, int itemOrder) {
        Server server = getDataManager().getDbHelper().getServer(extIp);
        if (server.isFavourite()) {
            getDataManager().getDbHelper().removeFromFavourite(extIp);
        } else {
            getDataManager().getDbHelper().addToFavourite(extIp);
        }
        view.onFavoriteChecked(itemOrder, !server.isFavourite());
    }

    @Override
    public void getServer(String extIp) {
        Server server = getDataManager().getDbHelper().getServer(extIp);
        view.showServersInfo(server);
    }

    @Override
    public void getHddList(String extIp) {
        List<String> hddList = new ArrayList<>();
        String disks = getDataManager().getDbHelper().getDisks(extIp);
        if (disks != null) {
            String[] hdds = disks.split(" ");
            for (String str: hdds) {
                String tempDisk = "";
                String[] parts = str.split(";");
                tempDisk = parts[0] +" " + parts[1] + "/" + parts[2];
                hddList.add(tempDisk);
            }
        }
        view.onHddListLoaded(hddList);
    }
}
