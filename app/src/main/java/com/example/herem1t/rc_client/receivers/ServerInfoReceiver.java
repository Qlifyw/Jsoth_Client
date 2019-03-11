package com.example.herem1t.rc_client.receivers;

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
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.example.herem1t.rc_client.data.os.model.Shell;
import com.example.herem1t.rc_client.data.prefs.AppPrefsHelper;
import com.example.herem1t.rc_client.sockets.Actions;
import com.example.herem1t.rc_client.data.AppDataManager;
import com.example.herem1t.rc_client.data.os.AppOsHelper;
import com.example.herem1t.rc_client.data.sqlite.AppDbHelper;
import com.example.herem1t.rc_client.data.sqlite.DbOpenHelper;
import com.example.herem1t.rc_client.ui.screens.mainlist.DrawerActivity;
import com.example.herem1t.rc_client.R;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.herem1t.rc_client.sockets.Actions.PORT;
import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_AVAILABLE;
import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_DOWN;
import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_NOT_RESPONDING;
import static com.example.herem1t.rc_client.utils.NetworkUtils.checkPort;
import static com.example.herem1t.rc_client.utils.NetworkUtils.getClientInstance;
import static com.example.herem1t.rc_client.utils.NetworkUtils.pingServer;

/**
 * Created by Herem1t on 09.05.2018.
 */

public class ServerInfoReceiver extends BroadcastReceiver {

    private static final String EXTRAS_KEY_BUILDER_STATUS = "status";
    private static final int BUILDER_EMPTY = 0;
    private static final int BUILDER_COMPLETE = 1;

    @SuppressLint("CheckResult")
    @Override
    public void onReceive(Context context, Intent intent) {

        AppDataManager dataManager = new AppDataManager(new AppDbHelper(new DbOpenHelper(context)),
                new AppPrefsHelper(context), null, new AppOsHelper(context));


        Observable.fromIterable(dataManager.getDbHelper().getIPAddressList())
                .subscribeOn(Schedulers.io())
                .map(ip -> {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "serverChannel");
                    Bundle bundle = new Bundle();
                    bundle.putInt(EXTRAS_KEY_BUILDER_STATUS, BUILDER_EMPTY);
                    builder.setExtras(bundle);

                    int preStatus = dataManager.getDbHelper().getServerStatus(ip);
                    boolean wasAvailable = (preStatus != -1) && (preStatus != 0);
                    boolean isServerOnline = pingServer(new Shell(), ip);
                    boolean isPortOpened = checkPort(ip, PORT);
                    if (isServerOnline && isPortOpened) {
                        Actions actions = new Actions(dataManager);

                        SocketChannel gClient  = getClientInstance(ip, PORT);
                        SocketChannel rsClient  = getClientInstance(ip, PORT);

                        if (gClient != null && rsClient != null) {

                            boolean isHandshakeSuccessful = true;
                            boolean isGreetingSuccessful = actions.greeting(gClient, ip);
                            if (!isGreetingSuccessful) {
                                SocketChannel hClient  = getClientInstance(ip, PORT);
                                isHandshakeSuccessful = actions.handshake(hClient, ip);
                                hClient.close();
                            }


                            if (!isHandshakeSuccessful) {
                                gClient.close();
                                rsClient.close();
                                return builder;
                            }

                            if (!isGreetingSuccessful && isHandshakeSuccessful) {
                                SocketChannel shClient = getClientInstance(ip, PORT);
                                actions.sendHardwareInfo(shClient, ip);
                                shClient.close();
                            }

                            actions.receiveStat(rsClient, ip);

                            gClient.close();
                            rsClient.close();

                        }


                        int postStatus = dataManager.getDbHelper().getServerStatus(ip);
                        if (preStatus != postStatus) {
                            String logoPath = dataManager.getDbHelper().getServerLogo(ip);
                            builder = getNotificationBuilder(context, ip, logoPath,
                                    dataManager.getDbHelper().getServerStatus(ip));

                            bundle.putInt(EXTRAS_KEY_BUILDER_STATUS, BUILDER_COMPLETE);
                            builder.setExtras(bundle);
                        }
                        return builder;
                    } else {
                        if (wasAvailable) {
                            // if server was available before but now isn't;
                            // send notification

                            String logoPath = dataManager.getDbHelper().getServerLogo(ip);
                            builder = getNotificationBuilder(context, ip, logoPath,
                                    isServerOnline? SERVER_NOT_RESPONDING : SERVER_DOWN);

                            bundle.putInt(EXTRAS_KEY_BUILDER_STATUS, BUILDER_COMPLETE);
                            builder.setExtras(bundle);
                            return builder;
                        }
                        // if server wasn't available and now also not available
                        // don't send any notification

                        dataManager.getDbHelper().updateServerStatus(ip,
                                isServerOnline ? SERVER_NOT_RESPONDING : SERVER_DOWN );

                        return builder;
                    }

                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(builder -> {
                    boolean isNotifyEnable =  dataManager.getPrefsHelper().getNotificationEnabled();
                    Bundle bundle = builder.getExtras();
                    int builderStatus = bundle.getInt(EXTRAS_KEY_BUILDER_STATUS, 0);
                    if (builderStatus == BUILDER_EMPTY || !isNotifyEnable) {
                        return;
                    }
                    Notification notification = builder.build();

                    NotificationManager notificationManager =
                            (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(1, notification);

                }, error -> {
                    //error.printStackTrace();
                });

    }

    public NotificationCompat.Builder getNotificationBuilder(Context context, String ip, String logoPath, int status) {
        String statusText;
        switch (status) {
            case SERVER_AVAILABLE:
                statusText = context.getResources().getString(R.string.notification_server_available);
                break;
            case SERVER_NOT_RESPONDING:
                statusText = context.getResources().getString(R.string.notification_server_not_response);
                break;
            case SERVER_DOWN:
                statusText = context.getResources().getString(R.string.notification_server_down);
                break;
            default:
                statusText = "???";
                break;
        }

        Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier(logoPath, "mipmap", context.getPackageName()));
        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

        Intent resultIntent = new Intent(context, DrawerActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "serverChannel")
                .setSmallIcon(R.drawable.ic_timeline_white_24dp)
                .setLargeIcon(bitmap)
                .setContentTitle(ip)
                .setContentText(statusText)
                .setContentInfo(context.getResources().getString(R.string.notification_content_info))
                .setSound(soundUri)
                .setOnlyAlertOnce(true)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true);
        return builder;
    }


}
