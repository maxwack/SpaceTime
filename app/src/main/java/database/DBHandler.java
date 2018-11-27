package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {

    //information of database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SpaceTime.db";
    public static final String TABLE_NAME = "Menu_Com";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_LAST_EXECUTED = "last_executed";
    public static final String COLUMN_IS_ARRIVED = "is_arrived";
    public static final String COLUMN_IS_RETURNED = "is_returned";


    //initialize the database
    public DBHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_NAME +
                "TEXT PRIMARYKEY," + COLUMN_LAST_EXECUTED + " LONG,"+ COLUMN_IS_ARRIVED + "" +
                "INTEGER," + COLUMN_IS_RETURNED + " INTEGER )";
        db.execSQL(CREATE_TABLE);

        Menu_Com menu_Sun = new Menu_Com("SUN", 0, 0,0);
        insertData(menu_Sun);
        Menu_Com menu_Moon = new Menu_Com("MOON", 0, 0,0);
        insertData(menu_Moon);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}

    public String loadHandler() {
        String result = "";
        String query = "Select * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String result_0 = cursor.getString(0);
            long result_1 = cursor.getLong(1);
            result += result_0 + " " + String.valueOf(result_1) +
                    System.getProperty("line.separator");
        }
        cursor.close();
        db.close();
        return result;
    }

    public void insertData(Menu_Com menu_com) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, menu_com.getName());
        values.put(COLUMN_LAST_EXECUTED, "");
        values.put(COLUMN_IS_ARRIVED, 0);
        values.put(COLUMN_IS_RETURNED, 0);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Menu_Com searchData(String name) {
        String query = "Select * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = " + "'" + name + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Menu_Com menu_com = new Menu_Com();
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            menu_com.setName(cursor.getString(0));
            menu_com.setLast_execute(cursor.getLong(1));
            cursor.close();
        } else {
            menu_com = null;
        }
        db.close();
        return menu_com;
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

    public boolean updateData(String name, long last_executed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_NAME, name);
        args.put(COLUMN_LAST_EXECUTED, last_executed);
        return db.update(TABLE_NAME, args, COLUMN_NAME + "=" + name, null) > 0;
    }
    public boolean updateArrivedData(String name, long last_executed, int is_arrived) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_NAME, name);
        args.put(COLUMN_LAST_EXECUTED, last_executed);
        args.put(COLUMN_IS_ARRIVED, is_arrived);
        return db.update(TABLE_NAME, args, COLUMN_NAME + "=" + name, null) > 0;
    }
}
