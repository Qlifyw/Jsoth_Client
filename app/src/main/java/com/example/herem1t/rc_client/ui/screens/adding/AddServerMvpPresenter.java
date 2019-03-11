package com.example.herem1t.rc_client.ui.screens.adding;

public interface AddServerMvpPresenter {

    void onLoginDataInput(String extIp, String password);
    void onAddingServer(String extIp, String password, String description);

    void onStop();

}
