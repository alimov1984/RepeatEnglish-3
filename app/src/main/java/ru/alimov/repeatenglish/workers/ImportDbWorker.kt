package ru.alimov.repeatenglish.workers

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.alimov.repeatenglish.model.Word
import ru.alimov.repeatenglish.service.ServiceSupplier.getWordService
import ru.alimov.repeatenglish.service.WordService
import ru.alimov.repeatenglish.util.constant.IMPORT_DB_FILE_PATH
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.time.Instant
import ru.alimov.repeatenglish.R
import ru.alimov.repeatenglish.util.workerUtils.WorkerUtils

/**
 * Worker is used for import from file to db.
 */
class ImportDbWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private val wordService: WordService = getWordService(context)

    companion object {
        private val TAG = ExportDbWorker::class.java.simpleName
    }

    override fun doWork(): Result {
        val context = applicationContext
        var filePath = inputData.getString(IMPORT_DB_FILE_PATH)

        val split = filePath!!.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val externalStorage = Environment.getExternalStorageDirectory()
        filePath = externalStorage.path + "/" + split[1]

        var fileInputStream: FileInputStream? = null
        var inputStreamReader: InputStreamReader? = null
        try {
            fileInputStream = FileInputStream(filePath)
            inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            wordService.clearWordTable()
            //Read caption line from the file.
            var line = bufferedReader.readLine()
            while (bufferedReader.readLine().also { line = it } != null) {
                val wordAtr = line.split(",".toRegex(), limit = 10).toTypedArray()
                var word: Word? = null
                val wordOriginal = wordAtr[1]
                val wordTranslated = wordAtr[2]
                val dateCreated = Instant.parse(wordAtr[3])
                val dateUpdated = Instant.parse(wordAtr[4])
                var dateShowed: Instant? = null
                if (wordAtr[5] != "0") {
                    dateShowed = Instant.parse(wordAtr[5])
                }
                val addCounter = wordAtr[6].toLong()
                val correctCheckCounter = wordAtr[7].toLong()
                val incorrectCheckCounter = wordAtr[8].toLong()
                val rating = wordAtr[9].toLong()
                word = Word(
                    wordOriginal, wordTranslated, dateCreated, dateUpdated, dateShowed,
                    addCounter, correctCheckCounter, incorrectCheckCounter, rating
                )
                if (word != null) {
                    wordService.insertWord(word)
                }
            }
            inputStreamReader.close()
            fileInputStream.close()
            val notifyMessage = String.format(
                "Данные успешно импортированы из файла %s",
                filePath
            )
            WorkerUtils.makeStatusNotification(
                context.resources.getString(R.string.app_name),
                notifyMessage,
                context
            )
            return Result.success()
        } catch (ex: Exception) {
            Log.e(TAG, ex.message, ex)
            return Result.failure()
        } finally {
            try {
                fileInputStream?.close()
                inputStreamReader?.close()
            } catch (ex2: IOException) {
                Log.e(TAG, ex2.message, ex2)
            }
        }
    }

}
