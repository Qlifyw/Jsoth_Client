package com.example.herem1t.rc_client.ui.screens.hdd;

import android.util.Log;

import com.example.herem1t.rc_client.data.DataManager;
import com.example.herem1t.rc_client.data.sqlite.model.Server;
import com.example.herem1t.rc_client.ui.base.BasePresenter;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import static com.example.herem1t.rc_client.utils.DateTimeUtils.stringToDateSec;

public class DisksUsagesPresenter extends BasePresenter implements DisksUsagesMvpPresenter {

    private DisksUsagesMvpView view;
    private float startViewPoint = 0;

    private List<Server> serversDisksUsages;

    public DisksUsagesPresenter(DataManager dataManager, DisksUsagesMvpView view) {
        super(dataManager);
        this.view = view;
    }


    @Override
    public void getHddStats(String extIp, int item) {
        serversDisksUsages = getDataManager().getDbHelper().getDiskUsages(extIp);
        if (serversDisksUsages.size() <= 2) {
            view.showEmptyData();
        } else {
            Server firstReport = serversDisksUsages.get(0);
            String firstTimestamp = firstReport.getTimestamp();
            long startTimestampSec = stringToDateSec(firstTimestamp);

            List<Entry> data = new ArrayList<>();
            float previous = Float.valueOf(firstReport.getDisksUsages().split(" ")[item].split(";")[1]);
            data.add(new Entry(0,0));
            float deltaTime = 0;
            int serversCount = serversDisksUsages.size();
            for (int i=1; i<serversCount; i++) {
                Server server = serversDisksUsages.get(i);

                String tempTimestamp = server.getTimestamp();
                long sec = stringToDateSec(tempTimestamp);
                deltaTime = sec - startTimestampSec;

                String[] usages = server.getDisksUsages().split(" ");
                String[] disk1 = usages[item].split(";");
                data.add(new Entry(deltaTime, previous - Float.valueOf(disk1[1])));
                previous = Float.valueOf(disk1[1]);
                if (i == serversCount-144) startViewPoint = deltaTime;
            }

            view.setStartViewPoint(startViewPoint);
            view.loadDataset(data);

        }
    }



}
