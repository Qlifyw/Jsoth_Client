package com.example.herem1t.rc_client.ServerInfo;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.herem1t.rc_client.Database.DBOperations;
import com.example.herem1t.rc_client.R;

public class ChangeDescriptionActivity extends AppCompatActivity {

    EditText et_change_description;
    TextView tv_ext_ip;

    Button btn_save;
    Button btn_cancel;

    Intent intent;
    String ext_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_description);

        intent = getIntent();
        ext_ip = intent.getStringExtra("ext_ip");

        et_change_description = (EditText) findViewById(R.id.et_cd_description);
        et_change_description.setText(DBOperations.getServerDescription(getApplicationContext(), ext_ip));

        tv_ext_ip = (TextView) findViewById(R.id.tv_cd_ip);
        tv_ext_ip.setText(ext_ip);

        btn_save = (Button) findViewById(R.id.btn_cd_save);
        btn_save.setOnClickListener(v -> {
            String description = et_change_description.getText().toString();
            DBOperations.updateServerDescription(getApplicationContext(), ext_ip, description);
            Toast.makeText(getApplicationContext(), R.string.update_server_updated, Toast.LENGTH_SHORT).show();
            finish();
        });

        btn_cancel = (Button) findViewById(R.id.btn_cd_cancel);
        btn_cancel.setOnClickListener(v -> finish());

    }
}
