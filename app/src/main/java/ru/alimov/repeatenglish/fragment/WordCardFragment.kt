package ru.alimov.repeatenglish.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.alimov.repeatenglish.R
import ru.alimov.repeatenglish.service.ServiceSupplier
import ru.alimov.repeatenglish.service.WordService
import ru.alimov.repeatenglish.util.constant.WORD_CARD_ANSWER
import ru.alimov.repeatenglish.util.constant.WORD_CARD_QUESTION

/**
 * Fragment for view pager on checking page.
 */
class WordCardFragment : Fragment() {
    private var question: String? = null
    private var answer: String? = null
    private var wordService: WordService? = null

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param question Parameter 1.
         * @param answer   Parameter 2.
         * @return A new instance of fragment WordCardFragment.
         */
        fun newInstance(question: String, answer: String): WordCardFragment {
            val fragment = WordCardFragment()
            val args = Bundle()
            args.putString(WORD_CARD_QUESTION, question)
            args.putString(WORD_CARD_ANSWER, answer)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.wordService = ServiceSupplier.getWordService(requireContext())
        if (arguments != null) {
            val args = arguments
            question = args?.getString(WORD_CARD_QUESTION, "")
            answer = args?.getString(WORD_CARD_ANSWER, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val resultView: View = inflater.inflate(R.layout.fragment_word_card, container, false)
        val wordCardQuestion = resultView.findViewById<TextView>(R.id.word_card_question)
        wordCardQuestion.text = this.question
        return resultView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val wordCardQuestion = view.findViewById<TextView>(R.id.word_card_question)
        wordCardQuestion.text = this.question

        //After click on the word show dialog window with translation.
        wordCardQuestion.setOnClickListener { _: View ->
            val checkingDialog: CheckingDialog =
                CheckingDialog.newInstance(this.question!!, this.answer!!, requireContext())
            checkingDialog.show(requireActivity().supportFragmentManager, "checkingDialog")
        }
        //Increment count of word's show in db.
        ServiceSupplier.getWordService(requireActivity()).updateDateShowed(this.question!!)
    }
}