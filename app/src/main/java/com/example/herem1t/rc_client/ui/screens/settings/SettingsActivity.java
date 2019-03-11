package com.example.herem1t.rc_client.ui.screens.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;

import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.data.AppDataManager;
import com.example.herem1t.rc_client.data.network.api.AppApiHelper;
import com.example.herem1t.rc_client.data.os.AppOsHelper;
import com.example.herem1t.rc_client.data.prefs.AppPrefsHelper;
import com.example.herem1t.rc_client.data.sqlite.AppDbHelper;
import com.example.herem1t.rc_client.data.sqlite.DbOpenHelper;
import com.example.herem1t.rc_client.ui.screens.adding.AddServerPresenter;

/**
 * Created by Herem1t on 03.06.2018.
 */

public class SettingsActivity extends PreferenceActivity {

    SwitchPreference notification;
    SettingsMvpPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        AppDataManager dataManager = new AppDataManager(null,
                new AppPrefsHelper(this), null, null);
        presenter = new SettingsPresenter(dataManager,this);

        notification = (SwitchPreference) findPreference("settings_notification");
        notification.setOnPreferenceChangeListener((preference, o) -> {
            presenter.setNotificationPrefsEnabled(o.equals(true));
            return true;
        });

    }

}
