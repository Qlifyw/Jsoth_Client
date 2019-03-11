package com.example.herem1t.rc_client.ui.screens.details;

import com.example.herem1t.rc_client.data.sqlite.model.Server;

import java.util.List;

public interface DetailsMvpView {

    void onFavoriteChecked(int itemOrder, boolean isFavorite);
    void showServersInfo(Server server);
    void onHddListLoaded(List<String> hddList);

}
