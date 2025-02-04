package com.example.vtfc_master.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.vtfc_master.Camara;

import java.util.ArrayList;
import java.util.List;

public class CamaraBBDD  extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CamaraBBDD.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_CAMARAS = "Camara";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CAMERA_ID = "cameraId";
    public static final String COLUMN_SOURCE_ID = "sourceId";
    public static final String COLUMN_CAMERA_NAME = "cameraName";
    public static final String COLUMN_URL_IMAGE = "urlImage";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_KILOMETER = "kilometer";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_ROAD = "road";
    public static final String COLUMN_FAVORITOCAM = "isFavoritoCam";

    public CamaraBBDD(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_CAMARAS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CAMERA_ID + " TEXT, " +
                COLUMN_SOURCE_ID + " TEXT, " +
                COLUMN_CAMERA_NAME + " TEXT, " +
                COLUMN_URL_IMAGE + " TEXT, " +
                COLUMN_LATITUDE + " TEXT, " +
                COLUMN_LONGITUDE + " TEXT, " +
                COLUMN_KILOMETER + " TEXT, " +
                COLUMN_ADDRESS + " TEXT, " +
                COLUMN_ROAD + " TEXT, " +
                COLUMN_FAVORITOCAM + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_CAMARAS + " ADD COLUMN " + COLUMN_FAVORITOCAM + " INTEGER DEFAULT 0");
        }
    }

    public long insertarCamara(Camara camara) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAMERA_ID, camara.getCameraId());
        values.put(COLUMN_SOURCE_ID, camara.getSourceId());
        values.put(COLUMN_CAMERA_NAME, camara.getCameraName());
        values.put(COLUMN_URL_IMAGE, camara.getUrlImage());
        values.put(COLUMN_LATITUDE, camara.getLatitude());
        values.put(COLUMN_LONGITUDE, camara.getLongitude());
        values.put(COLUMN_KILOMETER, camara.getKilometer());
        values.put(COLUMN_ADDRESS, camara.getAddress());
        values.put(COLUMN_ROAD, camara.getRoad());
        values.put(COLUMN_FAVORITOCAM, camara.isFavoritoCam() ? 1 : 0);
        long result = db.insert(TABLE_CAMARAS, null, values);
        db.close();
        return result;
    }

    public List<Camara> obtenerTodasCamaras() {
        List<Camara> camarasList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CAMARAS, null, null, null, null, null, COLUMN_CAMERA_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                Camara camara = new Camara(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMERA_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SOURCE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMERA_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KILOMETER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADDRESS)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROAD)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FAVORITOCAM)) == 1
                );
                camarasList.add(camara);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return camarasList;
    }

    public void toggleFavorito(String cameraId, boolean isFavorito) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FAVORITOCAM, isFavorito ? 1 : 0);
        db.update(TABLE_CAMARAS, values, COLUMN_CAMERA_ID + "=?", new String[]{cameraId});
        db.close();
    }



}
