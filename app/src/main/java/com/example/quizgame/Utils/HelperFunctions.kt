package com.example.quizgame.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.view.View
import com.example.quizgame.DatabaseHelper
import com.example.quizgame.Entities.MyBundle
import com.example.quizgame.Entities.MyQuestion
import com.example.quizgame.Entities.MyUser

class HelperFunctions {
    public fun visibilityTrigger(view: View, visibilityAction: String = ""){
        try {
            when(visibilityAction){
                "visible"->{
                    view.visibility = View.VISIBLE
                    return
                }
                "invisible"->{
                    view.visibility = View.INVISIBLE
                    return
                }
                else -> {
                    }
            }
        }catch(_: NullPointerException) {

        }
    }
    public fun goneTrigger(){

    }

    @SuppressLint("Range")
    fun initQuestionFromCursor(cursor: Cursor): MyQuestion{
        val id = cursor.getLong(cursor.getColumnIndex("_id"))
        val bundleId = cursor.getLong(cursor.getColumnIndex("bundle_id"))
        val questionText = cursor.getString(cursor.getColumnIndex("question_text"))
        val questionAnswer = cursor.getString(cursor.getColumnIndex("question_answer"))
        val questionPic = cursor.getBlob(cursor.getColumnIndex("question_pic"))
        val questionFilledAnswer = cursor.getString(cursor.getColumnIndex("question_filled_answer"))
        val questionStatus = cursor.getString(cursor.getColumnIndex("question_status"))
        val questionHintDeleteLetters = cursor.getString(cursor.getColumnIndex("question_hint_delete_letters")).toBoolean()

        return MyQuestion(id, bundleId, questionText, questionAnswer, questionPic, questionFilledAnswer, questionHintDeleteLetters, questionStatus)
    }

    @SuppressLint("Range")
    fun initBundleFromCursor(cursor: Cursor): MyBundle {
        val bundlePic = cursor.getBlob(cursor.getColumnIndex("bundle_pic"))
        val id = cursor.getLong(cursor.getColumnIndex("_id"))
        val bundleName = cursor.getString(cursor.getColumnIndex("bundle_name"))
        val bundleCategory = cursor.getString(cursor.getColumnIndex("bundle_category"))
        val bundleStatus = cursor.getString(cursor.getColumnIndex("bundle_status"))
        val bundleProgressString = cursor.getString(cursor.getColumnIndex("bundle_questions_progress"))
        val bundleTimestamp = cursor.getString(cursor.getColumnIndex("updated_at"))

        return MyBundle(id, bundleName, bundlePic, bundleCategory, bundleStatus, bundleProgressString, bundleTimestamp)
    }

    fun updateBundleProgress(context: Context, bundle: MyBundle): MyBundle{
        val dbHelper = DatabaseHelper(context)

        val questions = dbHelper.getQuestionsWithSpecificBundleId(bundleId = bundle.id)
        val uncompletedQuestions = questions.filter{it.questionStatus == "Uncompleted"}
        val completedQuestions = questions.filter { it.questionStatus == "Completed"}

        if(completedQuestions.size + uncompletedQuestions.size != questions.size){
            error("There is something wrong with questions quantity: completed=" +
                    "${completedQuestions.size}, uncompleted=${uncompletedQuestions.size}," +
                    " all-in-all=${questions.size}")
        }
        val progress = "${completedQuestions.size}/${questions.size}"
        if(bundle.bundleQuestionsProgress != progress){
            if(completedQuestions.size == questions.size) {
                bundle.bundleStatus = "Completed"
                dbHelper.updateTableField("bundles", "bundle_status", bundle.bundleStatus, bundle.id)
            } else {

                bundle.bundleStatus = "Uncompleted"
                dbHelper.updateTableField("bundles", "bundle_status", bundle.bundleStatus, bundle.id)
            }
            bundle.bundleQuestionsProgress = progress
            dbHelper.updateTableField("bundles", "bundle_questions_progress", progress, bundle.id)

        }
        return bundle
    }
    fun stringToArrayList(inputString: String): ArrayList<String> {
        val stringList = ArrayList<String>()
        if (inputString.isNotEmpty()) {
            inputString.forEach {
                stringList.add(it.toString())
            }

        }
        return stringList
    }
    fun arrayListToString(inputArrayList: ArrayList<String>): String{
        var result = ""
        inputArrayList.forEach{
            result+=it
        }
        return result
    }

    //TODO тут пока костыль
    fun initCommonUser(): MyUser{
        return MyUser(1, "1", "defaultUser", null, 0, 10)
    }
}