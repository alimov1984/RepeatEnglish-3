package ru.alimov.repeatenglish.activity

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.work.*
import ru.alimov.repeatenglish.R
import ru.alimov.repeatenglish.util.constant.*
import ru.alimov.repeatenglish.workers.ExportDbWorker
import ru.alimov.repeatenglish.workers.ImportDbWorker

/**
 * Setting page.
 */
class SettingsActivity : AppCompatActivity() {
    private var settings: SharedPreferences? = null

    private var workManager: WorkManager? = null

    private var dbImportActivity: ActivityResultLauncher<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        workManager = WorkManager.getInstance(applicationContext)

        val wordCountEdit = findViewById<EditText>(R.id.wordCountEdit)

        settings = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE)
        //Get value of current word count for checking page.
        val currentWordCount = settings!!.getInt(SETTING_WORD_CHECKING_COUNT, 10)
        wordCountEdit.setText(currentWordCount.toString())

        registerHandlerForFilePicker()

        val myToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(myToolbar)
    }

    fun saveSettings(view: View?) {
        val wordCountEdit = findViewById<EditText>(R.id.wordCountEdit)
        val wordCountStr = wordCountEdit.text.toString().trim()
        if (wordCountStr.isEmpty()) {
            Toast.makeText(
                this,
                "Необходимо ввести значение в поле 'Количество слов при проверке(1-100)'",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val wordCount = wordCountStr.toInt()
        if (wordCount <= 0 || wordCount > 100) {
            Toast.makeText(
                this,
                "Необходимо ввести корректное значение в поле 'Количество слов при проверке(1-100)'",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val settingsEditor = settings!!.edit()
        settingsEditor.putInt(SETTING_WORD_CHECKING_COUNT, wordCount)
        settingsEditor.apply()
        Toast.makeText(
            this,
            "Настройки успешно сохранены",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun registerHandlerForFilePicker() {
        dbImportActivity = registerForActivityResult<String, Uri>(
            ActivityResultContracts.GetContent()
        ) { result ->
            if (result == null || result.path == null) {
                Toast.makeText(
                    this,
                    "File isn't selected",
                    Toast.LENGTH_SHORT
                ).show()
                return@registerForActivityResult
            }
            val constraints: Constraints = Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .build()
            val inputData = Data.Builder()
                .putString(IMPORT_DB_FILE_PATH, result.path)
                .build()

            //Execute import in separated thread.
            val myWorkRequest: WorkRequest = OneTimeWorkRequest.Builder(ImportDbWorker::class.java)
                .setConstraints(constraints)
                .addTag(IMPORT_DB_OUTPUT)
                .setInputData(inputData)
                .build()
            workManager!!.enqueue(myWorkRequest).result
        }
    }

    fun onExportBtnClick(view: View?) {
        val constraints: Constraints = Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .build()
        //Execute export db in separated thread.
        val myWorkRequest: WorkRequest = OneTimeWorkRequest.Builder(ExportDbWorker::class.java)
            .setConstraints(constraints)
            .addTag(EXPORT_DB_OUTPUT)
            .build()

        workManager!!.enqueue(myWorkRequest).result
    }

    fun onImportBtnClick(view: View?) {
        dbImportActivity!!.launch("*/*")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuItemId = item.itemId
        when (menuItemId) {
            R.id.menu_word_add -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_word_check -> {
                val intent2 = Intent(this, CheckActivity::class.java)
                startActivity(intent2)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}