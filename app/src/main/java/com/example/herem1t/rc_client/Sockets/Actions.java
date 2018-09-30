package com.example.herem1t.rc_client.Sockets;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.Settings;

import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;

import com.example.herem1t.rc_client.Database.DBOperations;
import com.example.herem1t.rc_client.Database.Server;
import com.example.herem1t.rc_client.R;
import com.example.herem1t.rc_client.Recycler.DrawableAction;
import com.example.herem1t.rc_client.Net.Network;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import static com.example.herem1t.rc_client.Constants.SERVER_DOWN;
import static com.example.herem1t.rc_client.Constants.SERVER_AVAILABLE;
import static com.example.herem1t.rc_client.Constants.SERVER_NOT_RESPONDING;


/**
 * Created by Herem1t on 19.03.2018.
 */

public class Actions {
    public static boolean greeting(SocketChannel client, Context context, String ext_ip) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put(context.getResources().getString(R.string.JsonCode), GreetNIO.GREETING);
            data.put(context.getResources().getString(R.string.JsonHwid), getHWID(context));

            DBOperations.DBHelper dbHelper = new DBOperations.DBHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.query(DBOperations.TABLE_SERVER, new String[] {"enc_key"},"external_ip = ? AND enc_key IS NOT NULL ", new String[] {ext_ip}, null, null ,null );
            boolean hasRows = cursor.moveToFirst();
            cursor.close();
            db.close();

            data.put(context.getResources().getString(R.string.JsonIsKnown),hasRows ? 1 : 0);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String json_data = gson.toJson(data);
            Log.d("greeting", json_data);

            ByteBuffer buffer = ByteBuffer.allocate(json_data.getBytes().length);
            buffer.put(json_data.getBytes());
            buffer.flip();
            client.write(buffer);
            buffer.clear();

            int readed_bytes = client.read(buffer);
            // TODO return (readed_bytes == -1)

            if (readed_bytes == -1) {
                System.out.println("Server return -1 (need handshake)");
                return false;
            } else {
                System.out.println("Sever returned something. Now we can send data");
                return  true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean handshake(SocketChannel client, Context context, String ext_ip) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean isSuccessful = true;
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put(context.getResources().getString(R.string.JsonCode), GreetNIO.HANDSHAKE);
            data.put(context.getResources().getString(R.string.JsonHwid), getHWID(context));;

            Encryption.Generate_RSA grsa = new Encryption.Generate_RSA(2048);
            PrivateKey privateKey = grsa.getPrivateKey();
            PublicKey publicKey = grsa.getPublicKey();

            // TODO
            // get password
            DBOperations.DBHelper dbHelper = new DBOperations.DBHelper(context);
            db = dbHelper.getWritableDatabase();
            cursor = db.query(DBOperations.TABLE_SERVER, new String[] {"hash_pass"}, "external_ip = ? ", new  String[] {ext_ip}, null, null, null);

            String hash_pass = "";
            if(cursor.moveToFirst()) {
                int hash_pass_ColumnID = cursor.getColumnIndex("hash_pass");
                hash_pass = cursor.getString(hash_pass_ColumnID);
            }

            byte[] encrypted_rsa_hash = Encryption.RSA_enc(Base64.decode(hash_pass.getBytes(), Base64.DEFAULT), publicKey);

            data.put(context.getResources().getString(R.string.JsonPublicKey), Base64.encodeToString(publicKey.getEncoded(), Base64.NO_WRAP));
            data.put(context.getResources().getString(R.string.JsonEncMsg), Base64.encodeToString(encrypted_rsa_hash, Base64.NO_WRAP));

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String json_data = gson.toJson(data);

            ByteBuffer buffer = ByteBuffer.allocate(json_data.getBytes().length);
            buffer.put(json_data.getBytes());
            buffer.flip();
            client.write(buffer);

            ByteBuffer request = ByteBuffer.allocate(16);
            ByteArrayOutputStream baos_request = new ByteArrayOutputStream();
            int pos = client.read(request);

            while ((pos == -1) || (pos == 0)) {
                pos = client.read(request);
            }

            while ((pos != -1) && (pos != 0)) {
                //request.rewind();
                baos_request.write(Arrays.copyOf(request.array(), pos));
                request.clear();
                pos = client.read(request);
            }

            String json_response = new String(baos_request.toByteArray());
            Map mapResponse = gson.fromJson(json_response, Map.class);
            byte[] encAES = Base64.decode((String)mapResponse.get("EncAES"), Base64.NO_WRAP);
            byte[] encAEShash = Base64.decode((String)mapResponse.get("EncAES_hash"), Base64.NO_WRAP);
            Log.d("handshake", (String)mapResponse.get("EncAES"));
            Log.d("handshake", (String)mapResponse.get("EncAES_hash"));


            // Get checksum
            System.out.println("----- Check hash -----");
            byte[] dec_aes = new byte[16];
            byte[] dec_hash_aes = new byte[20];
            MessageDigest md = MessageDigest.getInstance("SHA1");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(dec_aes);
            baos.write(Base64.decode(hash_pass.getBytes(), Base64.DEFAULT));

            byte[] hash_dec_aes = md.digest(baos.toByteArray());
            byte[] enc_hash_aes = Encryption.RSA_enc(hash_dec_aes, publicKey);
            boolean isEquals = Arrays.equals(encAEShash, enc_hash_aes);

            ContentValues cv = new ContentValues();
            if (!isEquals) {
                cv.putNull("enc_key");
                int rowsID = db.update(DBOperations.TABLE_SERVER, cv, "external_ip = ? ", new String[] {ext_ip});
                return false;
            } else {
                cv.put("enc_key", Base64.encodeToString(dec_aes, android.util.Base64.DEFAULT));
                int rowsID = db.update(DBOperations.TABLE_SERVER, cv , "external_ip = ? ", new String[] {ext_ip});
            }


        } catch (Exception e) {
            // TODO
            // Delete AES from DB
            isSuccessful = false;
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                db.close();
            }
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isSuccessful;
    }

    public static Map<String, String> sendTerminalCommand(SocketChannel client, Context context, String ext_ip, String command) {
        Map<String, String> result = new LinkedHashMap<>();
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put(context.getResources().getString(R.string.JsonCode), GreetNIO.TERMINAL);
            data.put(context.getResources().getString(R.string.JsonHwid), getHWID(context));;

            SecretKeySpec sks = new SecretKeySpec(Base64.decode(DBOperations.getKey(context, ext_ip).getBytes(), Base64.DEFAULT), "AES");
            Encryption.Generate_AES gaes = new Encryption.Generate_AES();
            Cipher cipher = gaes.getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            byte[] enc_command = cipher.doFinal(command.getBytes());

            data.put(context.getResources().getString(R.string.JsonTerminalCommand), Base64.encodeToString(enc_command, Base64.NO_WRAP));
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String json_data = gson.toJson(data);

            ByteBuffer buffer = ByteBuffer.allocate(json_data.getBytes().length);
            buffer.put(json_data.getBytes());
            buffer.flip();
            client.write(buffer);



            ByteBuffer enc_buffer = ByteBuffer.allocate(16);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int pos1 = client.read(enc_buffer);

            while ((pos1 == -1) || (pos1 == 0)) {
                pos1 = client.read(enc_buffer);
            }

            while ((pos1 != -1) && (pos1 != 0)) {
                enc_buffer.rewind();
                baos.write(enc_buffer.array());
                enc_buffer.clear();
                pos1 = client.read(enc_buffer);
            }

            Cipher dec_cipher = gaes.getCipher();
            dec_cipher.init(Cipher.DECRYPT_MODE, sks);
            Log.d("terminal", baos.toByteArray().length + "");

            byte[] dec_result = dec_cipher.doFinal(baos.toByteArray());

            String s = new String(dec_result);
            Map map_response = gson.fromJson(s, Map.class);
            String username = (String)map_response.get("username");
            String path = (String)map_response.get("path");
            String output = (String)map_response.get("output");

            Log.d("terminal", new String(dec_result));

            result.put("path", path);
            result.put("username", username);
            result.put("output", output);
            return result;
        } catch (BadPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                IllegalBlockSizeException | NoSuchPaddingException | IOException e ) {
            e.printStackTrace();
            return result;
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static boolean sendHardwareInfo(SocketChannel client, Context context, String ext_ip) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put(context.getResources().getString(R.string.JsonCode), GreetNIO.HWID);
            data.put(context.getResources().getString(R.string.JsonHwid), getHWID(context));

            SecretKeySpec sks = new SecretKeySpec(Base64.decode(DBOperations.getKey(context, ext_ip ).getBytes(), Base64.DEFAULT), "AES");
            Encryption.Generate_AES gaes = new Encryption.Generate_AES();
            Cipher cipher = gaes.getCipher();
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            Map info = Actions.getDeviceInfo(context);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String json = gson.toJson(info);
            byte[] encJSON = cipher.doFinal(json.getBytes());

            data.put(context.getResources().getString(R.string.JsonHardwareInfo), Base64.encodeToString(encJSON, Base64.NO_WRAP));
            String json_data = gson.toJson(data);

            ByteBuffer info_buffer = ByteBuffer.allocate(json_data.getBytes().length);
            info_buffer.put(json_data.getBytes());
            info_buffer.flip();
            client.write(info_buffer);
            return true;
        } catch (BadPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                IllegalBlockSizeException | NoSuchPaddingException | IOException e ) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void receiveStat(SocketChannel client, String ip_address, Context context) {
        try {
            if (client == null ) throw new IOException();

            Map<String, Object> data = new LinkedHashMap<>();
            data.put(context.getResources().getString(R.string.JsonCode), GreetNIO.STATISTICS);
            data.put(context.getResources().getString(R.string.JsonHwid), getHWID(context));

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String json_data = gson.toJson(data);

            ByteBuffer buffer = ByteBuffer.allocate(json_data.getBytes().length);
            buffer.put(json_data.getBytes());
            buffer.flip();
            client.write(buffer);

            DBOperations.updateServerStatus(context, ip_address, SERVER_AVAILABLE);

            try {
                byte[] AES_key = DBOperations.getKey(context, ip_address).getBytes();
                SecretKeySpec sks = new SecretKeySpec(Base64.decode(AES_key, Base64.DEFAULT), "AES");
                Encryption.Generate_AES gaes = new Encryption.Generate_AES();
                Cipher cipher = gaes.getCipher();
                cipher.init(Cipher.DECRYPT_MODE, sks);

                ByteBuffer info_buffer = ByteBuffer.allocate(16);
                int pos1 = client.read(info_buffer);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                while ((pos1 == -1) || (pos1 == 0)) {
                    pos1 = client.read(info_buffer);
                }

                while ((pos1 != -1) && (pos1 != 0)) {
                    info_buffer.rewind();
                    baos.write(info_buffer.array());
                    info_buffer.clear();
                    pos1 = client.read(info_buffer);
                }

                byte[] dec_info = cipher.doFinal(baos.toByteArray());
                Map dec_map_info = gson.fromJson(new String(dec_info), Map.class);
                System.out.println("SysInfo: " + new String(dec_info));

                String machine_name = (String)dec_map_info.get("MachineName");
                String os_name = (String)dec_map_info.get("OSName");
                String uptime = (String)dec_map_info.get("UpTime");
                System.out.println(machine_name + " " + os_name + " " + uptime);

                Map net = (Map)dec_map_info.get("Net");
                String external_ip = (String)net.get("ExtIP");
                String local_ip = (String)net.get("LocalIP");
                String country_code = (String)net.get("CountryCode");
                System.out.println(external_ip + " " + local_ip + " " + country_code);

                Map cpu = (Map)dec_map_info.get("CPU");
                Map cpu_details = (Map)cpu.get("Details");
                int cpu_cores = ((Double)cpu_details.get("Cores")).intValue();
                String cpu_model = (String)cpu_details.get("Model");
                String cpu_vendor = (String)cpu_details.get("Vendor");
                int cpu_mhz = ((Double)cpu_details.get("Mhz")).intValue();
                Map cpu_usage = (Map)cpu.get("Usage");
                double cpu_user = ((Double)cpu_usage.get("User"));
                double cpu_system = (Double)cpu_usage.get("System");
                double cpu_idle = (Double)cpu_usage.get("Idle");
                System.out.println(cpu_model + " " + cpu_vendor + " " + cpu_mhz + " " + cpu_cores);
                System.out.println(cpu_user + " " + cpu_system + " " + cpu_idle);

                Map memory = (Map)dec_map_info.get("Memory");
                Map ram = (Map)memory.get("RAM");
                double ram_total = (Double)ram.get("Total");
                double ram_used = (Double)ram.get("Used");
                double ram_free = (Double)ram.get("Free");
                double ram_used_perc = (Double)ram.get("UsedPerc");
                System.out.println(ram_total + " " + ram_used + " " + ram_free + " " + ram_used_perc);

                Map swap = (Map) memory.get("SWAP");
                double swap_total = (Double) swap.get("Total");
                System.out.println(swap_total);

                Map disks = (Map) dec_map_info.get("Disks");
                ArrayList<Map> local_disks = (ArrayList<Map>) disks.get("LocalDisks");
                Iterator<Map> iterator = local_disks.iterator();
                StringBuilder disks_full = new StringBuilder();
                while (iterator.hasNext()) {
                    Map tempDisk = iterator.next();
                    String temp = tempDisk.get("DevName") + ";" + tempDisk.get("Used") + ";" + tempDisk.get("Total") + " ";
                    disks_full.append(temp);

                    System.out.println(tempDisk.get("Total"));
                    System.out.println(tempDisk.get("Used"));
                    System.out.println(tempDisk.get("Free"));
                    System.out.println(tempDisk.get("UsedPerc"));
                }
                String disks_full_str = disks_full.deleteCharAt(disks_full.length()-1).toString();

                Server server = new Server();
                // TODO server.setExternalIP(external_ip);
                server.setExternalIP(external_ip);
                server.setLocalIP(local_ip);
                server.setMachineName(machine_name);
                server.setLogoPath(DrawableAction.getOSIconName(os_name));
                server.setOSname(os_name);
                server.setCountyCode(country_code);
                server.setUptime(uptime);
                server.setSWAP(swap_total);
                server.setRam(String.valueOf(ram_total));
                server.setCPUInfo(cpu_vendor + ";" + cpu_model + ";" + cpu_cores + ";" + cpu_mhz);
                server.setDisks(disks_full_str);

                server.setRamUsages(String.valueOf(ram_total));
                server.setCpuUsages(cpu_system+ "/" + cpu_user);
                server.setDisksUsages(disks_full_str);

                DBOperations.updateServerInfo(context, server);

            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                    BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            // TODO ping server
            int server_status = Network.pingServer(ip_address)? SERVER_NOT_RESPONDING : SERVER_DOWN;
            DBOperations.updateServerStatus(context, ip_address, server_status);
        } finally {
            try {
                if (client != null) {
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String getHWID(Context context) {
        @SuppressLint("HardwareIds") String hwid_str = Settings.Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        // length = 16
        return hwid_str;
    }

    public static Map<String, Object> getDeviceInfo(Context context) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("hwid", new String(getHWID(context)));
        info.put("Model", Build.MODEL);
        info.put("Version", Build.VERSION.RELEASE);
        info.put("API", Build.VERSION.SDK_INT);
        return info;
    }

}
