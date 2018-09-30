package com.example.herem1t.rc_client.Net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Herem1t on 17.05.2018.
 */

public class Network {

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // Move to Network.java
    public static boolean pingServer(String host){
        Runtime runtime = Runtime.getRuntime();
        try
        {
            Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 "+ host);
            int mExitValue = mIpAddrProcess.waitFor();
            Log.d("BCR", "ExitValue "+ mExitValue);
            return mExitValue == 0;
        }
        catch (InterruptedException ignore)
        {
            ignore.printStackTrace();
            Log.d("BCR", " Exception:"+ ignore);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.d("BCR", " Exception:"+ e);
        }
        return false;
    }
}
