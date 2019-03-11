package com.example.herem1t.rc_client.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.herem1t.rc_client.data.AppDataManager;
import com.example.herem1t.rc_client.data.sqlite.AppDbHelper;
import com.example.herem1t.rc_client.data.sqlite.DbOpenHelper;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class DateReceiver  extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

        Completable.fromRunnable(() -> {
            AppDataManager dataManager = new AppDataManager(new AppDbHelper(new DbOpenHelper(context)),
                    null, null, null);
            dataManager.getDbHelper().deleteMonthRows();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

    }

}
