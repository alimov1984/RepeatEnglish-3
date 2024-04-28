package ru.alimov.repeatenglish.workers

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.alimov.repeatenglish.service.ServiceSupplier.getWordService
import ru.alimov.repeatenglish.service.WordService
import ru.alimov.repeatenglish.util.workerUtils.WorkerUtils
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.time.LocalDateTime
import ru.alimov.repeatenglish.R
import ru.alimov.repeatenglish.util.constant.EXPORT_DB_FILE_PATH

/**
 * Worker is used to export db to file.
 */
class ExportDbWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private val wordService: WordService

    init {
        wordService = getWordService(context)
    }

    companion object {
        private val TAG = ExportDbWorker::class.java.simpleName
    }

    override fun doWork(): Result {
        val context = applicationContext
        val wordList = wordService.getWordsForExport()
        val fileName = String.format(
            "repeatEnglish_%s_%s_%s_%s_%s_%s.csv",
            LocalDateTime.now().dayOfMonth,
            LocalDateTime.now().monthValue,
            LocalDateTime.now().year,
            LocalDateTime.now().hour,
            LocalDateTime.now().minute,
            LocalDateTime.now().second
        )
        val path = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!.path
        val filePath = "$path/$fileName"
        var fileOutputStream: FileOutputStream? = null
        var outputStreamWriter: OutputStreamWriter? = null
        try {
            fileOutputStream = FileOutputStream(filePath)
            outputStreamWriter = OutputStreamWriter(fileOutputStream)
            outputStreamWriter.write("Id,WordOriginal,WordTranslated,DateCreated,DateUpdated,DateShowed,AddCounter,CorrectCheckCounter,IncorrectCheckCounter,Rating\n")
            for (word in wordList) {
                outputStreamWriter.write(
                    String.format(
                        "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                        word.id,
                        word.wordOriginal,
                        word.wordTranslated,
                        word.dateCreated.toString(),
                        word.dateUpdated.toString(),
                        if (word.dateShowed != null) word.dateShowed.toString() else "0",
                        word.addCounter,
                        word.correctCheckCounter,
                        word.incorrectCheckCounter,
                        word.rating
                    )
                )
            }
            outputStreamWriter.close()
            val notifyMessage = String.format(
                "Данные успешно экспортированы в файл %s",
                filePath
            )
            WorkerUtils.makeStatusNotification(
                context.resources.getString(R.string.app_name),
                notifyMessage,
                context
            )
            val outputData = Data.Builder()
                .putString(EXPORT_DB_FILE_PATH, filePath)
                .build()
            fileOutputStream.close()
            return Result.success(outputData)
        } catch (ex: Exception) {
            Log.e(TAG, ex.message, ex)
            return Result.failure()
        } finally {
            try {
                fileOutputStream?.close()
                outputStreamWriter?.close()
            } catch (ex2: IOException) {
                Log.e(TAG, ex2.message, ex2)
            }
        }
    }

}