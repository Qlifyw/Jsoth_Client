package com.example.herem1t.rc_client.ui.screens.details;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.herem1t.rc_client.Constants;
import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.data.AppDataManager;
import com.example.herem1t.rc_client.data.sqlite.AppDbHelper;
import com.example.herem1t.rc_client.data.sqlite.DbOpenHelper;
import com.example.herem1t.rc_client.data.sqlite.model.Server;
import com.example.herem1t.rc_client.ui.screens.cpu.CpuUsagesActivity;
import com.example.herem1t.rc_client.ui.screens.hdd.DisksUsagesActivity;
import com.example.herem1t.rc_client.ui.screens.ram.RamUsagesActivity;
import com.example.herem1t.rc_client.ui.screens.settings.SettingsActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.herem1t.rc_client.Constants.INTENT_EXTRAS_EXTERNAL_IP;

public class DetailsActivity extends AppCompatActivity implements DetailsMvpView ,AppBarLayout.OnOffsetChangedListener {

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private static final String EXTRAS_ITEM_ORDER = "item";

    private boolean isTheTitleVisible = false;
    private boolean ssTheTitleContainerVisible = true;

    private LinearLayout linear_TitleContainer;
    private TextView tv_title;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;

    private String extIp;
    private Menu menu;

    private List<String> disks;

    CircleImageView iv_logo;

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

    LinearLayout linear_block_hdd;
    LinearLayout linear_block_cpu;
    LinearLayout linear_block_ram;

    private DetailsMvpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        bindActivity();

        AppDbHelper dbHelper = new AppDbHelper(new DbOpenHelper(this));
        AppDataManager dataManager = new AppDataManager(dbHelper ,
                null, null, null);
        presenter = new DetailsPresenter(dataManager,this);

        extIp = getIntent().getStringExtra(INTENT_EXTRAS_EXTERNAL_IP);

        appBarLayout.addOnOffsetChangedListener(this);

        toolbar.inflateMenu(R.menu.menu_details);
        menu = toolbar.getMenu();
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        Intent intent = new Intent(DetailsActivity.this, SettingsActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.menu_favourite:
                        presenter.onFavoriteClicked(extIp, item.getOrder());
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        startAlphaAnimation(tv_title, 0, View.INVISIBLE);

        presenter.getServer(extIp);
    }

    private void bindActivity() {
        toolbar = findViewById(R.id.main_toolbar);
        tv_title = findViewById(R.id.main_textview_title);
        linear_TitleContainer = findViewById(R.id.main_linearlayout_title);
        appBarLayout = findViewById(R.id.main_appbar);

        tv_header_ext_ip = findViewById(R.id.tv_title);
        iv_logo = findViewById(R.id.iv_logo_circle);
        tv_description = findViewById(R.id.tv_description);
        tv_hostname = findViewById(R.id.tv_machine_name);
        tv_os_name = findViewById(R.id.tv_os_name);
        tv_uptime = findViewById(R.id.tv_uptime);
        tv_ext_ip = findViewById(R.id.tv_ext_ip);
        tv_local_ip = findViewById(R.id.tv_local_ip);
        tv_country = findViewById(R.id.tv_country);
        tv_ram = findViewById(R.id.tv_ram);
        tv_swap = findViewById(R.id.tv_swap);
        tv_hdd = findViewById(R.id.tv_hdd);

        linear_block_cpu = findViewById(R.id.block_cpu);
        linear_block_cpu.setOnClickListener(v -> {
            Intent intent = new Intent(this, CpuUsagesActivity.class);
            intent.putExtra(INTENT_EXTRAS_EXTERNAL_IP, extIp);
            startActivity(intent);

        });
        linear_block_ram = findViewById(R.id.block_memory);
        linear_block_ram.setOnClickListener(v -> {
            Intent intent = new Intent(this, RamUsagesActivity.class);
            intent.putExtra(INTENT_EXTRAS_EXTERNAL_IP, extIp);
            startActivity(intent);

        });

        linear_block_hdd = findViewById(R.id.block_hdd);
        linear_block_hdd.setOnClickListener(view -> {
            presenter.getHddList(extIp);
            PopupMenu menu = new PopupMenu(this, view);
            for (int i = 0; i< disks.size(); i++) {
                menu.getMenu().add(0, 0, i, disks.get(i));
            }

            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(DetailsActivity.this, DisksUsagesActivity.class);
                    intent.putExtra(INTENT_EXTRAS_EXTERNAL_IP, extIp);
                    intent.putExtra(EXTRAS_ITEM_ORDER, item.getOrder());
                    startActivity(intent);
                    return false;
                }
            });
            menu.show();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!isTheTitleVisible) {
                startAlphaAnimation(tv_title, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                isTheTitleVisible = true;
            }

        } else {

            if (isTheTitleVisible) {
                startAlphaAnimation(tv_title, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                isTheTitleVisible = false;
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(ssTheTitleContainerVisible) {
                startAlphaAnimation(linear_TitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                ssTheTitleContainerVisible = false;
            }

        } else {

            if (!ssTheTitleContainerVisible) {
                startAlphaAnimation(linear_TitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                ssTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    @Override
    public void onFavoriteChecked(int itemOrder, boolean isFavorite) {
        if (isFavorite) {
            menu.getItem(itemOrder).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_red_400_24dp));
        } else {
            menu.getItem(itemOrder).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_24dp));
        }
    }

    @Override
    public void showServersInfo(Server server) {
        tv_title.setText(server.getOSname());
        tv_description.setText(server.getDescription());
        tv_header_ext_ip.setText(server.getExternalIP());
        tv_hostname.setText(server.getMachineName());
        tv_os_name.setText(server.getOSname());
        tv_uptime.setText(server.getUptime());
        tv_ext_ip.setText(server.getExternalIP());
        tv_local_ip.setText(server.getLocalIP());
        tv_country.setText(server.getCountyCode());
        tv_ram.setText((server.getRam() == null? 0.0 : server.getRam()) + " Gb");
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

        MenuItem menuitem = menu.findItem(R.id.menu_favourite);
        if (server.isFavourite()) {
            menuitem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_red_400_24dp));
        } else {
            menuitem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_24dp));
        }

        String logoPath = server.getLogoPath() == null ? Constants.UNKNOWN_LINUX: server.getLogoPath();
        Drawable drawable = this.getResources().getDrawable(this.getResources().getIdentifier(logoPath, "mipmap", this.getPackageName()));
        iv_logo.setImageDrawable(drawable);

    }

    @Override
    public void onHddListLoaded(List<String> hddList) {
        this.disks = hddList;
    }
}
