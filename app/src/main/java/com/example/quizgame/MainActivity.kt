package com.example.quizgame

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.quizgame.Utils.ClientServerLogic
import com.example.quizgame.ui.theme.QuizGameTheme
import java.io.ByteArrayOutputStream

class MainActivity : ComponentActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizGameTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting("Android")
                }

            }
            setContentView(R.layout.activity_main)
            val line: LinearLayout = findViewById(R.id.mainLayout)
            val anim: AnimationDrawable = line.background as AnimationDrawable
            anim.setEnterFadeDuration(2500)
            anim.setExitFadeDuration(5000)
            anim.start()

            // Get a writable database, which will create the database if it doesn't exist
            val dbHelper = DatabaseHelper(this)
            val db = dbHelper.writableDatabase
//            val dbName. = "anime_quiz.db"
//            val databaseExists = dbHelper.isDatabaseExists(this, dbName)
//            dbHelper.deleteTable("questions")
//            dbHelper.deleteTable("bundles")

            dbHelper.onUpgrade(db,1,2)

            // Prepare bundle data
//            val resourceId = R.drawable.png_bundle_alchemist
//            val bitmap = BitmapFactory.decodeResource(resources, resourceId)
//            val outputStream = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//            val byteArray = outputStream.toByteArray()
//            dbHelper.addBundle("Стальной алхимик", byteArray, "Одно аниме")

            // Prepare question data
//            val resourceId = R.drawable.png_alchemist
//            val bitmap = BitmapFactory.decodeResource(resources, resourceId)
//            val outputStream = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//            val byteArray = outputStream.toByteArray()
//            dbHelper.addQuestion(1,"Определить аниме по картинке", "С А", byteArray)
            //TODO получить androidId.
            // Проверить совпадение с текущим пользователем.
            // Если androidId совпадают, продолжить работу.
            // Нет
            // Если androidId сохраненный в приложении пуст, сохранить текущий androidId, \
            // отправить на сервер запрос, на создание нового пользователя
            // Если dndroidId сохраненный в приложении не пуст,
            // сделать запрос на сервер, с проверкой androidId(?????)
//            val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
//            println("androidId=$androidId")
//            println(DatabaseHelper.androidId == androidId)
            val button: Button = findViewById(R.id.button)
            button.setOnClickListener {
//                val token = ClientServerLogic().requestToken(DatabaseHelper.ANDROID_ID)
                val intent = Intent(this, BundleListActivity::class.java)
                startActivity(intent)
            }


        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuizGameTheme {
        Greeting("Android")
    }
}