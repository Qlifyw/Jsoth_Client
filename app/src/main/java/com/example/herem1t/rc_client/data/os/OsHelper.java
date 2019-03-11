package com.example.herem1t.rc_client.data.os;

import android.content.Context;

import java.util.Map;

public interface OsHelper {

    String getHWID();
    Map<String, Object> getDeviceInfo();

}
