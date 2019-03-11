package com.example.herem1t.rc_client.ui.screens.terminal;

import android.util.Log;

import com.example.herem1t.rc_client.sockets.Actions;
import com.example.herem1t.rc_client.data.DataManager;
import com.example.herem1t.rc_client.ui.base.BasePresenter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.herem1t.rc_client.sockets.Actions.PORT;
import static com.example.herem1t.rc_client.utils.NetworkUtils.getClientInstance;

public class TerminalPresenter extends BasePresenter implements TerminalMvpPresenter {

    private TerminalMvpView view;
    private Disposable disposable;

    public TerminalPresenter(DataManager dataManager, TerminalMvpView view) {
        super(dataManager);
        this.view = view;
    }


    @Override
    public void onCommandTyped(String command, String extIp) {

        if (command.equalsIgnoreCase("clear")) {
            view.clearTerminal();
        } else {
            disposable = Observable.just(command)
                    .subscribeOn(Schedulers.io())
                    .map(s -> sendCommand(command, extIp))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(map -> {
                        if(map.size() == 0) {
                            view.showConnectionFailed();
                        } else {
                            view.displayResult(map);
                        }

                    });
        }

    }

    @Override
    public void onStop() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }


    private Map<String, String> sendCommand(String shellCommand, String extIp) {
        Map<String, String> result = new LinkedHashMap<>();
        boolean isInitSuccessful;

        SocketChannel gClient  = getClientInstance(extIp, PORT);
        SocketChannel tClient  = getClientInstance(extIp, PORT);

        if (gClient != null && tClient != null) {
            Actions actions = new Actions(getDataManager());
            isInitSuccessful = actions.init(gClient, extIp);

            if (isInitSuccessful) {
                result = actions.sendTerminalCommand(tClient, extIp, shellCommand);
            }
            try {
                gClient.close();
                tClient.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }

        }

        return result;
    }


}
