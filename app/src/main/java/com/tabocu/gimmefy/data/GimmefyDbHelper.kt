package com.tabocu.gimmefy.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

import com.tabocu.gimmefy.data.GimmefyContract.ExerciseEntry;
import com.tabocu.gimmefy.data.GimmefyContract.ActivityEntry;

class GimmefyDbHelper(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "gimmefy.db"
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        val SQL_CREATE_EXERCISE_TABLE =
                "CREATE TABLE ${ExerciseEntry.TABLE_NAME} (" +

                        "${ExerciseEntry._ID} INTEGER PRIMARY KEY, " +
                        "${ExerciseEntry.COLUMN_NAME} TEXT NOT NULL);"

        val SQL_CREATE_ACTIVITY_TABLE =
                "CREATE TABLE ${ActivityEntry.TABLE_NAME} (" +
                        "${ActivityEntry._ID} INTEGER PRIMARY KEY, " +

                        "${ActivityEntry.COLUMN_EXERCISE_ID} INTEGER, " +
                        "${ActivityEntry.COLUMN_SERIES} INTEGER, " +
                        "${ActivityEntry.COLUMN_CYCLES} INTEGER, " +
                        "${ActivityEntry.COLUMN_LOAD} INTEGER, " +

                        "FOREIGN KEY (${ActivityEntry.COLUMN_EXERCISE_ID}) REFERENCES " +
                        "${ExerciseEntry.TABLE_NAME} (${ExerciseEntry._ID});"

        sqLiteDatabase.execSQL(SQL_CREATE_EXERCISE_TABLE)
        sqLiteDatabase.execSQL(SQL_CREATE_ACTIVITY_TABLE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ${ActivityEntry.TABLE_NAME}")
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ${ExerciseEntry.TABLE_NAME}")
        onCreate(sqLiteDatabase);
    }
}