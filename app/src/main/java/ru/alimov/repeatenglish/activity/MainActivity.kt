package ru.alimov.repeatenglish.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ru.alimov.repeatenglish.R
import ru.alimov.repeatenglish.model.MainActivityUiState
import ru.alimov.repeatenglish.model.MainActivityViewModel
import ru.alimov.repeatenglish.service.ServiceSupplier
import ru.alimov.repeatenglish.service.WordService

/**
 * Main page is used for input new words.
 */
class MainActivity : AppCompatActivity() {
    private var wordService: WordService? = null

    private var uiModel: MainActivityViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen: SplashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }
        super.onCreate(savedInstanceState)
        wordService = ServiceSupplier.getWordService(applicationContext)

        //Keeping logo some time before showing main page.
        Handler(Looper.getMainLooper()).postDelayed({
            splashScreen.setKeepOnScreenCondition { false }
            mainActivityProcess()
        }, 500)
    }

    //Rest part that executes before main page showing.
    private fun mainActivityProcess() {
        setContentView(R.layout.activity_main)
        val myToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(myToolbar)
        val wordOriginalEdit = findViewById<EditText>(R.id.wordOriginalEdit)
        val wordTranslatedEdit = findViewById<EditText>(R.id.wordTranslatedEdit)

        //Define model view.
        this.uiModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        val uiObserver = Observer<MainActivityUiState>() { mainActivityUiState ->
            wordOriginalEdit.setText(mainActivityUiState.wordOriginate)
            wordTranslatedEdit.setText(mainActivityUiState.wordTranslated)
        }

        this.uiModel?.uiState?.observe(this, uiObserver)
        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener { sendMessage() }
    }

    //Occurs after rotate device.
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        val wordOriginalEdit = findViewById<EditText>(R.id.wordOriginalEdit)
        val wordTranslatedEdit = findViewById<EditText>(R.id.wordTranslatedEdit)

        //Update model view.
        val uiState = MainActivityUiState(
            wordOriginalEdit.text.toString(),
            wordTranslatedEdit.text.toString()
        )
        this.uiModel?.uiState?.value = uiState
    }

    //User inserted new word.
    private fun sendMessage() {
        val wordOriginalEdit = findViewById<EditText>(R.id.wordOriginalEdit)
        val wordTranslatedEdit = findViewById<EditText>(R.id.wordTranslatedEdit)

        //Update model view.
        var uiState = MainActivityUiState(
            wordOriginalEdit.text.toString(),
            wordTranslatedEdit.text.toString()
        )
        this.uiModel?.uiState?.value = uiState

        //Validating input data.
        val wordOriginal = uiState.wordOriginate!!.trim()
        if (wordOriginal.isEmpty()) {
            Toast.makeText(this, "Необходимо заполнить поле 'Новое слово'", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val wordTranslated = uiState.wordTranslated!!.trim()
        if (wordTranslated.isEmpty()) {
            Toast.makeText(this, "Необходимо заполнить поле 'Перевод'", Toast.LENGTH_SHORT).show()
            return
        }

        //Insert new word in the database.
        val result = this.wordService!!.insertWord(wordOriginal, wordTranslated)
        if (result.isNotEmpty()) {
            uiState = MainActivityUiState("", "")
            this.uiModel?.uiState?.value = uiState
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuItemId = item.itemId
        when (menuItemId) {
            R.id.menu_word_check -> {
                val intent = Intent(this, CheckActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_settings -> {
                val intent2 = Intent(this, SettingsActivity::class.java)
                startActivity(intent2)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}