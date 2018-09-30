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

public class ChangePasswordActivity extends AppCompatActivity {

    Button btn_save;
    Button btn_cancel;

    EditText et_pass;
    TextView tv_et_ip;

    Intent intent;
    String ext_ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        intent = getIntent();
        ext_ip = intent.getStringExtra("ext_ip");

        et_pass = (EditText) findViewById(R.id.et_cp_pass);
        tv_et_ip = (TextView) findViewById(R.id.tv_cp_ip);
        tv_et_ip.setText(ext_ip);

        btn_cancel = (Button) findViewById(R.id.btn_cp_cancel);
        btn_cancel.setOnClickListener(v -> finish());

        btn_save = (Button) findViewById(R.id.btn_cp_save);
        btn_save.setOnClickListener(v -> {
            String pass = et_pass.getText().toString().equals("") ? " ": et_pass.getText().toString();
            DBOperations.changeServerConnectionPassword(getApplicationContext(), ext_ip, pass);
            Toast.makeText(getApplicationContext(), R.string.update_server_updated, Toast.LENGTH_SHORT).show();
            finish();
        });

    }
}
