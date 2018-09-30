package com.example.herem1t.rc_client.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;

import com.example.herem1t.rc_client.Constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Created by Herem1t on 08.04.2018.
 */

public class DBOperations {

    static final String DB_NAME = "Server.db";
    static final int DB_VERSION = 1;
    public static final String TABLE_SERVER = "Servers";
    static final String TABLE_STATS = "Statistics";

    public static final int ORDER_BY_OS = 10;
    public static final int ORDER_BY_FAVOURITE = 11;
    public static final int ORDER_BY_STATUS = 12;
    public static final int ORDER_BY_DATE = 13;

    private static DBHelper dbHelper = null;
//    private static SQLiteDatabase sqlite_db = null;

    public static class DBHelper extends SQLiteOpenHelper {

        public DBHelper (Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql_server = "CREATE TABLE IF NOT EXISTS " + TABLE_SERVER + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "status INTEGER DEFAULT 0," +
                    "favourite INTEGER DEFAULT 0," +
                    "external_ip TEXT NOT NULL," +
                    "lat REAL," +
                    "lon REAL," +
                    "local_ip TEXT," +
                    "machine_name TEXT," +
                    "os_name TEXT," +
                    "logo TEXT," +
                    "ram TEXT," +
                    "swap TEXT," +
                    "country_code TEXT," +
                    "cpu_info TEXT," +
                    "uptime TEXT," +
                    "disks TEXT," +
                    "enc_key TEXT," +
                    "hash_pass TEXT," +
                    "description TEXT," +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
            db.execSQL(sql_server);

            String sql_stats = "CREATE TABLE IF NOT EXISTS " + TABLE_STATS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "server INTEGER," +
                    "ram TEXT," +
                    "cpu TEXT," +
                    "disks TEXT," +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    " FOREIGN KEY (server) REFERENCES "+TABLE_SERVER+" (id))";
            db.execSQL(sql_stats);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private static SQLiteOpenHelper getHelperInstance(Context context) {
        return dbHelper == null ? new DBHelper(context) : dbHelper;
    }

    public static void updateServerStatus(Context context, String ext_ip, int status) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        db.update(TABLE_SERVER, cv, "external_ip = ? ", new String[] {ext_ip});
        db.close();
    }


    public static long addServer(Context context, String ext_ip, String pass, String description, double lat, double lon) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash_pass = md.digest(pass.getBytes());

        ContentValues cv = new ContentValues();
        cv.put("external_ip", ext_ip);
        cv.put("description", description);
        cv.put("hash_pass", Base64.encodeToString(hash_pass, Base64.DEFAULT));
        //cv.put("timestamp", "2018-03-01 15:17:53");
        cv.put("lat", lat);
        cv.put("lon", lon);
//        Drawable drawable = context.getResources().getDrawable(context.getResources().getIdentifier("ic_marker", "drawable", context.getPackageName()));
//
//        cv.put("logo", );
        long rowID = db.insert(DBOperations.TABLE_SERVER, null, cv);
        System.out.println("Insert row with id = " + rowID);
        db.close();
        return rowID;
    }


    public static String getServerDescription(Context context, String ext_ip) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();
        String description = "";


        Cursor cursor = db.query(TABLE_SERVER, new String[]{"description"}, "external_ip = ?", new String[]{ext_ip}, null, null, null);
        int descr_index = cursor.getColumnIndex("description");
        if (cursor.moveToFirst()) {
            description = cursor.getString(descr_index);
        }
        cursor.close();
        db.close();
        return description;
    }

    public static int updateServerDescription(Context context, String ext_ip, String description) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("description", description);
        db.close();
        return db.update(TABLE_SERVER, cv, "external_ip = ?", new String[]{ext_ip});
    }

    public static int getServerStatus(Context context, String ext_ip) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        int status = Constants.SERVER_DOWN;
        Cursor cursor = db.query(TABLE_SERVER, new String[]{"status"}, "external_ip = ?", new String[]{ext_ip}, null, null, null);
        if (cursor.moveToFirst()) {
            status = cursor.getInt(cursor.getColumnIndex("status"));
        }
        cursor.close();
        db.close();
        return status;
    }

    public static String getServerLogo(Context context, String ext_ip) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        // default logo
        String logo_path = Constants.UNKNOWN_LINUX;
        Cursor cursor = db.query(TABLE_SERVER, new String[]{"logo"}, "external_ip = ?", new String[]{ext_ip}, null, null, null);
        if (cursor.moveToFirst()) {
            logo_path = cursor.getString(cursor.getColumnIndex("logo"));
        }
        cursor.close();
        db.close();
        return logo_path;
    }

    public static int changeServerConnectionPassword(Context context, String ext_ip, String pass) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash_pass = md.digest(pass.getBytes());

        ContentValues cv = new ContentValues();
        cv.put("hash_pass", Base64.encodeToString(hash_pass, Base64.DEFAULT));
        db.close();
        return db.update(TABLE_SERVER, cv, "external_ip = ?", new String[]{ext_ip});
    }

    public static List<Server> getLocationInfo(Context context) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        List<Server> servers = new ArrayList<>();
        Cursor cursor = db.query(DBOperations.TABLE_SERVER, new String[] {"external_ip", "lat", "lon"}, null, null, null, null, null);

        int ip_colunm = cursor.getColumnIndex("external_ip");
        int lat_column = cursor.getColumnIndex("lat");
        int lon_column = cursor.getColumnIndex("lon");

        while(cursor.moveToNext()) {
            Server server = new Server(cursor.getString(ip_colunm), cursor.getDouble(lat_column), cursor.getDouble(lon_column));
            servers.add(server);
        }
        cursor.close();
        db.close();
        return servers;
    }


    public static Map<String, Integer> getServersStatusValue(Context context){
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        long available = DatabaseUtils.queryNumEntries(db, TABLE_SERVER,
                "status=?", new String[] {String.valueOf(Constants.SERVER_AVAILABLE)});
        long not_responding = DatabaseUtils.queryNumEntries(db, TABLE_SERVER,
                "status=?", new String[] {String.valueOf(Constants.SERVER_NOT_RESPONDING)});
        long down = DatabaseUtils.queryNumEntries(db, TABLE_SERVER,
                "status=?", new String[] {String.valueOf(Constants.SERVER_DOWN)});

        Map<String, Integer> result = new LinkedHashMap<>();
        result.put("available", (int)available);
        result.put("not_responding", (int)not_responding);
        result.put("down", (int)down);
        db.close();
        return  result;
    }

    public static List<Server> getRamUsages(Context context, String ext_ip) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        List<Server> ram_usage = new ArrayList<>();
        String sql = "SELECT ram, timestamp FROM " + TABLE_STATS +
                " WHERE server = (SELECT id FROM " + TABLE_SERVER + " WHERE external_ip = ?) ORDER BY timestamp";
        Cursor cursor = db.rawQuery(sql, new String[] {ext_ip});
        int ram_index = cursor.getColumnIndex("ram");
        int timestamp = cursor.getColumnIndex("timestamp");
        while (cursor.moveToNext()) {
            Server server = new Server(cursor.getString(ram_index), cursor.getString(timestamp), Server.INIT_RAM);
            ram_usage.add(server);
        }
        cursor.close();
        db.close();
        return ram_usage;
    }

    public static List<Server> getCPUUsages(Context context, String ext_ip) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        List<Server> cpu_usage = new ArrayList<>();
        String sql = "SELECT cpu, timestamp FROM " + TABLE_STATS +
                " WHERE server = (SELECT id FROM " + TABLE_SERVER + " WHERE external_ip = ?) ORDER BY timestamp";
        Cursor cursor = db.rawQuery(sql, new String[] {ext_ip});
        int cpu_index = cursor.getColumnIndex("cpu");
        int timestamp = cursor.getColumnIndex("timestamp");
        while (cursor.moveToNext()) {
            Server server = new Server(cursor.getString(cpu_index), cursor.getString(timestamp), Server.INIT_CPU);
            cpu_usage.add(server);
        }
        cursor.close();
        db.close();
        return cpu_usage;
    }

    public static List<Server> getDiskUsages(Context context, String ext_ip) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        List<Server> disk_usage = new ArrayList<>();
        String sql = "SELECT disks, timestamp FROM " + TABLE_STATS +
                " WHERE server = (SELECT id FROM " + TABLE_SERVER + " WHERE external_ip = ?) ORDER BY timestamp";
        Cursor cursor = db.rawQuery(sql, new String[] {ext_ip});
        int disks_index = cursor.getColumnIndex("disks");
        int timestamp = cursor.getColumnIndex("timestamp");
        while (cursor.moveToNext()) {
            Server server = new Server(cursor.getString(disks_index), cursor.getString(timestamp), Server.INIT_HDD);
            disk_usage.add(server);
        }
        cursor.close();
        db.close();
        return disk_usage;
    }

    public static List<Server> getAllServers(Context context) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        List<Server> all_servers = new ArrayList<Server>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SERVER, null);
        int ext_ip_index = cursor.getColumnIndex("external_ip");
        int description_index = cursor.getColumnIndex("description");
        int favourite_index = cursor.getColumnIndex("favourite");
        int status_index = cursor.getColumnIndex("status");
        int logo_index = cursor.getColumnIndex("logo");
        while (cursor.moveToNext()) {
            String ext_ip = cursor.getString(ext_ip_index);
            String description = cursor.getString(description_index);
            String logo = cursor.getString(logo_index);
            int favourite = cursor.getInt(favourite_index);
            int status = cursor.getInt(status_index);
            Server server = new Server(ext_ip, description, logo, favourite, status);
            all_servers.add(server);
        }
        cursor.close();
        db.close();
        return all_servers;
    }

    public static List<Server> sortBy(Context context, int by) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        String orderBy;
        switch (by) {
            case ORDER_BY_DATE:
                orderBy = "timestamp" + " ASC";
                break;
            case ORDER_BY_FAVOURITE:
                orderBy = "favourite" + " DESC";
                break;
            case ORDER_BY_STATUS:
                orderBy = "status" + " DESC";
                break;
            case ORDER_BY_OS:
                orderBy = "os_name" + " ASC";
                break;
            default:
                orderBy = "timestamp" + " ASC";
                break;
        }

        List<Server> all_servers = new ArrayList<Server>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SERVER + " ORDER BY " + orderBy, null);
        int ext_ip_index = cursor.getColumnIndex("external_ip");
        int description_index = cursor.getColumnIndex("description");
        int favourite_index = cursor.getColumnIndex("favourite");
        int status_index = cursor.getColumnIndex("status");
        int logo_index = cursor.getColumnIndex("logo");
        while (cursor.moveToNext()) {
            String ext_ip = cursor.getString(ext_ip_index);
            String description = cursor.getString(description_index);
            String logo = cursor.getString(logo_index);
            int favourite = cursor.getInt(favourite_index);
            int status = cursor.getInt(status_index);
            Server server = new Server(ext_ip, description, logo, favourite, status);
            all_servers.add(server);
        }
        cursor.close();
        db.close();
        return all_servers;
    }

    public static Server getServer(Context context, String ext_ip) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        Server server = null;
        Cursor cursor = db.query(TABLE_SERVER, null, "external_ip = ?", new String[]{ext_ip}, null, null, null);

        int ext_ip_index = cursor.getColumnIndex("external_ip");
        int description_index = cursor.getColumnIndex("description");
        int country_code_index = cursor.getColumnIndex("country_code");
        int local_ip_index = cursor.getColumnIndex("local_ip");
        int hostname_index = cursor.getColumnIndex("machine_name");
        int os_name_index = cursor.getColumnIndex("os_name");
        int cpu_info_index = cursor.getColumnIndex("cpu_info");
        int uptime_index = cursor.getColumnIndex("uptime");
        int disks_index = cursor.getColumnIndex("disks");
        int ram_index = cursor.getColumnIndex("ram");
        int logo_path_index = cursor.getColumnIndex("logo");
        int lat_index = cursor.getColumnIndex("lat");
        int lon_index = cursor.getColumnIndex("lon");
        int status_index = cursor.getColumnIndex("status");
        int favourite_index = cursor.getColumnIndex("favourite");

        if(cursor.moveToFirst()) {
            String external_ip = cursor.getString(ext_ip_index);
            String description = cursor.getString(description_index);
            String country_code = cursor.getString(country_code_index);
            String local_ip = cursor.getString(local_ip_index);
            String hostname = cursor.getString(hostname_index);
            String os_name = cursor.getString(os_name_index);
            String cpu_info = cursor.getString(cpu_info_index);
            String uptime = cursor.getString(uptime_index);
            String disks = cursor.getString(disks_index);
            String ram = cursor.getString(ram_index);
            String logo_path = cursor.getString(logo_path_index);
            double lat = cursor.getDouble(lat_index);
            double lon = cursor.getDouble(lon_index);
            int status = cursor.getInt(status_index);
            int favourite = cursor.getInt(favourite_index);

            server = new Server(external_ip, description, country_code, local_ip, hostname, os_name,
                    cpu_info, uptime, disks, ram, logo_path, lat, lon, status, favourite);
        }
        cursor.close();
        db.close();
        return server;
    }

    public static void updateServerInfo(Context context, Server server) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        String ext_ip = server.getExternalIP();
        ContentValues cv = new ContentValues();

        cv.put("local_ip", server.getLocalIP());
        cv.put("machine_name", server.getMachineName());
        cv.put("logo", server.getLogoPath());
        cv.put("os_name", server.getOSname());
        cv.put("ram", server.getRam());
        cv.put("swap", String.valueOf(server.getSWAP()));
        cv.put("country_code", server.getCountyCode() );
        cv.put("cpu_info", server.getCPUInfo());
        cv.put("uptime", server.getUptime());
        cv.put("disks", server.getDisks());
        db.update(TABLE_SERVER, cv, "external_ip = ?", new String[]{ext_ip});

        ContentValues cv1 = new ContentValues();
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_SERVER + " WHERE external_ip = ?" , new String[] {ext_ip});
        int server_id = 0;
        if (cursor.moveToFirst()) {
            server_id = cursor.getInt(cursor.getColumnIndex("id"));
        }
        cv1.put("ram", server.getRamUsages());
        cv1.put("cpu", server.getCpuUsages());
        cv1.put("disks", server.getDisksUsages());
        cv1.put("server", server_id );
        db.insert(TABLE_STATS, null, cv1);

        cursor.close();
        db.close();
    }

    public static String getDisks(Context context, String ext_ip) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        Cursor cursor = db.query(TABLE_SERVER, new String[] {"disks"}, "external_ip = ?", new String[]{ext_ip}, null, null, null);
        String result = null;
        if(cursor.moveToFirst()) {
            result = cursor.getString(cursor.getColumnIndex("disks"));
        }
        cursor.close();
        db.close();
        return result;
    }

    public static int deleteRows(Context context, String ext_ip) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        int deleted = db.delete(TABLE_SERVER, "external_ip = ?", new String[]{ext_ip});
        //db.delete(TABLE_STATS, "external_ip = ?", new String[]{ext_ip});
        db.rawQuery("DELETE FROM " + TABLE_STATS + " WHERE server = (SELECT id FROM " + TABLE_SERVER + " WHERE external_ip = ? )" ,new String[]{ext_ip});
        db.close();
        return deleted;
    }

    public static int addToFavourite(Context context, String ext_ip) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("favourite", 1);
        int updated = db.update(TABLE_SERVER, cv, "external_ip = ?", new String[] {ext_ip});
        db.close();
        return updated;
    }

    public static int removeFromFavourite(Context context, String ext_ip) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("favourite", 0);
        int updated = db.update(TABLE_SERVER, cv, "external_ip = ?", new String[] {ext_ip});
        db.close();
        return updated;
    }

    public static void setData(Context context) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();

        addServer(context, "53.216.121.248", "fsdf", "webserver mydomain.com", 12.0, 35.1);
        for (int i=0; i<22; i++) {
            Random random = new Random();
            String ext_ip = random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255);
            addServer(context, ext_ip, random.nextInt() + "pass", "cloud server " + random.nextInt(12) , random.nextDouble(), random.nextDouble());
            if (i<19) {
                updateServerStatus(context, ext_ip, Constants.SERVER_AVAILABLE);
            } else {
                updateServerStatus(context,ext_ip, Constants.SERVER_NOT_RESPONDING);
            }
        }


        String givenDateString = "2018-05-17 09:40:12";
        long dateSec = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm");
        try {
            Date mDate = sdf.parse(givenDateString);
            long timeInMilliseconds = mDate.getTime();
            dateSec = (long)timeInMilliseconds/1000;
            Log.d("linechart", "Date in sec :: " + timeInMilliseconds/1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long startPoint = dateSec * 1000;
        float delta = 0;

        db.beginTransaction();
        for(int i=0; i<400; i++) {
            Random random = new Random();
            ContentValues cv = new ContentValues();
            cv.put("ram", 20 + random.nextInt(20));
            cv.put("cpu", random.nextInt(10) + "/" + (10 + random.nextInt(25)));
            cv.put("disks", "C:\\;"+random.nextInt(20)+";"+random.nextInt(150)+" "+
                    "D:\\;"+random.nextInt(20)+";"+random.nextInt(120)+" "+
                    "E:\\;"+random.nextInt(20)+";"+random.nextInt(250));
            cv.put("server", 1);

            //delta = dateSec - startPoint;
            String dateFormat = "yy-MM-dd HH:mm:ss";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            Date date = new Date(startPoint);
            String result_date = simpleDateFormat.format(date);

            cv.put("timestamp", result_date);
            db.insert(TABLE_STATS, null, cv );
            startPoint += 300*1000;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public static void deleteAll(Context context) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();
        int deleteCount = db.delete(DBOperations.TABLE_SERVER, null, null);
        System.out.println("Deleted " + deleteCount + " rows");
        db.close();
    }

    public static String getKey(Context context, String ext_ip) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();
        Cursor cursor = db.query(DBOperations.TABLE_SERVER, new String[] {"enc_key"}, "external_ip = ?", new String[] {ext_ip}, null, null, null);
        String key = "";
        if (cursor.moveToFirst()){
            int key_ColumnID = cursor.getColumnIndex("enc_key");
            key = cursor.getString(key_ColumnID);
        }
        cursor.close();
        db.close();
        return key;
    }

    public static boolean isServerExists(Context context, String ext_ip) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();
        Cursor cursor = db.query(TABLE_SERVER, new String[] {"external_ip"}, "external_ip = ?", new String[] {ext_ip}, null, null, null);
        boolean ifExists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return ifExists;
    }

    public static void deleteMonthRows(Context context) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();
        String sql = "DELETE FROM " + TABLE_STATS + " WHERE timestamp < datetime('now', '-1 month')";
        db.execSQL(sql);
        db.close();
    }

    public static List<String> getIPAddressList(Context context) {
        SQLiteDatabase db = getHelperInstance(context).getWritableDatabase();
        Cursor cursor = db.query(TABLE_SERVER, new String[] {"external_ip"}, null, null, null, null, null);
        List<String> ip_list = new ArrayList<>();
        int ipColumnIndex = cursor.getColumnIndex("external_ip");
        if (cursor.moveToFirst()) {
            // If not at last row
            while (!cursor.isAfterLast()) {
                ip_list.add(cursor.getString(ipColumnIndex));
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();
        return ip_list;
    }

}
