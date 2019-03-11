package com.example.herem1t.rc_client.ui.screens.backup;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.data.DataManager;
import com.example.herem1t.rc_client.data.sqlite.DbOpenHelper;
import com.example.herem1t.rc_client.ui.base.BasePresenter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.DB_NAME;

public class BackupPresenter extends BasePresenter implements BackupMvpPresenter {

    private final BackupMvpView view;

    private DriveResourceClient driveResourceClient;
    private GoogleSignInClient signInClient;
    private GoogleSignInAccount signInAccount;

    final static int DOWNLOAD_BACKUP = 1;
    final static int CREATE_BACKUP = 2;

    private Disposable disposable;

    public BackupPresenter(DataManager dataManager, BackupMvpView mvpView) {
        super(dataManager);
        this.view = mvpView;

    }

    @Override
    public void onStop() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @Override
    public void createBackup() {
        disposable = Observable.just(1)
                .subscribeOn(Schedulers.computation())
                .map(i -> {
                    updateBackup();
                    return i;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> view.showBackupsUploaded());
    }

    @Override
    public void restoreFromBackup() {
        disposable = Observable.just(1)
                .subscribeOn(Schedulers.computation())
                .map(i -> {
                    downloadBackup();
                    return i;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(i -> view.showBackupsDownloaded());
    }


    private void downloadBackup() {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, DB_NAME))
                .build();

        Task<MetadataBuffer> queryTask = driveResourceClient.query(query);
        queryTask.addOnSuccessListener((Activity) view, metadatas -> {

            if (metadatas.getCount() > 0) {
                for (int i = 0; i< metadatas.getCount(); i++) {
                    if (!metadatas.get(i).isInAppFolder()) continue;

                    OpenFileCallback openCallback = new OpenFileCallback() {
                        @Override
                        public void onProgress(long bytesDownloaded, long bytesExpected) {
                            // Update progress dialog with the latest progress.
                            int progress = (int) (bytesDownloaded * 100 / bytesExpected);
                            view.updateProgressBar(progress);
                        }

                        @Override
                        public void onContents(@NonNull DriveContents driveContents) {
                            // onProgress may not be called for files that are already
                            // available on the device. Mark the progress as complete
                            // when contents available to ensure status is updated.
                            // Read contents
                            // ...
                            view.setProgressBarVisibility(ProgressBar.VISIBLE);
                            int count = 0;
                            int readBytes = 0;
                            byte[] data = new byte[1024];
                            InputStream is = driveContents.getInputStream();
                            try {
                                FileOutputStream fos = new FileOutputStream(new File(getDataDir() + "/databases/Server.db"));
                                while ((readBytes = is.read(data)) != -1) {
                                    fos.write(data, 0, readBytes);

                                    count += readBytes;
                                    onProgress(count, metadatas.get(0).getFileSize());

                                }
                                view.setProgressBarVisibility(ProgressBar.INVISIBLE);
                                is.close();
                                fos.close();
                            } catch (IOException e) {
                                //e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(@NonNull Exception e) {
                            // Handle error
                            // ...
                            view.setProgressBarVisibility(ProgressBar.INVISIBLE);
                        }
                    };
                    view.setProgressBarMax(100);
                    driveResourceClient.openFile(metadatas.get(0).getDriveId().asDriveFile(), DriveFile.MODE_READ_ONLY, openCallback);

                }

            }

        });
    }


    private void updateBackup() {

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, DB_NAME))
                .build();

        Task<MetadataBuffer> queryTask = driveResourceClient.query(query).addOnFailureListener(e -> {
        });
        queryTask.addOnSuccessListener(metadatas -> {


            if (metadatas.getCount() > 0) {
                for (int i = 0; i<metadatas.getCount(); i++ ) {
                    if (metadatas.get(i).isInAppFolder()) {
                        driveResourceClient.delete(metadatas.get(i).getDriveId().asDriveFile())
                                .addOnSuccessListener(avoid -> {
                                })
                                .addOnFailureListener(e -> {});
                    }
                }
            }
            createFileInAppFolder();
        });
    }


    private void createFileInAppFolder() {

        final Task<DriveFolder> appFolderTask = driveResourceClient.getAppFolder();
        final Task<DriveContents> createContentsTask = driveResourceClient.createContents();
        Tasks.whenAll(appFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent =  appFolderTask.getResult();

                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();

                    File file = new File(getDataDir() + "/databases/" + DB_NAME);
                    int size = (int) file.length();
                    byte[] bytes = new byte[size];
                    try {
                        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }

                    outputStream.write(bytes);

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(DB_NAME)
                            .setMimeType("application/x-sqlite3")
                            .setStarred(true)
                            .build();

                    return driveResourceClient.createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener((Activity) view,
                        driveFile -> {
                            ;
                        })
                .addOnFailureListener((Activity) view, e -> {

                });
    }


    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient((Activity) view, signInOptions);
    }

    private GoogleSignInAccount silentSignIn() {
        signInClient = buildGoogleSignInClient();
        Task<GoogleSignInAccount> task = signInClient.silentSignIn();
        task.continueWith(t -> signInAccount = task.getResult());
        return signInAccount;
    }


    @Override
    public void getUserAccount(int action){
        signInAccount = silentSignIn();
        if (signInAccount == null) {
            view.showSignInDialog(action);

        } else {
            view.onSuccessfulSignIn(action);
            driveResourceClient = getDataManager().getApiHelper().getDriveResourceClient(signInAccount);

        }
    }

    @Override
    public void setSignInAccount(GoogleSignInAccount account) {
        signInAccount = account;
        driveResourceClient = getDataManager().getApiHelper().getDriveResourceClient(signInAccount);
    }

    private String getDataDir() {
        return ((Activity) view).getApplicationInfo().dataDir;
    }

}
