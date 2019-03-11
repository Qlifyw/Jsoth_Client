package com.example.herem1t.rc_client.ui.screens.ram;

import android.util.Log;

import com.example.herem1t.rc_client.data.DataManager;
import com.example.herem1t.rc_client.data.sqlite.model.Server;
import com.example.herem1t.rc_client.ui.base.BasePresenter;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import static com.example.herem1t.rc_client.utils.DateTimeUtils.MsToDate;
import static com.example.herem1t.rc_client.utils.DateTimeUtils.stringToDateSec;

public class RamUsagesPresenter extends BasePresenter implements RamUsagesMvpPresenter {

    private List<Server> serversRamUsages;
    private float startViewPoint = 0;
    private RamUsagesMvpView view;

    public RamUsagesPresenter(DataManager dataManager, RamUsagesMvpView view) {
        super(dataManager);
        this.view = view;
    }


    @Override
    public void getRamStats(String extIp) {

        serversRamUsages = getDataManager().getDbHelper().getRamUsages(extIp);
        if (serversRamUsages.size() <= 2) {
            view.showEmptyData();
        } else {
            Server firstReport = serversRamUsages.get(0);
            String firstTimestamp = firstReport.getTimestamp();
            long startTimestampSec = stringToDateSec(firstTimestamp);
            view.setStartTimestamp(startTimestampSec);


            ArrayList<Entry> yVals = new ArrayList<Entry>();


            float deltaTime = 0;
            int serversCount = serversRamUsages.size();
            for(int i=0; i<serversCount; i++){
                Server server = serversRamUsages.get(i);
                String tempTimestamp = server.getTimestamp();
                long sec = stringToDateSec(tempTimestamp);
                deltaTime = sec - startTimestampSec;
                yVals.add(new Entry(deltaTime, Float.valueOf(server.getRamUsages())));
                if (i == serversCount-144) startViewPoint = deltaTime;
            }

            view.setStartViewPoint(startViewPoint);
            view.loadDataset(yVals);

        }
    }



}
