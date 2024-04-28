package ru.alimov.repeatenglish.service

import ru.alimov.repeatenglish.model.Word

/**
 * Word service interface.
 */
interface WordService {
    fun insertWord(wordOriginal: String, wordTranslated: String): String

    fun getWordsForChecking(): List<Word>

    fun incrementIncorrectCheckCounter(wordOriginal: String): Boolean

    fun incrementCorrectCheckCounter(wordOriginal: String): Boolean

    fun updateDateShowed(wordOriginal: String): Boolean

    fun getWordsForExport(): List<Word>

    fun insertWord(word: Word): Long

    fun clearWordTable()
}