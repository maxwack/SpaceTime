package com.mobile.hinde.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.util.ArrayList
import java.util.HashMap

class DBHandler(val context: Context):SQLiteOpenHelper(context, DATABASE_NAME,null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1;
        private const val DATABASE_NAME = "SpaceTime.db"
        private const val TABLE_NAME_COM = "Menu_Com"
        private const val TABLE_NAME_SET = "App_Settings"
        private const val COLUMN_PROPERTY = "property"
        private const val COLUMN_VALUE = "value"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_LAST_EXECUTED = "last_executed"
        private const val COLUMN_EXPECTED_END = "expected_end"
        private const val COLUMN_IS_ARRIVED = "is_arrived"
        private const val COLUMN_IS_RETURNED = "is_returned"
    }

    init{
        writableDatabase
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableCom = ("CREATE TABLE $TABLE_NAME_COM ($COLUMN_NAME " +
                "TEXT PRIMARY KEY,$COLUMN_LAST_EXECUTED LONG, $COLUMN_EXPECTED_END LONG, "
                + "$COLUMN_IS_ARRIVED INTEGER, $COLUMN_IS_RETURNED INTEGER )")
        val createTableSet = ("CREATE TABLE $TABLE_NAME_SET ($COLUMN_PROPERTY TEXT PRIMARY KEY,"
                + "$COLUMN_VALUE TEXT)")
        db!!.execSQL(createTableCom)
        db!!.execSQL(createTableSet)

        initDB(db)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        val deleteTableCom = "DROP TABLE IF EXISTS $TABLE_NAME_COM"
        val deleteTableSet = "DROP TABLE IF EXISTS $TABLE_NAME_SET"
        db.execSQL(deleteTableCom)
        db.execSQL(deleteTableSet)
        onCreate(db)
    }


    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    private fun initDB(db: SQLiteDatabase) {
        var values = ContentValues()
        values.put(COLUMN_NAME, "SUN")
        values.put(COLUMN_LAST_EXECUTED, "")
        values.put(COLUMN_EXPECTED_END, 0)
        values.put(COLUMN_IS_ARRIVED, 0)
        values.put(COLUMN_IS_RETURNED, 0)
        db.insert(TABLE_NAME_COM, null, values)
        values = ContentValues()
        values.put(COLUMN_NAME, "MOON")
        values.put(COLUMN_LAST_EXECUTED, "")
        values.put(COLUMN_EXPECTED_END, 0)
        values.put(COLUMN_IS_ARRIVED, 0)
        values.put(COLUMN_IS_RETURNED, 0)
        db.insert(TABLE_NAME_COM, null, values)
        values = ContentValues()
        values.put(COLUMN_NAME, "VOYAGER1")
        values.put(COLUMN_LAST_EXECUTED, "")
        values.put(COLUMN_EXPECTED_END, 0)
        values.put(COLUMN_IS_ARRIVED, 0)
        values.put(COLUMN_IS_RETURNED, 0)
        db.insert(TABLE_NAME_COM, null, values)
        values = ContentValues()
        values.put(COLUMN_NAME, "INSIGHT")
        values.put(COLUMN_LAST_EXECUTED, "")
        values.put(COLUMN_EXPECTED_END, 0)
        values.put(COLUMN_IS_ARRIVED, 0)
        values.put(COLUMN_IS_RETURNED, 0)
        db.insert(TABLE_NAME_COM, null, values)
        values = ContentValues()
        values.put(COLUMN_PROPERTY, "user_id")
        values.put(COLUMN_VALUE, "")
        db.insert(TABLE_NAME_SET, null, values)
    }

    fun getUserId(): AppSettings? {
        val db = this.readableDatabase

        val selection = "$COLUMN_PROPERTY = ?"
        val selectionArgs = arrayOf("user_id")
        val cursor = db.query(
                TABLE_NAME_SET,
                null,
                selection,
                selectionArgs,
                null, null, null
        )
        var setting: AppSettings? = null
        if (cursor.moveToFirst()) {
            setting = AppSettings("user_id", cursor.getString(1))
            cursor.close()
        }

        db.close()
        return setting
    }

    fun registerUser(user_ID: String): Boolean {
        val db = this.writableDatabase
        val args = ContentValues()
        args.put(COLUMN_VALUE, user_ID)

        // Which row to update, based on the title
        val selection = "$COLUMN_PROPERTY = ?"
        val selectionArgs = arrayOf("user_id")
        return db.update(TABLE_NAME_SET, args, selection, selectionArgs) > 0
    }

    fun searchEndedData(endTime: Long): List<String> {
        val db = this.readableDatabase
        val selectCol = arrayOf(COLUMN_NAME)

        val selection = "$COLUMN_EXPECTED_END < ? AND $COLUMN_EXPECTED_END > 0 "
        val selectionArgs = arrayOf(endTime.toString())
        val cursor = db.query(
                TABLE_NAME_COM,
                selectCol,
                selection,
                selectionArgs,
                null
                , null, null
        )

        val targetList = ArrayList<String>()
        while (cursor.moveToNext()) {
            targetList.add(cursor.getString(0))
        }
        cursor.close()
        db.close()
        return targetList
    }

    fun searchStartedData(target: String): Map<String, Long> {
        val db = this.readableDatabase

        val selectCol = arrayOf(COLUMN_NAME, COLUMN_EXPECTED_END)

        val selection = "$COLUMN_EXPECTED_END > 0 AND $COLUMN_NAME = ? "
        val selectionArgs = arrayOf(target)
        val cursor = db.query(
                TABLE_NAME_COM,
                selectCol,
                selection,
                selectionArgs,
                null
                , null, null
        )

        val targetMap = HashMap<String, Long>()
        while (cursor.moveToNext()) {
            targetMap[cursor.getString(0)] = cursor.getLong(1)
        }
        cursor.close()
        db.close()
        return targetMap
    }

    fun updateData(name: String, last_executed: Long, expected_end: Long): Boolean {
        val db = this.writableDatabase
        val args = ContentValues()
        args.put(COLUMN_LAST_EXECUTED, last_executed)
        args.put(COLUMN_EXPECTED_END, expected_end)

        // Which row to update, based on the title
        val selection = "$COLUMN_NAME = ?"
        val selectionArgs = arrayOf(name)
        return db.update(TABLE_NAME_COM, args, selection, selectionArgs) > 0
    }

    fun resetExpectedEnd(name: String): Boolean {
        val db = this.writableDatabase
        val args = ContentValues()
        args.put(COLUMN_EXPECTED_END, 0)

        // Which row to update, based on the title
        val selection = "$COLUMN_NAME = ?"
        val selectionArgs = arrayOf(name)
        return db.update(TABLE_NAME_COM, args, selection, selectionArgs) > 0
    }
}