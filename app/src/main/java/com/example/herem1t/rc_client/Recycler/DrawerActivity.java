package com.example.herem1t.rc_client.Recycler;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.example.herem1t.rc_client.Charts.OverviewActivity;
import com.example.herem1t.rc_client.Receivers.DateReceiver;
import com.example.herem1t.rc_client.Receivers.ServerInfoReceiver;
import com.example.herem1t.rc_client.ServerInfo.AddServerActivity;
import com.example.herem1t.rc_client.ServerInfo.ChangeDescriptionActivity;
import com.example.herem1t.rc_client.ServerInfo.ChangePasswordActivity;
import com.example.herem1t.rc_client.Database.DBOperations;
import com.example.herem1t.rc_client.GoogleAPI.DriveDocActivity;
import com.example.herem1t.rc_client.GoogleAPI.ServersMapActivity;
import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.Database.Server;
import com.example.herem1t.rc_client.ServerOptions.ServerMenuActivity;
import com.example.herem1t.rc_client.Settings.SettingsActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Server> serverList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ServerAdapter serverAdapter;

    private SharedPreferences sp;

    private PendingIntent pendingIntentServerReceiver;
    private PendingIntent pendingIntentDateReceiver;

    private final int PERMISSION_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Statusbar transparent. If  19 > API > 21 then set statusbar translucent. If API > 21 then set statusbar transparent
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
//            Window w = getWindow();
//            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        } else
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(this.getResources().getString(R.string.title_activity_denver));

        sp = getPreferences(MODE_PRIVATE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DrawerActivity.this, AddServerActivity.class);
                startActivity(intent);
            }
        });

        setServersList(serverList);
        //serverList = DBOperations.getAllServers(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        recyclerView = (RecyclerView) findViewById(R.id.rv_server_list);
        serverAdapter = new ServerAdapter(serverList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new CustomDeviderItemDecoration(DrawerActivity.this, LinearLayoutManager.VERTICAL, 70));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(serverAdapter);
        recyclerView.addOnItemTouchListener(new ServerListTouch(DrawerActivity.this, recyclerView, new ServerListTouch.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                String ext_ip = serverList.get(position).getExternalIP();
                Log.d("qweqwe", "Drawer: " + ext_ip + " " + position);
                Intent intent = new Intent(DrawerActivity.this, ServerMenuActivity.class);
                intent.putExtra("ext_ip", ext_ip);
                startActivity(intent);
                //Toast.makeText(ServerListActivity.this, "Selected " + position + " item", Toast.LENGTH_SHORT).show();
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onLongClick(View view, int position) {
                //Toast.makeText(DrawerActivity.this, "Long click on " + position + " item", Toast.LENGTH_SHORT).show();

                final Context context = view.getContext();
                PopupMenu menu = new PopupMenu(context, view);
                menu.inflate(R.menu.server_list_popup);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent();
                        intent.putExtra("ext_ip", serverList.get(position).getExternalIP());
                        switch (item.getItemId()) {
                            case R.id.menu_delete:
                                //Toast.makeText(context, "Option delete is called", Toast.LENGTH_SHORT).show();
                                deleteItem(position);
                                break;
                            case R.id.menu_favourite:
                                addToFavourite(position);
                                break;
                            case R.id.menu_change_pass:
                                intent.setClass(DrawerActivity.this, ChangePasswordActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.menu_edit_description:
                                intent.setClass(DrawerActivity.this, ChangeDescriptionActivity.class);
                                startActivity(intent);
                                break;
                        }
                        return false;
                    }
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
        startAlarm(DrawerActivity.this, pendingIntentServerReceiver, 5*60_000);

        Intent alarmIntentDateReceiver = new Intent(DrawerActivity.this, DateReceiver.class);
        pendingIntentDateReceiver = PendingIntent.getBroadcast(DrawerActivity.this, 0, alarmIntentDateReceiver, PendingIntent.FLAG_UPDATE_CURRENT);
        startAlarm(DrawerActivity.this, pendingIntentDateReceiver, 60_000*60*24);

        //DBOperations.setData(getApplicationContext());
        //setData();
        //prepareSeverData();
    }

    void startAlarm(Context context, PendingIntent pendingIntent, int interval) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Log.d("BCR", "Start");
        Toast.makeText(context, "Alarm was started", Toast.LENGTH_SHORT).show();
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
    protected void onResume() {
        super.onResume();
        serverList.clear();
        setServersList(serverList);
        serverAdapter.notifyDataSetChanged();
    }

    private void setServersList(List<Server> serversList) {
        int order_by = sp.getInt("order_by", DBOperations.ORDER_BY_DATE);
        switch (order_by) {
            case DBOperations.ORDER_BY_DATE:
                serversList.addAll(DBOperations.sortBy(getApplicationContext(), DBOperations.ORDER_BY_DATE));
                break;
            case DBOperations.ORDER_BY_FAVOURITE:
                serversList.addAll(DBOperations.sortBy(getApplicationContext(), DBOperations.ORDER_BY_FAVOURITE));
                break;
            case DBOperations.ORDER_BY_OS:
                DBOperations.sortBy(getApplicationContext(), DBOperations.ORDER_BY_OS);
                break;
            case DBOperations.ORDER_BY_STATUS:
                serversList.addAll(DBOperations.sortBy(getApplicationContext(), DBOperations.ORDER_BY_STATUS));
                break;
            default:
                serversList.addAll(DBOperations.sortBy(getApplicationContext(), DBOperations.ORDER_BY_DATE));
                break;
        }
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
                Toast.makeText(DrawerActivity.this, "Permission is gotten", Toast.LENGTH_SHORT).show();
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Toast.makeText(DrawerActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private int deleteItem(int position) {
        int deleted = DBOperations.deleteRows(getApplicationContext(), serverList.get(position).getExternalIP());
        serverList.remove(position);
        recyclerView.getRecycledViewPool().clear();
//        serverAdapter.notifyItemChanged(position);
        serverAdapter.notifyDataSetChanged();
        return deleted;
    }

    public int addToFavourite(int position) {
        int updated = 0;
        Server server = serverList.get(position);
        if (server.isFavourite()) {
            updated = DBOperations.removeFromFavourite(getApplicationContext(), server.getExternalIP());
            server.setFavourite(!server.isFavourite());
        } else {
            updated = DBOperations.addToFavourite(getApplicationContext(), server.getExternalIP());
            server.setFavourite(!server.isFavourite());
        }
        serverAdapter.notifyDataSetChanged();
        return updated;
    }


//    private void setData() {
//
//        //serverList = DBOperations.getAllServers(getApplicationContext());
//        Log.d("qweqwe", serverList.size() + "");
//
//        Server movie = new Server("Mad Max: Fury Road", "Action & Adventure");
//        serverList.add(movie);
//
//        serverAdapter.notifyDataSetChanged();
//    }

//    private void prepareSeverData() {
//        Server movie = new Server("Mad Max: Fury Road", "Action & Adventure");
//        serverList.add(movie);
//
//        movie = new Server("Inside Out", "Animation, Kids & Family");
//        serverList.add(movie);
//
//        movie = new Server("Star Wars: Episode VII - The Force Awakens", "Action");
//        serverList.add(movie);
//
//        movie = new Server("Shaun the Sheep", "Animation");
//        serverList.add(movie);
//
//        movie = new Server("The Martian", "Science Fiction & Fantasy");
//        serverList.add(movie);
//
//        movie = new Server("Mission: Impossible Rogue Nation", "Action");
//        serverList.add(movie);
//
//        movie = new Server("Up", "Animation");
//        serverList.add(movie);
//
//        movie = new Server("Star Trek", "Science Fiction");
//        serverList.add(movie);
//
//        movie = new Server("The LEGO Movie", "Animation");
//        serverList.add(movie);
//
//        movie = new Server("Iron Man", "Action & Adventure");
//        serverList.add(movie);
//
//        movie = new Server("Aliens", "Science Fiction");
//        serverList.add(movie);
//
//        movie = new Server("Chicken Run", "Animation");
//        serverList.add(movie);
//
//        movie = new Server("Back to the Future", "Science Fiction");
//        serverList.add(movie);
//
//        movie = new Server("Raiders of the Lost Ark", "Action & Adventure");
//        serverList.add(movie);
//
//        movie = new Server("Goldfinger", "Action & Adventure");
//        serverList.add(movie);
//
//        movie = new Server("Guardians of the Galaxy", "Science Fiction & Fantasy");
//        serverList.add(movie);
//
//        serverAdapter.notifyDataSetChanged();
//    }

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
        serverList.clear();
        SharedPreferences.Editor ed = sp.edit();
        switch (item.getItemId()) {
            case R.id.drawer_order_by_date:
                serverList.addAll(DBOperations.sortBy(getApplicationContext(), DBOperations.ORDER_BY_DATE));
                ed.putInt("order_by",DBOperations.ORDER_BY_DATE);
                break;
            case R.id.drawer_order_by_favourite:
                serverList.addAll(DBOperations.sortBy(getApplicationContext(), DBOperations.ORDER_BY_FAVOURITE));
                ed.putInt("order_by",DBOperations.ORDER_BY_FAVOURITE);
                break;
            case R.id.drawer_order_by_os:
                DBOperations.sortBy(getApplicationContext(), DBOperations.ORDER_BY_OS);
                ed.putInt("order_by",DBOperations.ORDER_BY_OS);
                break;
            case R.id.drawer_order_by_status:
                serverList.addAll(DBOperations.sortBy(getApplicationContext(), DBOperations.ORDER_BY_STATUS));
                ed.putInt("order_by",DBOperations.ORDER_BY_STATUS);
                break;
            default:
                serverList.addAll(DBOperations.sortBy(getApplicationContext(), DBOperations.ORDER_BY_DATE));
                ed.putInt("order_by",DBOperations.ORDER_BY_DATE);
                break;
        }
        ed.apply();
        serverAdapter.notifyDataSetChanged();


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
                intent.setClass(DrawerActivity.this, DriveDocActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_send:
                break;
            default:
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
