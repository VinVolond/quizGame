package com.example.quizgame.Utils

import android.content.Context
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class ClientServerLogic {
    companion object {
        var token: String? = null
    }
    private val hostPort = "http://192.168.31.198:3000"
    fun requestUsersData(context: Context){
        val client = OkHttpClient()

        val url = "$hostPort/api/data" // Replace with your server URL

        val request = Request.Builder().url(url).build()

        var b = OkHttpClient()

        var response = client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // Handle the error
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    // Handle the error
                } else {
                    // Handle the response
                    val responseData = response.body?.string()
                    // Do something with the data
//                    Toast.makeText(context, "you have successfully receive response", Toast.LENGTH_SHORT).show()
                    println("responseData = $responseData")
                    // If you need to update UI, make sure to run it on the main thread
                    // For example, using runOnUiThread in an Activity
                }
            }
        })
    }
    fun requestToken(androidId: String): String? {
        val client = OkHttpClient()
        println("androidId=$androidId")
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val jsonBody = JSONObject().apply {
            put("androidId", androidId)
        }.toString()
        val body = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$hostPort/api/request-token")
            .post(body)
            .build()
        println("request=$request")
        println("requestBody=$body")


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // Handle failure
                println("erroronFailure")

            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    println("erroronResponse")
                    // Handle error
                } else {
                    token = response.body?.string()
                    // Store the token securely and use it for subsequent requests
                }
            }
        })
        //TODO throw error if token == null
        return token
    }
    fun requestDataByToken(api: String, token: String){
        val url = "$hostPort/$api"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://yourserveraddress:port/protected-route")
            .addHeader("Authorization", "Bearer $token") // Add token here
            .build()

        val response = client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // Handle the error
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    // Handle the error
                } else {
                    // Handle the response
                    val responseData = response.body?.string()
                    // Do something with the data
//                    Toast.makeText(context, "you have successfully receive response", Toast.LENGTH_SHORT).show()
                    println("responseData = $responseData")
                    // If you need to update UI, make sure to run it on the main thread
                    // For example, using runOnUiThread in an Activity
                }
            }
            // Implement the callback methods
        })
    }

}