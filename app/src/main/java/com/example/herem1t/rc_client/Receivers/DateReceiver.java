package com.example.herem1t.rc_client.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.herem1t.rc_client.Database.DBOperations;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Herem1t on 07.05.2018.
 */

public class DateReceiver  extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Run DB operation task
        //Toast.makeText(context, "Run some task", Toast.LENGTH_SHORT).show();
        Log.d("BCR", "run task");

        Observable.just(1)
                .subscribeOn(Schedulers.computation())
                .map(i -> {
                    DBOperations.deleteMonthRows(context);
                    return i;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

    }

}
