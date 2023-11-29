package com.example.quizgame.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.quizgame.Entities.MyQuestion
import com.example.quizgame.R
import de.hdodenhof.circleimageview.CircleImageView

class QuestionListAdapter(context: Context, var textViewResourceId: Int, objects: ArrayList<MyQuestion>) :
    ArrayAdapter<Any?>(context, textViewResourceId, objects as List<Any?>) {
    var questionArray: ArrayList<*> = ArrayList<Any>()

    init {
        questionArray = objects
    }

    override fun getCount(): Int {
        return super.getCount()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = inflater.inflate(textViewResourceId, null)
        val imageView = v.findViewById<CircleImageView>(R.id.questionInListImage)
        var myQuestion = questionArray[position] as MyQuestion
        val questionStatus = v.findViewById<TextView>(R.id.questionStatus)

        //Here it should go if only there was no saved progress
        if(myQuestion.questionStatus == "Completed"){
            v.setBackgroundColor(Color.argb(255, 0, 0, 0))
        }
        else {
            v.setBackgroundColor(Color.argb(255, 0, 255, 255))
        }
        questionStatus.text = myQuestion.questionStatus
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(myQuestion.questionPic, 0, myQuestion.questionPic.size))

        return v
    }
}
