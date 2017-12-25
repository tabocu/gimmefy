package com.tabocu.gimmefy.data

import android.net.Uri
import android.provider.BaseColumns
import android.content.ContentResolver
import android.content.ContentUris

class GimmefyContract {
    companion object {
        const val CONTENT_AUTHORITY = "com.tabocu.gimmefy"
        val BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY)

        const val PATH_EXERCISE = "exercise"
        const val PATH_ACTIVITY = "activity"
        const val PATH_CARD     = "card"
        const val PATH_TRAINING = "training"

        fun getIdFromUri(uri: Uri): Long {
            return uri.lastPathSegment.toLong()
        }
    }

    class ExerciseEntry {
        companion object {
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EXERCISE).build();

            val CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXERCISE
            val CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXERCISE

            val TABLE_NAME = "exercise"

            val _ID = BaseColumns._ID
            val COLUMN_NAME = "exercise_name"

            fun buildExerciseUri(id: Long?): Uri {
                val uriBuilder = CONTENT_URI.buildUpon()
                if(id != null) uriBuilder.appendPath(id.toString())
                return uriBuilder.build()
            }
        }
    }

    class ActivityEntry {
        companion object {
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACTIVITY).build();

            val CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACTIVITY
            val CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ACTIVITY

            val TABLE_NAME = "activity"

            val _ID = BaseColumns._ID

            val COLUMN_EXERCISE_ID = "exercise_id"

            val COLUMN_SERIES = "activity_series"
            val COLUMN_CYCLES = "activity_cycles"
            val COLUMN_LOAD   = "activity_load"

            fun buildActivityUri(id: Long?, includeExercises: Boolean = false): Uri {
                val uriBuilder = CONTENT_URI.buildUpon()
                if (includeExercises) uriBuilder.appendPath(PATH_EXERCISE)
                if(id != null) uriBuilder.appendPath(id.toString())
                return uriBuilder.build()
            }
        }
    }

    class CardEntry {
        companion object {
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CARD).build();

            const val CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARD
            const val CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CARD

            const val TABLE_NAME = "card"

            fun buildCardUri(id: Long): Uri {
                return ContentUris.withAppendedId(CONTENT_URI, id)
            }
        }
    }

    class TrainingEntry {
        companion object {
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAINING).build();

            const val CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAINING
            const val CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAINING

            const val TABLE_NAME = "training"

            val _ID = BaseColumns._ID
            val COLUMN_NAME = "training_name"

            fun buildTrainingUri(id: Long): Uri {
                return ContentUris.withAppendedId(CONTENT_URI, id)
            }
        }
    }
}