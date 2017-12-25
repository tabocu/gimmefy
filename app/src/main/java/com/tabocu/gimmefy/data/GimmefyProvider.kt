package com.tabocu.gimmefy.data

import android.annotation.TargetApi
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.database.sqlite.SQLiteQueryBuilder

import com.tabocu.gimmefy.data.GimmefyContract.ExerciseEntry;
import com.tabocu.gimmefy.data.GimmefyContract.ActivityEntry;

class GimmefyProvider : ContentProvider() {
    companion object {
        const val EXERCISE = 100
        const val EXERCISE_ID = 101
        const val ACTIVITY = 200
        const val ACTIVITY_ID = 201
        const val ACTIVITY_EXERCISE = 202
        const val ACTIVITY_ID_EXERCISE = 203

        private val sExerciseIdSelection = "${ExerciseEntry.TABLE_NAME}.${ExerciseEntry._ID} = ?"
        private val sActivityIdSelection = "${ActivityEntry.TABLE_NAME}.${ActivityEntry._ID} = ?"
        private val sActivityWithExercise = SQLiteQueryBuilder()
        private val sUriMatcher = uriMatcher()

        fun uriMatcher(): UriMatcher {
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = GimmefyContract.CONTENT_AUTHORITY

            matcher.addURI(authority, GimmefyContract.PATH_EXERCISE, EXERCISE)
            matcher.addURI(authority, GimmefyContract.PATH_ACTIVITY, ACTIVITY)
            matcher.addURI(authority, "${GimmefyContract.PATH_EXERCISE}/#", EXERCISE_ID)
            matcher.addURI(authority, "${GimmefyContract.PATH_ACTIVITY}/#", ACTIVITY_ID)
            matcher.addURI(authority,
                    "${GimmefyContract.PATH_ACTIVITY}/${GimmefyContract.PATH_EXERCISE}",
                    ACTIVITY_EXERCISE)
            matcher.addURI(authority,
                    "${GimmefyContract.PATH_ACTIVITY}/${GimmefyContract.PATH_EXERCISE}/#",
                    ACTIVITY_ID_EXERCISE)
            return matcher
        }
    }

    init {
        sActivityWithExercise.tables =
                "${ActivityEntry.TABLE_NAME} LEFT JOIN ${ExerciseEntry.TABLE_NAME} " +
                        "ON ${ActivityEntry.TABLE_NAME}.${ActivityEntry.COLUMN_EXERCISE_ID} "
                        "= ${ExerciseEntry.TABLE_NAME}.${ExerciseEntry._ID}"
    }

    private lateinit var mGimmefyDbHelper: GimmefyDbHelper

    private fun getExerciseById(uri: Uri, projection: Array<out String>?): Cursor {
        val id = GimmefyContract.getIdFromUri(uri)
        return  mGimmefyDbHelper.readableDatabase.query(
            ExerciseEntry.TABLE_NAME,
            projection,
                sExerciseIdSelection,
                arrayOf("$id"),
            null,
            null,
            null)
    }

    private fun updateExerciseById(uri: Uri, values: ContentValues?): Int {
        val id = GimmefyContract.getIdFromUri(uri)
        return  mGimmefyDbHelper.readableDatabase.update(
                ExerciseEntry.TABLE_NAME,
                values,
                sExerciseIdSelection,
                arrayOf("$id"))
    }

    private fun deleteExerciseById(uri: Uri): Int {
        val id = GimmefyContract.getIdFromUri(uri)
        return  mGimmefyDbHelper.readableDatabase.delete(
                ExerciseEntry.TABLE_NAME,
                sExerciseIdSelection,
                arrayOf("$id"))
    }

    private fun getActivityById(uri: Uri, projection: Array<out String>?): Cursor {
        val id = GimmefyContract.getIdFromUri(uri)
        return  mGimmefyDbHelper.readableDatabase.query(
                ActivityEntry.TABLE_NAME,
                projection,
                sActivityIdSelection,
                arrayOf("$id"),
                null,
                null,
                null)
    }

    private fun updateActivityById(uri: Uri, values: ContentValues?): Int {
        val id = GimmefyContract.getIdFromUri(uri)
        return  mGimmefyDbHelper.readableDatabase.update(
                ActivityEntry.TABLE_NAME,
                values,
                sActivityIdSelection,
                arrayOf("$id"))
    }

    private fun deleteActivityById(uri: Uri): Int {
        val id = GimmefyContract.getIdFromUri(uri)
        return  mGimmefyDbHelper.readableDatabase.delete(
                ActivityEntry.TABLE_NAME,
                sActivityIdSelection,
                arrayOf("$id"))
    }

    private fun getActivityWithExerciseById(uri: Uri, projection: Array<out String>?): Cursor {
        val id = GimmefyContract.getIdFromUri(uri)
        return sActivityWithExercise.query(
                mGimmefyDbHelper.readableDatabase,
                projection,
                sExerciseIdSelection,
                arrayOf("$id"),
                null,
                null,
                null)
    }

    override fun onCreate(): Boolean {
        mGimmefyDbHelper = GimmefyDbHelper(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        return when(sUriMatcher.match(uri)) {
            EXERCISE -> mGimmefyDbHelper.readableDatabase.query(
                    ExerciseEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder)
            EXERCISE_ID -> getExerciseById(uri, projection)
            ACTIVITY -> mGimmefyDbHelper.readableDatabase.query(
                    ActivityEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder)
            ACTIVITY_ID -> getActivityById(uri, projection)
            ACTIVITY_EXERCISE -> return sActivityWithExercise.query(
                    mGimmefyDbHelper.readableDatabase,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder)
            ACTIVITY_ID_EXERCISE -> getActivityWithExerciseById(uri, projection)
            else -> throw UnsupportedOperationException("Unknown uri: $uri");
        }
    }

    override fun insert(uri: Uri, values: ContentValues): Uri {
        val db = mGimmefyDbHelper.writableDatabase
        val returnUri = when(sUriMatcher.match(uri)) {
            EXERCISE -> {
                val id = db.insert(ExerciseEntry.TABLE_NAME, null, values)
                if (id > 0) ExerciseEntry.buildExerciseUri(id);
                else throw android.database.SQLException("Failed to insert row into $uri")
            }
            ACTIVITY -> {
                val id = db.insert(ActivityEntry.TABLE_NAME, null, values)
                if (id > 0) ActivityEntry.buildActivityUri(id)
                else throw android.database.SQLException("Failed to insert row into $uri")
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        context.contentResolver.notifyChange(uri, null)
        return returnUri
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<out String>?): Int {
        val db = mGimmefyDbHelper.writableDatabase
        val rowsUpdated = when(sUriMatcher.match(uri)) {
            EXERCISE -> db.update(ExerciseEntry.TABLE_NAME, values, selection, selectionArgs)
            EXERCISE_ID -> updateExerciseById(uri, values)
            ACTIVITY -> db.update(ActivityEntry.TABLE_NAME, values, selection, selectionArgs)
            ACTIVITY_ID -> updateActivityById(uri, values)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        if (rowsUpdated != 0) context.contentResolver.notifyChange(uri, null)
        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = mGimmefyDbHelper.writableDatabase
        val selection = selection ?: "1"
        val rowsDeleted = when(sUriMatcher.match(uri)) {
            EXERCISE -> db.delete(ExerciseEntry.TABLE_NAME, selection, selectionArgs)
            EXERCISE_ID -> deleteExerciseById(uri)
            ACTIVITY -> db.delete(ActivityEntry.TABLE_NAME, selection, selectionArgs)
            ACTIVITY_ID -> deleteActivityById(uri)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        if (rowsDeleted != 0) context.contentResolver.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun getType(uri: Uri): String {
        return when(sUriMatcher.match(uri)) {
            EXERCISE -> ExerciseEntry.CONTENT_TYPE
            EXERCISE_ID -> ExerciseEntry.CONTENT_ITEM_TYPE
            ACTIVITY -> ActivityEntry.CONTENT_TYPE
            ACTIVITY_ID -> ActivityEntry.CONTENT_ITEM_TYPE
            ACTIVITY_EXERCISE -> ActivityEntry.CONTENT_TYPE
            ACTIVITY_ID_EXERCISE -> ActivityEntry.CONTENT_ITEM_TYPE
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
    }

    @TargetApi(11)
    override fun shutdown() {
        mGimmefyDbHelper.close()
        super.shutdown()
    }
}