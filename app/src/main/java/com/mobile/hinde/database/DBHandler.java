package com.mobile.hinde.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "SpaceTime.db";
    private static final String TABLE_NAME = "Menu_Com";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LAST_EXECUTED = "last_executed";
    private static final String COLUMN_EXPECTED_END = "expected_end";
    private static final String COLUMN_IS_ARRIVED = "is_arrived";
    private static final String COLUMN_IS_RETURNED = "is_returned";


    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = getWritableDatabase();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_NAME +
                " TEXT PRIMARY KEY," + COLUMN_LAST_EXECUTED + " LONG,"+ COLUMN_EXPECTED_END + " LONG, "
                + COLUMN_IS_ARRIVED + " INTEGER," + COLUMN_IS_RETURNED + " INTEGER )";
        db.execSQL(CREATE_TABLE);


        initDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    private void initDB(SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, "SUN");
        values.put(COLUMN_LAST_EXECUTED, "");
        values.put(COLUMN_EXPECTED_END, 0);
        values.put(COLUMN_IS_ARRIVED, 0);
        values.put(COLUMN_IS_RETURNED, 0);
        db.insert(TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(COLUMN_NAME, "MOON");
        values.put(COLUMN_LAST_EXECUTED, "");
        values.put(COLUMN_EXPECTED_END, 0);
        values.put(COLUMN_IS_ARRIVED, 0);
        values.put(COLUMN_IS_RETURNED, 0);
        db.insert(TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(COLUMN_NAME, "VOYAGER1");
        values.put(COLUMN_LAST_EXECUTED, "");
        values.put(COLUMN_EXPECTED_END, 0);
        values.put(COLUMN_IS_ARRIVED, 0);
        values.put(COLUMN_IS_RETURNED, 0);
        db.insert(TABLE_NAME, null, values);
    }

//    public String loadHandler() {
//        String result = "";
//        String query = "Select * FROM " + TABLE_NAME;
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(query, null);
//        while (cursor.moveToNext()) {
//            String result_0 = cursor.getString(0);
//            long result_1 = cursor.getLong(1);
//            result += result_0 + " " + String.valueOf(result_1) +
//                    System.getProperty("line.separator");
//        }
//        cursor.close();
//        db.close();
//        return result;
//    }

    public void insertData(Menu_Com menu_com) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, menu_com.getName());
        values.put(COLUMN_LAST_EXECUTED, "");
        values.put(COLUMN_EXPECTED_END, 0);
        values.put(COLUMN_IS_ARRIVED, 0);
        values.put(COLUMN_IS_RETURNED, 0);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Menu_Com searchData(String name) {
//        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = " + "'" + name + "'";
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = COLUMN_NAME + " = ?";
        String[] selectionArgs = { name };
        Cursor cursor = db.query(
                TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        Menu_Com menu_com = new Menu_Com();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            menu_com.setName(cursor.getString(0));
            menu_com.setLast_execute(cursor.getLong(1));
            menu_com.setExpected_end(cursor.getLong(2));
            cursor.close();
        } else {
            menu_com = null;
        }
        db.close();
        return menu_com;
    }

    public ArrayList<String> searchEndedData(long endTime) {
//        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = " + "'" + name + "'";
        SQLiteDatabase db = this.getReadableDatabase();

        String[] select_Col = {COLUMN_NAME};

        String selection = COLUMN_EXPECTED_END + " < ? AND " + COLUMN_EXPECTED_END + " > 0 ";
        String[] selectionArgs = { String.valueOf(endTime) };
        Cursor cursor = db.query(
                TABLE_NAME,   // The table to query
                select_Col,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        ArrayList<String> targetList = new ArrayList<>();
        while(cursor.moveToNext()){
            cursor.moveToFirst();
            targetList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return targetList;
    }

    public HashMap<String,Long> searchStartedData() {
//        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = " + "'" + name + "'";
        SQLiteDatabase db = this.getReadableDatabase();

        String[] select_Col = {COLUMN_NAME, COLUMN_EXPECTED_END};

        String selection = COLUMN_EXPECTED_END + " > 0 ";
        Cursor cursor = db.query(
                TABLE_NAME,   // The table to query
                select_Col,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        HashMap<String,Long> targetMap = new HashMap<>();
        while(cursor.moveToNext()){
            cursor.moveToFirst();
            targetMap.put(cursor.getString(0), cursor.getLong(1));
        }
        cursor.close();
        db.close();
        return targetMap;
    }

    public boolean deleteData(String name) {
        boolean result = false;
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + "= '" + name + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Menu_Com menu_com = new Menu_Com();
        if (cursor.moveToFirst()) {
            menu_com.setName(cursor.getString(0));
            db.delete(TABLE_NAME, COLUMN_NAME + " = ? ",
                    new String[] {
                            menu_com.getName()
            });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public boolean updateData(String name, long last_executed, long expected_end) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_LAST_EXECUTED, last_executed);
        args.put(COLUMN_EXPECTED_END, expected_end);

        // Which row to update, based on the title
        String selection = COLUMN_NAME + " = ?";
        String[] selectionArgs = { name };
        return db.update(TABLE_NAME, args, selection, selectionArgs) > 0;
    }

    public boolean resetExpectedEnd(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_EXPECTED_END, 0);

        // Which row to update, based on the title
        String selection = COLUMN_NAME + " = ?";
        String[] selectionArgs = { name };
        return db.update(TABLE_NAME, args, selection, selectionArgs) > 0;
    }
}
