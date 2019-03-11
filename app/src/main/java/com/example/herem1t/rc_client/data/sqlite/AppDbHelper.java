package com.example.herem1t.rc_client.data.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Base64;

import com.example.herem1t.rc_client.Constants;
import com.example.herem1t.rc_client.data.sqlite.model.Server;
import com.example.herem1t.rc_client.utils.DrawableUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_COUNTRY_CODE;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_CPU;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_CPU_INFO;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_DESCRIPTION;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_DISKS;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_ENC_KEY;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_EXTERNAL_IP;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_FAVORITE;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_HASHED_PASS;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_ID;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_LATITUDE;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_LOCAL_IP;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_LOGO;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_LONGITUDE;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_MACHINE_NAME;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_OS_NAME;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_RAM;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_SERVER;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_STATUS;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_SWAP;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_TIMESTAMP;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.FIELD_UPTIME;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.TABLE_SERVER;
import static com.example.herem1t.rc_client.data.sqlite.DbOpenHelper.TABLE_STATS;
import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_AVAILABLE;
import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_DOWN;
import static com.example.herem1t.rc_client.data.sqlite.model.Server.SERVER_NOT_RESPONDING;

public class AppDbHelper implements DbHelper {

    public static final int ORDER_BY_OS = 10;
    public static final int ORDER_BY_FAVOURITE = 11;
    public static final int ORDER_BY_STATUS = 12;
    public static final int ORDER_BY_DATE = 13;

    private SQLiteDatabase db;

    public AppDbHelper(DbOpenHelper dbOpenHelper) {
        this.db = dbOpenHelper.getWritableDatabase();
    }


    @Override
    public void updateServerStatus(String extIp, int status) {
        ContentValues cv = new ContentValues();
        cv.put(FIELD_STATUS, status);
        db.update(TABLE_SERVER, cv, FIELD_EXTERNAL_IP + " = ? ", new String[] {extIp});
    }


    @Override
    public long addServer(String extIp, String pass, String description, double lat, double lon) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hashedPass = md.digest(pass.getBytes());

        ContentValues cv = new ContentValues();
        cv.put(FIELD_EXTERNAL_IP, extIp);
        cv.put(FIELD_DESCRIPTION, description);
        cv.put(FIELD_HASHED_PASS, Base64.encodeToString(hashedPass, Base64.DEFAULT));
        cv.put(FIELD_LOGO, DrawableUtils.getOSIconName(null));
        cv.put(FIELD_LATITUDE, lat);
        cv.put(FIELD_LONGITUDE, lon);

        return db.insert(TABLE_SERVER, null, cv);
    }


    @Override
    public String getServerDescription(String extIp) {
        String description = "";

        Cursor cursor = db.query(TABLE_SERVER, new String[]{FIELD_DESCRIPTION},
                FIELD_EXTERNAL_IP + " = ?", new String[]{extIp}, null, null, null);
        int descrIndex = cursor.getColumnIndex(FIELD_DESCRIPTION);
        if (cursor.moveToFirst()) {
            description = cursor.getString(descrIndex);
        }
        cursor.close();
        return description;
    }

    @Override
    public int updateServerDescription(String extIp, String description) {
        ContentValues cv = new ContentValues();
        cv.put(FIELD_DESCRIPTION, description);
        return db.update(TABLE_SERVER, cv,  FIELD_EXTERNAL_IP +" = ?", new String[]{extIp});
    }

    @Override
    public int getServerStatus(String extIp) {
        int status = SERVER_DOWN;
        Cursor cursor = db.query(TABLE_SERVER, new String[]{FIELD_STATUS},
                FIELD_EXTERNAL_IP+ " = ?", new String[]{extIp}, null, null, null);
        if (cursor.moveToFirst()) {
            status = cursor.getInt(cursor.getColumnIndex(FIELD_STATUS));
        }
        cursor.close();
        return status;
    }

    @Override
    public String getServerLogo(String extIp) {
        String logoPath = Constants.UNKNOWN_LINUX;
        Cursor cursor = db.query(TABLE_SERVER, new String[]{FIELD_LOGO},
                FIELD_EXTERNAL_IP + " = ?", new String[]{extIp}, null, null, null);
        if (cursor.moveToFirst()) {
            logoPath = cursor.getString(cursor.getColumnIndex(FIELD_LOGO));
        }
        cursor.close();
        return logoPath;
    }

    @Override
    public int changeServerConnectionPassword(String extIp, String pass) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hashedPass = md.digest(pass.getBytes());

        ContentValues cv = new ContentValues();
        cv.put(FIELD_HASHED_PASS, Base64.encodeToString(hashedPass, Base64.DEFAULT));
        return db.update(TABLE_SERVER, cv, FIELD_EXTERNAL_IP + " = ?", new String[]{extIp});
    }

    @Override
    public List<Server> getLocationInfo() {
        List<Server> servers = new ArrayList<>();
        Cursor cursor = db.query(TABLE_SERVER, new String[] {FIELD_EXTERNAL_IP, FIELD_LATITUDE, FIELD_LONGITUDE},
                null, null, null, null, null);

        int ipColunm = cursor.getColumnIndex(FIELD_EXTERNAL_IP);
        int latColumn = cursor.getColumnIndex(FIELD_LATITUDE);
        int lonColumn = cursor.getColumnIndex(FIELD_LONGITUDE);

        while(cursor.moveToNext()) {
            Server server = new Server(cursor.getString(ipColunm), cursor.getDouble(latColumn), cursor.getDouble(lonColumn));
            servers.add(server);
        }
        cursor.close();
        return servers;
    }


    @Override
    public int getServersCountByStatus(int filter){
        long count = DatabaseUtils.queryNumEntries(db, TABLE_SERVER,
                FIELD_STATUS + " = ?", new String[] {String.valueOf(filter)});
        return (int) count;
    }


    @Override
    public List<Server> getRamUsages(String extIp) {
        List<Server> ramUsage = new ArrayList<>();
        String sql = "SELECT " + FIELD_RAM + " , " + FIELD_TIMESTAMP + " FROM " + TABLE_STATS +
                " WHERE " + FIELD_SERVER + " = (SELECT " + FIELD_ID + " FROM " + TABLE_SERVER +
                " WHERE "+ FIELD_EXTERNAL_IP +" = ?) ORDER BY " + FIELD_TIMESTAMP;
        Cursor cursor = db.rawQuery(sql, new String[] {extIp});
        int ramIndex = cursor.getColumnIndex(FIELD_RAM);
        int timestamp = cursor.getColumnIndex(FIELD_TIMESTAMP);
        while (cursor.moveToNext()) {
            Server server = new Server(cursor.getString(ramIndex), cursor.getString(timestamp),
                    Server.INIT_RAM);
            ramUsage.add(server);
        }
        cursor.close();
        return ramUsage;
    }

    @Override
    public List<Server> getCPUUsages(String extIp) {
        List<Server> cpuUsage = new ArrayList<>();
        String sql = "SELECT "+ FIELD_CPU +", "+ FIELD_TIMESTAMP +" FROM " + TABLE_STATS +
                " WHERE "+ FIELD_SERVER +" = (SELECT "+ FIELD_ID +" FROM " + TABLE_SERVER +
                " WHERE "+ FIELD_EXTERNAL_IP +" = ?) ORDER BY " + FIELD_TIMESTAMP;
        Cursor cursor = db.rawQuery(sql, new String[] {extIp});
        int cpuIndex = cursor.getColumnIndex(FIELD_CPU);
        int timestamp = cursor.getColumnIndex(FIELD_TIMESTAMP);
        while (cursor.moveToNext()) {
            Server server = new Server(cursor.getString(cpuIndex), cursor.getString(timestamp), Server.INIT_CPU);
            cpuUsage.add(server);
        }
        cursor.close();
        return cpuUsage;
    }

    @Override
    public List<Server> getDiskUsages(String extIp) {
        List<Server> diskUsage = new ArrayList<>();
        String sql = "SELECT "+ FIELD_DISKS +", "+ FIELD_TIMESTAMP +" FROM " + TABLE_STATS +
                " WHERE "+ FIELD_SERVER +" = (SELECT "+ FIELD_ID +" FROM " + TABLE_SERVER +
                " WHERE "+ FIELD_EXTERNAL_IP +" = ?) ORDER BY " + FIELD_TIMESTAMP;
        Cursor cursor = db.rawQuery(sql, new String[] {extIp});
        int disksIndex = cursor.getColumnIndex(FIELD_DISKS);
        int timestamp = cursor.getColumnIndex(FIELD_TIMESTAMP);
        while (cursor.moveToNext()) {
            Server server = new Server(cursor.getString(disksIndex), cursor.getString(timestamp), Server.INIT_HDD);
            diskUsage.add(server);
        }
        cursor.close();
        return diskUsage;
    }

    @Override
    public List<Server> getAllServers() {
        List<Server> allServers = new ArrayList<Server>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SERVER, null);
        int extIpIndex = cursor.getColumnIndex(FIELD_EXTERNAL_IP);
        int descriptionIndex = cursor.getColumnIndex(FIELD_DESCRIPTION);
        int favouriteIndex = cursor.getColumnIndex(FIELD_FAVORITE);
        int statusIndex = cursor.getColumnIndex(FIELD_STATUS);
        int logoIndex = cursor.getColumnIndex(FIELD_LOGO);
        while (cursor.moveToNext()) {
            String extIp = cursor.getString(extIpIndex);
            String description = cursor.getString(descriptionIndex);
            String logo = cursor.getString(logoIndex);
            int favourite = cursor.getInt(favouriteIndex);
            int status = cursor.getInt(statusIndex);
            Server server = new Server(extIp, description, logo, favourite, status);
            allServers.add(server);
        }
        cursor.close();
        return allServers;
    }

    @Override
    public List<Server> sortBy(int by) {
        String orderBy;
        switch (by) {
            case ORDER_BY_DATE:
                orderBy = FIELD_TIMESTAMP + " ASC";
                break;
            case ORDER_BY_FAVOURITE:
                orderBy = FIELD_FAVORITE + " DESC";
                break;
            case ORDER_BY_STATUS:
                orderBy = FIELD_STATUS + " DESC";
                break;
            case ORDER_BY_OS:
                orderBy = FIELD_OS_NAME + " ASC";
                break;
            default:
                orderBy = FIELD_TIMESTAMP + " ASC";
                break;
        }

        List<Server> allServers = new ArrayList<Server>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SERVER + " ORDER BY " + orderBy, null);
        int extIpIndex = cursor.getColumnIndex(FIELD_EXTERNAL_IP);
        int descriptionIndex = cursor.getColumnIndex(FIELD_DESCRIPTION);
        int favouriteIndex = cursor.getColumnIndex(FIELD_FAVORITE);
        int statusIndex = cursor.getColumnIndex(FIELD_STATUS);
        int logoIndex = cursor.getColumnIndex(FIELD_LOGO);
        while (cursor.moveToNext()) {
            String extIp = cursor.getString(extIpIndex);
            String description = cursor.getString(descriptionIndex);
            String logo = cursor.getString(logoIndex);
            int favourite = cursor.getInt(favouriteIndex);
            int status = cursor.getInt(statusIndex);
            Server server = new Server(extIp, description, logo, favourite, status);
            allServers.add(server);
        }
        cursor.close();
        return allServers;
    }

    @Override
    public Server getServer(String extIp) {
        Server server = null;
        Cursor cursor = db.query(TABLE_SERVER, null,  FIELD_EXTERNAL_IP+ " = ?", new String[]{extIp}, null, null, null);

        int extIpIndex = cursor.getColumnIndex(FIELD_EXTERNAL_IP);
        int descriptionIndex = cursor.getColumnIndex(FIELD_DESCRIPTION);
        int countryCodeIndex = cursor.getColumnIndex(FIELD_COUNTRY_CODE);
        int localIpIndex = cursor.getColumnIndex(FIELD_LOCAL_IP);
        int hostnameIndex = cursor.getColumnIndex(FIELD_MACHINE_NAME);
        int osNameIndex = cursor.getColumnIndex(FIELD_OS_NAME);
        int cpuInfoIndex = cursor.getColumnIndex(FIELD_CPU_INFO);
        int uptimeIndex = cursor.getColumnIndex(FIELD_UPTIME);
        int disksIndex = cursor.getColumnIndex(FIELD_DISKS);
        int ramIndex = cursor.getColumnIndex(FIELD_RAM);
        int logoPathIndex = cursor.getColumnIndex(FIELD_LOGO);
        int latIndex = cursor.getColumnIndex(FIELD_LATITUDE);
        int lonIndex = cursor.getColumnIndex(FIELD_LONGITUDE);
        int statusIndex = cursor.getColumnIndex(FIELD_STATUS);
        int favouriteIndex = cursor.getColumnIndex(FIELD_FAVORITE);

        if(cursor.moveToFirst()) {
            String externalIp = cursor.getString(extIpIndex);
            String description = cursor.getString(descriptionIndex);
            String countryCode = cursor.getString(countryCodeIndex);
            String localIp = cursor.getString(localIpIndex);
            String hostname = cursor.getString(hostnameIndex);
            String osName = cursor.getString(osNameIndex);
            String cpuInfo = cursor.getString(cpuInfoIndex);
            String uptime = cursor.getString(uptimeIndex);
            String disks = cursor.getString(disksIndex);
            String ram = cursor.getString(ramIndex);
            String logoPath = cursor.getString(logoPathIndex);
            double lat = cursor.getDouble(latIndex);
            double lon = cursor.getDouble(lonIndex);
            int status = cursor.getInt(statusIndex);
            int favourite = cursor.getInt(favouriteIndex);

            server = new Server(externalIp, description, countryCode, localIp, hostname, osName,
                    cpuInfo, uptime, disks, ram, logoPath, lat, lon, status, favourite);
        }
        cursor.close();
        return server;
    }

    @Override
    public void updateServerInfo(Server server) {
        String extIp = server.getExternalIP();
        ContentValues cvUpdate = new ContentValues();

        cvUpdate.put(FIELD_LOCAL_IP, server.getLocalIP());
        cvUpdate.put(FIELD_MACHINE_NAME, server.getMachineName());
        cvUpdate.put(FIELD_LOGO, server.getLogoPath());
        cvUpdate.put(FIELD_OS_NAME, server.getOSname());
        cvUpdate.put(FIELD_RAM, server.getRam());
        cvUpdate.put(FIELD_SWAP, String.valueOf(server.getSWAP()));
        cvUpdate.put(FIELD_COUNTRY_CODE, server.getCountyCode() );
        cvUpdate.put(FIELD_CPU_INFO, server.getCPUInfo());
        cvUpdate.put(FIELD_UPTIME, server.getUptime());
        cvUpdate.put(FIELD_DISKS, server.getDisks());
        db.update(TABLE_SERVER, cvUpdate, FIELD_EXTERNAL_IP + " = ?", new String[]{extIp});

        ContentValues cvInsert = new ContentValues();
        Cursor cursor = db.rawQuery("SELECT " + FIELD_ID + " FROM " + TABLE_SERVER + " WHERE "+ FIELD_EXTERNAL_IP +" = ?" , new String[] {extIp});
        int serverId = 0;
        if (cursor.moveToFirst()) {
            serverId = cursor.getInt(cursor.getColumnIndex(FIELD_ID));
        }
        cvInsert.put(FIELD_RAM, server.getRamUsages());
        cvInsert.put(FIELD_CPU, server.getCpuUsages());
        cvInsert.put(FIELD_DISKS, server.getDisksUsages());
        cvInsert.put(FIELD_SERVER, serverId );
        db.insert(TABLE_STATS, null, cvInsert);

        cursor.close();
    }

    @Override
    public String getDisks(String extIp) {
        Cursor cursor = db.query(TABLE_SERVER, new String[] {FIELD_DISKS},
                FIELD_EXTERNAL_IP + " = ?", new String[]{extIp}, null, null, null);
        String result = null;
        if(cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex(FIELD_DISKS));
        }
        cursor.close();
        return result;
    }

    @Override
    public int deleteRows(String extIp) {
        int deleted = db.delete(TABLE_SERVER, FIELD_EXTERNAL_IP + " = ?", new String[]{extIp});
        Cursor cursor = db.rawQuery("DELETE FROM " + TABLE_STATS + " WHERE "+ FIELD_SERVER +
                " = (SELECT "+ FIELD_ID + " FROM " + TABLE_SERVER + " WHERE "+ FIELD_EXTERNAL_IP + " = ? )" ,new String[]{extIp});
        cursor.close();
        return deleted;
    }

    @Override
    public int addToFavourite(String extIp) {
        ContentValues cv = new ContentValues();
        cv.put(FIELD_FAVORITE, 1);
        return db.update(TABLE_SERVER, cv,  FIELD_EXTERNAL_IP+ " = ?", new String[] {extIp});
    }

    @Override
    public int removeFromFavourite(String extIp) {
        ContentValues cv = new ContentValues();
        cv.put(FIELD_FAVORITE, 0);
        return db.update(TABLE_SERVER, cv, FIELD_EXTERNAL_IP + " = ?", new String[] {extIp});
    }


    @Override
    public void deleteAll() {
        int deleteCount = db.delete(TABLE_SERVER, null, null);
    }

    @Override
    public String getKey(String extIp) {
        Cursor cursor = db.query(TABLE_SERVER, new String[] {FIELD_ENC_KEY},
                FIELD_EXTERNAL_IP + " = ?", new String[] {extIp}, null, null, null);
        String key = "";
        if (cursor.moveToFirst()){
            int keyColumnId = cursor.getColumnIndex(FIELD_ENC_KEY);
            key = cursor.getString(keyColumnId);
        }
        cursor.close();
        return key;
    }

    @Override
    public boolean isServerExists(String extIp) {
        Cursor cursor = db.query(TABLE_SERVER, new String[] {FIELD_EXTERNAL_IP},
                FIELD_EXTERNAL_IP+ " = ?", new String[] {extIp}, null, null, null);
        boolean hasRows = cursor.moveToFirst();
        cursor.close();
        return hasRows;
    }

    @Override
    public boolean isHandshakeGotten(String extIp) {
        Cursor cursor = db.query(TABLE_SERVER, new String[] {FIELD_ENC_KEY},FIELD_EXTERNAL_IP
                + " = ? AND "+ FIELD_ENC_KEY +" IS NOT NULL ", new String[] {extIp}, null, null ,null );
        boolean hasRows = cursor.moveToFirst();
        cursor.close();
        return hasRows;
    }

    @Override
    public String getHashedPass(String extIp) {
        Cursor cursor = db.query(TABLE_SERVER, new String[] {FIELD_HASHED_PASS},
                FIELD_EXTERNAL_IP + " = ? ", new  String[] {extIp}, null, null, null);

        String hashedPass = null;
        if(cursor.moveToFirst()) {
            int hashedPassColumnId = cursor.getColumnIndex(FIELD_HASHED_PASS);
            hashedPass = cursor.getString(hashedPassColumnId);
        }
        cursor.close();

        return hashedPass;
    }

    @Override
    public void updateKey(byte[] key, String extIp) {
        ContentValues cv = new ContentValues();
        if (key == null) {
            cv.putNull(FIELD_ENC_KEY);
            db.update(TABLE_SERVER, cv, FIELD_EXTERNAL_IP + " = ? ", new String[] {extIp});
        } else {
            cv.put(FIELD_ENC_KEY, Base64.encodeToString(key, android.util.Base64.DEFAULT));
            db.update(TABLE_SERVER, cv , FIELD_EXTERNAL_IP + " = ? ", new String[] {extIp});
        }
     }

    @Override
    public void deleteMonthRows() {
        String sql = "DELETE FROM " + TABLE_STATS + " WHERE "+ FIELD_TIMESTAMP +" < datetime('now', '-1 month')";
        db.execSQL(sql);
    }

    @Override
    public List<String> getIPAddressList() {
        Cursor cursor = db.query(TABLE_SERVER, new String[] {FIELD_EXTERNAL_IP}, null, null, null, null, null);
        List<String> ipList = new ArrayList<>();
        int ipColumnIndex = cursor.getColumnIndex(FIELD_EXTERNAL_IP);
        if (cursor.moveToFirst()) {
            // If not at last row
            while (!cursor.isAfterLast()) {
                ipList.add(cursor.getString(ipColumnIndex));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return ipList;
    }


}
