package ru.alimov.repeatenglish.fragment

import ru.alimov.repeatenglish.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.alimov.repeatenglish.service.ServiceSupplier

import ru.alimov.repeatenglish.service.WordService
import ru.alimov.repeatenglish.util.constant.WORD_CARD_ANSWER
import ru.alimov.repeatenglish.util.constant.WORD_CARD_QUESTION

/**
 * Dialog window with translation.
 */
class CheckingDialog : DialogFragment() {
    private var question: String? = null
    private var answer: String? = null
    private var wordService: WordService? = null

    companion object {
        fun newInstance(question: String, answer: String, context: Context): CheckingDialog {
            val checkingDialog = CheckingDialog()
            checkingDialog.wordService = ServiceSupplier.getWordService(context)
            val args = Bundle()
            args.putString(WORD_CARD_QUESTION, question)
            args.putString(WORD_CARD_ANSWER, answer)
            checkingDialog.arguments = args
            return checkingDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        question = requireArguments().getString(WORD_CARD_QUESTION)
        answer = requireArguments().getString(WORD_CARD_ANSWER)
        val inflater = requireActivity().layoutInflater
        val dialogView: View = inflater.inflate(R.layout.fragment_checking_dialog, null)

        //Show translation of selected word.
        val word_card_question = dialogView.findViewById<TextView>(R.id.word_answer)
        word_card_question.text = this.answer

        //Set handler for positive button.
        val btnCorrectAnswer = dialogView.findViewById<Button>(R.id.btn_correct_answer)
        btnCorrectAnswer.setOnClickListener { _: View? ->
            if (wordService!!.incrementCorrectCheckCounter(
                    this.question!!
                )
            ) {
                dismiss()
            }
        }

        //Set handler for negative button.
        val btnIncorrectAnswer = dialogView.findViewById<Button>(R.id.btn_incorrect_answer)
        btnIncorrectAnswer.setOnClickListener { view: View? ->
            if (wordService!!.incrementIncorrectCheckCounter(
                    this.question!!
                )
            ) {
                dismiss()
            }
        }
        val builder = AlertDialog.Builder(requireActivity())
        return builder
            .setTitle("Rate yourself!")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setView(dialogView)
            .create()
    }
}