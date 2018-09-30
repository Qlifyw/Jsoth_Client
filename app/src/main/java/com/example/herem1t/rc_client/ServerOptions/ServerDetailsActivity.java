package com.example.herem1t.rc_client.ServerOptions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.herem1t.rc_client.Charts.CpuUsagesActivity;
import com.example.herem1t.rc_client.Charts.DisksUsagesActivity;
import com.example.herem1t.rc_client.Charts.RamUsagesActivity;
import com.example.herem1t.rc_client.Constants;
import com.example.herem1t.rc_client.Database.DBOperations;
import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.Database.Server;

import java.util.ArrayList;
import java.util.List;

public class ServerDetailsActivity extends AppCompatActivity {

    ImageView iv_logo;
    ImageView iv_logo_bg;

    ImageView iv_favourite;
    ImageView iv_favourite_icon;

    TextView tv_hdd;
    TextView tv_header_ext_ip;
    TextView tv_description;
    TextView tv_hostname;
    TextView tv_os_name;
    TextView tv_uptime;
    TextView tv_ext_ip;
    TextView tv_local_ip;
    TextView tv_country;
    TextView tv_ram;
    TextView tv_swap;
    TextView tv_cpu_vendor;
    TextView tv_cpu_model;
    TextView tv_cpu_cores;
    TextView tv_cpu_mhz;

    ConstraintLayout cl_block_hdd;
    ConstraintLayout cl_block_cpu;
    ConstraintLayout cl_block_ram;

    String ext_ip;
    Server server;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_details);

        context = ServerDetailsActivity.this;

        Intent app_intent = getIntent();
        ext_ip = app_intent.getStringExtra("ext_ip");
        Log.d("qweqwe", "Details: " + ext_ip);
        server = DBOperations.getServer(getApplicationContext(), ext_ip);

        tv_hdd = (TextView) findViewById(R.id.tv_hdd);
        tv_hdd.setText(context.getResources().getString(R.string.details_tap_open));

        tv_description = (TextView) findViewById(R.id.tv_description);
        //tv_description.setText(server.getDescription());

        tv_hostname = (TextView) findViewById(R.id.tv_machine_name);
        tv_hostname.setText(server.getMachineName());

        tv_os_name = (TextView) findViewById(R.id.tv_os_name);
        tv_os_name.setText(server.getOSname());

        tv_uptime = (TextView) findViewById(R.id.tv_uptime);
        tv_uptime.setText(server.getUptime());

        tv_ext_ip = (TextView) findViewById(R.id.tv_ext_ip);
        tv_ext_ip.setText(server.getExternalIP());

        tv_local_ip = (TextView) findViewById(R.id.tv_local_ip);
        tv_local_ip.setText(server.getLocalIP());

        tv_country = (TextView) findViewById(R.id.tv_country);
        tv_country.setText(server.getCountyCode());

        tv_ram = (TextView) findViewById(R.id.tv_ram);
        tv_ram.setText((server.getRam() == null? 0.0 : server.getRam()) + " Gb");

        tv_swap = (TextView) findViewById(R.id.tv_swap);
        tv_swap.setText(String.valueOf(server.getSWAP() + " Gb"));

        if (server.getCPUInfo() != null) {
            String[] cpu_info = server.getCPUInfo().split(";");
            tv_cpu_vendor = (TextView) findViewById(R.id.tv_vendor);
            tv_cpu_vendor.setText(cpu_info[0]);
            tv_cpu_model = (TextView) findViewById(R.id.tv_model);
            tv_cpu_model.setText(cpu_info[1]);
            tv_cpu_cores = (TextView) findViewById(R.id.tv_cores);
            tv_cpu_cores.setText(cpu_info[2]);
            tv_cpu_mhz = (TextView) findViewById(R.id.tv_mhz);
            tv_cpu_mhz.setText(cpu_info[3]);
        }


        cl_block_hdd = (ConstraintLayout) findViewById(R.id.block_hdd);
        cl_block_cpu = (ConstraintLayout) findViewById(R.id.block_cpu);
        cl_block_cpu.setOnClickListener(v -> {
            Intent intent = new Intent(ServerDetailsActivity.this, CpuUsagesActivity.class);
            intent.putExtra("ext_ip", ext_ip);
            startActivity(intent);

        });
        cl_block_ram = (ConstraintLayout) findViewById(R.id.ram);
        cl_block_ram.setOnClickListener(v -> {
            Intent intent = new Intent(ServerDetailsActivity.this, RamUsagesActivity.class);
            intent.putExtra("ext_ip", ext_ip);
            startActivity(intent);

        });

        tv_header_ext_ip = (TextView) findViewById(R.id.tv_header_ext_ip);
        Log.d("qweqwe", "Details: " + server.getExternalIP());
        tv_header_ext_ip.setText(server.getExternalIP());

        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        iv_logo_bg = (ImageView) findViewById(R.id.iv_logo_bg);

        iv_favourite = (ImageView) findViewById(R.id.iv_favourite);

        iv_favourite_icon = (ImageView) findViewById(R.id.iv_favourite_icon);
//        Drawable favourite_while = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_unfavourite);
//        Drawable favourite_yellow = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_recycler_favourite);
//        Drawable favourite_while = context.getResources().getDrawable(context.getResources().getIdentifier("ic_unfavourite", "drawable", context.getPackageName()));
//        Drawable favourite_yellow = context.getResources().getDrawable(context.getResources().getIdentifier("ic_recycler_favourite", "drawable", context.getPackageName()));
        if (!server.isFavourite()) {
            iv_favourite_icon.setImageResource(R.drawable.ic_unfavourite);
        } else {
            iv_favourite_icon.setImageResource(R.drawable.ic_recycler_favourite);
        }


        //Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("ic_os_debian", "mipmap", context.getPackageName()));
        String logoPath = server.getLogoPath() == null ? Constants.UNKNOWN_LINUX: server.getLogoPath();
        Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier(logoPath, "mipmap", context.getPackageName()));
        iv_logo.setImageDrawable(drawable);

        //iv_favourite_icon.setColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.SRC_IN);
        iv_favourite.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.light_magenta), PorterDuff.Mode.SRC_IN);
        iv_favourite_icon.setOnClickListener(v -> {
            if (server.isFavourite()) {
                DBOperations.removeFromFavourite(getApplicationContext(), ext_ip);
                server.setFavourite(!server.isFavourite());
                iv_favourite_icon.setImageResource(R.drawable.ic_unfavourite);
            } else {
                DBOperations.addToFavourite(getApplicationContext(), ext_ip);
                server.setFavourite(!server.isFavourite());
                iv_favourite_icon.setImageResource(R.drawable.ic_recycler_favourite);
            }
        });


        List<String> hdd_list = new ArrayList<>();
        String disks = DBOperations.getDisks(context, ext_ip);
        if (disks != null) {
            String[] hdds = disks.split(" ");
            for (String str: hdds) {
                String temp_disk = "";
                String[] parts = str.split(";");
                temp_disk = parts[0] +" " + parts[1] + "/" + parts[2];
                hdd_list.add(temp_disk);
            }
        }

        cl_block_hdd.setOnClickListener(view -> {
            PopupMenu menu = new PopupMenu(context, view);
            for (int i=0; i<hdd_list.size(); i++) {
                Log.d("details", "oc click " + hdd_list.get(i));
                menu.getMenu().add(0, 0, i, hdd_list.get(i));
            }

            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(ServerDetailsActivity.this, DisksUsagesActivity.class);
                    intent.putExtra("ext_ip", ext_ip);
                    startActivity(intent);
                    Log.d("details", "" + item.getOrder());
                    return false;
                }
            });
            menu.show();
        });


    }


}
