package com.example.herem1t.rc_client.ui.screens.overview;

import com.example.herem1t.rc_client.data.DataManager;
import com.example.herem1t.rc_client.data.sqlite.DbHelper;
import com.example.herem1t.rc_client.data.sqlite.model.Server;
import com.example.herem1t.rc_client.ui.base.BasePresenter;

import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_AVAILABLE;
import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_DOWN;
import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_NOT_RESPONDING;

public class OverviewPresenter extends BasePresenter implements OverviewMvpPresenter {

    private OverviewMvpView view;

    public OverviewPresenter(DataManager dataManager, OverviewMvpView view) {
        super(dataManager);
        this.view = view;
    }


    @Override
    public void getServersStatus() {

        DbHelper dbHelper = getDataManager().getDbHelper();

        int available = dbHelper.getServersCountByStatus(SERVER_AVAILABLE);
        int notResponse = dbHelper.getServersCountByStatus(SERVER_NOT_RESPONDING);
        int down = dbHelper.getServersCountByStatus(SERVER_DOWN);

        view.setServerStatusValue(available, notResponse, down);

    }
}
