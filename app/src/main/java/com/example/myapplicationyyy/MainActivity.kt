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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplicationyyy.databinding.ActivityNavigationDrawerBinding
import com.google.android.material.navigation.NavigationView
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
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue


class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences : SharedPreferences
    lateinit var retrofit : Retrofit
    lateinit var retrofitAPI : GithubAPI
    lateinit var githubItems: Collection<GithubItem>
    lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration
    public lateinit var binding: ActivityNavigationDrawerBinding
    private lateinit var menuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


     binding = ActivityNavigationDrawerBinding.inflate(layoutInflater)
     setContentView(binding.root)

        setSupportActionBar(binding.appBarNavigationDrawer.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_navigation_drawer)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        // To auto open and close the nav--
        //drawerLayout.open()
        //drawerLayout.close()


        //Log.e("", navView.addView(findViewById(R.id.groupItems)).toString())
        // Check github


        // First, we open the app. We need to check if this is the first open,
        // and if not, check if the user is already logged in to fetch their info.

        // Grab local preferences
        // Encrypting the user token is useless, apparently. Shared prefs are usually private.
        // https://stackoverflow.com/questions/10161266/how-to-securely-store-access-token-and-secret-in-android
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // If first time opening the app----
        if(sharedPreferences.getBoolean("firstTimeOpeningApp", true)) {
            binding.appBarNavigationDrawer.toolbar.isVisible = false

            navController.navigate(R.id.nav_login)

            sharedPreferences.edit().putBoolean("firstTimeOpeningApp", false).commit();

        } else {
            // Otherwise, check if the user is logged in or not for info--

            binding.appBarNavigationDrawer.toolbar.isVisible = true

            if(getSavedToken() != "null") {
                getSavedToken()?.let { createRetrofit(it) }
                Log.e("", "Logged in from memory.")
            } else {
                // User is not logged in. If the intent contains login info, pass it.
                val intent = intent
                onHandleAuthIntent(intent)
                Log.e("", "Not logged in, grabbed login info.")

            }

        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation_drawer, menu)

        // Menu has been created. Do any menu modifications--
        if(getSavedToken() == "null")
        findViewById<ImageView>(R.id.imageVieww).setOnClickListener {
            binding.drawerLayout.close()
            binding.appBarNavigationDrawer.toolbar.isVisible = false
            navController.navigate(R.id.nav_login)
        }

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

                var res = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null);

                CoroutineScope(Dispatchers.IO).launch {
                    res = drawableFromUrl(entity.getAvatar())
                }
                Thread.sleep(1000); // 5 Seconds
                findViewById<ImageView>(R.id.imageVieww).setImageDrawable(res)

//                findViewById<TextView>(R.id.message).text = "You're logged in as " + entity.getLog()
                findViewById<TextView>(R.id.textViewww).text = entity.getLog()
                findViewById<TextView>(R.id.textVieww).text = entity.getNamee()

                //findViewById<ImageView>(R.id.imageVieww).setImageDrawable(res)
                //findViewById<ImageView>(R.id.imageView).setImageDrawable(res)


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

    suspend fun getRepoInfo(ACCESSTOKEN: String, urlDirectory: String) : BlockingQueue<Collection<GithubItem?>?> {

        val blockingQueue: BlockingQueue<Collection<GithubItem?>?> = ArrayBlockingQueue(1)


        var call = retrofitAPI.getRepo("token " + ACCESSTOKEN, urlDirectory)

        var githubItems : Collection<GithubItem?>? = null
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
                    Log.e("", stringResponse)
                }
                Log.e("", response.raw().toString())

                val gson = Gson()

                val collectionType: Type = object : TypeToken<Collection<GithubItem?>?>() {}.type
                githubItems = gson.fromJson(stringResponse, collectionType)

                blockingQueue.add(githubItems)

            }

                /*

                for(githubItem in githubItems)
                    if(githubItem.type == "dir")
                        binding.navView.menu.add(1,1,1,githubItem.name)


                Log.e("",binding.navView.menu.size().toString())
                binding.navView.menu.forEach {
                    if(it.toString().equals("Settings") || it.toString().equals("Home") || it.toString().equals("ExampleDoc"))
                        return@forEach

                    Log.e("", it.toString())

                    // Recursively go through every language


                }
                //Log.e("",binding.navView.menu[0].actionView.toString())
*/
/*
                binding.navView.menu.getItem(5).setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem?): Boolean {
                        Log.e("","pressed")
                        if(binding.navView.menu.size() > 6) {
                            var size = binding.navView.menu.size()-1
                            for (x in 6..size) {
                                if (binding.navView.menu.getItem(x).isVisible)
                                    binding.navView.post {
                                        binding.navView.menu.getItem(x).setVisible(false)
                                    }
                                else
                                    binding.navView.post {
                                        binding.navView.menu.getItem(x).setVisible(true)
                                    }
                                //binding.navView.menu.
                            }
                        } else {
                            binding.navView.post { binding.navView.menu.add("└  Classes") }
                            binding.navView.post { binding.navView.menu.add("   └  Any.kt") }
                        }
                        //binding.navView.menu.clear(); //clear old inflated items.
                        //binding.navView.inflateMenu(R.menu.activity_navigation_drawer_drawer);
                        return true
                    }
                }) */


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


            //}
        })
        return blockingQueue
    }

    // Offset by the number of initial menu buttons
    var menuID = 2

    // Important-- recursive function to scan and apply directory structure from Github
    suspend fun getRecursiveDirectory(ACCESSTOKEN: String, urlDirectory : String, spacing : String) {

        val result: BlockingQueue<Collection<GithubItem?>?> = getRepoInfo(ACCESSTOKEN, urlDirectory)
        val items = result.take() // this will block your thread

        if (items != null) {
            items.forEach {
                if (it != null) {
                    menuID++

                    // Can't edit the view directly from this thread--
                    runOnUiThread {
                            var menuItemName = if(spacing == "") it.name else spacing + "└  " + it.name
                            // Start of a subsection
                            var i = menuID + 1
                            var temp = i
                            var off = false

                            var currentMenuItem = binding.navView.menu.add(menuItemName)
                                .setOnMenuItemClickListener {

                                    while (binding.navView.menu.getItem(i).title.indexOf("└") > spacing.length) {
                                        var nextItem = binding.navView.menu.getItem(i)
                                        binding.navView.post {
                                            // When closed, do NOT open subcontainers.
                                            var bool = if(spacing == "")
                                                spacing.length + 3 >= nextItem.title.indexOf("└")
                                             else
                                                it.title.indexOf("└") + spacing.length >= nextItem.title.indexOf("└")

                                            if(!off || bool )
                                            nextItem.setVisible(off)
                                        }
                                        // Handle the out-of-bounds error, go to next menuitem
                                        if(++i == binding.navView.menu.size()) {
                                            i = temp
                                            break }
                                    }
                                    i = temp
                                    off = !off

                                    true
                                }

                        // Disable all non-top levels
                        if(spacing != "") {
                            currentMenuItem.setVisible(false)
                        }

                    }


                    if(it.type == "dir") {
                        //Recursive call--
                        getRecursiveDirectory(
                            ACCESSTOKEN,
                            urlDirectory + it.name + "/",
                            spacing + "   "
                        )
                    }
                    else {
                        // File level hit- this is a Markdown file.
                        Log.e("", it.name)

                    }


                }

            }
        }

        Log.e("",items.toString())


        // If it is a folder versus a file.

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
            //getRepoInfo(ACCESSTOKEN, "")
            getRecursiveDirectory(ACCESSTOKEN,"", "")

        }

    }

    // Field from default config.
    val appId = "com.example.myapplicationyyy"
    val clientId = "60a1586f001e9e2a5be6"
    val clientSecret = "8a5dbfdcbe35eef9d0bac44f54ff44486e767434"
    val redirectUrl = ""


    fun logOut() {
        var miniRetrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/") // as we are sending data in json format so
            .addConverterFactory(GsonConverterFactory.create()) // at last we are building our retrofit builder.
            //.client(httpClient)
            .build()
        var miniRetrofitAPI = miniRetrofit.create(GithubAPI::class.java)

        var call = miniRetrofitAPI.logOut("access_token " + getSavedToken(), clientId)
        call.enqueue( object: Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                //handle error here
                Log.e("TAG", "onFailure: $t")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                //your raw string response
                val stringResponse = response.body()?.string()
                if (stringResponse != null) {
                    Log.e("", stringResponse)
                }
                Log.e("", response.raw().toString())


            }
        })
    }

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
    var name : String = ""
    fun getToken(): String {
        return access_token
    }
    fun getLog() : String {
        return login
    }
    fun getNamee() : String {
        return name
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

    @GET("/repos/ryanhlewis/Central-Documentation/contents/{directoryInfo}")
    @Headers("Accept: application/vnd.github.v3+json")
    fun getRepo(
        @Header("Authorization") token: String,
        @Path(value = "directoryInfo", encoded = true) directoryInfo : String
        ) : Call<ResponseBody>

    /*
    @DELETE("/applications/{client_id}/grant")
    @Headers("Accept: application/vnd.github.v3+json")
    fun logOut(
        @Body() access_token: String,
        @Path(value = "client_id", encoded = true) clientId: String
    ) : Call<ResponseBody> */

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/applications/{client_id}/grant", hasBody = true)
    fun logOut(
        @Body body: String,
        @Path(value = "client_id", encoded = true) clientId: String
    ): Call<ResponseBody>

}

