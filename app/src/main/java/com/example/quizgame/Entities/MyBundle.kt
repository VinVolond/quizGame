package com.example.quizgame.Entities

import android.annotation.SuppressLint
import android.database.Cursor
import java.sql.Timestamp

class MyBundle(var id: Long,             // Corresponds to _id
               var bundleName: String,  // Corresponds to bundle_name
               var bundlePic: ByteArray, // Corresponds to bundle_pic (as a byte array)
               var bundleCategory: String, // Corresponds to bundle_category
               var bundleStatus: String,  // Corresponds to bundle_status
               var bundleQuestionsProgress: String, // Corresponds to bundle_questions_progress
               var bundleTimestamp: String
)
