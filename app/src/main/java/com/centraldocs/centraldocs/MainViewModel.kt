package com.centraldocs.centraldocs

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import centraldocs.centraldocs.R
import centraldocs.centraldocs.databinding.ActivityNavigationDrawerBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.InputStream
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

class Container {
    lateinit var githubItems : Collection<GithubItem?>
    var spacing : String = ""
    var conts : MutableList<Container> = mutableListOf()
    override fun toString(): String {
        if(this::githubItems.isInitialized)
        return githubItems.toString() + conts.toString()
        else
            return ""
    }
    fun isReady() : Boolean {
        return this::githubItems.isInitialized
    }
}

class MainViewModel : ViewModel() {

    lateinit var sharedPreferences : SharedPreferences
    lateinit var retrofit : Retrofit
    lateinit var retrofitAPI : GithubAPI
    var githubItems: Container = Container()
    lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration
    public lateinit var binding: ActivityNavigationDrawerBinding
    private lateinit var menuItem: MenuItem
    private lateinit var entity: LoginToken

    var mainactivity = MainActivity.getMainInstance()



    init {
        Log.e("Tarund", "View Model created")

        navController = mainactivity.navController
        binding = mainactivity.binding


        // First, we open the app. We need to check if this is the first open,
        // and if not, check if the user is already logged in to fetch their info.

        // Grab local preferences
        // Encrypting the user token is useless, apparently. Shared prefs are usually private.
        // https://stackoverflow.com/questions/10161266/how-to-securely-store-access-token-and-secret-in-android
        sharedPreferences = mainactivity.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE);

        // Ensure dark mode

        val darkModeString = sharedPreferences.getString("theme_color","System Default")
        val darkModeValues = mainactivity.resources.getStringArray(R.array.themes_array)
        when (darkModeString) {
            darkModeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            darkModeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            darkModeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        // If first time opening the app----
        if(sharedPreferences.getBoolean("firstTimeOpeningApp", true)) {
            binding.appBarNavigationDrawer.toolbar.isVisible = false

            navController.navigate(R.id.nav_login)

            sharedPreferences.edit().putBoolean("firstTimeOpeningApp", false).commit();

        } else {
            // Otherwise, check if the user is logged in or not for info--

            binding.appBarNavigationDrawer.toolbar.isVisible = true

            if(getSavedToken() != "null" && getSavedToken() != "") {
                getSavedToken()?.let { createRetrofit(it) }
                Log.e("", "Logged in from memory.")
            } else {
                // User is not logged in. If the intent contains login info, pass it.
                    // Create API requests without login---
                    GlobalScope.launch {


                        retrofit = Retrofit.Builder()
                            .baseUrl("https://api.github.com/") // as we are sending data in json format so
                            .addConverterFactory(GsonConverterFactory.create()) // at last we are building our retrofit builder.
                            //.client(httpClient)
                            .build()

                        retrofitAPI = retrofit.create(GithubAPI::class.java)

                        getRecursiveDirectory("", "", "", githubItems)

                        items.postValue(githubItems)

                    }

            }

        }



    }



    private val user: MutableLiveData<LoginToken> = MutableLiveData<LoginToken>();

    fun getUser(): LiveData<LoginToken> {
        return user
    }

    private val items: MutableLiveData<Container> = MutableLiveData<Container>();

    fun getItems(): LiveData<Container> {
        return items
    }

    private fun loadUser(user : MutableLiveData<LoginToken>) {
        // Do an asynchronous operation to fetch users.
        //user.postValue(listOf("Tarun", "Chawla"))
    }


    // copy paste from MainActivity.kt




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
                } else {
                    return
                }
                Log.e("", response.raw().toString())

                val gson = Gson()
                entity =
                    gson.fromJson(stringResponse, LoginToken::class.java)

                // Not given
                entity.access_token = ACCESSTOKEN

                var res = ResourcesCompat.getDrawable(mainactivity.getResources(), R.drawable.ic_launcher_background, null);

                CoroutineScope(Dispatchers.IO).launch {
                    res = drawableFromUrl(entity.getAvatar())
                }
                // TERRIBLE CODE- Please replace with actual block.
                Thread.sleep(1000); // 5 Seconds
                entity.res = res
                mainactivity.findViewById<ImageView>(R.id.imageVieww).setImageDrawable(res)

//                findViewById<TextView>(R.id.message).text = "You're logged in as " + entity.getLog()
                mainactivity.findViewById<TextView>(R.id.textViewww).text = entity.getLog()
                mainactivity.findViewById<TextView>(R.id.textVieww).text = entity.getNamee()
                user.postValue(entity)
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

        var call : Call<ResponseBody>
        if(ACCESSTOKEN != "")
            call = retrofitAPI.getRepo("token " + ACCESSTOKEN, urlDirectory)
        else
            call = retrofitAPI.getRepoNotLoggedIn( urlDirectory)


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
                } else {
                    Log.e("Error", "errored out")
                    return
                }
                Log.e("", response.raw().toString())

                val gson = Gson()

                // Make sure to catch errors ----

                val collectionType: Type = object : TypeToken<Collection<GithubItem?>?>() {}.type
                githubItems = gson.fromJson(stringResponse, collectionType)

                blockingQueue.add(githubItems)

            }
        })
        return blockingQueue
    }

    // Offset by the number of initial menu buttons
    var menuID = 2

    // Important-- recursive function to scan and apply directory structure from Github
    suspend fun getRecursiveDirectory(ACCESSTOKEN: String, urlDirectory : String, spacing : String, cont : Container) {


        // IMPORTANT - This makes an order of n calls to each directory-
        // which is VERY bad. Github accounts for this by allowing a recursive call
        // to a tree structure. Future- Revamp to use recursive call.
        //https://docs.github.com/en/rest/reference/git#trees

        val result: BlockingQueue<Collection<GithubItem?>?> = getRepoInfo(ACCESSTOKEN, urlDirectory)
        val items = result.take() // this will block your thread


        if (items != null) {
            items.forEach {
                if (it != null) {
                    menuID++

                    var cont1 = Container()
                    if (items != null) {
                        cont.githubItems = items
                        cont.spacing = spacing
                        cont.conts.add(cont1)
                    }

                    // Can't edit the view directly from this thread--
                    mainactivity.runOnUiThread {
                        var gitItem = it
                        var name = it.name
                        var potentialURL = it.download_url
                        var menuItemName = if(spacing == "") it.name else spacing + "└  " + it.name
                        // Start of a subsection
                        var i = menuID + 1
                        var temp = i
                        var off = false

                        var currentMenuItem : MenuItem
/*
                        if(it.type == "dir")
                            currentMenuItem = binding.navView.menu.add(menuItemName)
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
                        // Not a directory- custom file Markdown button click
                        else {

                            // Ignore hidden files, README, licenses, etc.
                            //    if(menuItemName == "README.md" || menuItemName[0] == '.')
                            //        return@runOnUiThread

                            currentMenuItem = binding.navView.menu.add(menuItemName)
                                .setOnMenuItemClickListener {
                                    binding.drawerLayout.close()

                                    // Grab the raw text---
                                    mainactivity.lifecycleScope.launch {
                                        getRawText(potentialURL, name, it, gitItem)
                                    }

                                    true
                                }
                        }




                        // Disable all non-top levels
                        if(spacing != "") {
                            currentMenuItem.setVisible(false)
                        }
                        */


                    }


                    if(it.type == "dir") {
                        //Recursive call--
                        getRecursiveDirectory(
                            ACCESSTOKEN,
                            urlDirectory + it.name + "/",
                            spacing + "   ", cont1
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

    suspend fun getRawText(url : String, name : String, it: MenuItem, gitItem : GithubItem)  {
        Log.e("","being called")
        val retrofit = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl("https://raw.githubusercontent.com/")
            .build()

        var retrofitAPI = retrofit.create(GithubAPI::class.java)


        var call = retrofitAPI.getStringResponse(url)

        if (call != null) {
            call.enqueue( object: Callback<String?> {
                override fun onFailure(call: Call<String?>, t: Throwable) {
                    //handle error here
                    Log.e("TAG", "onFailure: $t")
                }

                override fun onResponse(call: Call<String?>?, response: Response<String?>) {
                    if (response.isSuccessful) {
                        val responseString = response.body()
                        // Update UI based on Markdown file----

                        mainactivity.runOnUiThread {
                            var markDownRawText = responseString
                            val bundle = bundleOf("md" to markDownRawText, "gitItem" to gitItem)
                            navController.navigate(R.id.nav_gallery, bundle)
                            binding.appBarNavigationDrawer.toolbar.setTitle(name)
                            it.isChecked = true
                        }

                    }
                }

            })
        }

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

            getRecursiveDirectory(ACCESSTOKEN,"", "", githubItems)
            items.postValue(githubItems)

        }

    }

    // Field from default config.
    val appId = "com.centraldocs.centraldocs"
    val clientId = "60a1586f001e9e2a5be6"
    val clientSecret = "8a5dbfdcbe35eef9d0bac44f54ff44486e767434"
    val redirectUrl = ""

    class logoutClass(base: String) {
        var access_token : String = base
    }

    fun logOut() {

        var logoutclass = logoutClass(entity.getToken())

        val gson = Gson()
        var json : String = gson.toJson(logoutclass, logoutClass::class.java)

        Log.e("Json is ",json)
        Log.e("Json again is " ,gson.toJson(logoutclass))

        val jsonParser = JsonParser()
        val jo = jsonParser.parse(json) as JsonObject

        var call = retrofitAPI.logOut("token " + entity.getToken(),
            jo,  clientId)
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
                    Toast.makeText(mainactivity.getApplicationContext(),"Failed to login.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    var ref = this
    fun sendPullRequest(editedText: String, normalText: String, gitItem: GithubItem) {

        // To send a pull request, we first have to make sure the user has a fork
        // on their own account, if not, fork it. Then, push changes to their fork and pull.

        Log.e("", "Why!!")

        //BUG- Cannot display toasts in ANY context. Find workaround.


        if(getSavedToken() == "null") {
            mainactivity.runOnUiThread {
                Toast.makeText(
                    mainactivity.applicationContext,
                    "You must login first to upload edits.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Log.e("", "Not logged in.")
            return
        }

        if(editedText.equals(normalText)) {
            mainactivity.runOnUiThread {
                Toast.makeText(
                    mainactivity.applicationContext,
                    "You must make changes first!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("", "Did not change text.")

            }
            return
        }

        mainactivity.runOnUiThread {
            Toast.makeText(
                mainactivity.applicationContext,
                "Uploaded changes online.",
                Toast.LENGTH_SHORT
            ).show()
            Log.e("", "Uploaded changes.")
        }


        // Check if user has fork-
        mainactivity.runOnUiThread {

            var call = retrofitAPI.checkFork("token " + entity.getToken(), entity.getLog())

            call.enqueue( object: Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //handle error here
                    Log.e("TAG", "onFailure: $t")
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    //your raw string response

                    // Github will successfully return with either 404 or 200
                    if(response.code() == 404) {
                        // Create fork-

                        Log.e("", "User does not have repo. Forking!")
                        createFork(gitItem,editedText)
                    } else if(response.code() == 200) {
                        // Push changes to current fork
                        Log.e("", "User has repo. Pushing to theirs!")
                        getSHA(gitItem,editedText)

                    }

                    Log.e("TAG", "onSuccess " + response.raw())
                }
            })

        }



    }

    fun createFork(gitItem: GithubItem,editedText: String) {

        mainactivity.runOnUiThread {

            var call = retrofitAPI.forkCentralDocumentation("token " + entity.getToken())

            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //handle error here
                    Log.e("TAG", "onFailure: $t")
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    //your raw string response

                    sendPullRequest(editedText, "",gitItem)

                    Log.e("TAG", "onSuccess " + response.raw())
                }
            })
        }

    }

    fun getSHA(gitItem: GithubItem,editedText: String) {

        mainactivity.runOnUiThread {
            Log.e("", entity.getToken())
            var call = retrofitAPI.getSHA("token " + entity.getToken(),entity.getLog())

            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //handle error here
                    Log.e("TAG", "onFailure: $t")
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    //your raw string response

                    // Github will successfully return with either 404 or 200
                    if (response.code() == 404) {
                        // Create fork-

                    }
                    val stringResponse = response.body()?.string()
                    Log.e("TAG", "onSuccess " + stringResponse)


                    val gson = Gson()
                    var info: GithubInfo =
                        gson.fromJson(stringResponse!!, GithubInfo::class.java)


                    // Use SHA for blobs?
                    makeBlobs(info.commit.sha,gitItem,editedText)

                    Log.e("TAG", "onSuccess " + info.commit.sha)
                }
            })
        }

    }


    fun makeBlobs(origSHA : String,gitItem: GithubItem,editedText: String) {

        mainactivity.runOnUiThread {

            val json = "{\"content\":\""+editedText+"\"}"
            val jsonParser = JsonParser()
            val jo = jsonParser.parse(json) as JsonObject

            var call = retrofitAPI.createBlob("token " + entity.getToken(),
                jo,  entity.getLog())

            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //handle error here
                    Log.e("TAG", "onFailure: $t")
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    //your raw string response

                    val stringResponse = response.body()?.string()
                    Log.e("TAG", "onSuccess " + stringResponse)

                    val gson = Gson()
                    var info: CommitInfo =
                        gson.fromJson(stringResponse!!, CommitInfo::class.java)

                    // Trees
                    makeTrees(origSHA, info.sha,gitItem)

                    Log.e("TAG", "onSuccess " + response.raw())
                }
            })
        }

    }


    class tree(pathh : String, modee : String, typee : String, shaa : String) {
        var path : String = pathh
        var mode : String = modee
        var type : String = typee
        var sha : String = shaa
    }
    class treeClass(base: String,treee : tree) {
        var base_tree : String = base
        var tree : MutableList<tree> = mutableListOf<tree>(treee)
    }
    fun makeTrees(origSHA : String, blobSHA : String,gitItem: GithubItem) {

        mainactivity.runOnUiThread {


            var treeclass = treeClass(origSHA, tree(gitItem.path,"100644","blob",blobSHA))

            val gson = Gson()
            var json : String = gson.toJson(treeclass, treeClass::class.java)

            Log.e("Json is ",json)
            Log.e("Json again is " ,gson.toJson(treeclass))

            val jsonParser = JsonParser()
            val jo = jsonParser.parse(json) as JsonObject

            var call = retrofitAPI.createTree("token " + entity.getToken(),
                jo,  entity.getLog())

            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //handle error here
                    Log.e("TAG", "onFailure: $t")
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    //your raw string response

                    val stringResponse = response.body()?.string()
                    Log.e("TAG", "onSuccess " + stringResponse)

                    val gson = Gson()
                    var info: CommitInfo =
                        gson.fromJson(stringResponse!!, CommitInfo::class.java)

                    // Commit
                    makeCommit(origSHA,info.sha,gitItem)

                    Log.e("TAG", "onSuccess " + response.raw())
                }
            })
        }

    }

    class Commit(pathh : String, modee : String, typee : String) {
        var message : String = pathh
        var parents : MutableList<String> = mutableListOf<String>(modee)
        var tree : String = typee
    }

    fun makeCommit(origSHA : String, treeSHA : String,gitItem: GithubItem) {

        mainactivity.runOnUiThread {


            var commit = Commit("Edited " + gitItem.name,origSHA, treeSHA)

            val gson = Gson()
            var json : String = gson.toJson(commit, Commit::class.java)

            Log.e("Json is ",json)
            Log.e("Json again is " ,gson.toJson(commit))

            val jsonParser = JsonParser()
            val jo = jsonParser.parse(json) as JsonObject

            var call = retrofitAPI.createCommit("token " + entity.getToken(),
                jo,  entity.getLog())

            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //handle error here
                    Log.e("TAG", "onFailure: $t")
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    //your raw string response

                    val stringResponse = response.body()?.string()
                    Log.e("TAG", "onSuccess " + stringResponse)

                    Log.e("TAG", "onSuccess " + response.raw())

                    val gson = Gson()
                    var info: CommitInfo =
                        gson.fromJson(stringResponse!!, CommitInfo::class.java)

                    // Ref
                    updateRef(info.sha,gitItem)

                }
            })
        }

    }

    class RefInfo(pathh : String, typee : String) {
        var refs : String = pathh
        var sha : String = typee
    }

    fun updateRef(newSHA : String, gitItem: GithubItem) {

        mainactivity.runOnUiThread {


            var commit = RefInfo("refs/heads/main",newSHA)

            val gson = Gson()
            var json : String = gson.toJson(commit, RefInfo::class.java)

            Log.e("Json is ",json)
            Log.e("Json again is " ,gson.toJson(commit))

            val jsonParser = JsonParser()
            val jo = jsonParser.parse(json) as JsonObject

            var call = retrofitAPI.updateRef("token " + entity.getToken(),
                jo,  entity.getLog())

            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //handle error here
                    Log.e("TAG", "onFailure: $t")
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    //your raw string response

                    val stringResponse = response.body()?.string()
                    Log.e("TAG", "onSuccess " + stringResponse)


                    // Ref
                    pullRequest(gitItem)
                    Log.e("", "FINALLY PUSHED COMMIT!")

                    Log.e("TAG", "onSuccess " + response.raw())
                }
            })
        }

    }


    class pullReq(title: String, body:String, head:String,base:String) {
        var title: String = title
        var body: String = body
        var head: String = head
        var base: String = base
    }

    fun pullRequest(gitItem: GithubItem) {

        mainactivity.runOnUiThread {

            var commit = pullReq("Edited " + gitItem.name,"Edited using Central Documentation App!",entity.getLog() + ":main","main")

            val gson = Gson()
            var json : String = gson.toJson(commit, pullReq::class.java)

            Log.e("Json is ",json)
            Log.e("Json again is " ,gson.toJson(commit))

            val jsonParser = JsonParser()
            val jo = jsonParser.parse(json) as JsonObject

            var call = retrofitAPI.pullRequest("token " + entity.getToken(),
                jo)

            call.enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    //handle error here
                    Log.e("TAG", "onFailure: $t")
                }

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    //your raw string response

                    val stringResponse = response.body()?.string()
                    Log.e("TAG", "onSuccess " + stringResponse)


                    // Ref
                    Log.e("", "FINALLY PULLED REQUEST!")

                    Log.e("TAG", "onSuccess " + response.raw())
                }
            })
        }

    }




}