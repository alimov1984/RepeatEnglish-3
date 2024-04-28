package ru.alimov.repeatenglish.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import ru.alimov.repeatenglish.model.Word
import java.time.Instant
import java.util.concurrent.TimeUnit



/**
 * Word repository implementation.
 */
class WordRepository(_context: Context) {
    companion object {
        private const val DICTIONARY_TABLE_NAME = "word_dictionary"
        private const val PRIMARY_COLUMN_ID = "id"
    }

    private val database: Database = Database(_context)

    private fun wordConstructor(dbCursor: Cursor): Word {
        val id = dbCursor.getLong(0)
        val wordOriginal = dbCursor.getString(1)
        val wordTranslated = dbCursor.getString(2)
        Instant.ofEpochMilli(dbCursor.getLong(3))
        val dateCreated = Instant.now()
        val dateUpdated = Instant.ofEpochMilli(dbCursor.getLong(4))
        var dateShowed: Instant? = null
        if (!dbCursor.isNull(5)) {
            dateShowed = Instant.ofEpochMilli(dbCursor.getLong(5))
        }
        val addCounter = dbCursor.getLong(6)
        val correctCheckCounter = dbCursor.getLong(7)
        val incorrectCheckCounter = dbCursor.getLong(8)
        val rating = dbCursor.getLong(9)
        return Word(
            id, wordOriginal, wordTranslated, dateCreated, dateUpdated,
            dateShowed, addCounter, correctCheckCounter, incorrectCheckCounter, rating
        )
    }

    fun clearWordTable() {
        database.deleteAllRows(DICTIONARY_TABLE_NAME)
    }

    fun getWordById(id: Int): Word? {
        val sql =
            (""""SELECT id, word_original, word_translated, dateCreated, dateUpdated,
                    dateShowed, add_counter, correct_check_counter, incorrect_check_counter, rating
                    FROM [word_dictionary] WHERE rowid = ?""")
        return database.getObject<Word>(
            sql,
            this::wordConstructor, arrayOf(id.toString())
        )
    }

    fun getWordByOriginal(wordOriginal: String?): Word? {
        val sql =
            (""""SELECT id, word_original, word_translated, dateCreated, dateUpdated,
                    dateShowed, add_counter, correct_check_counter, incorrect_check_counter, rating
                     FROM [word_dictionary] WHERE word_original = ?"""")
        return database.getObject(sql, this::wordConstructor, arrayOf(wordOriginal))
    }

    fun insertWord(word: Word): Long {
        val cv = ContentValues()
        cv.put("word_original", word.wordOriginal.replace(",", ""))
        cv.put("word_translated", word.wordTranslated.replace(",", ""))
        cv.put("dateCreated", word.dateCreated.toEpochMilli())
        cv.put("dateUpdated", word.dateUpdated.toEpochMilli())
        cv.putNull("dateShowed")
        cv.put("add_counter", word.addCounter)
        cv.put("correct_check_counter", word.correctCheckCounter)
        cv.put("incorrect_check_counter", word.incorrectCheckCounter)
        cv.put("rating", word.rating)
        return database.insertRow(DICTIONARY_TABLE_NAME, cv)
    }

    fun updateWord(word: Word): Int {
        val cv = ContentValues()
        cv.put("word_original", word.wordOriginal.replace(",", ""))
        cv.put("word_translated", word.wordTranslated.replace(",", ""))
        cv.put("dateUpdated", word.dateUpdated.toEpochMilli())
        cv.put("add_counter", word.addCounter)
        cv.put("correct_check_counter", word.correctCheckCounter)
        cv.put("incorrect_check_counter", word.incorrectCheckCounter)
        cv.put("rating", word.rating)
        return database.updateRow(
            DICTIONARY_TABLE_NAME,
            PRIMARY_COLUMN_ID, word.id, cv
        )
    }

    fun getWordsForChecking(wordCount: Int): List<Word> {
        val sql =
            """SELECT id, word_original, word_translated, dateCreated, dateUpdated, dateShowed,
                add_counter, correct_check_counter, incorrect_check_counter, rating
                FROM [word_dictionary]   
                WHERE dateShowed IS NULL OR (CAST(? AS INTEGER) - dateShowed) > CAST(? AS INTEGER)
                ORDER BY rating DESC
                LIMIT ?"""
        val params = arrayOf<String?>(
            Instant.now().toEpochMilli().toString(),
            TimeUnit.DAYS.toMillis(1).toString(),
            wordCount.toString()
        )
        return database.getObjectList(sql, this::wordConstructor, params)
    }

    fun incrementWordCorrectCheckCounter(word: Word): Int {
        val cv = ContentValues()
        cv.put("correct_check_counter", word.correctCheckCounter)
        cv.put("rating", word.rating)
        return database.updateRow(DICTIONARY_TABLE_NAME, PRIMARY_COLUMN_ID, word.id, cv)
    }

    fun incrementWordIncorrectCheckCounter(word: Word): Int {
        val cv = ContentValues()
        cv.put("incorrect_check_counter", word.incorrectCheckCounter)
        cv.put("rating", word.rating)
        return database.updateRow(DICTIONARY_TABLE_NAME, PRIMARY_COLUMN_ID, word.id, cv)
    }

    fun updateWordDateShowed(word: Word): Int {
        val cv = ContentValues()
        cv.put("dateShowed", Instant.now().toEpochMilli())
        return database.updateRow(DICTIONARY_TABLE_NAME, PRIMARY_COLUMN_ID, word.id, cv)
    }

    fun getWordsForExport(): List<Word> {
        val sql =
            """SELECT id, word_original, word_translated, dateCreated, dateUpdated, dateShowed,
                add_counter, correct_check_counter, incorrect_check_counter, rating
                FROM [word_dictionary]
                ORDER BY id"""
        return database.getObjectList(sql, this::wordConstructor, null)
    }
}