package com.example.herem1t.rc_client.Receivers;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.herem1t.rc_client.Sockets.Actions;
import com.example.herem1t.rc_client.Recycler.DrawerActivity;
import com.example.herem1t.rc_client.Constants;
import com.example.herem1t.rc_client.Database.DBOperations;
import com.example.herem1t.rc_client.Sockets.GreetNIO;
import com.example.herem1t.rc_client.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.herem1t.rc_client.Sockets.GreetNIO.PORT;

/**
 * Created by Herem1t on 09.05.2018.
 */

public class ServerInfoReceiver extends BroadcastReceiver {

    @SuppressLint("CheckResult")
    @Override
    public void onReceive(Context context, Intent intent) {
        // production
        Observable.fromIterable(DBOperations.getIPAddressList(context))
        //Observable.just("192.168.1.6")
                .subscribeOn(Schedulers.io())
                .map(ip -> {
                    Log.d("BCR", "before init " + ip);
                    boolean isInitSuccessful = false;
                    try {
                        isInitSuccessful = GreetNIO.init(context, ip);
                    } catch (IOException e) {
                        Log.d("BCR", "init failed");
                    }
                    Log.d("BCR", "after init " + isInitSuccessful);
                    SocketChannel client = getClientInstance(ip, PORT);
                    Map<String, String> dataMap = new LinkedHashMap<>();
                    if (!isInitSuccessful) {
                        Log.d("BCR", "init isn't successful");
                        return dataMap;
                    }
                    if (client != null) {
                        int pre_status = DBOperations.getServerStatus(context, ip);
                        Log.d("BCR", "before recieve stat");
                        Actions.receiveStat(client, ip, context);
                        Log.d("BCR", "after receive stat");
                        int post_status = DBOperations.getServerStatus(context, ip);
                        if (pre_status != post_status) {
                            String status_text;
                            int small_icon;
                            switch (post_status) {
                                case Constants.SERVER_AVAILABLE:
                                    status_text = context.getResources().getString(R.string.notification_server_available);
                                    small_icon = R.drawable.notification_available;
                                    break;
                                case Constants.SERVER_NOT_RESPONDING:
                                    status_text = context.getResources().getString(R.string.notification_server_not_response);
                                    small_icon = R.drawable.notification_not_responding;
                                    break;
                                case Constants.SERVER_DOWN:
                                    status_text = context.getResources().getString(R.string.notification_server_down);
                                    small_icon = R.drawable.notification_down;
                                    break;
                                default:
                                    status_text = "???";
                                    small_icon = R.drawable.notification_not_responding;
                                    break;
                            }
                            String logo_path = DBOperations.getServerLogo(context, ip);
                            dataMap.put("status_text", status_text);
                            dataMap.put("ext_ip", ip);
                            dataMap.put("small_icon", String.valueOf(small_icon));
                            dataMap.put("large_icon", logo_path);

                        }
                    }
                    return dataMap;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(map -> {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    boolean isNotifEable = prefs.getBoolean("settings_notifications",true);
                    if (map.size() == 0 || !isNotifEable) {
                        return;
                    }
                    Intent resultIntent = new Intent(context, DrawerActivity.class);
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier(map.get("large_icon"), "mipmap", context.getPackageName()));
                    Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(Integer.valueOf(map.get("small_icon")))
                                    .setLargeIcon(bitmap)
                                    .setContentTitle(map.get("ext_ip"))
                                    .setContentText(map.get("status_text"))
                                    .setContentInfo(context.getResources().getString(R.string.notification_content_info))
                                    .setContentIntent(resultPendingIntent)
                                    .setAutoCancel(true);

                    Notification notification = builder.build();

                    NotificationManager notificationManager =
                            (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(1, notification);
                });


    }


    static SocketChannel getClientInstance(String host, int port) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
        SocketChannel client = null;
        try {
            client = SocketChannel.open(inetSocketAddress);
        } catch (IOException e) {
            Log.d("rxdebug", e.toString());
        }
        return  client;
    }
}
