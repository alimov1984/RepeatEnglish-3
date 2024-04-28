package ru.alimov.repeatenglish.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/**
 * DB(under repository) implementation.
 */
class Database(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, SCHEMA_VERSION) {
    companion object {
        private const val DATABASE_NAME = "repeatEnglish.db"
        private const val SCHEMA_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val script =
            (""""CREATE TABLE [word_dictionary] ( id INTEGER PRIMARY KEY, word_original TEXT NOT NULL,
                    word_translated TEXT NOT NULL, dateCreated INTEGER NOT NULL, dateUpdated INTEGER NOT NULL,
                    dateShowed INTEGER, add_counter INTEGER NOT NULL, correct_check_counter INTEGER NOT NULL,
                    incorrect_check_counter INTEGER NOT NULL, rating INTEGER NOT NULL);"""")
        db.execSQL(script)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun <T> getObject(
        sql: String,
        constructor: (Cursor) -> T,
        sqlParams: Array<String?>?
    ): T? {
        var dbObject: T? = null
        var dbCursor: Cursor? = null
        try {
            val database = this.readableDatabase
            dbCursor = database.rawQuery(sql, sqlParams)
            if (dbCursor.moveToFirst()) {
                dbObject = constructor(dbCursor)
            }
        } catch (ex: Exception) {
            Log.e("Database", ex.message, ex)
        } finally {
            dbCursor?.close()
            super.close()
        }
        return dbObject
    }

    fun <T> getObjectList(
        sql: String,
        constructor: (Cursor) -> T,
        sqlParams: Array<String?>?
    ): List<T> {
        var dbCursor: Cursor? = null
        var resultList: MutableList<T> = ArrayList()
        try {
            val database = this.readableDatabase
            dbCursor = database.rawQuery(sql, sqlParams)
            while (dbCursor.moveToNext()) {
                resultList!!.add(constructor(dbCursor))
            }
        } catch (ex: Exception) {
            Log.e("Database", ex.message, ex)
        } finally {
            dbCursor?.close()
            super.close()
        }
        return resultList
    }

    fun insertRow(tableName: String, values: ContentValues): Long {
        var resultRowId: Long = 0
        try {
            val database = this.writableDatabase
            resultRowId = database.insert(tableName, null, values)
        } catch (ex: Exception) {
            Log.e("Database", ex.message, ex)
        } finally {
            super.close()
        }
        return resultRowId
    }

    fun updateRow(
        tableName: String,
        idColumn: String,
        idColumnValue: Long,
        values: ContentValues?
    ): Int {
        val whereClause = "$idColumn = ?"
        val whereArgs: Array<String> = arrayOf(idColumnValue.toString())
        var affectedRows: Int = 0
        try {
            val database = this.writableDatabase
            affectedRows = database.update(tableName, values, whereClause, whereArgs)
        } catch (ex: Exception) {
            Log.e("Database", ex.message, ex)
        } finally {
            super.close()
        }
        return affectedRows
    }

    fun deleteRow(tableName: String, idColumn: String, idColumnValue: Long): Int {
        var rawsAffected = 0
        val whereClause = "$idColumn = ?"
        val whereArgs: Array<String> = arrayOf(idColumnValue.toString())
        try {
            val database = this.writableDatabase
            rawsAffected = database.delete(tableName, whereClause, whereArgs)
        } catch (ex: Exception) {
            Log.e("Database", ex.message, ex)
        } finally {
            super.close()
        }
        return rawsAffected
    }

    fun deleteAllRows(tableName: String): Int {
        var rowsAffected = 0
        try {
            val database = this.writableDatabase
            rowsAffected = database.delete(tableName, "1", null)
        } catch (ex: Exception) {
            Log.e("Database", ex.message, ex)
        } finally {
            super.close()
        }
        return rowsAffected
    }

    fun executeQuery(sql: String, args: Array<Any?>?) {
        try {
            val database = this.writableDatabase
            database.execSQL(sql, args)
        } catch (ex: Exception) {
            Log.e("Database", ex.message, ex)
        } finally {
            super.close()
        }
    }
}