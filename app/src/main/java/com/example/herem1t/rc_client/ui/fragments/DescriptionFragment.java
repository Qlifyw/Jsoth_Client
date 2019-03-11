package com.example.herem1t.rc_client.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.herem1t.rc_client.R;

public class DescriptionFragment extends android.app.Fragment {

    private EditText et_description;

    private onDescriptionListener onDescriptionListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_description, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        et_description = view.findViewById(R.id.et_description);
    }

    public void getDescription() {
        onDescriptionListener.receiveDescription(et_description.getText().toString());
    };

    public void setDescription(String description) {
        et_description.setText(description);
    }


    public void setOnDescriptionDataListener(DescriptionFragment.onDescriptionListener onDescriptionListener) {
        this.onDescriptionListener = onDescriptionListener;
    }

    public interface onDescriptionListener {
        void receiveDescription(String description);
    }
}
