package com.egco428.gradesimulator

/**
 * Created by lalita on 16/10/2017 AD.
 */
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MySQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(DATABASE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.w(MySQLiteHelper::class.java.name,
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data")
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECT)
        onCreate(db)
    }

    companion object {

        val TABLE_SUBJECT = "courseForEachSemesters"
        val COLUMN_ID = "_id"
        val COLUMN_courseNO = "courseNo"
        val COLUMN_name = "name"
        val COLUMN_category = "category"
        val COLUMN_credit = "credit"
        val COLUMN_categoryName = "categoryName"
        val COLUMN_groupId = "groupId"
        val COLUMN_grade = "grade"

        private val DATABASE_NAME = "courseForEachSemester.db"
        private val DATABASE_VERSION = 1

        // Database creation sql statement
        private val DATABASE_CREATE = ("create table "
                + TABLE_SUBJECT + "(" + COLUMN_ID
                + " integer primary key autoincrement, "
                + COLUMN_courseNO + " text not null,"
                + COLUMN_name +" text not null,"
                + COLUMN_category + " integer not null,"
                + COLUMN_credit + " integer not null,"
                + COLUMN_categoryName + " text not null,"
                + COLUMN_groupId + " integer not null,"
                + COLUMN_grade + " text not null);")
    }

}