package ru.alimov.repeatenglish.service

import android.content.Context

/**
 * Factory for services.
 */
object ServiceSupplier {
    @Volatile
    private var wordService: WordService? = null

    fun getWordService(context: Context): WordService {
        if (wordService == null) {
            synchronized(this) {
                if (wordService == null) {
                    wordService = WordServiceImpl(context)
                }
            }
        }
        return wordService!!
    }
}