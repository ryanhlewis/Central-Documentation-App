package com.example.myapplicationyyy
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ActionMenuView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.forEach
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplicationyyy.databinding.ActivityNavigationDrawerBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
import retrofit2.http.*
import java.io.InputStream
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences : SharedPreferences
    lateinit var retrofit : Retrofit
    lateinit var retrofitAPI : GithubAPI
    lateinit var githubItems: Collection<GithubItem>

    private lateinit var appBarConfiguration: AppBarConfiguration
private lateinit var binding: ActivityNavigationDrawerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


     binding = ActivityNavigationDrawerBinding.inflate(layoutInflater)
     setContentView(binding.root)


        setSupportActionBar(binding.appBarNavigationDrawer.toolbar)

        binding.appBarNavigationDrawer.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //drawerLayout.open()
        // Great code to add API dynamically--
        //navView.menu.add("hey")

        //Log.e("", navView.addView(findViewById(R.id.groupItems)).toString())
        // Check github

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


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation_drawer, menu)
        return true
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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
                Thread.sleep(2000); // 5 Seconds
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

    suspend fun getRepoInfo(ACCESSTOKEN: String) {
        var call = retrofitAPI.getRepo("token " + ACCESSTOKEN)

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

                val collectionType: Type = object : TypeToken<Collection<GithubItem?>?>() {}.type
                githubItems = gson.fromJson(stringResponse, collectionType)


                for(githubItem in githubItems)
                    if(githubItem.type == "dir")
                        binding.navView.menu.add(githubItem.name)


                Log.e("",binding.navView.menu.size().toString())
                binding.navView.menu.forEach {
                    Log.e("", it.toString())
                    if(it.toString().equals("C#")) {
                        it.setIcon(R.drawable.account_circle)
                        //it.setActionView(R.layout.fragment_home)
                        //it.collapseActionView()
                        //it.subMenu.add("Hey")
                        //it.
                        it.setOnMenuItemClickListener {
                            binding.navView.menu.add("Classes")
                            binding.navView.menu.addSubMenu("Hs")
                                //it.setEnabled(false)
                            Log.e("", "clicked")
                            true
                        }
                    }
                }
                //Log.e("",binding.navView.menu[0].actionView.toString())

                binding.navView.menu.getItem(6).setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        Log.e("","pressed")
                        binding.navView.menu.add("SEFSFSEFS")
                        //binding.navView.menu.clear(); //clear old inflated items.
                        //binding.navView.inflateMenu(R.menu.activity_navigation_drawer_drawer);
                        return true
                    }
                })


                lifecycleScope.launch {
                    delay(10000L)
                    binding.navView.invalidate()
                }


/*
                binding.navView.menu.getItem(6).setOnMenuItemClickListener {
                    it.setEnabled(false)
                    binding.navView.menu.add("Classes")

                    @Override
                    fun onClick(v: View) {
                        binding.navView.menu.add("Classes")

                        //handle the click here.
                    }

                    true
                }*/
                //binding.navView.menu.getItem(binding.navView.menu.size()-1).subMenu.add("Cool")
                //binding.navView.menu.getItem(binding.navView.menu.size()-1).subMenu.addSubMenu("Classes")


            }
        })
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
            getRepoInfo(ACCESSTOKEN)

        }

    }

    // Field from default config.
    val appId = "com.example.myapplicationyyy"
    val clientId = "60a1586f001e9e2a5be6"
    val clientSecret = "8a5dbfdcbe35eef9d0bac44f54ff44486e767434"
    val redirectUrl = ""


    fun onHandleAuthIntent(intent: Intent?) = GlobalScope.async {
        if (intent != null && intent.data != null) {
            val uri = intent.data
            if (uri.toString().startsWith("centraldocs://login")) {
                val tokenCode = uri!!.getQueryParameter("code")
                if (!TextUtils.isEmpty(tokenCode)) {

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
                    Toast.makeText(getApplicationContext(),"Failed to login.", Toast.LENGTH_SHORT).show();
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


class GithubItem {
    var name : String = ""
    var type : String = ""
    override fun toString(): String {
        return name + " " + type
    }
}

interface GithubAPI {

    @FormUrlEncoded
    @POST("login/oauth/access_token")
    @Headers("Accept: application/json")
    fun getAccessToken(
        @Field("code") code: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("state") state: String,
        @Field("redirect_uri") redirectUrl: String
    ) : Call<ResponseBody>

    @GET("user")
    fun getUser(
        @Header("Authorization") token: String
    ): Call<ResponseBody>

    @GET("/repos/ryanhlewis/Central-Documentation/contents")
    @Headers("Accept: application/vnd.github.v3+json")
    fun getRepo(
        @Header("Authorization") token: String
    ) : Call<ResponseBody>



}


