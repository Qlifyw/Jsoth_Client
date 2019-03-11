package com.example.herem1t.rc_client.ui.screens.backup;

public interface BackupMvpView {
    void onSuccessfulSignIn(int action);

    void updateProgressBar(int position);
    void setProgressBarVisibility(int visibility);
    void setProgressBarMax(int max);

    void showBackupsUploaded();
    void showBackupsDownloaded();

    void showSignInDialog(int action);

}
