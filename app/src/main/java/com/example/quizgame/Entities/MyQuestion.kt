package com.example.quizgame.Entities


class MyQuestion(
    val id: Long,
    val bundleId: Long,
    val questionText: String,
    val questionAnswer: String,
    val questionPic: ByteArray,
    var questionFilledAnswer: String,
    var questionHintDeleteLetters: Boolean,
    val questionStatus: String
) {

}