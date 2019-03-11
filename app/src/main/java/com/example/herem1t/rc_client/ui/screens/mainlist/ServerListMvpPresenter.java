package com.example.herem1t.rc_client.ui.screens.mainlist;

public interface ServerListMvpPresenter {

    void getServerList();
    void deleteServer(int position);
    void addToFavorite(int position);

    void onServerSelected(int position);

    void orderServersBy(int order);

    void changePassword(int position);
    void changeDescription(int position);

    void updateDataset();

}
