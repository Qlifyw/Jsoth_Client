package com.example.herem1t.rc_client.GoogleAPI;

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

import com.example.herem1t.rc_client.Recycler.DrawerActivity;
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
import com.google.android.gms.drive.Metadata;
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

public class DriveDocActivity extends AppCompatActivity {
    final static int DOWNLOAD_BACKUP = 1;
    final static int CREATE_BACKUP = 2;

    GoogleSignInClient signInClient;

    GoogleSignInAccount signInAccount;
    DriveResourceClient driveResourceClient;;

    final String LOG_TAG = "drive";
    final String FILENAME = "test.txt";

    ConstraintLayout cl_google;
    ConstraintLayout cl_upload;
    ConstraintLayout cl_download;

    ProgressBar progressBar_download_backup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_doc);

        cl_google = (ConstraintLayout) findViewById(R.id.cl_google);
        cl_google.setOnClickListener(v ->  {
            Task<GoogleSignInAccount> task = signInClient.silentSignIn();
            task.continueWith(t -> signInAccount = task.getResult());
            if (signInAccount == null) {
                Intent intent = signInClient.getSignInIntent();
                startActivityForResult(intent, 5);
            }
        } );

        cl_download = (ConstraintLayout) findViewById(R.id.cl_download);
        cl_download.setOnClickListener(v -> {
            getUserAccount(DOWNLOAD_BACKUP);
            //downloadBackup();
        });

        cl_upload = (ConstraintLayout) findViewById(R.id.cl_upload);
        cl_upload.setOnClickListener(v -> {
            getUserAccount(CREATE_BACKUP);

        });


        progressBar_download_backup = (ProgressBar) findViewById(R.id.progressbar_download_backup);
        progressBar_download_backup.setOnClickListener(v ->  {
            Context context = DriveDocActivity.this;

            Observable.just(1)
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(i -> {
                        Intent resultIntent = new Intent(context, DrawerActivity.class);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("ic_os_fedora", "mipmap", context.getPackageName()));
                        Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();

                        NotificationCompat.Builder builder =
                                new NotificationCompat.Builder(context)
                                        .setSmallIcon(R.drawable.notification_available)
                                        .setLargeIcon(bitmap)
                                        .setContentTitle("201.8.6.11")
                                        .setContentText("Server again available")
                                        .setContentInfo(context.getResources().getString(R.string.notification_content_info))
                                        .setContentIntent(resultPendingIntent)
                                        .setAutoCancel(true);

                        Notification notification = builder.build();

                        NotificationManager notificationManager =
                                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(1, notification);
                    });
        });

        signInClient = buildGoogleSignInClient();

    }

    static SocketChannel getClientInstance(String host, int port) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
        SocketChannel client = null;
        try {
            client = SocketChannel.open(inetSocketAddress);
        } catch (IOException e) {
            Log.d("rxdebug", e.toString());
        }
        return  client;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        signInClient.signOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "onActivityResult");

        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        Log.d(LOG_TAG, "onActivityResult:GET_AUTH_CODE:success:" + result.getStatus().isSuccess());

        if (result.isSuccess()) {
            signInAccount = result.getSignInAccount();

            switch (requestCode) {
                case DOWNLOAD_BACKUP:
                    Observable.just(1)
                    .subscribeOn(Schedulers.computation())
                        .map(i -> {
                            downloadBackup();
                            return i;
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(i -> Toast.makeText(getApplicationContext(), R.string.backups_downloaded, Toast.LENGTH_SHORT).show());
                    break;
                case CREATE_BACKUP:
                    Observable.just(1)
                            .subscribeOn(Schedulers.computation())
                            .map(i -> {
                                updateBackup();
                                return i;
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(i -> Toast.makeText(getApplicationContext(), R.string.backups_uploaded, Toast.LENGTH_SHORT).show());
                    break;
                default:
                    break;
            }

        }


    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }


    private void getUserAccount(int action){
        Intent intent = signInClient.getSignInIntent();
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);

        if (result != null && result.isSuccess()) {
            signInAccount = result.getSignInAccount();
        } else {
            startActivityForResult(intent, action);
        }
    }

    private void downloadBackup() {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "Server.db"))
                .build();

        Task<MetadataBuffer> queryTask = getDriveResourceClient().query(query);
        queryTask.addOnSuccessListener(this, metadatas -> {
            Log.d(LOG_TAG, "" + metadatas.getCount());
            if (metadatas.getCount() == 1) { // TODO isinappfolderfolder
                Log.d(LOG_TAG, "find 1 backup");
                OpenFileCallback openCallback = new OpenFileCallback() {
                    @Override
                    public void onProgress(long bytesDownloaded, long bytesExpected) {
                        // Update progress dialog with the latest progress.
                        int progress = (int) (bytesDownloaded * 100 / bytesExpected);
                        Log.d(LOG_TAG, String.format("Loading progress: %d percent", progress));
                        progressBar_download_backup.setProgress(progress);
                    }

                    @Override
                    public void onContents(@NonNull DriveContents driveContents) {
                        // onProgress may not be called for files that are already
                        // available on the device. Mark the progress as complete
                        // when contents available to ensure status is updated.
                        // Read contents
                        // ...
                        int count = 0;
                        int readBytes = 0;
                        byte[] data = new byte[1024];
                        InputStream is = driveContents.getInputStream();
                        try {
                            FileOutputStream fos = new FileOutputStream(new File(getApplicationInfo().dataDir + "/databases/Server.db"));
                            while ((readBytes = is.read(data)) != -1) {
                                fos.write(data, 0, readBytes);

                                count += readBytes;
                                onProgress(count, metadatas.get(0).getFileSize());
                                progressBar_download_backup.setVisibility(ProgressBar.INVISIBLE);
                            }
                            is.close();
                            fos.close();
                            Log.d(LOG_TAG, "File Downloaded " + metadatas.get(0).getFileSize());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(@NonNull Exception e) {
                        // Handle error
                        // ...
                    }
                };
                progressBar_download_backup.setVisibility(ProgressBar.VISIBLE);
                progressBar_download_backup.setMax(100);
                getDriveResourceClient().openFile(metadatas.get(0).getDriveId().asDriveFile(), DriveFile.MODE_READ_ONLY, openCallback);
            }
        });
    }

    private void updateBackup() {

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "Server.db"))
                .build();

        Task<MetadataBuffer> queryTask = getDriveResourceClient().query(query);
        queryTask.addOnSuccessListener(metadatas -> {
            if (metadatas.getCount() == 1) { // TODO add isinappfolder
                Log.d(LOG_TAG, "Founded 1 file ... deleting ");
                getDriveResourceClient().delete(metadatas.get(0).getDriveId().asDriveFile())
                        .addOnSuccessListener(avoid -> {
                            Log.d(LOG_TAG, "deleted");
                                createFileInAppFolder();
                            })
                        .addOnFailureListener(e -> Log.d(LOG_TAG, "Unable to delete file"));
            } else if (metadatas.getCount() == 0) {
                createFileInAppFolder();
            }
        });
    }

    private void createFileInAppFolder() {

        final Task<DriveFolder> appFolderTask = getDriveResourceClient().getAppFolder(); // TODO change to getAppFolder
        final Task<DriveContents> createContentsTask = getDriveResourceClient().createContents();
        Tasks.whenAll(appFolderTask, createContentsTask)
                .continueWithTask(task -> {
//                    DriveFolder parent = queryTask.getResult().get(0).getDriveId().asDriveFolder();
                    DriveFolder parent =  appFolderTask.getResult();

                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();

                    File file = new File(getApplicationInfo().dataDir + "/databases/Server.db");
                    int size = (int) file.length();
                    byte[] bytes = new byte[size];
                    try {
                        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    outputStream.write(bytes);

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("Server.db")
                            .setMimeType("application/x-sqlite3")
                            .setStarred(true)
                            .build();

                    return getDriveResourceClient().createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(this,
                        driveFile -> {
                            Log.e(LOG_TAG, "File created by app");
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(LOG_TAG, "Unable to create file " + e.toString());
                });
    }

    private void createFolder() {
        getDriveResourceClient()
                .getRootFolder()
                .continueWithTask(task -> {
                    DriveFolder parentFolder = task.getResult();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("Backup")
                            .setMimeType(DriveFolder.MIME_TYPE)
                            .setStarred(true)
                            .build();
                    return getDriveResourceClient().createFolder(parentFolder, changeSet);
                })
                .addOnSuccessListener(this,
                        driveFolder -> {
                            Log.d(LOG_TAG, "Folder created " +
                                    driveFolder.getDriveId().encodeToString());
                            updateBackup();
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(LOG_TAG, "Unable to create backup folder", e);
                });
    }


    public DriveResourceClient getDriveResourceClient() {
        return driveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
    }
}
