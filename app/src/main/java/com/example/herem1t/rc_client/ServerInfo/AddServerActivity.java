package com.example.herem1t.rc_client.ServerInfo;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.herem1t.rc_client.Charts.DisksUsagesActivity;
import com.example.herem1t.rc_client.Database.DBOperations;
import com.example.herem1t.rc_client.Net.IPV4Validator;
import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.Receivers.ServerInfoReceiver;

import io.reactivex.disposables.Disposable;

public class AddServerActivity extends AppCompatActivity {

    Button btn_next, btn_cancel;
    EditText et_ext_ip, et_pass;

    // check date sql

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test__add);

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(btn_onNext);

        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v -> finish());

        et_ext_ip = (EditText) findViewById(R.id.et_ext_ip);
        et_pass = (EditText) findViewById(R.id.et_description);


    }

    View.OnClickListener btn_onNext = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Validate if IP is not empty
            if(!et_ext_ip.getText().toString().equals("")) {
                String pass = et_pass.getText().toString().equals("")? " " : et_pass.getText().toString();
                String ext_ip = et_ext_ip.getText().toString();
                IPV4Validator ipv4Validator = new IPV4Validator();
                // Check IP address mask (regex)
                if (ipv4Validator.validate(ext_ip)) {
                    // Check if server already exists
                    if (DBOperations.isServerExists(AddServerActivity.this, ext_ip)){
                        Toast.makeText(AddServerActivity.this, "Server already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(AddServerActivity.this, AddDescriptionActivity.class);
                        intent.putExtra("ext_ip", ext_ip);
                        intent.putExtra("pass", pass);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(AddServerActivity.this, "Please enter correct IP address", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AddServerActivity.this, "Please enter server's IP", Toast.LENGTH_SHORT).show();
            }
        }
    };

}
