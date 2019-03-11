package com.example.herem1t.rc_client.ui.screens.adding;

public interface AddServerMvpView {

    void showErrorIncorrectIp();
    void showErrorNoIp();
    void showErrorServerAlreadyExists();
    void showDescriptionFragment();
    void toMainActivity(int msgResId);
    void showMessage(String msg);
    void setAddButtonEnabled(boolean state);
    void showProgressDialog();
    void hideProgressDialog();

}
