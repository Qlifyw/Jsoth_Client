package com.example.herem1t.rc_client.ui.screens.cpu;

import com.example.herem1t.rc_client.data.DataManager;
import com.example.herem1t.rc_client.data.sqlite.DbHelper;
import com.example.herem1t.rc_client.data.sqlite.model.Server;
import com.example.herem1t.rc_client.ui.base.BasePresenter;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

import static com.example.herem1t.rc_client.utils.DateTimeUtils.stringToDateSec;

public class CpuUsagesPresenter extends BasePresenter implements CpuUsagesMvpPresenter {

    private CpuUsagesMvpView view;
    private List<Server> serversCpuUsages;
    private float startViewPoint = 0;

    public CpuUsagesPresenter(DataManager dataManager, CpuUsagesMvpView view) {
        super(dataManager);
        this.view = view;
    }


    @Override
    public void getCpuStats(String extIp) {

        DbHelper dbHelper = getDataManager().getDbHelper();

        serversCpuUsages = dbHelper.getCPUUsages( extIp);
        if (serversCpuUsages.size() <= 2) {
            view.showEmptyData();
        } else {
            Server firstReport = serversCpuUsages.get(0);
            String firstTimestamp = firstReport.getTimestamp();
            long startTimestampSec = stringToDateSec(firstTimestamp);
            view.setStartTimestamp(startTimestampSec);


            ArrayList<Entry> yVals1 = new ArrayList<Entry>();
            ArrayList<Entry> yVals3 = new ArrayList<Entry>();

            float deltaTime = 0;
            int serversCount = serversCpuUsages.size();
            for (int i=0; i<serversCount; i++) {
                Server server = serversCpuUsages.get(i);

                String tempTimestamp = server.getTimestamp();
                long sec = stringToDateSec(tempTimestamp);
                deltaTime = sec - startTimestampSec;

                String[] usages = server.getCpuUsages().split("/");
                float system = Float.valueOf(usages[0]);
                float user = Float.valueOf(usages[1]);
                yVals1.add(new Entry(deltaTime, system));
                yVals3.add(new Entry(deltaTime, user));
                if (i == serversCount-144) startViewPoint = deltaTime;
            }

            view.setStartViewPoint(startViewPoint);
            view.loadDataset(yVals1, yVals3);

        }
    }




}
