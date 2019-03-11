package com.example.herem1t.rc_client.ui.screens.backup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.herem1t.rc_client.data.AppDataManager;
import com.example.herem1t.rc_client.data.network.api.AppApiHelper;
import com.example.herem1t.rc_client.ui.screens.mainlist.DrawerActivity;
import com.example.herem1t.rc_client.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.events.OpenFileCallback;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.example.herem1t.rc_client.ui.screens.backup.BackupPresenter.CREATE_BACKUP;
import static com.example.herem1t.rc_client.ui.screens.backup.BackupPresenter.DOWNLOAD_BACKUP;

public class BackupActivity extends AppCompatActivity implements BackupMvpView {

    GoogleSignInClient signInClient;
    GoogleSignInAccount signInAccount;

    ConstraintLayout cl_upload;
    ConstraintLayout cl_download;

    ProgressBar progressBar;

    private BackupMvpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_doc);

        AppApiHelper apiHelper = new AppApiHelper(this);
        AppDataManager dataManager = new AppDataManager(null ,null, apiHelper, null );
        presenter = new BackupPresenter(dataManager,this);


        cl_download = findViewById(R.id.cl_download);
        cl_download.setOnClickListener(v -> {
            presenter.getUserAccount(DOWNLOAD_BACKUP);
        });

        cl_upload = findViewById(R.id.cl_upload);
        cl_upload.setOnClickListener(v -> {
            presenter.getUserAccount(CREATE_BACKUP);
        });


        progressBar = findViewById(R.id.progressbar_download_backup);

        signInClient = buildGoogleSignInClient();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        signInClient.signOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

        if (result.isSuccess()) {
            signInAccount = result.getSignInAccount();
            presenter.setSignInAccount(signInAccount);
            onSuccessfulSignIn(requestCode);
        }

    }


    @Override
    public void onSuccessfulSignIn(int action) {

        switch (action) {
            case DOWNLOAD_BACKUP:
                presenter.restoreFromBackup();
                break;
            case CREATE_BACKUP:
                presenter.createBackup();
                break;
            default:
                break;
        }
    }

    @Override
    public void updateProgressBar(int position) {
        progressBar.setProgress(position);
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    @Override
    public void setProgressBarMax(int max) {
        progressBar.setMax(max);
    }

    @Override
    public void showBackupsUploaded() {
        Toast.makeText(getApplicationContext(), R.string.backups_uploaded, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showBackupsDownloaded() {
        Toast.makeText(getApplicationContext(), R.string.backups_downloaded, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSignInDialog(int action) {
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, action);
    }


    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE, Drive.SCOPE_APPFOLDER)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }


}
