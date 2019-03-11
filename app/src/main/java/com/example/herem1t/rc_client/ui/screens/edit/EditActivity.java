package com.example.herem1t.rc_client.ui.screens.edit;

import android.app.FragmentTransaction;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.data.AppDataManager;
import com.example.herem1t.rc_client.data.sqlite.AppDbHelper;
import com.example.herem1t.rc_client.data.sqlite.DbOpenHelper;
import com.example.herem1t.rc_client.ui.fragments.DescriptionFragment;
import com.example.herem1t.rc_client.ui.fragments.LoginFragment;

import static com.example.herem1t.rc_client.Constants.EDIT_INTENT_OPTION;
import static com.example.herem1t.rc_client.Constants.INTENT_EXTRAS_EXTERNAL_IP;

public class EditActivity extends AppCompatActivity implements EditMvpView,
        DescriptionFragment.onDescriptionListener, LoginFragment.OnLoginDataListener {

    private Button btn_save;
    private Button btn_cancel;

    private LoginFragment loginFrag;
    private DescriptionFragment descrFrag;
    private FragmentTransaction fragmentTransaction;

    private String extIp;
    private int option;

    private EditMvpPresenter presenter;

    TextView tv_extIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_description);


        AppDbHelper dbHelper = new AppDbHelper(new DbOpenHelper(this));
        AppDataManager dataManager = new AppDataManager(dbHelper ,null, null, null);
        presenter = new EditPresenter(dataManager,this);

        extIp = getIntent().getStringExtra(INTENT_EXTRAS_EXTERNAL_IP);
        option = getIntent().getIntExtra(EDIT_INTENT_OPTION, 0);

        presenter.defineOptionsType(option, extIp);

        tv_extIp = findViewById(R.id.tv_cd_ip);
        tv_extIp.setText(extIp);

        btn_save = findViewById(R.id.btn_cd_save);
        btn_save.setOnClickListener(v -> {
            presenter.onSave(option);
        });

        btn_cancel = findViewById(R.id.btn_cd_cancel);
        btn_cancel.setOnClickListener(v -> finish());

    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.setData(option);
    }

    @Override
    public void showDescriptionFragment() {
        descrFrag = new DescriptionFragment();
        descrFrag.setOnDescriptionDataListener(this);
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frag_option, descrFrag);
        fragmentTransaction.commit();
    }

    @Override
    public void showLoginFragment() {
        loginFrag = new LoginFragment();
        loginFrag.setOnLoginDataListener(this);
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frag_option, loginFrag);
        fragmentTransaction.commit();
    }

    @Override
    public void setDescription(String oldDescription) {
        descrFrag.setDescription(oldDescription);
    }

    @Override
    public void setLoginField() {
        loginFrag.setIpFieldEnabled(false);
    }

    @Override
    public void showMessage(int resId) {
        Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void receiveDescription(String description) {
        presenter.changeDescription(description);
    }

    @Override
    public void receiveLoginData(String ip, String password) {
        presenter.changePassword(password);
    }

    @Override
    public void saveChangedPass() {
        loginFrag.getPassword();
    }

    @Override
    public void saveChangedDescription() {
        descrFrag.getDescription();
    }
}
