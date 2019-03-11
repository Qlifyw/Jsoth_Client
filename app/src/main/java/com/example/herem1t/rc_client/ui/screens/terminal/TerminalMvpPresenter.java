package com.example.herem1t.rc_client.ui.screens.terminal;

public interface TerminalMvpPresenter {

    void onCommandTyped(String command, String extIp);
    void onStop();

}
