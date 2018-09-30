package com.example.herem1t.rc_client.ServerOptions;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.herem1t.rc_client.Sockets.Actions;
import com.example.herem1t.rc_client.Sockets.GreetNIO;
import com.example.herem1t.rc_client.R;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TerminalActivity extends AppCompatActivity {
    
    TextView tv_results;
    EditText et_command;
    ImageView iv_send;

    Intent intent;
    String ext_ip;

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_terminal);

        intent = getIntent();
        ext_ip = intent.getStringExtra("ext_ip");

        tv_results = (TextView) findViewById(R.id.tv_bash_results);
        if (savedInstanceState != null) {
            tv_results.setText(savedInstanceState.getString("output"));
        }

        et_command = (EditText) findViewById(R.id.et_command);
        iv_send = (ImageView) findViewById(R.id.iv_send);
        iv_send.setOnClickListener(onSend);


    }


    public Map<String, String> sendCommand(String shellCommand) {
        Log.d("terminal", "on create ...");
        //String command = "dir *.db";
        Map<String, String> result = new LinkedHashMap<>();
        boolean isInitSuccessful;
        try {
            isInitSuccessful = GreetNIO.init(getApplicationContext(), ext_ip);
            Log.d("terminal", "init " + isInitSuccessful);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(ext_ip, GreetNIO.PORT);
            SocketChannel client  = SocketChannel.open(inetSocketAddress);
            if (isInitSuccessful) {
                Log.d("terminal", "after init ...");
                result = Actions.sendTerminalCommand(client, getApplicationContext(), ext_ip, shellCommand);
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("output", tv_results.getText().toString());
    }

    View.OnClickListener onSend = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String command = et_command.getText().toString();
            et_command.setText("");
            if (command.equalsIgnoreCase("clear")) {
                tv_results.setText("");
                return;
            }

            disposable = Observable.just(command)
                    .subscribeOn(Schedulers.io())
                    .map(s -> sendCommand(command))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(map -> {
                        if(map.size() == 0) {
                            Toast.makeText(getApplicationContext(), R.string.terminal_toast_error, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //Log.d("terminal", s);
                        String path = map.get("path");
                        String[] paths = path.split("@");
                        String username = "<font color='#EF2929'>" + paths[0] + "</font>";
                        String separator = "<font color='#FFFFFF'>" + "@" + "</font>";
                        String execDir = "<font color='#729FCF'>" + paths[1] + " $" + "</font>";
                        String output = map.get("output");
                        Log.d("terminal", "123123 " +username + execDir + output);
                        tv_results.append(Html.fromHtml(username + separator + execDir));
                        tv_results.append("\t" + command + "\n");
                        tv_results.append(output);
                        //tv_results.setText(username + execDir + output);
                    });

        }
    };

    @Override
    protected void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        super.onDestroy();

    }

}
