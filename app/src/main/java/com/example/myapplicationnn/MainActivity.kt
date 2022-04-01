package com.example.myapplicationnn

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.myapplicationnn.ui.main.MainFragment
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences : SharedPreferences
    lateinit var retrofit : Retrofit
    lateinit var retrofitAPI : GithubAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        // Check if user is logged in--
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        if(getSavedToken() == "null") {
            // User is not logged in. Log them in.
            val intent = intent
            onHandleAuthIntent(intent)
            Log.e("", "Not logged in, grabbed login info.")

        } else {

            getSavedToken()?.let { createRetrofit(it) }
            Log.e("", "Logged in from memory.")

        }

        //        findViewById<TextView>(R.id.message).text = getSavedToken()



    }

    suspend fun getUserInfo(ACCESSTOKEN: String) {
        var call = retrofitAPI.getUser("token " + ACCESSTOKEN)

        Log.e("TAG", call.toString())

        call.enqueue( object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //handle error here
                Log.e("TAG", "onFailure: $t")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                //your raw string response
                val stringResponse = response.body()?.string()
                if (stringResponse != null) {
                    Log.e("",stringResponse)
                }
                Log.e("", response.raw().toString())

                val gson = Gson()
                val entity: LoginToken =
                    gson.fromJson(stringResponse, LoginToken::class.java)


                findViewById<TextView>(R.id.message).text = "You're logged in as " + entity.getLog()

                var res = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null);

                CoroutineScope(Dispatchers.IO).launch {
                    res = drawableFromUrl(entity.getAvatar())
                }
                Thread.sleep(5000); // 5 Seconds
                findViewById<ImageView>(R.id.imageView).setImageDrawable(res)


            }

        })




    }

    // Drawable from URL code from https://stackoverflow.com/questions/3375166/android-drawable-images-from-url
    @Throws(IOException::class)
    suspend fun drawableFromUrl(url: String?): Drawable {
        val x: Bitmap
        val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        val input: InputStream = connection.getInputStream()
        x = BitmapFactory.decodeStream(input)
        return BitmapDrawable(Resources.getSystem(), x)
    }

    fun getSavedToken(): String? {
        val ACCESSTOKEN : String? = sharedPreferences.getString("access_token","null")
        return ACCESSTOKEN
    }

    fun createRetrofit(ACCESSTOKEN : String) {

        Log.e("access tokem", ACCESSTOKEN)
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)


        val httpClient = OkHttpClient.Builder()
            // Attempting to add headers to every request
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "token" + ACCESSTOKEN)
                chain.proceed(request.build())
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/") // as we are sending data in json format so
            .addConverterFactory(GsonConverterFactory.create()) // at last we are building our retrofit builder.
            //.client(httpClient)
            .build()

        retrofitAPI = retrofit.create(GithubAPI::class.java)

        Log.e("", "New retrofit built.")

        CoroutineScope(Dispatchers.IO).launch {
            getUserInfo(ACCESSTOKEN)

        }

    }

    // Field from default config.
    val appId = "com.example.myapplicationnn"
    val clientId = "60a1586f001e9e2a5be6"
    val clientSecret = "8a5dbfdcbe35eef9d0bac44f54ff44486e767434"
    val redirectUrl = ""


    fun onHandleAuthIntent(intent: Intent?) = GlobalScope.async {
        if (intent != null && intent.data != null) {
            val uri = intent.data
            if (uri.toString().startsWith("centraldocs://login")) {
                val tokenCode = uri!!.getQueryParameter("code")
                if (!isEmpty(tokenCode)) {

                    var retrofit: Retrofit = Retrofit.Builder()
                        .baseUrl("https://github.com/") // as we are sending data in json format so
                        .addConverterFactory(GsonConverterFactory.create()) // at last we are building our retrofit builder.
                        .build()
                    val retrofitAPI: GithubAPI = retrofit.create(GithubAPI::class.java)
                    var call = retrofitAPI.getAccessToken(
                        tokenCode!!,
                        clientId, clientSecret,
                        appId, redirectUrl
                    )
                    call.enqueue( object: Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            //handle error here
                            Log.e("TAG", "onFailure: $t")
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            //your raw string response
                            val stringResponse = response.body()?.string()
                            if (stringResponse != null) {

                                val gson = Gson()
                                val entity: LoginToken =
                                    gson.fromJson(stringResponse, LoginToken::class.java)

                                // Store ACCESS TOKEN
                                sharedPreferences.edit().putString("access_token", entity.getToken()).commit()

                                // Create network object with login token.
                               createRetrofit(entity.getToken())

                                Log.e("", retrofit.toString())

                            }

                        }
                    })

                } else {
                    Toast.makeText(getApplicationContext(),"Failed to login.",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}


class LoginToken {
        var access_token: String = ""
        var login : String = ""
        var avatar_url : String = ""
        fun getToken(): String {
            return access_token
        }
        fun getLog() : String {
            return login
        }
        fun getAvatar() : String {
            return avatar_url
        }

    }

