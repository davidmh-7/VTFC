package com.example.vtfc_master.db;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vtfc_master.Incidencia;

import java.util.ArrayList;
import java.util.List;

public class IncidenciasBBDD extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "IncidenciasBBDD.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_INCIDENCIAS  = "incidencia";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_INCIDENCE_ID = "incidenceId";
    public static final String COLUMN_SOURCE_ID = "sourceId";
    public static final String COLUMN_INCIDENCE_TYPE = "incidenceType";
    public static final String COLUMN_AUTONOMOUS_REGION = "autonomousRegion";
    public static final String COLUMN_PROVINCE = "province";
    public static final String COLUMN_CAUSE = "cause";
    public static final String COLUMN_CITY_TOWN = "cityTown";
    public static final String COLUMN_START_DATE = "startDate";
    public static final String COLUMN_END_DATE = "endDate";
    public static final String COLUMN_PK_START = "pkStart";
    public static final String COLUMN_PK_END = "pkEnd";
    public static final String COLUMN_DIRECTION = "direction";
    public static final String COLUMN_INCIDENCE_NAME = "incidenceName";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_FAVORITO = "isFavorito";



    public IncidenciasBBDD(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_INCIDENCIAS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_INCIDENCE_ID + " TEXT, " +
                COLUMN_SOURCE_ID + " TEXT, " +
                COLUMN_INCIDENCE_TYPE + " TEXT, " +
                COLUMN_AUTONOMOUS_REGION + " TEXT, " +
                COLUMN_PROVINCE + " TEXT, " +
                COLUMN_CAUSE + " TEXT, " +
                COLUMN_CITY_TOWN + " TEXT, " +
                COLUMN_START_DATE + " TEXT, " +
                COLUMN_END_DATE + " TEXT, " +
                COLUMN_PK_START + " TEXT, " +
                COLUMN_PK_END + " TEXT, " +
                COLUMN_DIRECTION + " TEXT, " +
                COLUMN_INCIDENCE_NAME + " TEXT, " +
                COLUMN_LATITUDE + " TEXT, " +
                COLUMN_LONGITUDE + " TEXT, " +
                COLUMN_FAVORITO + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_INCIDENCIAS + " ADD COLUMN " + COLUMN_FAVORITO + " INTEGER DEFAULT 0");
        }
    }


    public int actualizarIncidencia(Incidencia incidencia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FAVORITO, incidencia.isFavorito() ? 1 : 0);
        int rows = db.update(TABLE_INCIDENCIAS, values, COLUMN_INCIDENCE_ID + "=?", new String[]{incidencia.getIncidenceId()});
        db.close();
        return rows;
    }

    public long insertarIncidencia(Incidencia incidencia) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INCIDENCE_ID, incidencia.getIncidenceId());
        values.put(COLUMN_SOURCE_ID, incidencia.getSourceId());
        values.put(COLUMN_INCIDENCE_TYPE, incidencia.getIncidenceType());
        values.put(COLUMN_AUTONOMOUS_REGION, incidencia.getAutonomousRegion());
        values.put(COLUMN_PROVINCE, incidencia.getProvince());
        values.put(COLUMN_CAUSE, incidencia.getCause());
        values.put(COLUMN_CITY_TOWN, incidencia.getCityTown());
        values.put(COLUMN_START_DATE, incidencia.getStartDate());
        values.put(COLUMN_END_DATE, incidencia.getEndDate());
        values.put(COLUMN_PK_START, incidencia.getPkStart());
        values.put(COLUMN_PK_END, incidencia.getPkEnd());
        values.put(COLUMN_DIRECTION, incidencia.getDirection());
        values.put(COLUMN_INCIDENCE_NAME, incidencia.getIncidenceName());
        values.put(COLUMN_LATITUDE, incidencia.getLatitude());
        values.put(COLUMN_LONGITUDE, incidencia.getLongitude());
        values.put(COLUMN_FAVORITO, incidencia.isFavorito() ? 1 : 0);
        long result = db.insert(TABLE_INCIDENCIAS, null, values);
        db.close();
        return result;
    }
    public List<Incidencia> obterTodasIncidencias() {
        List<Incidencia> incidenciasList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INCIDENCIAS, null, null, null, null, null, COLUMN_INCIDENCE_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Incidencia incidencia = new Incidencia(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INCIDENCE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SOURCE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INCIDENCE_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTONOMOUS_REGION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROVINCE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAUSE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY_TOWN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PK_START)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PK_END)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIRECTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INCIDENCE_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FAVORITO)) == 1
                );
                incidenciasList.add(incidencia);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return incidenciasList;
    }

    public void toggleFavorito(String incidenceId, boolean isFavorito) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FAVORITO, isFavorito ? 1 : 0);
        db.update(TABLE_INCIDENCIAS, values, COLUMN_INCIDENCE_ID + "=?", new String[]{incidenceId});
        db.close();
    }



}
