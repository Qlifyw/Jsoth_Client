package com.example.herem1t.rc_client.ui.screens.adding;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.herem1t.rc_client.data.network.api.AppApiHelper;
import com.example.herem1t.rc_client.data.os.AppOsHelper;
import com.example.herem1t.rc_client.data.sqlite.AppDbHelper;
import com.example.herem1t.rc_client.data.sqlite.DbOpenHelper;
import com.example.herem1t.rc_client.data.AppDataManager;
import com.example.herem1t.rc_client.ui.fragments.DescriptionFragment;
import com.example.herem1t.rc_client.ui.fragments.LoginFragment;
import com.example.herem1t.rc_client.R;

public class AddServerActivity extends AppCompatActivity implements AddServerMvpView,
        LoginFragment.OnLoginDataListener, DescriptionFragment.onDescriptionListener{

    private Button btn_next;
    private Button btn_cancel;

    private LoginFragment loginFrag;
    private DescriptionFragment descrFrag;
    private FragmentTransaction fragmentTransaction;

    private String externalIp;
    private String password;
    private String description;

    ProgressDialog dialog;

    private AddServerMvpPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);

        AppDbHelper dbHelper = new AppDbHelper(new DbOpenHelper(this));
        AppApiHelper apiHelper = new AppApiHelper(this);
        AppDataManager dataManager = new AppDataManager(dbHelper ,
                null, apiHelper, new AppOsHelper(this));
        presenter = new AddServerPresenter(dataManager,this);

        loginFrag = new LoginFragment();
        loginFrag.setOnLoginDataListener(this);

        descrFrag = new DescriptionFragment();
        descrFrag.setOnDescriptionDataListener(this);

        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frag_login, loginFrag);
        fragmentTransaction.commit();

        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(btn_onNext);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v -> finish());



    }

    View.OnClickListener btn_onNext = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isOnDescriptionFragment()) {
                descrFrag.getDescription();
                presenter.onAddingServer(externalIp, password, description);
            } else {
                loginFrag.getLoginData();
                presenter.onLoginDataInput(externalIp, password);
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!isOnDescriptionFragment()) {
            btn_next.setText(R.string.adding_server_next);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void showErrorIncorrectIp(){
        Toast.makeText(AddServerActivity.this, R.string.add_server_invalid_ip, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorNoIp() {
        Toast.makeText(AddServerActivity.this, R.string.add_server_empty_ip, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorServerAlreadyExists() {
        Toast.makeText(AddServerActivity.this, R.string.add_server_server_already_exists, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setAddButtonEnabled(boolean enabled) {
        btn_next.setEnabled(enabled);
    }

    @Override
    public void showProgressDialog() {
        dialog = ProgressDialog.show(this, "",
                getString(R.string.progress_loading), true);
    }

    @Override
    public void hideProgressDialog() {
        dialog.dismiss();
    }

    @Override
    public void showDescriptionFragment() {
        //login_frag.getLoginData();
        btn_next.setText(R.string.adding_server_add);
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frag_login, descrFrag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void toMainActivity(int msgResId) {
        Toast.makeText(getApplicationContext(), getString(msgResId), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void receiveLoginData(String ip, String password) {
        this.externalIp = ip;
        this.password = password;
    }

    @Override
    public void receiveDescription(String description) {
        this.description = description;
    }

    private boolean isOnDescriptionFragment() {
        return descrFrag.isVisible();
    }

}
