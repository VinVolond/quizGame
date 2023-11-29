package com.example.quizgame

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.quizgame.Entities.MyQuestion
import com.example.quizgame.Utils.ClientServerLogic
import com.example.quizgame.Utils.HelperFunctions
import com.example.quizgame.Utils.ListMapFunctions
import com.example.quizgame.adapters.QuestionAnswerAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.NullPointerException


class QuizActivity : ComponentActivity() {

    /**
     * correctLettersMap is a map of integers with positions of gridItems from answerLettersList
     * and selectLettersList
     */
    private var currentLettersMap: HashMap<Int, Int?> = hashMapOf()

    /**
     * List that stores true false value of gridItem position in answerLettersList
     */
    private var correctLettersFixedValues: HashMap<Int, Boolean> = hashMapOf()

    /**
     * Map of answerLetters. key = position value = letter
     */
    var correctLettersList: HashMap<Int, String> = hashMapOf()
    /**
     * Map of selectLetters. key = position value = letter
     */
    var selectLettersList: HashMap<Int, String> = hashMapOf()
    /**
     * Map of letters that should be deleted if field  question_hint_delete_letters equals true
     */
    var deleteUnsuitableLetters: MutableList<Int?>? = null
    /**
     * NyQuestion class of current layout
     */
    private lateinit var myQuestion: MyQuestion

    @SuppressLint("Range", "SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_layout)
//        println("SEARCH_ID${DatabaseHelper.QUESTION_ID}")

        val dbHelper = DatabaseHelper(this)
        // Retrieve questions from the database
        val cursor = dbHelper.getQuestions()


        cursor.move(DatabaseHelper.QUESTION_ID.toInt())

        myQuestion = HelperFunctions().initQuestionFromCursor(cursor)
        // Create a view for the question


        // Set the question text
        val questionTextView = this.findViewById<TextView>(R.id.questionTextView)
        questionTextView.text = myQuestion.questionText.toString()
        val questionImage = this.findViewById<ImageView>(R.id.questionsImage)
        questionImage.setImageBitmap(BitmapFactory.decodeByteArray(myQuestion.questionPic, 0, myQuestion.questionPic.size))

        val correctLettersGrid = this.findViewById<GridView>(R.id.correctLettersGrid)
        val selectLettersGrid = this.findViewById<GridView>(R.id.selectLettersGrid)

//        val answerLettersList: MutableList<HashMap<String, String>> = mutableListOf()
//        val selectLettersList: MutableList<HashMap<String, String>> = mutableListOf()
        fillAnswerLetters()
        fillRandomLetters(20)
        currentValuesInit()
        deleteUnsuitableLettersInit()

        val answerLetters = correctLettersList.values.toList()
        val selectLetters = selectLettersList.values.toList()
//        println("answerLetters")
//        println(selectLetters)
//        println(answerLetters)
        if(correctLettersGrid.numColumns > answerLetters.size){
            correctLettersGrid.numColumns = answerLetters.size
        }//TODO if more than 20 letters in answer bug occurs

        correctLettersGrid.adapter = QuestionAnswerAdapter(applicationContext,
            R.layout.question_block_answer, answerLetters, R.id.questionAnswerTextView)
        selectLettersGrid.adapter = QuestionAnswerAdapter(applicationContext,
            R.layout.question_block_select, selectLetters, R.id.questionSelectTextView, currentLettersMap, deleteUnsuitableLetters)

        correctLettersGrid.setOnItemClickListener { parent, view, position, id ->

            onCorrectLetterClick(position, view, correctLettersGrid, selectLettersGrid)
        }
        selectLettersGrid.setOnItemClickListener { parent, view, position, id ->

            onSelectLetterClick(position, view, correctLettersGrid, selectLettersGrid)
        }

//        checkAnswerLettersWithSelectLettersCorrelation(correctLettersGrid, selectLettersGrid)
        cursor.close()

        onReturnButton()
        onHintButton()
    }

    private fun onReturnButton(){
        val returnButton = findViewById<FloatingActionButton>(R.id.exitFromQuestion)

        returnButton.setOnClickListener {
            finish()
        }
    }
    override fun onStop() {

        super.onStop()
    }
    private fun onSelectLetterClick(position: Int, view: View, correctLettersGrid: GridView, selectLettersGrid: GridView ){
        for ((key, value) in currentLettersMap) {
            if(value == null && !correctLettersFixedValues[key]!!){
                println("selectLetters")
                println(selectLettersList)
//                println(selectLetters)
//                println(answerLetters)
                currentLettersMap[key] = position
                correctLettersList[key] = selectLettersList[position]!!

                val answerLetters = correctLettersList.values.toList()
                val selectLetters = selectLettersList.values.toList()


                correctLettersGrid.adapter = QuestionAnswerAdapter(applicationContext,
                    R.layout.question_block_answer, answerLetters, R.id.questionAnswerTextView)
                selectLettersGrid.adapter = QuestionAnswerAdapter(applicationContext,
                    R.layout.question_block_select, selectLetters, R.id.questionSelectTextView,
                    currentLettersMap, deleteUnsuitableLetters)
                checkAnswerCorrectness(this, myQuestion.questionAnswer)
//                    Toast.makeText(this, "Item clicked at position $position and added to position $key", Toast.LENGTH_SHORT).show()
                break
            }
        }
    }

    private fun onCorrectLetterClick(position: Int, view: View, correctLettersGrid: GridView, selectLettersGrid: GridView ){
        try {
            val answerLetters = correctLettersList.values.toList()
            val selectLetters = selectLettersList.values.toList()
            println("HEREIWAS")
            println(answerLetters)
            println(selectLetters)
            println("TOkenDebug")
            println(ClientServerLogic.token)
            if (correctLettersFixedValues[position] == false) {
                if(currentLettersMap[position] != null){

                    selectLettersList[currentLettersMap[position]!!] = correctLettersList[position]!!
                    correctLettersList[position] = ""
                    currentLettersMap[position] = null

                    val answerLetters1 = correctLettersList.values.toList()
                    val selectLetters1 = selectLettersList.values.toList()
                    println("HEREIAM")
                    println(answerLetters1)
                    println(selectLetters1)
                    correctLettersGrid.adapter = QuestionAnswerAdapter(applicationContext,
                        R.layout.question_block_answer, answerLetters1, R.id.questionAnswerTextView)
                    selectLettersGrid.adapter = QuestionAnswerAdapter(applicationContext,
                        R.layout.question_block_select, selectLetters1, R.id.questionSelectTextView,
                        currentLettersMap, deleteUnsuitableLetters)

                    checkAnswerCorrectness(this, myQuestion.questionAnswer)
                }
            }
        }
        catch(_: NullPointerException){

        }

    }

    private fun checkAnswerCorrectness(context: Context, answer: String, tries: Int = 0): Boolean{
        try {
            var checker = false
            var index = 0
            for(it in answer){
                checker = true
                val answerItem = correctLettersList[index]
                index++
                if(answerItem != it.toString() && answerItem != "-"){
                    checker = false
                    break
                }
            }
             if(checker){
                completeQuestion(context, answer)
            }
            return checker

        }
        catch (error: NullPointerException){
            if(tries < 4) {

                CoroutineScope(Dispatchers.IO).launch {
                    delay(1000)
                    checkAnswerCorrectness(context, answer, tries + 1)
                }
            }
            return false
        }
    }

    private fun completeQuestion(context: Context = this, answer: String = myQuestion.questionAnswer){
        val dbHelper = DatabaseHelper(context)
        dbHelper.updateTableField("questions", "question_status", "Completed")
        dbHelper.updateTableField("questions", "question_filled_answer", answer)
//            Toast.makeText(context, "Your Answer Is correct", Toast.LENGTH_SHORT).show()
        showQuizCompletionPopup()
    }

    private fun addLetterToQuestion(letter: String, letterPosition: Int, context: Context = this){
        val dbHelper = DatabaseHelper(context)
        var questionFilledAnswer = ""
        if(myQuestion.questionFilledAnswer.isNullOrEmpty()){
            for (it in myQuestion.questionAnswer){
                questionFilledAnswer += when(it.toString()){
                    " ", "=", "," -> {
                        it.toString()
                    }

                    else -> {
                        "#"
                    }
                }
            }
        }
        else {
            questionFilledAnswer = myQuestion.questionFilledAnswer
        }
//        println("addLetterToQuestion")
//        println(questionFilledAnswer)
        val filledAnswerList = HelperFunctions().stringToArrayList(questionFilledAnswer)
//        println(filledAnswerList)
        filledAnswerList[letterPosition] = letter
//        println(filledAnswerList)
        questionFilledAnswer = HelperFunctions().arrayListToString(filledAnswerList)
//        println(questionFilledAnswer)
        myQuestion.questionFilledAnswer = questionFilledAnswer
        dbHelper.updateTableField("questions", "question_filled_answer", questionFilledAnswer)
    }

    private fun createRandomLettersList(questionAnswer: String, lettersCount: Int): String{
        var answer: String = questionAnswer
            .replace(" ", "")
            .replace("-", "")
            .replace("_","")
            .replace("#","")

        val utilClass = ListMapFunctions()
        while (answer.length<lettersCount){
            val newLetter = utilClass.weightedRandomLetter()
            answer += newLetter
        }
//        println("answer${answer}")
        val chars: List<Char> = answer.toList()
        return String(chars.shuffled().toCharArray())
    }

    fun onClearButtonClick(view: View) {
        val dbHelper = DatabaseHelper(this)
        dbHelper.updateTableField("questions", "question_status", "Uncompleted")
        dbHelper.updateTableField("questions", "question_filled_answer", "")
        dbHelper.updateTableField("questions", "question_hint_delete_letters", "false")
        // Finish the current activity
        finish()

// Start the activity again
        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("Range")
    private fun showQuizCompletionPopup() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.question_completeted_pop_up)

        // Customize the dialog's appearance if needed
        // For example, dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)

        val closeButton = dialog.findViewById<Button>(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
            finish()// Close the popup when the "Close" button is clicked
        }
        val goToNextButton = dialog.findViewById<Button>(R.id.goToNextQuiz)
        goToNextButton.setOnClickListener{
            var question = DatabaseHelper(this).getNextQuestionWithSpecificBundleId()
            val intent: Intent
            if(question == null){
                intent = Intent(this, QuestionListActivity::class.java)
            }
            else {
                val quizId = question.id
                println("DatabaseHelper.QUIZ_ID = ${DatabaseHelper.QUESTION_ID }")
                println("nextQuestionIndex = $quizId")
                DatabaseHelper.QUESTION_ID = quizId
                intent = Intent(this, QuizActivity::class.java)
            }
            finish()
            startActivity(intent)

        }
        dialog.show()
        dialog.setCanceledOnTouchOutside(true)
        dialog.setOnCancelListener {
            recreate()
        }
    }
    private fun onHintButton(){
        val hintButton = findViewById<ImageButton>(R.id.hintButton)
        hintButton.setOnClickListener{
            hintPopUpLayout()
        }
    }
    private fun onSkipQuestionClick() {
        if(myQuestion.questionStatus == "Completed"){
            Toast.makeText(this, "You have already completed this quest", Toast.LENGTH_SHORT).show()
            return
        }
        completeQuestion()
    }

    private fun onOpenFirstLetterClick() {
        if(myQuestion.questionStatus == "Completed"){
            Toast.makeText(this, "You have already completed this quest", Toast.LENGTH_SHORT).show()
            return
        }
        val selectLettersGrid = this.findViewById<GridView>(R.id.selectLettersGrid)
        val correctLettersGrid = this.findViewById<GridView>(R.id.correctLettersGrid)
        val selectAdapter = selectLettersGrid.adapter as QuestionAnswerAdapter

        val firstIndex = correctLettersFixedValues.values.indexOfFirst{ !it }
        if(firstIndex == -1) {
            checkAnswerCorrectness(this, myQuestion.questionAnswer)
            return
        }

        val addedLetter = myQuestion.questionAnswer[firstIndex].toString()

        /**
         * When letter box that we need to fill is not empty
         */
        if(currentLettersMap[firstIndex] != null){
            correctLettersList[firstIndex] = ""
            currentLettersMap[firstIndex] = null
        }

        /**
         * When there is already a letter that we need to fill in correctLetters list,
         * we change it position to firstIndex when it isn't fixed,
         * change currentLettersMap letter position to firstIndex
         */
        for((index, key) in correctLettersList){
            if(key == addedLetter && !correctLettersFixedValues[index]!!){
                if(currentLettersMap[index] != null) {
                    correctLettersList[index] = ""

                    currentLettersMap[firstIndex] = currentLettersMap[index]
                    currentLettersMap[index] = null

                    break
                }
            }
        }
        /**
         * finally add letter to list
         */
        correctLettersList[firstIndex] = addedLetter

        correctLettersFixedValues[firstIndex] = true
        addLetterToQuestion(addedLetter, firstIndex)
        currentValuesInit()

        val answerLetters = correctLettersList.values.toList()
        val selectLetters = selectLettersList.values.toList()


        correctLettersGrid.adapter = QuestionAnswerAdapter(applicationContext,
            R.layout.question_block_answer, answerLetters, R.id.questionAnswerTextView)
        selectLettersGrid.adapter = QuestionAnswerAdapter(applicationContext,
            R.layout.question_block_select, selectLetters, R.id.questionSelectTextView,
            currentLettersMap, deleteUnsuitableLetters)
        checkAnswerCorrectness(this, myQuestion.questionAnswer)
    }

    fun onOpenRandomLetterClick() {
        if(myQuestion.questionStatus == "Completed"){
            Toast.makeText(this, "You have already completed this quest", Toast.LENGTH_SHORT).show()
            return
        }
        val selectLettersGrid = this.findViewById<GridView>(R.id.selectLettersGrid)
        val correctLettersGrid = this.findViewById<GridView>(R.id.correctLettersGrid)
        val selectAdapter = selectLettersGrid.adapter as QuestionAnswerAdapter

        val correctList = correctLettersFixedValues.filter{ !it.value }
        if(correctList.size == myQuestion.questionAnswer.length) {
            checkAnswerCorrectness(this, myQuestion.questionAnswer)
            return
        }
        val randomKey = correctList.keys.shuffled().first()
        val addedLetter = myQuestion.questionAnswer[randomKey].toString()

        /**
         * When letter box that we need to fill is not empty
         */
        if(currentLettersMap[randomKey] != null){
            correctLettersList[randomKey] = ""
            currentLettersMap[randomKey] = null
        }

        /**
         * When there is already a letter that we need to fill in correctLetters list,
         * we change it position to firstIndex when it isn't fixed,
         * change currentLettersMap letter position to firstIndex
         */
        for((index, key) in correctLettersList){
            if(key == addedLetter && !correctLettersFixedValues[index]!!){
                if(currentLettersMap[index] != null) {
                    correctLettersList[index] = ""

                    currentLettersMap[randomKey] = currentLettersMap[index]
                    currentLettersMap[index] = null

                    break
                }
            }
        }
        /**
         * finally add letter to list
         */
        correctLettersList[randomKey] = addedLetter

        correctLettersFixedValues[randomKey] = true
        addLetterToQuestion(addedLetter, randomKey)
        currentValuesInit()

        val answerLetters = correctLettersList.values.toList()
        val selectLetters = selectLettersList.values.toList()


        correctLettersGrid.adapter = QuestionAnswerAdapter(applicationContext,
            R.layout.question_block_answer, answerLetters, R.id.questionAnswerTextView)
        selectLettersGrid.adapter = QuestionAnswerAdapter(applicationContext,
            R.layout.question_block_select, selectLetters, R.id.questionSelectTextView,
            currentLettersMap, deleteUnsuitableLetters)
        checkAnswerCorrectness(this, myQuestion.questionAnswer)
    }

    private fun onDeleteUnsuitableLettersClick() {
        if(myQuestion.questionStatus == "Completed"){
            Toast.makeText(this, "You have already completed this quest", Toast.LENGTH_SHORT).show()
            return
        }
        val selectLettersGrid = this.findViewById<GridView>(R.id.selectLettersGrid)
        val correctLettersGrid = this.findViewById<GridView>(R.id.correctLettersGrid)
        myQuestion.questionHintDeleteLetters = true
        DatabaseHelper(this).updateTableField("questions", "question_hint_delete_letters", "true")
        deleteUnsuitableLettersInit()

        val answerLetters = correctLettersList.values.toList()
        val selectLetters = selectLettersList.values.toList()

        correctLettersGrid.adapter = QuestionAnswerAdapter(applicationContext,
            R.layout.question_block_answer, answerLetters, R.id.questionAnswerTextView)
        selectLettersGrid.adapter = QuestionAnswerAdapter(applicationContext,
            R.layout.question_block_select, selectLetters, R.id.questionSelectTextView,
            currentLettersMap, deleteUnsuitableLetters)
    }
    @SuppressLint("MissingInflatedId")
    private fun hintPopUpLayout(){
        val popupView: View = layoutInflater.inflate(R.layout.hint_popup_layout, null)
        val root = findViewById<LinearLayout>(R.id.questionMainLayout)
// Create a PopupWindow

// Create a PopupWindow
        val hintPopup = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        hintPopup.animationStyle = R.style.PopupAnimation

        hintPopup.showAtLocation(root, Gravity.BOTTOM, 0, 0)

        val deleteUnstableButton = popupView.findViewById<ImageButton>(R.id.hintPopupDeleteUnsuitableLetters)
        deleteUnstableButton.setOnClickListener{
            onDeleteUnsuitableLettersClick()
        }

        val skipLevelButton = popupView.findViewById<ImageButton>(R.id.hintPopupSkipQuestion)
        skipLevelButton.setOnClickListener{
            onSkipQuestionClick()
        }

        val openFirstButton = popupView.findViewById<ImageButton>(R.id.hintPopupOpenFirstLetter)
        openFirstButton.setOnClickListener{
            onOpenFirstLetterClick()
        }

        val openRandomButton = popupView.findViewById<ImageButton>(R.id.hintPopupOpenRandomLetter)
        openRandomButton.setOnClickListener{
            onOpenRandomLetterClick()
        }
    }

    private fun fillAnswerLetters(): ArrayList<String>{
        val answerLetters = arrayListOf<String>()
        println("myquestion.questionFilledAnswer")
        println(myQuestion.questionFilledAnswer)
        if(myQuestion.questionFilledAnswer.isNullOrEmpty()){
            myQuestion.questionAnswer.forEachIndexed { index, char ->
                currentLettersMap[index] = null
                when(char){
                    ' ', '-', '_' -> {
                        correctLettersFixedValues[index] = true
                        currentLettersMap[index] = null
                        correctLettersList[index]= char.toString()
                        answerLetters.add(char.toString())
//                        answerLettersList.add(current)

                    }
                    else -> {
                        answerLetters.add("")
                        correctLettersList[index] = ""
                        correctLettersFixedValues[index] = false
//                        answerLettersList.add(current)
                    }
                }
            }
        }
        else{
            myQuestion.questionFilledAnswer.forEachIndexed { index, char ->
//            correctLettersGrid.addView(textView)
                currentLettersMap[index] = null

                when(char){
                    '#' ->{
                        correctLettersFixedValues[index] = false
                    }
                    else -> {
                        correctLettersFixedValues[index] = true
                    }
                }
                answerLetters.add(char.toString())
                correctLettersList[index] = char.toString()
            }
        }
//        println(answerLetters)
        return answerLetters
    }

    private fun fillRandomLetters(lettersCount: Int): ArrayList<String>{

        val selectLetters = arrayListOf<String>()

        val randomLettersList = createRandomLettersList(myQuestion.questionAnswer, lettersCount)
        randomLettersList.forEachIndexed() {index, it ->

            selectLetters.add(it.toString())
            selectLettersList[index] = it.toString()

        }
        println("randomLetters")
        println(selectLetters)
        return selectLetters
    }

    /**
     * init currentLetterMap values, use only after selectLettersList initiation
     */
    private fun currentValuesInit(){
        myQuestion.questionFilledAnswer.forEachIndexed { index, chr ->
            if(currentLettersMap[index] == null) {
                when (val str = chr.toString()) {
                    "#" -> {
                        currentLettersMap[index] = null
                    }

                    else -> {
                        val valuesList = selectLettersList.filterValues { str == it }
                        if (valuesList.isNotEmpty()) {
                            currentLettersMap[index] = valuesList.keys.first()
                        }
                    }
                }
            }
        }
    }

    private fun deleteUnsuitableLettersInit(){
        if(!myQuestion.questionHintDeleteLetters){
            return
        }
        deleteUnsuitableLetters = mutableListOf()

        val answerLetters = HelperFunctions().stringToArrayList(myQuestion.questionAnswer)

        selectLettersList.forEach{ myItem ->
            val firstIndex = answerLetters.indexOfFirst { myItem.value == it}

            if (firstIndex != -1){
                answerLetters.removeAt(firstIndex)
            }
            else {
                deleteUnsuitableLetters!!.add(myItem.key)
            }
        }

        correctLettersFixedValues.forEach { (index, it) ->
            val correctPosition = currentLettersMap[index]
            if(it){
                deleteUnsuitableLetters!!.add(correctPosition)
            }
            else {
                fillAnswerLetters()
                currentLettersMap[index] = null
            }
        }
    }
}
/**Insert a new question into the "questions" table
val resourceId = R.drawable.png_alchemist
val bitmap = BitmapFactory.decodeResource(resources, resourceId)
val outputStream = ByteArrayOutputStream()
bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
val byteArray = outputStream.toByteArray()
dbHelper.addQuestion("Определить аниме по картинке", "Стальной алхимик", byteArray)
**/

/**
 * Comment this function because it didn't work
 */
//    private fun checkAnswerLettersWithSelectLettersCorrelation(correctLettersGrid: GridView, selectLettersGrid: GridView){
//        var correctLettersAdapter = correctLettersGrid.adapter
//        var selectLettersAdapter = selectLettersGrid.adapter
//
//        for (correctListIndex in 0 until correctLettersAdapter.count) {
//            val correctLetterString = correctLettersAdapter.getItem(correctListIndex) as String
//
//
//            if(!correctLetterString.isNullOrEmpty() && currentLettersMap[correctListIndex] == null){
//                for (selectListIndex in 0 until selectLettersAdapter.count) {
//                    val selectLetterString = selectLettersAdapter.getItem(selectListIndex) as String
//
//                    if(selectLetterString == correctLetterString){
//                        println(correctLetterString)
//                        println("fgddsf")
//                        var selectView = findViewById<GridView>(R.id.userLettersGrid)
//                        var blockAnswer = selectView.adapter.getView(selectListIndex, null, selectView)
//                        println(blockAnswer.visibility)
//                        HelperFunctions().visibilityTrigger(blockAnswer)
//                        println(blockAnswer.visibility)
//
//                        currentLettersMap[correctListIndex] = selectListIndex
//                        break
//                    }
//                }
//            }
//        }
//    }