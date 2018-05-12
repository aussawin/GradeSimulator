package com.egco428.gradesimulator

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.sql.SQLException

/**
 * Created by Onewdivide on 5/12/2560.
 */
class CourseDataSource(context: Context) {

    //Database Fields
    private var database: SQLiteDatabase? = null
    private val dbHelper: MySQLiteHelper = MySQLiteHelper(context)
    private val allColumns = arrayOf(MySQLiteHelper.COLUMN_ID,
                                     MySQLiteHelper.COLUMN_courseNO,
                                     MySQLiteHelper.COLUMN_name,
                                     MySQLiteHelper.COLUMN_category,
                                     MySQLiteHelper.COLUMN_credit,
                                     MySQLiteHelper.COLUMN_categoryName,
                                     MySQLiteHelper.COLUMN_groupId,
                                     MySQLiteHelper.COLUMN_grade)

    fun readAllSQLData(): List<CourseLocalDatabase> {
        val subjects = ArrayList<CourseLocalDatabase>()

        val cursor = database!!.query(MySQLiteHelper.TABLE_SUBJECT,
                allColumns, null, null, null, null, null)

        cursor.moveToFirst()
        while (!cursor.isAfterLast){
            val subject = cursorToSubject(cursor)
            subjects.add(subject)
            cursor.moveToNext()
        }
        cursor.close()
        return subjects
    }

    fun readSQLDataWithPosition(position: Int): List<CourseLocalDatabase> {
        val subjects = ArrayList<CourseLocalDatabase>()

        val cursor = database!!.query(MySQLiteHelper.TABLE_SUBJECT,
                allColumns, MySQLiteHelper.COLUMN_groupId+"=" + position, null, null, null, null)

        cursor.moveToFirst()
        while (!cursor.isAfterLast){
            val subject = cursorToSubject(cursor)
            subjects.add(subject)
            cursor.moveToNext()
        }
        cursor.close()
        return subjects
    }

    @Throws(SQLException::class)
    fun open(){
        database = dbHelper.writableDatabase
    }
    fun close(){
        dbHelper.close()
    }

    fun createSubject(courseNoInput: String,
                      nameInput: String,
                      categoryInput: Int,
                      creditInput: Int,
                      categoryNameInput: String,
                      groupIdInput: Int,
                      gradeInput: String): CourseLocalDatabase {

        val values = ContentValues()

        values.put(MySQLiteHelper.COLUMN_courseNO, courseNoInput)
        values.put(MySQLiteHelper.COLUMN_name, nameInput)
        values.put(MySQLiteHelper.COLUMN_category,categoryInput)
        values.put(MySQLiteHelper.COLUMN_credit,creditInput)
        values.put(MySQLiteHelper.COLUMN_categoryName, categoryNameInput)
        values.put(MySQLiteHelper.COLUMN_groupId,groupIdInput)
        values.put(MySQLiteHelper.COLUMN_grade,gradeInput)

        val insertId = database!!.insert(MySQLiteHelper.TABLE_SUBJECT, null, values)
        val cursor = database!!.query(MySQLiteHelper.TABLE_SUBJECT,
                allColumns, MySQLiteHelper.COLUMN_ID+"="+insertId,
                null ,null, null, null)

        cursor.moveToFirst()
        val newSubject = cursorToSubject(cursor)

        cursor.close()
        return newSubject
    }

    fun deleteAllData() {
        database!!.delete(MySQLiteHelper.TABLE_SUBJECT,null,null)
    }

    fun deleteAllSubjectInPosition(valPosition: Int) {
        database!!.delete(MySQLiteHelper.TABLE_SUBJECT,MySQLiteHelper.COLUMN_groupId + "=" + valPosition,null)
    }

    private fun cursorToSubject(cursor: Cursor): CourseLocalDatabase{
        val courseLocalDatabase = CourseLocalDatabase()
        courseLocalDatabase.id = cursor.getLong(0)
        courseLocalDatabase.courseNo = cursor.getString(1)
        courseLocalDatabase.name = cursor.getString(2)
        courseLocalDatabase.category = cursor.getInt(3)
        courseLocalDatabase.credit = cursor.getInt(4)
        courseLocalDatabase.categoryName = cursor.getString(5)
        courseLocalDatabase.groupId = cursor.getInt(6)
        courseLocalDatabase.grade = cursor.getString(7)
        return courseLocalDatabase
    }

}