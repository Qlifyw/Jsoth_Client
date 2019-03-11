package com.example.herem1t.rc_client.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.herem1t.rc_client.data.os.model.Shell;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Created by Herem1t on 17.05.2018.
 */

public class NetworkUtils {

    public static SocketChannel getClientInstance(String host, int port) {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
        SocketChannel client = null;
        try {
            client = SocketChannel.open(inetSocketAddress);
        } catch (IOException e) {
        }
        return  client;
    }

    public static boolean checkPort(String host, int port){
        boolean isOpen = false;
        Socket socket = new Socket();
        try {
            SocketAddress sockaddr = new InetSocketAddress(host, port);
            int timeoutMs = 2000;   // 2 seconds
            socket.connect(sockaddr, timeoutMs);
            isOpen = true;

        } catch(IOException e) {
            // Handle exception
            //e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }

        return isOpen;
    }

    public static boolean pingServer(Shell shell, String host){
        if (shell == null || host == null) return false;
        try {
            Process process = shell.exec("/system/bin/ping -c 1 " + host);
            if (process == null) return false;
            int exitValue = process.waitFor();
            return exitValue == 0;
        }
        catch (InterruptedException e)
        {
            //e.printStackTrace();
            return false;
        }
    }

}
