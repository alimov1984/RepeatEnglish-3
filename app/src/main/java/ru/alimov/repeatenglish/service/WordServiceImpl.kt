package ru.alimov.repeatenglish.service

import android.content.Context
import ru.alimov.repeatenglish.model.Word
import ru.alimov.repeatenglish.repository.RepositorySupplier
import ru.alimov.repeatenglish.repository.WordRepository
import ru.alimov.repeatenglish.util.constant.PREFERENCE_NAME
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime


class WordServiceImpl(_context: Context) : WordService {
    companion object {
        private const val DEFAULT_WORD_CHECKING_COUNT = 10
    }

    private val context: Context = _context
    private val wordRepository: WordRepository = RepositorySupplier.getWordRepository(context)

    private fun getRatingValue(word: Word): Long {
        var result: Long = 1000
        val k1 = word.addCounter
        val k2 = ((ZonedDateTime.now().year -
                word.dateUpdated.atZone(ZoneId.systemDefault()).year) * 12 +
                ZonedDateTime.now().monthValue -
                word.dateUpdated.atZone(ZoneId.systemDefault()).monthValue).toLong()
        val k3 = word.correctCheckCounter
        val k4 = word.incorrectCheckCounter
        val k5 = word.wordOriginal.length.toLong()
        result = result + k1 - k2 - k3 + k4 + k5
        return result
    }

    override fun insertWord(wordOriginal: String, wordTranslated: String): String {
        var result = ""
        val word = wordRepository.getWordByOriginal(wordOriginal)
        if (word != null) {
            word.dateUpdated = Instant.now()
            word.addCounter = word.addCounter + 1
            word.rating = getRatingValue(word)
            if (wordRepository.updateWord(word) > 0) {
                result = "Update word successfully"
            }
        } else {
            val now = Instant.now()
            val newWord = Word(
                wordOriginal,
                wordTranslated,
                now,
                now,
                null,
                0,
                0,
                0,
                0
            )
            newWord.rating = getRatingValue(newWord)
            val insertResult: Long = wordRepository.insertWord(newWord)
            if (insertResult > 0) {
                result = "Insert word successfully:"
            }
        }
        return result
    }

    override fun getWordsForChecking(): List<Word> {
        val settings =
            context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val wordCount =
            settings.getInt("word_checking_count", DEFAULT_WORD_CHECKING_COUNT)
        return wordRepository.getWordsForChecking(wordCount)
    }

    override fun incrementCorrectCheckCounter(wordOriginal: String): Boolean {
        val word = wordRepository.getWordByOriginal(wordOriginal)
        if (word != null) {
            word.correctCheckCounter = word.correctCheckCounter + 1
            word.rating = getRatingValue(word)
            if (wordRepository.incrementWordCorrectCheckCounter(word) > 0) {
                return true
            }
        }
        return false
    }

    override fun incrementIncorrectCheckCounter(wordOriginal: String): Boolean {
        val word = wordRepository.getWordByOriginal(wordOriginal)
        if (word != null) {
            word.incorrectCheckCounter = word.incorrectCheckCounter + 1
            word.rating = getRatingValue(word)
            if (wordRepository.incrementWordIncorrectCheckCounter(word) > 0) {
                return true
            }
        }
        return false
    }

    override fun updateDateShowed(wordOriginal: String): Boolean {
        val word = wordRepository.getWordByOriginal(wordOriginal)
        if (word != null) {
            if (wordRepository.updateWordDateShowed(word) > 0) {
                return true
            }
        }
        return false
    }

    override fun getWordsForExport(): List<Word> {
        return wordRepository.getWordsForExport()
    }

    override fun insertWord(word: Word): Long {
        return wordRepository.insertWord(word)
    }

    override fun clearWordTable() {
        wordRepository.clearWordTable()
    }
}