package com.example.quizgame

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.GridView

import androidx.activity.ComponentActivity
import com.example.quizgame.Entities.MyQuestion
import com.example.quizgame.adapters.QuestionListAdapter


class QuestionListActivity: ComponentActivity() {
    var flag = true
    @SuppressLint("Range", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_list)

        questionList()
    }
//    Works but truly fucked
    override fun onStop() {
        super.onStop()
        flag = false
    }
    override fun onResume() {
        super.onResume()
        // Reload or refresh the activity here
        if(!flag){
            flag = true
            questionList()
        }
    }
    private fun questionList(){
        val dbHelper = DatabaseHelper(this)
        val questionsList = dbHelper.getQuestionsWithSpecificBundleId()
        var container = findViewById<GridView>(R.id.questionListView)
        val arrayList = arrayListOf<MyQuestion>()
        questionsList.forEach {
            arrayList.add(it)


            // Check if the ImageView already has a parent
//            val parent = imageButton.parent as? ViewGroup
//            parent?.removeView(imageButton) // Remove it from the previous parent, if any

            // Add the ImageView to the new container
            val customAdapter = QuestionListAdapter(applicationContext,R.layout.question_list_item, arrayList)
            container.adapter = customAdapter
            container = findViewById<GridView>(R.id.questionListView)
            println(container)


            container.setOnItemClickListener { parent, view, position, id ->

                val intent = Intent(this, QuizActivity::class.java)
                DatabaseHelper.QUESTION_ID = questionsList[position].id

                startActivity(intent)
            }
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}