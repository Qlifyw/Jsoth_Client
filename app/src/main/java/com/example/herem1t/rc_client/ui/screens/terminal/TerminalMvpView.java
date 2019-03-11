package com.example.herem1t.rc_client.ui.screens.terminal;

import java.util.Map;

public interface TerminalMvpView {

    void clearTerminal();
    void showConnectionFailed();
    void displayResult(Map<String, String> map);

}
