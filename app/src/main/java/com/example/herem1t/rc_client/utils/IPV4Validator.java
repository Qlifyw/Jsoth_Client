package com.example.herem1t.rc_client.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Herem1t on 27.04.2018.
 */

public class IPV4Validator {
    private Pattern pattern;
    private Matcher matcher;

    private static final String IPADDRESS_PATTERN =
        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public IPV4Validator() {
        pattern = Pattern.compile(IPADDRESS_PATTERN);
    }

    public boolean validate(final String ip) {
        if (ip == null) return false;
        matcher = pattern.matcher(ip);
        return  matcher.matches();
    }
}
