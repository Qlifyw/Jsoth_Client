package com.example.herem1t.rc_client.ui.screens.backup;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public interface BackupMvpPresenter {

    void createBackup();
    void restoreFromBackup();
    void getUserAccount(int action);
    void setSignInAccount(GoogleSignInAccount account);
    void onStop();

}
