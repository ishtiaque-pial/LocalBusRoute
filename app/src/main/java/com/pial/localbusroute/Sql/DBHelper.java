package com.pial.localbusroute.Sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static String DATABASE_NAME="local_bus_route";
    public static final int DATABASE_VERSION=22;
    public static final String TABLE_BUS="busRoute";
    public static final String COLUMN_ROUTE_NAME="routeName";
    public static final String COLUMN_LATITUDE="latitude";
    public static final String COLUMN_LONGITUDE="longitude";


    private String CREATE_TABLE_BUS="create table "+TABLE_BUS+"("+COLUMN_ROUTE_NAME+" text,"+COLUMN_LATITUDE+" text, "
            +COLUMN_LONGITUDE+" text);";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BUS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_BUS);
        onCreate(db);

    }


}
