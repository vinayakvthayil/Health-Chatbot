package com.example.healthi.ui.profile

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_MOOD_ENTRIES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOOD_ENTRIES)
        onCreate(db)
    }

    // Mood Entry Operations
    fun insertMoodEntry(moodEntry: MoodEntry): Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_MOOD_VALUE, moodEntry.getMoodValue())
        values.put(COLUMN_MOOD_NOTE, moodEntry.getMoodNote())
        val id = db.insert(TABLE_MOOD_ENTRIES, null, values)
        db.close()
        return id
    }

    val allMoodEntries: List<Any>
        get() {
            val moodEntries: MutableList<MoodEntry> = ArrayList<MoodEntry>()
            val selectQuery = "SELECT * FROM " + TABLE_MOOD_ENTRIES +
                    " ORDER BY " + COLUMN_TIMESTAMP + " DESC"
            val db = this.readableDatabase
            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val moodEntry = MoodEntry()
                    moodEntry.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)))
                    moodEntry.setMoodValue(cursor.getInt(cursor.getColumnIndex(COLUMN_MOOD_VALUE)))
                    moodEntry.setMoodNote(cursor.getString(cursor.getColumnIndex(COLUMN_MOOD_NOTE)))
                    moodEntry.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)))
                    moodEntries.add(moodEntry)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return moodEntries
        }

    fun getMoodEntryById(id: Long): MoodEntry? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_MOOD_ENTRIES, null,
            COLUMN_ID + "=?", arrayOf(id.toString()),
            null, null, null
        )
        var moodEntry: MoodEntry? = null
        if (cursor.moveToFirst()) {
            moodEntry = MoodEntry()
            moodEntry.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)))
            moodEntry.setMoodValue(cursor.getInt(cursor.getColumnIndex(COLUMN_MOOD_VALUE)))
            moodEntry.setMoodNote(cursor.getString(cursor.getColumnIndex(COLUMN_MOOD_NOTE)))
            moodEntry.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)))
        }
        cursor.close()
        db.close()
        return moodEntry
    }

    fun deleteMoodEntry(id: Long): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_MOOD_ENTRIES, COLUMN_ID + "=?", arrayOf(id.toString()))
        db.close()
        return result
    }

    companion object {
        private const val DATABASE_NAME = "chatbot.db"
        private const val DATABASE_VERSION = 1

        // Table Names
        const val TABLE_MESSAGES = "messages"
        const val TABLE_MOOD_ENTRIES = "mood_entries"
        const val TABLE_USER_PREFERENCES = "user_preferences"

        // Common column names
        const val COLUMN_ID = "id"
        const val COLUMN_TIMESTAMP = "timestamp"

        // Messages Table columns
        const val COLUMN_MESSAGE_TEXT = "message_text"
        const val COLUMN_IS_USER = "is_user"

        // Mood Entries Table columns
        const val COLUMN_MOOD_VALUE = "mood_value"
        const val COLUMN_MOOD_NOTE = "mood_note"

        // Create table statements
        private const val CREATE_MOOD_ENTRIES_TABLE = ("CREATE TABLE " + TABLE_MOOD_ENTRIES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MOOD_VALUE + " INTEGER NOT NULL,"
                + COLUMN_MOOD_NOTE + " TEXT,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")")
    }
}