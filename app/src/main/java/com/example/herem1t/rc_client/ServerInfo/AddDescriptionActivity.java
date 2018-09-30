package com.example.herem1t.rc_client.ServerInfo;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.herem1t.rc_client.Sockets.Actions;
import com.example.herem1t.rc_client.Recycler.DrawerActivity;
import com.example.herem1t.rc_client.Database.DBOperations;
import com.example.herem1t.rc_client.Net.GeoClient;
import com.example.herem1t.rc_client.Net.GeoServiceGenerator;
import com.example.herem1t.rc_client.Sockets.GreetNIO;
import com.example.herem1t.rc_client.Net.Network;
import com.example.herem1t.rc_client.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddDescriptionActivity extends AppCompatActivity {

    EditText et_description;

    Button btn_add;
    Button btn_cancel;

    String ext_ip;
    String pass;


    Intent intent;

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_description);

        et_description = (EditText) findViewById(R.id.et_description);

        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setOnClickListener(btn_add_server);
        btn_cancel = (Button) findViewById(R.id.btn_cancel_descr);
        btn_cancel.setOnClickListener( v -> finish());

        intent = getIntent();
        ext_ip = intent.getStringExtra("ext_ip");
        pass = intent.getStringExtra("pass");

    }


    View.OnClickListener btn_add_server = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Network.checkInternetConnection(AddDescriptionActivity.this)) {
                // test rx2 & retrofit2
                String description = et_description.getText().toString();
                disposable = GeoServiceGenerator.createService(GeoClient.class).serverCoordinates(ext_ip)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(__ -> {Log.d("rxdebug", Thread.currentThread().getName());})
                        .map((e) -> DBOperations.addServer(getApplicationContext(), ext_ip, pass, description, e.getLatitude(), e.getLongitude()))
                        .all(l -> l != -1)
                        .map((b) -> {
                            InetSocketAddress inetSocketAddress = new InetSocketAddress(ext_ip, GreetNIO.PORT);
                            SocketChannel client  = SocketChannel.open(inetSocketAddress);
                            boolean isInitSuccessful = false;
                            try {
                                isInitSuccessful = GreetNIO.init(getApplicationContext(), ext_ip);
                                if (isInitSuccessful) {
                                    Actions.sendHardwareInfo(client, getApplicationContext(), ext_ip);
                                }
                            } catch (IOException e) {
                                Log.d("rxdebug", e.toString());
                            }
                            return isInitSuccessful;
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(bool -> {
                                    Intent intent = new Intent(AddDescriptionActivity.this, DrawerActivity.class);
                                    if(bool) {
                                        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Can't connect", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                    }},
                                error -> {
                                    Toast.makeText(getApplicationContext(), "Error adding", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                });
        } else {
                Toast.makeText(getApplicationContext(), "Please connect to Internet", Toast.LENGTH_SHORT).show();
            }
    }};

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();

    }
}
