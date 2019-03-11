package com.example.herem1t.rc_client.sockets;


import android.util.Base64;
import android.util.Log;

import com.example.herem1t.rc_client.data.DataManager;
import com.example.herem1t.rc_client.data.os.model.Shell;
import com.example.herem1t.rc_client.data.sqlite.model.Server;
import com.example.herem1t.rc_client.utils.DrawableUtils;
import com.example.herem1t.rc_client.utils.NetworkUtils;
import com.example.herem1t.rc_client.utils.crypto.AesClient;
import com.example.herem1t.rc_client.utils.crypto.RsaClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static com.example.herem1t.rc_client.Constants.SERVER_BLOCK_CPU;
import static com.example.herem1t.rc_client.Constants.SERVER_BLOCK_DISKS;
import static com.example.herem1t.rc_client.Constants.SERVER_BLOCK_MEMORY;
import static com.example.herem1t.rc_client.Constants.SERVER_BLOCK_NET;
import static com.example.herem1t.rc_client.Constants.SERVER_BLOCK_RAM;
import static com.example.herem1t.rc_client.Constants.SERVER_BLOCK_SWAP;
import static com.example.herem1t.rc_client.Constants.SERVER_BLOCK_CPU_DETAILS;
import static com.example.herem1t.rc_client.Constants.SERVER_COUNTRY_CODE;
import static com.example.herem1t.rc_client.Constants.SERVER_CPU_CORES;
import static com.example.herem1t.rc_client.Constants.SERVER_CPU_IDLE;
import static com.example.herem1t.rc_client.Constants.SERVER_CPU_LOADED_SYSTEM;
import static com.example.herem1t.rc_client.Constants.SERVER_CPU_LOADED_USER;
import static com.example.herem1t.rc_client.Constants.SERVER_CPU_MHZ;
import static com.example.herem1t.rc_client.Constants.SERVER_CPU_MODEL;
import static com.example.herem1t.rc_client.Constants.SERVER_CPU_USAGES;
import static com.example.herem1t.rc_client.Constants.SERVER_CPU_VENDOR;
import static com.example.herem1t.rc_client.Constants.SERVER_DISK_DEVNAME;
import static com.example.herem1t.rc_client.Constants.SERVER_DISK_FREE;
import static com.example.herem1t.rc_client.Constants.SERVER_DISK_TOTAL;
import static com.example.herem1t.rc_client.Constants.SERVER_DISK_USED;
import static com.example.herem1t.rc_client.Constants.SERVER_DISK_USED_PERCENT;
import static com.example.herem1t.rc_client.Constants.SERVER_EXTERNAL_IP;
import static com.example.herem1t.rc_client.Constants.SERVER_LOCAL_DISKS;
import static com.example.herem1t.rc_client.Constants.SERVER_LOCAL_IP;
import static com.example.herem1t.rc_client.Constants.SERVER_MACHINE_NAME;
import static com.example.herem1t.rc_client.Constants.SERVER_OS;
import static com.example.herem1t.rc_client.Constants.SERVER_RAM_FREE;
import static com.example.herem1t.rc_client.Constants.SERVER_RAM_TOTAL;
import static com.example.herem1t.rc_client.Constants.SERVER_RAM_USED;
import static com.example.herem1t.rc_client.Constants.SERVER_RAM_USED_PERCENT;
import static com.example.herem1t.rc_client.Constants.SERVER_SWAP_TOTAL;
import static com.example.herem1t.rc_client.Constants.SERVER_UPTIME;
import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_AVAILABLE;
import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_DOWN;
import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_NOT_RESPONDING;
import static com.example.herem1t.rc_client.utils.NetworkUtils.getClientInstance;


/**
 * Created by Herem1t on 19.03.2018.
 */

public class Actions {

    public final static int PORT = 4157;
    final static int GREETING = 100;
    final static int HANDSHAKE = 200;
    final static int TERMINAL = 300;
    final static int SCREENSHOT = 400;
    final static int STATISTICS = 500;
    final static int HWID = 600;

    public static final String JSON_CODE = "code";
    public static final String JSON_HWID = "hwid";
    public static final String JSON_IS_KNOWN = "isKnown";
    public static final String JSON_RSA_PUBLIC_KEY = "publicKey";
    public static final String JSON_MESSAGE = "msg";
    public static final String JSON_TERMINAL_COMMAND = "command";
    public static final String JSON_HARDWARE_INFO =  "hardwareInfo";
    public static final String JSON_AES_KEY =  "Aes";
    public static final String JSON_AES_KEY_HASH =  "AesHash";

    public static final String TERMINAL_USERNAME = "username";
    public static final String TERMINAL_CURRENT_PATH = "path";
    public static final String TERMINAL_OUTPUT = "output";

    private DataManager dataManager;

    public Actions(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public boolean init(SocketChannel gClient, String extIp) {

        boolean isSuccessful = greeting(gClient, extIp);

        if (!isSuccessful) {
            SocketChannel hClient  = getClientInstance(extIp, PORT);
            isSuccessful = handshake(hClient, extIp);
            try {
                hClient.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
        }
        return isSuccessful;
    }

    public boolean greeting(SocketChannel client, String extIp) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put(JSON_CODE, GREETING);
            data.put(JSON_HWID, dataManager.getOsHelper().getHWID());

            boolean hasKey = dataManager.getDbHelper().isHandshakeGotten(extIp);

            data.put(JSON_IS_KNOWN,hasKey ? 1 : 0);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String jsonData = gson.toJson(data);

            ByteBuffer buffer = ByteBuffer.allocate(jsonData.getBytes().length);
            buffer.put(jsonData.getBytes());
            buffer.flip();
            client.write(buffer);
            buffer.clear();

            int readedBytes = client.read(buffer);

            if (readedBytes == -1) {
                return false;
            } else {
                return true;
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return true;
    }

    public boolean handshake(SocketChannel client, String extIp) {
        boolean isSuccessful = true;
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put(JSON_CODE, HANDSHAKE);
            data.put(JSON_HWID, dataManager.getOsHelper().getHWID());;

            RsaClient rsaClient = new RsaClient(2048);

            String hashedPass = dataManager.getDbHelper().getHashedPass(extIp);
            byte[] encryptedRsaHash = rsaClient.encrypt(Base64.decode(hashedPass.getBytes(), Base64.DEFAULT));

            data.put(JSON_RSA_PUBLIC_KEY, Base64.encodeToString(rsaClient.getPublicKey().getEncoded(), Base64.NO_WRAP));
            data.put(JSON_MESSAGE, Base64.encodeToString(encryptedRsaHash, Base64.NO_WRAP));

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String jsonData = gson.toJson(data);

            ByteBuffer buffer = ByteBuffer.allocate(jsonData.getBytes().length);
            buffer.put(jsonData.getBytes());
            buffer.flip();
            client.write(buffer);

            ByteBuffer request = ByteBuffer.allocate(16);
            ByteArrayOutputStream baosRequest = new ByteArrayOutputStream();
            int pos = client.read(request);

            while ((pos == -1) || (pos == 0)) {
                pos = client.read(request);
            }

            while ((pos != -1) && (pos != 0)) {
                baosRequest.write(Arrays.copyOf(request.array(), pos));
                request.clear();
                pos = client.read(request);
            }

            String jsonResponse = new String(baosRequest.toByteArray());
            Map mapResponse = gson.fromJson(jsonResponse, Map.class);
            byte[] encAes = Base64.decode((String)mapResponse.get(JSON_AES_KEY), Base64.NO_WRAP);
            byte[] encAesHash = Base64.decode((String)mapResponse.get(JSON_AES_KEY_HASH), Base64.NO_WRAP);


            // Get checksum
            byte[] decAes = new byte[16];
            decAes = Arrays.copyOfRange(rsaClient.decrypt(encAes), rsaClient.decrypt(encAes).length-16, rsaClient.decrypt(encAes).length);

            MessageDigest md = MessageDigest.getInstance("SHA1");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(decAes);
            baos.write(Base64.decode(hashedPass.getBytes(), Base64.DEFAULT));

            byte[] hashDecAes = md.digest(baos.toByteArray());
            byte[] encHashAes = rsaClient.encrypt(hashDecAes);
            boolean isEquals = Arrays.equals(encAesHash, encHashAes);
            dataManager.getDbHelper().updateKey(decAes, extIp);

        } catch (Exception e) {
            dataManager.getDbHelper().updateKey(null, extIp);
            isSuccessful = false;
            //e.printStackTrace();
        }
        return isSuccessful;
    }

    public  Map<String, String> sendTerminalCommand(SocketChannel client, String extIp, String command) {
        Map<String, String> result = new LinkedHashMap<>();
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put(JSON_CODE, TERMINAL);
            data.put(JSON_HWID, dataManager.getOsHelper().getHWID());;


            SecretKeySpec sks = new SecretKeySpec(Base64.decode(dataManager.getDbHelper().getKey(extIp).getBytes(), Base64.DEFAULT), "AES");
            byte[] encCommand = AesClient.encrypt(command.getBytes(), sks);

            data.put(JSON_TERMINAL_COMMAND, Base64.encodeToString(encCommand, Base64.NO_WRAP));
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String jsonData = gson.toJson(data);

            ByteBuffer buffer = ByteBuffer.allocate(jsonData.getBytes().length);
            buffer.put(jsonData.getBytes());
            buffer.flip();
            client.write(buffer);



            ByteBuffer encBuffer = ByteBuffer.allocate(16);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int pos1 = client.read(encBuffer);

            while ((pos1 == -1) || (pos1 == 0)) {
                pos1 = client.read(encBuffer);
            }

            while ((pos1 != -1) && (pos1 != 0)) {
                encBuffer.rewind();
                baos.write(encBuffer.array());
                encBuffer.clear();
                pos1 = client.read(encBuffer);
            }

            byte[] decResult = AesClient.decrypt(baos.toByteArray(), sks);

            String s = new String(decResult);
            Map mapResponse = gson.fromJson(s, Map.class);
            String username = (String)mapResponse.get(TERMINAL_USERNAME);
            String path = (String)mapResponse.get(TERMINAL_CURRENT_PATH);
            String output = (String)mapResponse.get(TERMINAL_OUTPUT);

            result.put(TERMINAL_CURRENT_PATH, path);
            result.put(TERMINAL_USERNAME, username);
            result.put(TERMINAL_OUTPUT, output);
            return result;
        } catch (BadPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                IllegalBlockSizeException | NoSuchPaddingException | IOException e ) {
            //e.printStackTrace();
            return result;
        }

    }

    public boolean sendHardwareInfo(SocketChannel client, String extIp) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put(JSON_CODE, HWID);
            data.put(JSON_HWID, dataManager.getOsHelper().getHWID());

            Map info = dataManager.getOsHelper().getDeviceInfo();
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(info);

            SecretKeySpec sks = new SecretKeySpec(Base64.decode(dataManager.getDbHelper().getKey(extIp).getBytes(), Base64.DEFAULT), "AES");
            byte[] encJSON = AesClient.encrypt(json.getBytes(), sks);

            data.put(JSON_HARDWARE_INFO, Base64.encodeToString(encJSON, Base64.NO_WRAP));
            String jsonData = gson.toJson(data);

            ByteBuffer infoBuffer = ByteBuffer.allocate(jsonData.getBytes().length);
            infoBuffer.put(jsonData.getBytes());
            infoBuffer.flip();
            client.write(infoBuffer);
            return true;
        } catch (BadPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                IllegalBlockSizeException | NoSuchPaddingException | IOException e ) {
            //e.printStackTrace();
            return false;
        }

    }

    public void receiveStat(SocketChannel client, String ipAddress) {
        try {
            if (client == null ) throw new IOException();

            Map<String, Object> data = new LinkedHashMap<>();
            data.put(JSON_CODE, STATISTICS);
            data.put(JSON_HWID, dataManager.getOsHelper().getHWID());

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String jsonData = gson.toJson(data);

            ByteBuffer buffer = ByteBuffer.allocate(jsonData.getBytes().length);
            buffer.put(jsonData.getBytes());
            buffer.flip();
            client.write(buffer);

            dataManager.getDbHelper().updateServerStatus(ipAddress, SERVER_AVAILABLE);

            try {
                byte[] aesKey = dataManager.getDbHelper().getKey(ipAddress).getBytes();
                SecretKeySpec sks = new SecretKeySpec(Base64.decode(aesKey, Base64.DEFAULT), "AES");

                ByteBuffer infoBuffer = ByteBuffer.allocate(16);
                int pos1 = client.read(infoBuffer);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                while ((pos1 == -1) || (pos1 == 0)) {
                    pos1 = client.read(infoBuffer);
                }

                while ((pos1 != -1) && (pos1 != 0)) {
                    infoBuffer.rewind();
                    baos.write(infoBuffer.array());
                    infoBuffer.clear();
                    pos1 = client.read(infoBuffer);
                }

                byte[] decInfo = AesClient.decrypt(baos.toByteArray(), sks);
                Map decMapInfo = gson.fromJson(new String(decInfo), Map.class);

                String machineName = (String)decMapInfo.get(SERVER_MACHINE_NAME);
                String osName = (String)decMapInfo.get(SERVER_OS);
                String uptime = (String)decMapInfo.get(SERVER_UPTIME);

                Map net = (Map)decMapInfo.get(SERVER_BLOCK_NET);
                String externalIp = (String)net.get(SERVER_EXTERNAL_IP);
                String localIp = (String)net.get(SERVER_LOCAL_IP);
                String countryCode = (String)net.get(SERVER_COUNTRY_CODE);

                Map cpu = (Map)decMapInfo.get(SERVER_BLOCK_CPU);
                Map cpuDetails = (Map)cpu.get(SERVER_BLOCK_CPU_DETAILS);
                int cpuCores = ((Double)cpuDetails.get(SERVER_CPU_CORES)).intValue();
                String cpuModel = (String)cpuDetails.get(SERVER_CPU_MODEL);
                String cpuVendor = (String)cpuDetails.get(SERVER_CPU_VENDOR);
                int cpuMhz = ((Double)cpuDetails.get(SERVER_CPU_MHZ)).intValue();
                Map cpuUsage = (Map)cpu.get(SERVER_CPU_USAGES);
                double cpuUser = ((Double)cpuUsage.get(SERVER_CPU_LOADED_USER));
                double cpuSystem = (Double)cpuUsage.get(SERVER_CPU_LOADED_SYSTEM);
                double cpuIdle = (Double)cpuUsage.get(SERVER_CPU_IDLE);

                Map memory = (Map)decMapInfo.get(SERVER_BLOCK_MEMORY);
                Map ram = (Map)memory.get(SERVER_BLOCK_RAM);
                double ramTotal = (Double)ram.get(SERVER_RAM_TOTAL);
                double ramUsed = (Double)ram.get(SERVER_RAM_USED);
                double ramFree = (Double)ram.get(SERVER_RAM_FREE);
                double ramUsedPerc = (Double)ram.get(SERVER_RAM_USED_PERCENT);

                Map swap = (Map) memory.get(SERVER_BLOCK_SWAP);
                double swapTotal = (Double) swap.get(SERVER_SWAP_TOTAL);

                Map disks = (Map) decMapInfo.get(SERVER_BLOCK_DISKS);
                ArrayList<Map> localDisks = (ArrayList<Map>) disks.get(SERVER_LOCAL_DISKS);
                Iterator<Map> iterator = localDisks.iterator();
                StringBuilder disksFull = new StringBuilder();
                while (iterator.hasNext()) {
                    Map tempDisk = iterator.next();
                    String temp = tempDisk.get(SERVER_DISK_DEVNAME) + ";" + tempDisk.get(SERVER_DISK_USED) + ";"
                            + tempDisk.get(SERVER_DISK_TOTAL) + " ";
                    disksFull.append(temp);

                }
                String disksFullStr = disksFull.deleteCharAt(disksFull.length()-1).toString();

                Server server = new Server();
                server.setExternalIP(externalIp);
                server.setLocalIP(localIp);
                server.setMachineName(machineName);
                server.setLogoPath(DrawableUtils.getOSIconName(osName));
                server.setOSname(osName);
                server.setCountyCode(countryCode);
                server.setUptime(uptime);
                server.setSWAP(swapTotal);
                server.setRam(String.valueOf(ramTotal));
                server.setCPUInfo(cpuVendor + ";" + cpuModel + ";" + cpuCores + ";" + cpuMhz);
                server.setDisks(disksFullStr);

                server.setRamUsages(String.valueOf(ramUsedPerc));
                server.setCpuUsages(cpuSystem+ "/" + cpuUser);
                server.setDisksUsages(disksFullStr);

                dataManager.getDbHelper().updateServerInfo(server);

            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                    BadPaddingException | IllegalBlockSizeException e) {
                //e.printStackTrace();
            }
        } catch (IOException e) {
            //e.printStackTrace();
            int serverStatus = NetworkUtils.pingServer(new Shell(), ipAddress)? SERVER_NOT_RESPONDING : SERVER_DOWN;
            dataManager.getDbHelper().updateServerStatus(ipAddress, serverStatus);
        }
    }


}
