package ru.alimov.repeatenglish.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ru.alimov.repeatenglish.R
import ru.alimov.repeatenglish.adapter.WordCardDeckAdapter
import ru.alimov.repeatenglish.service.ServiceSupplier.getWordService
import ru.alimov.repeatenglish.service.WordService

/**
 * Checking page.
 */
class CheckActivity : AppCompatActivity() {
    private var wordService: WordService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        wordService = getWordService(applicationContext)
        setContentView(R.layout.activity_check)

        val wordList = wordService!!.getWordsForChecking()

        val word_pager = findViewById<ViewPager2>(R.id.word_pager)
        val pageAdapter: FragmentStateAdapter = WordCardDeckAdapter(this, wordList)
        word_pager.adapter = pageAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)

        val tabLayoutMediator = TabLayoutMediator(
            tabLayout, word_pager
        ) { tab, position -> tab.text = "Word " + (position + 1) }
        tabLayoutMediator.attach()

        val myToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(myToolbar)

        val msgLabel = findViewById<TextView>(R.id.msgLabel)
        if (wordList.isEmpty()) {
            msgLabel.text = resources.getString(R.string.checking_words_empty)
            msgLabel.visibility = View.VISIBLE
            word_pager.visibility = View.INVISIBLE
        } else {
            msgLabel.visibility = View.INVISIBLE
            word_pager.visibility = View.VISIBLE
        }
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
            R.id.menu_settings -> {
                val intent2 = Intent(this, SettingsActivity::class.java)
                startActivity(intent2)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}