package com.example.herem1t.rc_client.ui.screens.settings;

import android.content.Context;

import com.example.herem1t.rc_client.data.DataManager;
import com.example.herem1t.rc_client.ui.base.BasePresenter;

public class SettingsPresenter extends BasePresenter implements SettingsMvpPresenter {

    Context context;

    public SettingsPresenter(DataManager dataManager, Context context) {
        super(dataManager);
        this.context = context;
    }


    @Override
    public void setNotificationPrefsEnabled(boolean enabled) {
        getDataManager().getPrefsHelper().setNotificationEnabled(enabled);
    }
}
