package com.example.herem1t.rc_client.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.herem1t.rc_client.R;

public class LoginFragment extends android.app.Fragment {

    private EditText et_ext_ip;
    private EditText et_password;

    public OnLoginDataListener onLoginDataListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        et_ext_ip = view.findViewById(R.id.et_ext_ip);
        et_password = view.findViewById(R.id.et_password);
    }

    public void setOnLoginDataListener(OnLoginDataListener onLoginDataListener) {
        this.onLoginDataListener = onLoginDataListener;
    }

    public void getLoginData(){
        String ip = et_ext_ip.getText().toString();
        String pass = et_password.getText().toString();
        onLoginDataListener.receiveLoginData(ip, pass);
    }

    public void getPassword() {
        String pass = et_password.getText().toString();
        onLoginDataListener.receiveLoginData(null, pass);
    }

    public void setIpFieldEnabled(boolean exp) {
        et_ext_ip.setEnabled(exp);
    }

    public interface OnLoginDataListener {
        void receiveLoginData(String ip, String password);
    };
}
