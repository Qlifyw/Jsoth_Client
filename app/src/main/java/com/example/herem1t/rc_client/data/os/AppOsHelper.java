package com.example.herem1t.rc_client.data.os;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.herem1t.rc_client.sockets.Actions.JSON_HWID;

public class AppOsHelper implements OsHelper {

    private static final String DEVICE_MODEL =  "model";
    private static final String DEVICE_VERSION =  "version";
    private static final String DEVICE_API =  "api";

    private final Context context;

    public AppOsHelper(Context context) {
        this.context = context;
    }

    @SuppressLint("HardwareIds")
    @Override
    public String getHWID() {
        // size = 16 byte
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public Map<String, Object> getDeviceInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put(JSON_HWID, getHWID());
        info.put(DEVICE_MODEL, Build.MODEL);
        info.put(DEVICE_VERSION, Build.VERSION.RELEASE);
        info.put(DEVICE_API, Build.VERSION.SDK_INT);
        return info;
    }
}
