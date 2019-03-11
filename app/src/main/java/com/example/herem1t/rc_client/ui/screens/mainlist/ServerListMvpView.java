package com.example.herem1t.rc_client.ui.screens.mainlist;

import com.example.herem1t.rc_client.data.sqlite.model.Server;

import java.util.List;

public interface ServerListMvpView {

    void displayServerMenu(String ip);

    void notifyItemDeleted(int position);
    void notifyItemChanged(int position);
    void notifyDataChanged();

    void setAdapterData(List<Server> servers);

    void onChangePasswordActivity(String ip);
    void onChangeDescriptionActivity(String ip);

}
