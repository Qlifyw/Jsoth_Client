package com.example.herem1t.rc_client.ui.screens.edit;

import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.data.DataManager;
import com.example.herem1t.rc_client.data.sqlite.DbHelper;
import com.example.herem1t.rc_client.ui.base.BasePresenter;

import static com.example.herem1t.rc_client.Constants.EDIT_OPTION_DESCRIPTION;
import static com.example.herem1t.rc_client.Constants.EDIT_OPTION_PASSWORD;

public class EditPresenter extends BasePresenter implements EditMvpPresenter {

    private EditMvpView view;
    private String ip;

    public EditPresenter(DataManager dataManager, EditMvpView view) {
        super(dataManager);
        this.view = view;
    }


    @Override
    public void defineOptionsType(int option, String ip) {
        this.ip = ip;
        switch (option) {
            case EDIT_OPTION_DESCRIPTION:
                view.showDescriptionFragment();
                break;
            case EDIT_OPTION_PASSWORD:
                view.showLoginFragment();
                break;
            default:
                break;
        }
    }

    @Override
    public void changeDescription(String description) {
        getDataManager().getDbHelper().updateServerDescription( ip, description);
        view.showMessage(R.string.update_server_updated);
    }

    @Override
    public void changePassword(String password) {
        String pass = password.equals("") ? " ": password;
        getDataManager().getDbHelper().changeServerConnectionPassword(ip, pass);
        view.showMessage(R.string.update_server_updated);
    }

    @Override
    public void onSave(int option) {
        switch (option) {
            case EDIT_OPTION_DESCRIPTION:
                view.saveChangedDescription();
                break;
            case EDIT_OPTION_PASSWORD:
                view.saveChangedPass();
                break;
            default:
                break;
        }
    }

    @Override
    public void setData(int option) {
        String description = getDataManager().getDbHelper().getServerDescription(ip);
        switch (option) {
            case EDIT_OPTION_DESCRIPTION:
                view.setDescription(description);
                break;
            case EDIT_OPTION_PASSWORD:
                view.setLoginField();
                break;
            default:
                break;
        }
    }



}
