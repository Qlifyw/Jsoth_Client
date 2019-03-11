package com.example.herem1t.rc_client.data;

import com.example.herem1t.rc_client.data.network.api.ApiHelper;
import com.example.herem1t.rc_client.data.os.OsHelper;
import com.example.herem1t.rc_client.data.prefs.PrefsHelper;
import com.example.herem1t.rc_client.data.sqlite.DbHelper;

public interface DataManager {

    ApiHelper getApiHelper();
    DbHelper getDbHelper();
    PrefsHelper getPrefsHelper();
    OsHelper getOsHelper();

}
