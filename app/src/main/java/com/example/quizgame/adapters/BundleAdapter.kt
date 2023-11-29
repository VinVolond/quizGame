package com.example.quizgame.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import com.example.quizgame.Entities.MyBundle
import com.example.quizgame.R
import com.example.quizgame.Utils.HelperFunctions
import de.hdodenhof.circleimageview.CircleImageView


class BundleAdapter(context: Context, var textViewResourceId: Int, objects: ArrayList<MyBundle>) :
    ArrayAdapter<Any?>(context, textViewResourceId, objects as List<Any?>) {
    var bundleArray: ArrayList<*> = ArrayList<Any>()

    init {
        bundleArray = objects
    }

    override fun getCount(): Int {
        return super.getCount()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = inflater.inflate(textViewResourceId, null)
        val imageView = v.findViewById<CircleImageView>(R.id.bundleView1)
        var myBundle = bundleArray[position] as MyBundle
        val progressBar = v.findViewById<ProgressBar>(R.id.bundleCompletedProgressBar)
        val progressText = v.findViewById<TextView>(R.id.bundleProgress)

        //Here it should go if only there was no saved progress

        myBundle = HelperFunctions().updateBundleProgress(context, myBundle)
        val bundleProgressInPair = myBundle.bundleQuestionsProgress.split("/").first().toInt() to
                myBundle.bundleQuestionsProgress.split("/").last().toInt()
        if(bundleProgressInPair.second != 0){
            progressBar.progress = bundleProgressInPair.first*100/bundleProgressInPair.second
        }
        else {
            progressBar.progress = 0
        }
        progressText.text = myBundle.bundleQuestionsProgress
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(myBundle.bundlePic, 0, myBundle.bundlePic.size))

        return v
    }
}
