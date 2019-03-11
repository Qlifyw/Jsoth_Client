package com.example.herem1t.rc_client.ui.base;

import com.example.herem1t.rc_client.data.DataManager;

public class BasePresenter {

    private final DataManager dataManager;

    public BasePresenter(DataManager dataManager) {
        this.dataManager = dataManager;
    }


    public DataManager getDataManager() {
        return dataManager;
    }
}
