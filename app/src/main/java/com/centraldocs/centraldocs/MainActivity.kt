package com.centraldocs.centraldocs

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import centraldocs.centraldocs.R
import centraldocs.centraldocs.databinding.ActivityNavigationDrawerBinding
import com.google.android.material.navigation.NavigationView
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import kotlinx.parcelize.Parcelize
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.*


class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences : SharedPreferences
    lateinit var retrofit : Retrofit
    lateinit var retrofitAPI : GithubAPI
    lateinit var githubItems: Collection<GithubItem>
    lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration
    public lateinit var binding: ActivityNavigationDrawerBinding
    public lateinit var drawerLayout: DrawerLayout

    private lateinit var menuItem: MenuItem
    private lateinit var entity: LoginToken

    lateinit var mainViewModel : MainViewModel


    companion object {
        private lateinit var instance: MainActivity

        @JvmStatic
        fun getMainInstance() = instance
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        instance = this

         binding = ActivityNavigationDrawerBinding.inflate(layoutInflater)
         setContentView(binding.root)


        setSupportActionBar(binding.appBarNavigationDrawer.toolbar)

        drawerLayout = binding.drawerLayout
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

        // ViewModel, preserve data, don't refresh---
        mainViewModel =
            ViewModelProvider(this).get(MainViewModel::class.java)
        // Get current activity!
        // Yes, incredibly discouraged by Google.
        mainViewModel.mainactivity = getMainInstance()
        mainViewModel.navController = navController
        mainViewModel.binding = binding
        mainViewModel.getUser().observe(this, Observer {
            // Update the UI
            Log.e("","Woah, detected entity!")

            GlobalScope.launch{
                delay(100)
                runOnUiThread {
                    findViewById<ImageView>(R.id.imageVieww).setImageDrawable(it.res)
                    findViewById<ImageView>(R.id.imageVieww).setOnClickListener {
                        true
                    }
                    findViewById<TextView>(R.id.textViewww).text = it.getLog()
                    findViewById<TextView>(R.id.textVieww).text = it.getNamee()
                }
            }
        })
        GlobalScope.launch {
            // Might be called after getting actual pfp above, need to fix--
            try {
                while (findViewById<TextView>(R.id.textViewww) == null)
                    delay(1000)
                if (findViewById<TextView>(R.id.textVieww).text.equals("Login with Github"))
                    runOnUiThread {
                        findViewById<ImageView>(R.id.imageVieww).setOnClickListener {
                            Log.e("", "clicked..")
                            drawerLayout.close()
                            navController.navigate(R.id.nav_login)
                            true
                        }
                    }
            } catch(e : Exception) {

            }
        }
        // Must observe person image, their respective username, etc.
        // and the tree.
        mainViewModel.getItems().observe(this, Observer {
            // Update the UI
            Log.e("","Woah, detected items!")

            Log.e("", it.toString())

            //var itt = Container()
            //itt.conts = mutableListOf<Container>(it)
            recursiveMakeMenu(it,"")

        })


        if(intent != null && intent.data != null) {
            val intent = intent
            mainViewModel.onHandleAuthIntent(intent)
            Log.e("", "Not logged in, grabbed login info.")
        }

        var sharedPreferences =
            getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)

        var colorNum = sharedPreferences?.getInt("color_color",-13981796)

        var chosenColor = colorNum as Int

        binding.appBarNavigationDrawer.toolbar.setBackgroundColor(chosenColor)
        binding.navView.getHeaderView(0).setBackgroundColor(chosenColor)
        getWindow().setStatusBarColor(chosenColor);

        //binding.drawerLayout.setStatusBarBackgroundColor(chosenColor)
        GlobalScope.launch {
            delay(500)
            try {
                if (isColorDark(colorNum as Int)) {
                    binding.appBarNavigationDrawer.toolbar.setTitleTextColor(
                        ContextCompat.getColor(this@MainActivity, R.color.white)
                    )
                    binding.root.findViewById<TextView>(R.id.textViewww)
                        .setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    binding.root.findViewById<TextView>(R.id.textVieww)
                        .setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.appBarNavigationDrawer.toolbar.get(0).findViewById<ImageButton>(R.id.sendButton).drawable),
                        ContextCompat.getColor(this@MainActivity, R.color.white)
                    );
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.appBarNavigationDrawer.toolbar.get(1).findViewById<ImageButton>(R.id.editButton).drawable),
                        ContextCompat.getColor(this@MainActivity, R.color.white)
                    );
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.appBarNavigationDrawer.toolbar.get(2).findViewById<ImageButton>(R.id.viewButton).drawable),
                        ContextCompat.getColor(this@MainActivity, R.color.white)
                    );
                    binding.appBarNavigationDrawer.toolbar.getNavigationIcon()
                        ?.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                } else {

                    binding.appBarNavigationDrawer.toolbar.setTitleTextColor(
                        ContextCompat.getColor(this@MainActivity, R.color.black)
                    )
                    binding.root.findViewById<TextView>(R.id.textViewww)
                        .setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))
                    binding.root.findViewById<TextView>(R.id.textVieww)
                        .setTextColor(ContextCompat.getColor(this@MainActivity, R.color.black))

                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.appBarNavigationDrawer.toolbar.get(0).findViewById<ImageButton>(R.id.sendButton).drawable),
                        ContextCompat.getColor(this@MainActivity, R.color.black)
                    );
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.appBarNavigationDrawer.toolbar.get(1).findViewById<ImageButton>(R.id.editButton).drawable),
                        ContextCompat.getColor(this@MainActivity, R.color.black)
                    );
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.appBarNavigationDrawer.toolbar.get(2).findViewById<ImageButton>(R.id.viewButton).drawable),
                        ContextCompat.getColor(this@MainActivity, R.color.black)
                    );
                    binding.appBarNavigationDrawer.toolbar.getNavigationIcon()
                        ?.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
                }
            }catch (e:Exception){
                Log.e("", e.toString())
            }
        }

        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);



    }

    //https://stackoverflow.com/questions/24260853/check-if-color-is-dark-or-light-in-android
    fun isColorDark(color: Int): Boolean {
        val darkness: Double =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return if (darkness < 0.3) {
            false // It's a light color
        } else {
            true // It's a dark color
        }
    }


    fun hideSoftKeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    // WARNING- Highly dependent on the number of ignored files in the directory--
    // Need to fix..
    var menuID = -1

    fun recursiveMakeMenu(it : Container, spacing : String) {



        var ref = it

        if (it.isReady()) {
            var items = it.githubItems
            items.forEachIndexed { index, it ->
                if (it != null) {
                    menuID++

                    // Can't edit the view directly from this thread--
                    runOnUiThread {
                        var gitItem = it
                        var name = it.name
                        var potentialURL = it.download_url
                        var menuItemName = if(spacing == "") it.name else spacing + "└  " + it.name
                        // Start of a subsection
                        var i = menuID + 1
                        var temp = i
                        var off = false

                        var currentMenuItem : MenuItem? = null

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
                                Log.e("e",name)
                                if(!name.equals("README.md") && !name.first().equals('.')) {
                                    currentMenuItem = binding.navView.menu.add(menuItemName)
                                        .setOnMenuItemClickListener {
                                            binding.drawerLayout.close()

                                            // Grab the raw text---
                                            lifecycleScope.launch {
                                                mainViewModel.getRawText(
                                                    potentialURL,
                                                    name,
                                                    it,
                                                    gitItem
                                                )
                                            }

                                            true
                                        }
                                }
                        }




                        // Disable all non-top levels
                        if(spacing != "" && currentMenuItem != null) {
                            currentMenuItem.setVisible(false)
                        }

                    }


                    if(it.type == "dir") {
                        //Recursive call--
                            if(index < ref.conts.size)
                                recursiveMakeMenu(ref.conts[index], spacing + "   ")
                    }
                    else {
                        // File level hit- this is a Markdown file.
                        Log.e("", it.name)

                    }


                }

            }
        }


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation_drawer, menu)
       // menuInflater.inflate(R.menu.nav_drawer, menu)

        // Menu has been created. Do any menu modifications--
        if(mainViewModel.getSavedToken() == "null")
        findViewById<ImageView>(R.id.imageVieww)?.setOnClickListener {
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



}


class LoginToken {
    var access_token: String = ""
    var login : String = ""
    var avatar_url : String = ""
    var name : String = ""
    var res : Drawable? = null
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

class GithubInfo {
    lateinit var commit : CommitInfo
}

class CommitInfo {
    var sha : String = ""
}

@Parcelize
class GithubItem : Parcelable {
    var name : String = ""
    var path : String = ""
    var type : String = ""
    var download_url : String = ""
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

    @GET("/repos/ryanhlewis/Central-Documentation/contents/{directoryInfo}")
    @Headers("Accept: application/vnd.github.v3+json")
    fun getRepoNotLoggedIn(
        @Path(value = "directoryInfo", encoded = true) directoryInfo : String
    ) : Call<ResponseBody>


    @GET("/repos/{user}/Central-Documentation")
    @Headers("Accept: application/vnd.github.v3+json")
    fun checkFork(
        @Header("Authorization") token: String,
        @Path(value = "user", encoded = true) user : String
    ) : Call<ResponseBody>

    @POST("/repos/ryanhlewis/Central-Documentation/forks")
    @Headers("Accept: application/vnd.github.v3+json")
    fun forkCentralDocumentation(
        @Header("Authorization") token: String
    ) : Call<ResponseBody>

    @POST("/repos/ryanhlewis/Central-Documentation/pulls")
    @Headers("Accept: application/vnd.github.v3+json")
    fun pullRequest(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ) : Call<ResponseBody>

    //@FormUrlEncoded
    @HTTP(method = "DELETE", path = "/applications/{client_id}/grant", hasBody = true)
    @Headers("Accept: application/json")
    fun logOut(
        @Header("Authorization") token: String,
        //@Field("access_token") access_token: String,
        @Body body : JsonObject,
        @Path(value = "client_id", encoded = true) clientId: String
    ): Call<ResponseBody>

    @GET
    fun getStringResponse(@Url url: String?): Call<String?>?

    // There are multiple steps to get a commit to work.
    // Adapted from pseudocode of https://stackoverflow.com/questions/11801983/how-to-create-a-commit-and-push-into-repo-with-github-api-v3?msclkid=6b4aba10c04311ec98b1a4f7bbc5a1fc

    @GET("/repos/{user}/Central-Documentation/branches/main")
    @Headers("Accept: application/vnd.github.v3+json")
    fun getSHA(
        @Header("Authorization") token: String,
        @Path(value = "user", encoded = true) user: String
    ): Call<ResponseBody>

    //@FormUrlEncoded
    @POST("/repos/{user}/Central-Documentation/git/blobs")
    @Headers("Accept: application/json")
    fun createBlob(
        @Header("Authorization") token: String,
        @Body body: JsonObject,
        //@Field("content") content: String,
        //@Field("encoding") encoding: String,
        @Path(value = "user", encoded = true) user: String
    ): Call<ResponseBody>

    @POST("/repos/{user}/Central-Documentation/git/trees")
    @Headers("Accept: application/vnd.github.v3+json")
    fun createTree(
        @Header("Authorization") token: String,
        @Body body: JsonObject,
        @Path(value = "user", encoded = true) user: String
    ): Call<ResponseBody>

    @POST("/repos/{user}/Central-Documentation/git/commits")
    fun createCommit(
        @Header("Authorization") token: String,
        @Body body: JsonObject,
        @Path(value = "user", encoded = true) user: String
    ): Call<ResponseBody>

    @POST("/repos/{user}/Central-Documentation/git/refs/heads/main")
    fun updateRef(
        @Header("Authorization") token: String,
        @Body body: JsonObject,
        @Path(value = "user", encoded = true) user: String
    ): Call<ResponseBody>

    @POST("/graphql")
    fun graphQL(
        @Header("Authorization") token: String,
        @Body body: JsonObject
    ): Call<ResponseBody>

    @GET("/repos/ryanhlewis/Central-Documentation/git/trees/main?recursive=1")
    @Headers("Accept: application/vnd.github.v3+json")
    fun getRecursiveDir(
        @Header("Authorization") token: String
    ): Call<ResponseBody>

    @GET("/repos/ryanhlewis/Central-Documentation/git/trees/main?recursive=1")
    @Headers("Accept: application/vnd.github.v3+json")
    fun getRecursiveDirNoAuth(
    ): Call<ResponseBody>


}

