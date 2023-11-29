package com.example.quizgame
import android.database.sqlite.SQLiteDatabase
lateinit var sqlDataBase: SQLiteDatabase

fun createDataBase(){
    sqlDataBase = SQLiteDatabase.openOrCreateDatabase("anime_quiz.db",null)
    sqlDataBase.close()
    sqlDataBase = SQLiteDatabase.openDatabase("anime_quiz.db",null,  SQLiteDatabase.CREATE_IF_NECESSARY)
}