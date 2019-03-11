package com.example.herem1t.rc_client.ui.screens.mainlist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.herem1t.rc_client.data.AppDataManager;
import com.example.herem1t.rc_client.data.os.model.Shell;
import com.example.herem1t.rc_client.data.prefs.AppPrefsHelper;
import com.example.herem1t.rc_client.data.prefs.PrefsHelper;
import com.example.herem1t.rc_client.data.sqlite.AppDbHelper;
import com.example.herem1t.rc_client.data.sqlite.DbOpenHelper;
import com.example.herem1t.rc_client.ui.screens.location.ServersMapPresenter;
import com.example.herem1t.rc_client.ui.screens.overview.OverviewActivity;
import com.example.herem1t.rc_client.receivers.DateReceiver;
import com.example.herem1t.rc_client.receivers.ServerInfoReceiver;
import com.example.herem1t.rc_client.ui.screens.adding.AddServerActivity;
import com.example.herem1t.rc_client.ui.screens.edit.EditActivity;
import com.example.herem1t.rc_client.ui.screens.backup.BackupActivity;
import com.example.herem1t.rc_client.ui.screens.location.ServersMapActivity;
import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.data.sqlite.model.Server;
import com.example.herem1t.rc_client.ui.screens.action.ServerMenuActivity;
import com.example.herem1t.rc_client.ui.screens.settings.SettingsActivity;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.example.herem1t.rc_client.Constants.EDIT_INTENT_OPTION;
import static com.example.herem1t.rc_client.Constants.EDIT_OPTION_DESCRIPTION;
import static com.example.herem1t.rc_client.Constants.EDIT_OPTION_PASSWORD;
import static com.example.herem1t.rc_client.Constants.INTENT_EXTRAS_EXTERNAL_IP;
import static com.example.herem1t.rc_client.utils.NetworkUtils.checkPort;
import static com.example.herem1t.rc_client.utils.NetworkUtils.pingServer;

public class DrawerActivity extends AppCompatActivity
        implements ServerListMvpView, NavigationView.OnNavigationItemSelectedListener {


    private RecyclerView recyclerView;
    private ServerAdapter serverAdapter;

    private PendingIntent pendingIntentServerReceiver;
    private PendingIntent pendingIntentDateReceiver;

    private ServerListMvpPresenter presenter;

    private final int PERMISSION_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denver);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(this.getResources().getString(R.string.title_activity_denver));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(DrawerActivity.this, AddServerActivity.class);
            startActivity(intent);
        });

        AppDbHelper dbHelper = new AppDbHelper(new DbOpenHelper(this));
        AppDataManager dataManager = new AppDataManager(dbHelper ,
                new AppPrefsHelper(this), null, null);
        presenter = new ServerListPresenter(dataManager,this);

        presenter.getServerList();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        recyclerView = (RecyclerView) findViewById(R.id.rv_server_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new CustomDeviderItemDecoration(DrawerActivity.this, LinearLayoutManager.VERTICAL, 70));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(serverAdapter);
        recyclerView.addOnItemTouchListener(new ServerListTouch(DrawerActivity.this, recyclerView, new ServerListTouch.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                presenter.onServerSelected(position);
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onLongClick(View view, int position) {

                final Context context = view.getContext();
                PopupMenu menu = new PopupMenu(context, view);
                menu.inflate(R.menu.server_list_popup);
                menu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_delete:
                            presenter.deleteServer(position);
                            break;
                        case R.id.menu_favourite:
                            presenter.addToFavorite(position);
                            break;
                        case R.id.menu_change_pass:
                            presenter.changePassword(position);
                            break;
                        case R.id.menu_edit_description:
                            presenter.changeDescription(position);
                            break;
                        default:
                            break;
                    }
                    return false;
                });
                @SuppressLint("RestrictedApi")
                MenuPopupHelper popupHelper = new MenuPopupHelper(context, (MenuBuilder) menu.getMenu(), view);
                popupHelper.setForceShowIcon(true);
                popupHelper.show();
            }
        }));

        // check date sql
        Intent alarmIntentServerReceiver = new Intent(DrawerActivity.this, ServerInfoReceiver.class);
        pendingIntentServerReceiver = PendingIntent.getBroadcast(DrawerActivity.this, 0, alarmIntentServerReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        startAlarm(DrawerActivity.this, pendingIntentServerReceiver, 1*60_000);

        Intent alarmIntentDateReceiver = new Intent(DrawerActivity.this, DateReceiver.class);
        pendingIntentDateReceiver = PendingIntent.getBroadcast(DrawerActivity.this, 0, alarmIntentDateReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        startAlarm(DrawerActivity.this, pendingIntentDateReceiver, 60_000*60*24);



    }

    void startAlarm(Context context, PendingIntent pendingIntent, int interval) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkIfAlreadyHavePermission()) {
            ActivityCompat.requestPermissions(DrawerActivity.this, new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE},
                    PERMISSION_CODE);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.updateDataset();
    }

    private boolean checkIfAlreadyHavePermission() {
        int perm1 = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int perm2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);

        int granted = PackageManager.PERMISSION_GRANTED;
        return (perm1 == granted) && (perm2 == granted);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {
            // If request is cancelled, the result arrays are empty.
            int grander = PackageManager.PERMISSION_GRANTED;
            if (grantResults.length > 0 && grantResults[0] == grander && grantResults[1] == grander) {
                Toast.makeText(DrawerActivity.this, R.string.permission_gotten, Toast.LENGTH_SHORT).show();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(DrawerActivity.this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
                finish();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.denver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement


        switch (item.getItemId()) {
            case R.id.drawer_order_by_date:
                presenter.orderServersBy(AppDbHelper.ORDER_BY_DATE);
                break;
            case R.id.drawer_order_by_favourite:
                presenter.orderServersBy(AppDbHelper.ORDER_BY_FAVOURITE);
                break;
            case R.id.drawer_order_by_os:
                presenter.orderServersBy(AppDbHelper.ORDER_BY_OS);
                break;
            case R.id.drawer_order_by_status:
                presenter.orderServersBy(AppDbHelper.ORDER_BY_STATUS);
                break;
            default:
                presenter.orderServersBy(AppDbHelper.ORDER_BY_DATE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = new Intent();
        switch (id) {
            case R.id.nav_overview:
                intent.setClass(DrawerActivity.this, OverviewActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_location:
                intent.setClass(DrawerActivity.this, ServersMapActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_setting:
                intent.setClass(DrawerActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_backups:
                intent.setClass(DrawerActivity.this, BackupActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_send:
            case R.id.nav_share:
                Toast.makeText(this, getString(R.string.drawer_error), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void displayServerMenu(String ip) {
        Intent intent = new Intent(DrawerActivity.this, ServerMenuActivity.class);
        intent.putExtra(INTENT_EXTRAS_EXTERNAL_IP, ip);
        startActivity(intent);
    }

    @Override
    public void notifyItemDeleted(int position) {
        recyclerView.getRecycledViewPool().clear();
        serverAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyItemChanged(int position) {
        serverAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyDataChanged() {
        serverAdapter.notifyDataSetChanged();
    }

    @Override
    public void setAdapterData(List<Server> servers) {
        serverAdapter = new ServerAdapter(servers);
        serverAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChangePasswordActivity(String ip) {
        Intent intent = new Intent();
        intent.putExtra(INTENT_EXTRAS_EXTERNAL_IP, ip);
        intent.putExtra(EDIT_INTENT_OPTION, EDIT_OPTION_PASSWORD);
        intent.setClass(DrawerActivity.this, EditActivity.class);
        startActivity(intent);
    }

    @Override
    public void onChangeDescriptionActivity(String ip) {
        Intent intent = new Intent();
        intent.putExtra(INTENT_EXTRAS_EXTERNAL_IP, ip);
        intent.putExtra(EDIT_INTENT_OPTION, EDIT_OPTION_DESCRIPTION);
        intent.setClass(DrawerActivity.this, EditActivity.class);
        startActivity(intent);
    }

}
