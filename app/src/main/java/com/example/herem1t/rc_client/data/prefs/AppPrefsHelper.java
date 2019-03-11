package com.example.herem1t.rc_client.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.data.sqlite.AppDbHelper;

import static android.content.Context.MODE_PRIVATE;

public class AppPrefsHelper implements PrefsHelper {

    private final static String PREFS_ITEM_KEY_NOTIFICATION = "settings_notifications";
    private final static String PREFS_KEY_ORDER_BY = "ORDER_BY";
    private static final String PREFS_FILE_NAME = "jsoth";

    private SharedPreferences preferences;


    public AppPrefsHelper(Context context) {
        preferences = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
    }


    @Override
    public void setItemsOrder(int order) {
        SharedPreferences.Editor prefsEditor = preferences.edit();

        switch (order) {
            case R.id.drawer_order_by_date:
                prefsEditor.putInt(PREFS_KEY_ORDER_BY, AppDbHelper.ORDER_BY_DATE);
                break;
            case R.id.drawer_order_by_favourite:
                prefsEditor.putInt(PREFS_KEY_ORDER_BY, AppDbHelper.ORDER_BY_FAVOURITE);
                break;
            case R.id.drawer_order_by_os:
                prefsEditor.putInt(PREFS_KEY_ORDER_BY, AppDbHelper.ORDER_BY_OS);
                break;
            case R.id.drawer_order_by_status:
                prefsEditor.putInt(PREFS_KEY_ORDER_BY, AppDbHelper.ORDER_BY_STATUS);
                break;
            default:
                prefsEditor.putInt(PREFS_KEY_ORDER_BY, AppDbHelper.ORDER_BY_DATE);
                break;
        }

        prefsEditor.apply();
    }

    @Override
    public int getItemsOrder() {
        return preferences.getInt(PREFS_KEY_ORDER_BY, AppDbHelper.ORDER_BY_DATE);
    }

    @Override
    public void setNotificationEnabled(boolean enabled) {
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putBoolean(PREFS_ITEM_KEY_NOTIFICATION, enabled);
        prefsEditor.apply();
    }

    @Override
    public boolean getNotificationEnabled() {
        return preferences.getBoolean(PREFS_ITEM_KEY_NOTIFICATION, true);
    }


}
