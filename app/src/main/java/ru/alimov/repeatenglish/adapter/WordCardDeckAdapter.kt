package ru.alimov.repeatenglish.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.alimov.repeatenglish.fragment.WordCardFragment
import ru.alimov.repeatenglish.model.Word

/**
 * Adapter store words for view pager.
 */
class WordCardDeckAdapter(_fragmentActivity:FragmentActivity, _wordList:List<Word>) : FragmentStateAdapter(_fragmentActivity) {
    val wordList: List<Word> = _wordList

    override fun createFragment(position: Int): Fragment {
        val word = wordList[position]
        return WordCardFragment.newInstance(word.wordOriginal, word.wordTranslated)
    }

    override fun getItemCount(): Int {
        return wordList.size
    }
}