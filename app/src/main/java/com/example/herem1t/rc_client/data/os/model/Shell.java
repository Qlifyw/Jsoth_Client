package com.example.herem1t.rc_client.data.os.model;

import java.io.IOException;

public class Shell {

    public Process exec(String command) {
        try {
            return Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            return null;
        }
    }

}
