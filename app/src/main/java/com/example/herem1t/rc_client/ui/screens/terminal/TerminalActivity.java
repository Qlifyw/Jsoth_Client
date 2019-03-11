package com.example.herem1t.rc_client.ui.screens.terminal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.data.AppDataManager;
import com.example.herem1t.rc_client.data.os.AppOsHelper;
import com.example.herem1t.rc_client.data.sqlite.AppDbHelper;
import com.example.herem1t.rc_client.data.sqlite.DbOpenHelper;

import java.util.Map;

import io.reactivex.disposables.Disposable;

import static com.example.herem1t.rc_client.Constants.INTENT_EXTRAS_EXTERNAL_IP;
import static com.example.herem1t.rc_client.Constants.INTENT_EXTRAS_TERMINAL_OUTPUT;

public class TerminalActivity extends AppCompatActivity implements TerminalMvpView {

    private final static String TERMINAL_PATH = "path";
    private final static String TERMINAL_OUTPUT = "output";

    TextView tv_results;
    EditText et_command;
    ImageView iv_send;

    Intent intent;
    String extIp;

    private String command;
    private Spanned htmlText;

    private TerminalMvpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else {
            Window w = getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(Color.TRANSPARENT);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        AppDbHelper dbHelper = new AppDbHelper(new DbOpenHelper(this));
        AppDataManager dataManager = new AppDataManager(dbHelper ,null, null, new AppOsHelper(this));
        presenter = new TerminalPresenter(dataManager,this);

        intent = getIntent();
        extIp = intent.getStringExtra(INTENT_EXTRAS_EXTERNAL_IP);

        tv_results = findViewById(R.id.tv_bash_results);
        if (savedInstanceState != null) {
            htmlText = fromHtml(savedInstanceState.getString(INTENT_EXTRAS_TERMINAL_OUTPUT));
            tv_results.setText(htmlText);
        }

        et_command = findViewById(R.id.et_command);
        iv_send = findViewById(R.id.iv_send);
        iv_send.setOnClickListener(onSend);


    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String savedOutput = htmlText == null ? "" : Html.toHtml(htmlText);
        outState.putString(INTENT_EXTRAS_TERMINAL_OUTPUT, savedOutput);
    }

    View.OnClickListener onSend = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            command = et_command.getText().toString();
            et_command.setText("");

            presenter.onCommandTyped(command, extIp);



        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void clearTerminal() {
        tv_results.setText("");
    }

    @Override
    public void showConnectionFailed() {
        Toast.makeText(getApplicationContext(), R.string.terminal_toast_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayResult(Map<String, String> map) {
        String path = map.get(TERMINAL_PATH);
        String[] paths = path.split("@");
        String username = "<font color='#EF2929'>" + paths[0] + "</font>";
        String separator = "<font color='#FFFFFF'>" + "@" + "</font>";
        String execDir = "<font color='#729FCF'>" + paths[1] + "$ " + "</font>";

        String output = map.get(TERMINAL_OUTPUT);
        tv_results.append(fromHtml(username + separator + execDir));
        tv_results.append("\t" + command + "\n");
        tv_results.append(output);

        output = "<br>" + output.replaceAll("<", "&#060").replaceAll(">", "&#062")
                .replaceAll("\\r\\n", "<br>").replaceAll("\\n", "<br>")
                .replaceAll("\\t", "    ");



        htmlText = fromHtml(username + separator + execDir + command + output);
    }


    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }

}
