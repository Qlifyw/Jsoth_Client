package com.example.herem1t.rc_client.utils;

import com.example.herem1t.rc_client.data.os.model.Shell;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class NetworkUtilsTest {

    String host = "host";

    @Mock
    Shell shell;

    @Mock
    Process process;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        shell = Mockito.mock(Shell.class);
        process = Mockito.mock(Process.class);
    }

    @Test
    public void testPingServerProcessNull() throws InterruptedException {

        when(shell.exec("/system/bin/ping -c 1 " + host)).thenReturn(null);
        when(process.waitFor()).thenReturn(0);

        assertFalse(NetworkUtils.pingServer(shell,host));
        assertFalse(NetworkUtils.pingServer(shell,null));
        assertFalse(NetworkUtils.pingServer(null,""));
        assertFalse(NetworkUtils.pingServer(null,null));
    }

    @Test
    public void testPingServerWaitForException() throws InterruptedException {

        when(shell.exec("/system/bin/ping -c 1 " + host)).thenReturn(process);
        when(process.waitFor()).thenThrow(new InterruptedException());

        assertFalse(NetworkUtils.pingServer(shell,host));
        assertFalse(NetworkUtils.pingServer(shell,null));
        assertFalse(NetworkUtils.pingServer(null,""));
        assertFalse(NetworkUtils.pingServer(null,null));
    }

    @Test
    public void testPingServerWaitForSuccess() throws InterruptedException {

        when(shell.exec("/system/bin/ping -c 1 " + host)).thenReturn(process);
        when(process.waitFor()).thenReturn(0);

        assertTrue(NetworkUtils.pingServer(shell,host));
        assertFalse(NetworkUtils.pingServer(shell,null));
        assertFalse(NetworkUtils.pingServer(null,""));
        assertFalse(NetworkUtils.pingServer(null,null));
    }

    @Test
    public void testPingServerWaitForFails() throws InterruptedException {

        when(shell.exec("/system/bin/ping -c 1 " + host)).thenReturn(process);
        when(process.waitFor()).thenReturn(1);

        assertFalse(NetworkUtils.pingServer(shell,host));
        assertFalse(NetworkUtils.pingServer(shell,null));
        assertFalse(NetworkUtils.pingServer(null,""));
        assertFalse(NetworkUtils.pingServer(null,null));
    }

}
