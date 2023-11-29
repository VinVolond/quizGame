package com.example.quizgame

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RadioGroup
import android.widget.SearchView
import androidx.activity.ComponentActivity
import com.example.quizgame.Entities.MyBundle
import com.example.quizgame.adapters.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BundleListActivity: ComponentActivity() {
    var flag = true
    @SuppressLint("Range", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bundle_list)
        mainBundleList()
//        searchView()
        buttonSort(this)

    }
    //Works but truly fucked
    override fun onStop() {
        super.onStop()
        flag = false
    }
    override fun onResume() {
        super.onResume()
        // Reload or refresh the activity here
        if(!flag){
            flag = true
            mainBundleList()
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun search(query: String?) {
        // Show a loading indicator

        // Use coroutines to load data asynchronously
        CoroutineScope(Dispatchers.IO).launch {
//            val result = fetchDataFromServer(query) // Perform the data fetch
//
//            // Update the UI on the main thread
//            withContext(Dispatchers.Main) {
//                updateLayout(result)
//            }
        }
    }

    //TODO
//    private fun searchView(){
//        val searchView = findViewById<SearchView>(R.id.bundleSearchBar)
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                // Perform the search and update the underlying layout
//                search(query)
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                // Optional: handle text changes as the user types
//                return true
//            }
//        })
//    }

    private fun mainBundleList(){
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase
        val bundleList = dbHelper.getBundleListByFilter(null)
        var block = 0
        var container = findViewById<GridView>(R.id.bundleListGridView)
        val arrayList = arrayListOf<MyBundle>()
        bundleList.forEach {
            arrayList.add(it)
        }
        val customAdapter = BundleAdapter(applicationContext,R.layout.bundle_list_item, arrayList)
        container.adapter = customAdapter
//        container = findViewById<GridView>(R.id.bundleListGridView)


        container.onItemClickListener = OnItemClickListener { parent, view, position, id ->

//            println(position)
//            println("Should redirect to BundleActivity")
            val intent = Intent(this, QuestionListActivity::class.java)
            val myBundle = customAdapter.bundleArray[position] as MyBundle
            DatabaseHelper.BUNDLE_ID = bundleList[position].id

            startActivity(intent)

        }
    }

    private fun buttonSort(context: Context){
        var buttonSortOrder = findViewById<Button>(R.id.sortOptionsButton)
        buttonSortOrder.setOnClickListener{
            val root = findViewById<LinearLayout>(R.id.bundleRoot)
            val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView: View = inflater.inflate(R.layout.sort_by_pop_up_layout, null)

            val popupWindow = PopupWindow(
                popupView,
                300,
                300,
                true
            )
//            popupWindow.animationStyle = R.anim.slide_from_bottom_up
            popupWindow.animationStyle = R.style.PopupAnimation
            //TODO(поправить отступ и верстку)
            popupWindow.showAtLocation(root, Gravity.BOTTOM, 0, 0)
            setListOrder(popupView)

        }
    }
    private fun setListOrder(view: View) {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                var radioGroup = view.findViewById<RadioGroup>(R.id.sortOptions)
                radioGroup.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->
                    DatabaseHelper.changeListOrder(checkedId)
                    println(DatabaseHelper.LIST_ORDER)
                    mainBundleList()
                }
            } catch (_: NullPointerException){
                delay(1000)
                setListOrder(view)
            }
        }
    }
}