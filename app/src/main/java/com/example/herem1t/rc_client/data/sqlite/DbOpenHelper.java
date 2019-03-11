package com.example.herem1t.rc_client.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "Server.db";
    static final int DB_VERSION = 1;
    static final String TABLE_SERVER = "Servers";
    static final String TABLE_STATS = "Statistics";

    static final String FIELD_STATUS = "status";
    static final String FIELD_FAVORITE = "favourite";
    static final String FIELD_EXTERNAL_IP = "external_ip";
    static final String FIELD_LATITUDE = "lat";
    static final String FIELD_LONGITUDE = "lon";
    static final String FIELD_LOCAL_IP = "local_ip";
    static final String FIELD_MACHINE_NAME = "machine_name";
    static final String FIELD_OS_NAME = "os_name";
    static final String FIELD_LOGO = "logo";
    static final String FIELD_RAM = "ram";
    static final String FIELD_SWAP = "swap";
    static final String FIELD_COUNTRY_CODE = "country_code";
    static final String FIELD_CPU_INFO = "cpu_info";
    static final String FIELD_UPTIME = "uptime";
    static final String FIELD_DISKS = "disks";
    static final String FIELD_ENC_KEY = "enc_key";
    static final String FIELD_HASHED_PASS = "hash_pass";
    static final String FIELD_DESCRIPTION = "description";
    static final String FIELD_TIMESTAMP = "timestamp";
    static final String FIELD_SERVER = "server";
    static final String FIELD_CPU = "cpu";
    static final String FIELD_ID = "id";


    public DbOpenHelper (Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlServer = "CREATE TABLE IF NOT EXISTS " + TABLE_SERVER + " (" +
                FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FIELD_STATUS + " INTEGER DEFAULT 0," +
                FIELD_FAVORITE + " INTEGER DEFAULT 0," +
                FIELD_EXTERNAL_IP + " TEXT NOT NULL," +
                FIELD_LATITUDE + " REAL," +
                FIELD_LONGITUDE + " REAL," +
                FIELD_LOCAL_IP + " TEXT," +
                FIELD_MACHINE_NAME + " TEXT," +
                FIELD_OS_NAME + " TEXT," +
                FIELD_LOGO + " TEXT," +
                FIELD_RAM + " TEXT," +
                FIELD_SWAP + " TEXT," +
                FIELD_COUNTRY_CODE + " TEXT," +
                FIELD_CPU_INFO + " TEXT," +
                FIELD_UPTIME + " TEXT," +
                FIELD_DISKS + " TEXT," +
                FIELD_ENC_KEY + " TEXT," +
                FIELD_HASHED_PASS + " TEXT," +
                FIELD_DESCRIPTION + " TEXT," +
                FIELD_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(sqlServer);

        String sqlStats = "CREATE TABLE IF NOT EXISTS " + TABLE_STATS + " (" +
                FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FIELD_SERVER + " INTEGER," +
                FIELD_RAM + " TEXT," +
                FIELD_CPU + " TEXT," +
                FIELD_DISKS + " TEXT," +
                FIELD_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
                " FOREIGN KEY (" + FIELD_SERVER + ") REFERENCES "+TABLE_SERVER+" (" + FIELD_ID + "))";
        db.execSQL(sqlStats);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
