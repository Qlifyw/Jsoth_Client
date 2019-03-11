package com.example.herem1t.rc_client.ui.screens.details;

public interface DetailsMvpPresenter {

    void onFavoriteClicked(String extIp, int itemOrder);
    void getServer(String extIp);
    void getHddList(String extIp);


}
