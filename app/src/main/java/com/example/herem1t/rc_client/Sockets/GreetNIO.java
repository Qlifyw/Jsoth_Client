package com.example.herem1t.rc_client.Sockets;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Created by Herem1t on 16.03.2018.
 */

public class GreetNIO {

    public final static int PORT = 4157;

    final static int GREETING = 100;
    final static int HANDSHAKE = 200;
    final static int TERMINAL = 300;
    final static int SCREENSHOT = 400;
    final static int STATISTICS = 500;
    final static int HWID = 600;

    public static boolean init(Context context, String ext_ip) throws IOException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ext_ip, PORT);
        SocketChannel client  = SocketChannel.open(inetSocketAddress);

        boolean isSuccessful = Actions.greeting(client, context, ext_ip);
        Log.d("BCR", "greeting " + isSuccessful);

        System.out.println("client. is conn? (after greeting)" + client.isConnected());

        if (!isSuccessful) {
            SocketChannel client1  = SocketChannel.open(inetSocketAddress);
            System.out.println(client1.isConnected());

            isSuccessful = Actions.handshake(client1, context, ext_ip);
        }
        client.close();
        return isSuccessful;
    }

}
