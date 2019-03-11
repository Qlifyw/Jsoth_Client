package com.example.herem1t.rc_client.ui.screens.edit;

public interface EditMvpPresenter {

    void defineOptionsType(int option, String ip);
    void changeDescription(String description);
    void changePassword(String password);
    void onSave(int option);
    void setData(int option);

}
