package com.example.quizgame.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.quizgame.R
import com.example.quizgame.Utils.HelperFunctions

class QuestionAnswerAdapter(context: Context,
                            var textViewResourceId: Int,
                            var mainObjectsList: List<String>,
                            var inflatedId: Int,
                            var additionalObjectList: HashMap<Int, Int?> = hashMapOf<Int, Int?>(),
                            var deleteUnsuitableLetters: List<Int?>? = null
) :
    ArrayAdapter<Any?>(context, textViewResourceId, mainObjectsList) {

    var positionErrorFix = false

    override fun getCount(): Int {
        return super.getCount()
    }

    override fun getItem(position: Int): Any? {
        return super.getItem(position)
    }
    @SuppressLint("SuspiciousIndentation")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = inflater.inflate(textViewResourceId, null)
        val textView = v.findViewById<TextView>(inflatedId)
        val myText = mainObjectsList[position]

        if(position == 0 && positionErrorFix){
            HelperFunctions().visibilityTrigger(v, "invisible")
            return v
        }
        setAnswerBlockTextLogic(textView, myText)
        setSelectBlockTextLogic(textView, myText, v, position)

        return v
    }

    private fun setSelectBlockTextLogic(textView: TextView, text: String, v: View, position: Int): Boolean{
        if(textViewResourceId != R.layout.question_block_select) {
            return false
        }
        textView.text = text
        deleteUnsuitableLetters?.forEach {
            if (it == position) {
                HelperFunctions().visibilityTrigger(v, "invisible")
                return true
            }
        }
        additionalObjectList.forEach {
            if (it.value == position) {
                HelperFunctions().visibilityTrigger(v, "invisible")
            }
        }
        return true
    }

    /**
     * returns true if new background and text was set to object,
     * else returns false
     */
    private fun setAnswerBlockTextLogic(textView: TextView, text: String): Boolean{
        if(textViewResourceId != R.layout.question_block_answer) {
            return false
        }
        return when (text) {
            "-", "_", " "  -> {
                textView.setBackgroundColor(255)
                textView.text = "-"
                true
            }
            "#" -> {
                textView.text = ""
                true
            }
            else -> {
                textView.text = text
                false
            }
        }
    }
}
