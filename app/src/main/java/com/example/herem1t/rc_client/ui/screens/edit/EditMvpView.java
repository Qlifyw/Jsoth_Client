package com.example.herem1t.rc_client.ui.screens.edit;

public interface EditMvpView {

    void showDescriptionFragment();
    void showLoginFragment();
    void showMessage(int resId);
    void saveChangedPass();
    void saveChangedDescription();
    void setDescription(String oldDescription);
    void setLoginField();

}
