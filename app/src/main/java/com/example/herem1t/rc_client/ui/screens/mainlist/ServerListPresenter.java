package com.example.herem1t.rc_client.ui.screens.mainlist;

import android.util.Log;

import com.example.herem1t.rc_client.data.DataManager;
import com.example.herem1t.rc_client.data.prefs.PrefsHelper;
import com.example.herem1t.rc_client.data.sqlite.model.Server;
import com.example.herem1t.rc_client.ui.base.BasePresenter;

import java.util.ArrayList;
import java.util.List;

public class ServerListPresenter extends BasePresenter implements ServerListMvpPresenter {

    private ServerListMvpView view;
    private List<Server> serverList = new ArrayList<>();

    public ServerListPresenter(DataManager dataManager, ServerListMvpView view) {
        super(dataManager);
        this.view = view;
    }


    @Override
    public void getServerList() {
        int orderBy = getDataManager().getPrefsHelper().getItemsOrder();
        serverList.clear();
        serverList.addAll(getDataManager().getDbHelper().sortBy(orderBy));
        view.setAdapterData(serverList);
    }

    @Override
    public void deleteServer(int position) {
        int deleted = getDataManager().getDbHelper().deleteRows(serverList.get(position).getExternalIP());
        serverList.remove(position);
        view.notifyItemDeleted(position);
    }

    @Override
    public void addToFavorite(int position) {
        int updated = 0;
        Server server = serverList.get(position);
        if (server.isFavourite()) {
            updated = getDataManager().getDbHelper().removeFromFavourite(server.getExternalIP());
            server.setFavourite(!server.isFavourite());
        } else {
            updated = getDataManager().getDbHelper().addToFavourite(server.getExternalIP());
            server.setFavourite(!server.isFavourite());
        }
        view.notifyItemChanged(position);
    }

    @Override
    public void onServerSelected(int position) {
        String extIp = serverList.get(position).getExternalIP();
        view.displayServerMenu(extIp);
    }


    @Override
    public void orderServersBy(int order) {
        getDataManager().getPrefsHelper().setItemsOrder(order);

        serverList.clear();
        serverList.addAll(getDataManager().getDbHelper().sortBy(order));
        view.notifyDataChanged();

    }

    @Override
    public void changePassword(int position) {
        view.onChangePasswordActivity(serverList.get(position).getExternalIP());
    }

    @Override
    public void changeDescription(int position) {
        view.onChangeDescriptionActivity(serverList.get(position).getExternalIP());
    }

    @Override
    public void updateDataset() {
        int orderBy = getDataManager().getPrefsHelper().getItemsOrder();
        serverList.clear();
        serverList.addAll(getDataManager().getDbHelper().sortBy(orderBy));
        view.notifyDataChanged();
    }
}
