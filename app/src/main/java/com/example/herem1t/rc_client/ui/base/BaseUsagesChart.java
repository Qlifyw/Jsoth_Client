package com.example.herem1t.rc_client.ui.base;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

public interface BaseUsagesChart {

    void showEmptyData();
    void setStartTimestamp(long startTimestamp);
    void setStartViewPoint(float startViewPoint);
    void loadDataset(List<Entry>... yvals);

}
