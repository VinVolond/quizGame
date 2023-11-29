package com.example.quizgame

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.quizgame.Entities.MyBundle
import com.example.quizgame.Entities.MyQuestion
import com.example.quizgame.Entities.MyUser
import com.example.quizgame.Utils.HelperFunctions


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "anime_quiz.db"
        public var LIST_ORDER: String = "bundle_name ASC"
        public var CURRENT_ID: Long = 0
        public var BUNDLE_ID: Long= 1
        public var QUESTION_ID: Long = 0
        public var ANDROID_ID: String = "4b19f8d9dbfd6f3a"
        var MY_USER: MyUser = HelperFunctions().initCommonUser()

        fun changeListOrder(checkedId: Int){
            when (checkedId) {
                R.id.sortByName -> LIST_ORDER = "bundle_name ASC" // Sort by name in ascending order
                R.id.sortByTimestamp -> LIST_ORDER = "updated_at DESC" // Sort by timestamp in descending order
                R.id.sortById -> LIST_ORDER = "_id ASC" // Sort by ID in ascending order
            }
        }
    }


    override fun onCreate(db: SQLiteDatabase) {

        val createTableQuestionsSql = "CREATE TABLE IF NOT EXISTS questions (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "bundle_id INTEGER," +
                "question_text TEXT," +
                "question_answer TEXT," +
                "question_pic BLOB," +
                "question_filled_answer TEXT," +
                "question_hint_delete_letters TEXT," +
                "question_status TEXT);"
        db.execSQL(createTableQuestionsSql)

        val createTableBundles = "CREATE TABLE IF NOT EXISTS bundles (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "bundle_name TEXT," +
                "bundle_pic BLOB," +
                "bundle_category TEXT," +
                "bundle_status TEXT," +
                "bundle_questions_progress TEXT," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);"
        db.execSQL(createTableBundles)

        val triggerOnUpdateBundles = "CREATE TRIGGER IF NOT EXISTS update_timestamp_trigger\n" +
                "AFTER UPDATE ON bundles\n" +
                "FOR EACH ROW\n" +
                "BEGIN\n" +
                "    UPDATE bundles\n" +
                "    SET updated_at = CURRENT_TIMESTAMP\n" +
                "    WHERE _id = OLD._id;\n" +
                "END;"
        db.execSQL(triggerOnUpdateBundles)

//        val createTableUsers = "CREATE TABLE IF NOT EXISTS users (" +
//                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "server_id INTEGER," +
//                "android_id TEXT," +
//                "user_logo BLOB," +
//                "user_hints INTEGER," +
//                "user_score INTEGER," +
//                "user_bundles TEXT," +
//                "user_name TEXT," +
//                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);"
//        db.execSQL(createTableUsers)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion > oldVersion) {
            when (oldVersion) {
                1 -> {
                    val createTableBundles = "CREATE TABLE IF NOT EXISTS bundles (" +
                            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "bundle_name TEXT," +
                            "bundle_pic BLOB," +
                            "bundle_category TEXT," +
                            "bundle_status TEXT," +
                            "bundle_questions_progress TEXT," +
                            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);"
                    db.execSQL(createTableBundles)

                    val createTableQuestionsSql = "CREATE TABLE IF NOT EXISTS questions (" +
                            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "bundle_id INTEGER," +
                            "question_text TEXT," +
                            "question_answer TEXT," +
                            "question_pic BLOB," +
                            "question_filled_answer TEXT," +
                            "question_hint_delete_letters TEXT," +
                            "question_status TEXT);"
                    db.execSQL(createTableQuestionsSql)

                    val triggerOnUpdateBundles = "CREATE TRIGGER IF NOT EXISTS update_timestamp_trigger\n" +
                            "AFTER UPDATE ON bundles\n" +
                            "FOR EACH ROW\n" +
                            "BEGIN\n" +
                            "    UPDATE bundles\n" +
                            "    SET updated_at = CURRENT_TIMESTAMP\n" +
                            "    WHERE _id = OLD._id;\n" +
                            "END;"
                    db.execSQL(triggerOnUpdateBundles)

//                    val createTableUsers = "CREATE TABLE IF NOT EXISTS users (" +
//                            "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                            "server_id INTEGER," +
//                            "android_id TEXT," +
//                            "user_logo BLOB," +
//                            "user_hints INTEGER," +
//                            "user_score INTEGER," +
//                            "user_bundles TEXT," +
//                            "user_name TEXT," +
//                            "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);"
//                    db.execSQL(createTableUsers)
                }

            }
        }

    }
    fun addUser(androidId: Int): Long{
        val db = writableDatabase
        val values = ContentValues()

        // Set the values for the columns
        values.put("user_name", "defaultUser")
        values.put("android_id", ANDROID_ID)
        values.put("user_hints", 10)
        values.put("user_score", 0)

        // Insert the new question into the table
        val rowId = db.insert("questions", null, values)

        // Close the database
        db.close()

        return rowId
    }
    /**
     * Adds new question to db table questions
     */
    fun addQuestion(bundleId: Int,
                    questionText: String,
                    question_answer: String,
                    question_pic: ByteArray,
                    question_filled_answer: String = "",
                    question_hint_delete_letters: Boolean = false,
                    question_status: String = "Uncompleted"): Long {
        val db = writableDatabase
        val values = ContentValues()

        // Set the values for the columns
        values.put("bundle_id", bundleId)
        values.put("question_text", questionText)
        values.put("question_answer", question_answer)
        values.put("question_pic", question_pic)
        values.put("question_filled_answer", question_filled_answer)
        values.put("question_hint_delete_letters", question_hint_delete_letters.toString())
        values.put("question_status", question_status)

        // Insert the new question into the table
        val rowId = db.insert("questions", null, values)

        // Close the database
        db.close()

        return rowId
    }

    /**
     * Adds new bundle to db table bundles
     */
    fun addBundle(bundleName: String,
                  bundlePic: ByteArray,
                  bundleCategory: String,
                  bundleStatus: String = "Uncompleted", bundleProgress: String = "0/0"): Long {
        val db = writableDatabase
        val values = ContentValues()

        values.put("bundle_name", bundleName)
        values.put("bundle_pic", bundlePic)
        values.put("bundle_category", bundleCategory)
        values.put("bundle_status", bundleStatus)

        val rowId = db.insert("bundles", null, values)

        db.close()

        return rowId
    }
    /**
     *
     */
    @SuppressLint("Range")
    fun getBundleListByFilter(filterMap: Map<String, String>?, quantity: Int? = null): List<MyBundle> {
        var quantity = quantity
        val myBundleList = mutableListOf<MyBundle>()
        val db = this.readableDatabase
        var selection: String? = ""
        var selectionArgs = mutableListOf<String>()
        filterMap?.forEach{
            if(selection == ""){
                selection = "${it.key} = ?"
            } else {
                selection+= " AND ${it.key} = ?"
            }
            selectionArgs.add(it.value)
        }
        if(selection == "") selection = null
        val cursor: Cursor = db.query("bundles", null, selection, selectionArgs.toTypedArray(), null, null, LIST_ORDER)
        if(quantity == null) {
            if (cursor.moveToFirst()) {
                do {
                    val myBundle = HelperFunctions().initBundleFromCursor(cursor)
                    myBundleList.add(myBundle)
                } while (cursor.moveToNext())
            } else {
                println("It returns empty list by my filter")
            }
        }
        else {
            if (cursor.moveToFirst()) {
                do {
                    val myBundle = HelperFunctions().initBundleFromCursor(cursor)
                    myBundleList.add(myBundle)
                    quantity--
                } while (cursor.moveToNext() && quantity != 0)
            } else {
                println("It returns empty list by my filter")
            }
        }
        cursor.close()
        db.close()

        return myBundleList
    }
    /**
     * Gets questions list from questions db table filtered by bundleId
     * @param bundleId bundle_id of question (by default = BUNDLE_ID)
     * @return question list of question entity (can be empty)
     */
    fun getQuestionsWithSpecificBundleId(bundleId: Long = BUNDLE_ID): List<MyQuestion>{
        return getQuestionsListByFilter(mapOf("bundle_id" to bundleId.toString()))
    }


    /**
     * Gets next question from questions db table filtered by bundleId
     * @param bundleId bundle_id of question (by default = BUNDLE_ID)
     * @return 1 question class entity or null
     */
    fun getNextQuestionWithSpecificBundleId(bundleId: Long = BUNDLE_ID): MyQuestion?{
        return getNextQuestionByFilter(mapOf("bundle_id" to bundleId.toString()))
    }

    /**
     * Gets questions list from questions db table filtered by filterMap where key is
     * filtered field and value is it value
     * @param filterMap map of filtered objects
     */
    @SuppressLint("Range")
    fun getQuestionsListByFilter(filterMap: Map<String, String>): List<MyQuestion> {
        val myQuestionList = mutableListOf<MyQuestion>()
        val db = this.readableDatabase
        var selection = ""
        var selectionArgs = mutableListOf<String>()
        filterMap.forEach{
            if(selection == ""){
                selection = "${it.key} = ?"
            }
            else {
                selection+= " AND ${it.key} = ?"
            }
            selectionArgs.add(it.value)
        }

        val cursor: Cursor = db.query("questions", null, selection, selectionArgs.toTypedArray(), null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val myQuestion = HelperFunctions().initQuestionFromCursor(cursor)
               myQuestionList.add(myQuestion)
            } while (cursor.moveToNext())
        }
        else {
            println("It returns empty list by my filter")
        }

        cursor.close()
        db.close()

        return myQuestionList
    }
    /**
     * Gets next question from questions db table right after filtered by filterMap where key is
     * filtered field and value is it value QUESTION_ID
     * @param filterMap map of filtered objects
     */
    @SuppressLint("Range")
    fun getNextQuestionByFilter(filterMap: Map<String, String>): MyQuestion? {
        val db = this.readableDatabase
        var selection = ""
        val selectionArgs = mutableListOf<String>()
        filterMap.forEach{
            if(selection == ""){
                selection = "${it.key} = ?"
            }
            else {
                selection+= " AND ${it.key} = ?"
            }
            selectionArgs.add(it.value)
        }

        val cursor: Cursor = db.query("questions", null, selection, selectionArgs.toTypedArray(), null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndex("_id"))
                if(id == QUESTION_ID){
                    break
                }
            } while (cursor.moveToNext())



        }
        else {
            println("It returns empty list by my filter")
        }

        return try {
            cursor.moveToNext()

            val myQuestion = HelperFunctions().initQuestionFromCursor(cursor)
            cursor.close()
            db.close()

            myQuestion
        } catch(ex: java.lang.IndexOutOfBoundsException){
            cursor.close()
            db.close()

            null
        }
    }

    /**
     * Select all questions from db
     * returns cursor
     */
    fun getQuestions(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM questions", null)
    }

    /**
     * Function to delete all tables in the database
     * @param tableName name of table that will be deleted
     */
    fun deleteTable(tableName: String) {
        val db = writableDatabase
        db.execSQL("DROP TABLE IF EXISTS ${tableName};")
        // Add more DROP TABLE statements for other tables as needed
        // Be careful when using this method, as it will delete all data and tables
    }

    /**
     * Function to update specific fields in the table
     * @param tableName name of db table to update
     * @param columnName name of db attribute to update
     * @param valueId table id to update
     */
    fun updateTableField(tableName: String, columnName: String, columnValue: String, valueId: Long = QUESTION_ID): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(columnName, columnValue)

        val whereClause = "_id = ?"
        val whereArgs = arrayOf(valueId.toString())

        return try {
            val rowsAffected = db.update(tableName, contentValues, whereClause, whereArgs)
            rowsAffected > 0
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }

    /**
     * Check is db exists
     * @param context current context
     * @param dbName name of current db
     */
    fun isDatabaseExists(context: Context, dbName: String): Boolean {
        val dbFile = context.getDatabasePath(dbName)
        println("MyPath")
        println(dbFile.absolutePath)
        return dbFile.exists()
    }
}
