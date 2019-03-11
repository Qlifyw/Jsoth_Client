package com.example.herem1t.rc_client.data;

import android.content.Context;

import com.example.herem1t.rc_client.data.network.api.ApiHelper;
import com.example.herem1t.rc_client.data.os.OsHelper;
import com.example.herem1t.rc_client.data.prefs.PrefsHelper;
import com.example.herem1t.rc_client.data.sqlite.DbHelper;

public class AppDataManager implements DataManager {

    private DbHelper dbHelper;
    private PrefsHelper preferencesHelper;
    private ApiHelper apiHelper;
    private OsHelper osHelper;

    public AppDataManager(DbHelper dbHelper, PrefsHelper preferencesHelper,
                          ApiHelper apiHelper, OsHelper osHelper) {
        this.dbHelper = dbHelper;
        this.preferencesHelper = preferencesHelper;
        this.apiHelper = apiHelper;
        this.osHelper = osHelper;
    }

    @Override
    public DbHelper getDbHelper() {
        return dbHelper;
    }

    @Override
    public PrefsHelper getPrefsHelper() {
        return preferencesHelper;
    }

    @Override
    public OsHelper getOsHelper() {
        return osHelper;
    }

    @Override
    public ApiHelper getApiHelper() {
        return apiHelper;
    }
}
