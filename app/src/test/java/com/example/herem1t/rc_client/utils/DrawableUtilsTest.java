package com.example.herem1t.rc_client.utils;

import com.example.herem1t.rc_client.Constants;

import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

public class DrawableUtilsTest {

    @Test
    public void testGetOsIconByName() {

        assertSame(DrawableUtils.getOSIconName("uBuNTu linux x64"), Constants.UBUNTU);
        assertSame(DrawableUtils.getOSIconName("WINDOWS SERVER 2008"), Constants.WINDOWS);
        assertSame(DrawableUtils.getOSIconName("macOS"), Constants.MACOS);
        assertSame(DrawableUtils.getOSIconName(""), Constants.UNKNOWN_LINUX);
        assertSame(DrawableUtils.getOSIconName(null), Constants.UNKNOWN_LINUX);
        assertSame(DrawableUtils.getOSIconName("linuX opensuse"), Constants.SUSE);
        assertSame(DrawableUtils.getOSIconName("Kali Linux"), Constants.KALI_LINUX);

    }

}
