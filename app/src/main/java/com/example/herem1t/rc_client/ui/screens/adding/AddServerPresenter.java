package com.example.herem1t.rc_client.ui.screens.adding;

import android.util.Log;

import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.data.network.api.geo.GeoClient;
import com.example.herem1t.rc_client.data.network.api.geo.GeoServiceGenerator;
import com.example.herem1t.rc_client.sockets.Actions;
import com.example.herem1t.rc_client.data.DataManager;
import com.example.herem1t.rc_client.ui.base.BasePresenter;
import com.example.herem1t.rc_client.utils.IPV4Validator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.herem1t.rc_client.sockets.Actions.PORT;
import static com.example.herem1t.rc_client.utils.NetworkUtils.getClientInstance;

public class AddServerPresenter extends BasePresenter implements AddServerMvpPresenter {

    private final AddServerMvpView view;
    private Disposable disposable;

    public AddServerPresenter(DataManager dataManager, AddServerMvpView addServerMvpView) {
        super(dataManager);
        this.view = addServerMvpView;

    }

    @Override
    public void onLoginDataInput(String extIp, String password) {
        if(!extIp.equals("")) {
            password = password.equals("")? " " : password;
            IPV4Validator ipv4Validator = new IPV4Validator();
            if (ipv4Validator.validate(extIp)) {
                if (getDataManager().getDbHelper().isServerExists(extIp)){
                    view.showErrorServerAlreadyExists();
                } else {
                    view.showDescriptionFragment();
                }
            } else {
                view.showErrorIncorrectIp();
            }
        } else {
            view.showErrorNoIp();
        }
    }

    @Override
    public void onAddingServer(String extIp, String password, String description) {

        view.setAddButtonEnabled(false);
        view.showProgressDialog();

        disposable = getDataManager().getApiHelper().getCoordinatesApiCall(extIp)
                .subscribeOn(Schedulers.io())
                .map((coord) -> getDataManager().getDbHelper().addServer(extIp, password, description, coord.getLatitude(), coord.getLongitude()))
                .all(l -> l != -1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bool -> {
                            view.hideProgressDialog();
                            view.setAddButtonEnabled(true);
                            if(bool) {
                                view.toMainActivity(R.string.add_server_done);
                            } else {
                                view.toMainActivity(R.string.add_server_failed);
                            }},
                        error -> {
                            view.toMainActivity(R.string.add_server_error);
                        });


        Disposable disposable = Single.just(extIp)
                .subscribeOn(Schedulers.io())
                .map((ip) -> {
                    SocketChannel gClient  = getClientInstance(ip, PORT);
                    SocketChannel shClient  = getClientInstance(ip, PORT);

                    boolean isInitSuccessful = false;

                    if (gClient != null && shClient != null){
                        Actions actions = new Actions(getDataManager());
                        isInitSuccessful = actions.init(gClient, ip);
                        if (isInitSuccessful) {
                            actions.sendHardwareInfo(shClient, ip);
                        }
                        gClient.close();
                        shClient.close();
                    }

                    return isInitSuccessful;
                })
                .subscribe(bool -> {}, e -> {});



    }

    @Override
    public void onStop() {
        if (disposable != null) {
            disposable.dispose();
        }
    }


}
