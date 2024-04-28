package ru.alimov.repeatenglish.repository

import android.content.Context

/**
 * Factory for repositories.
 */
object RepositorySupplier {
    @Volatile
    private var wordRepository: WordRepository? = null

    fun getWordRepository(context: Context): WordRepository {
        if (wordRepository == null) {
            synchronized(this) {
                if (wordRepository == null) {
                    wordRepository = WordRepository(context)
                }
            }
        }
        return wordRepository!!
    }
}