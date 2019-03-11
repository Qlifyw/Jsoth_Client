package com.example.herem1t.rc_client.ui.screens.action;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.ui.screens.details.DetailsActivity;
import com.example.herem1t.rc_client.ui.screens.terminal.TerminalActivity;

import static com.example.herem1t.rc_client.Constants.INTENT_EXTRAS_EXTERNAL_IP;

public class ServerMenuActivity extends AppCompatActivity {

    ImageView iv_terminal;
    ImageView iv_details;

    TextView tv_terminal;
    TextView tv_details;

    Intent intent;
    String extIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Statusbar transparent. If  19 > API > 21 then set statusbar translucent. If API > 21 then set statusbar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_menu);

        intent = getIntent();
        extIp = intent.getStringExtra(INTENT_EXTRAS_EXTERNAL_IP);

        iv_terminal = findViewById(R.id.iv_terminal);
        iv_terminal.setOnClickListener(v -> {
            Intent intent = new Intent(ServerMenuActivity.this, TerminalActivity.class);
            intent.putExtra(INTENT_EXTRAS_EXTERNAL_IP, extIp);
            startActivity(intent);
        });

        iv_details = findViewById(R.id.iv_details);
        iv_details.setOnClickListener(v -> {
            Intent intent = new Intent(ServerMenuActivity.this, DetailsActivity.class);
            intent.putExtra(INTENT_EXTRAS_EXTERNAL_IP, extIp);
            startActivity(intent);
        });

        tv_terminal = findViewById(R.id.tv_terminal);
        tv_details = findViewById(R.id.tv_details);


    }


}
