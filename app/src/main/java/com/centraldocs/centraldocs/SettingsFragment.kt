package com.centraldocs.centraldocs

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import centraldocs.centraldocs.R
import com.jaredrummler.android.colorpicker.ColorPickerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SettingsFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)


        var sharedPreferences =
            context?.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)

        val ACCESSTOKEN : String? = sharedPreferences?.getString("access_token","null")


        // Text Size Pref
        var number : Int? = sharedPreferences?.getInt("textsize",16)
        var mainactivity : MainActivity
        mainactivity = (activity as MainActivity?)!!
        var stylee = when (number) {
            6 -> R.style.FontSize6
            7 -> R.style.FontSize7
            8 -> R.style.FontSize8
            9 -> R.style.FontSize9
            10 -> R.style.FontSize10
            11 -> R.style.FontSize11
            12 -> R.style.FontSize12
            13 -> R.style.FontSize13
            14 -> R.style.FontSize14
            15 -> R.style.FontSize15
            16 -> R.style.FontSize16
            17 -> R.style.FontSize17
            18 -> R.style.FontSize18
            else -> R.style.FontSize12
        }
        if(stylee != null)
            mainactivity.setTheme(stylee)

        // Logout Visibility
        val signaturePreference: Preference? = findPreference("logout")
        signaturePreference?.isVisible = false
        val catPref: PreferenceCategory? = findPreference("logoutcategory") as PreferenceCategory?
        catPref?.isVisible = false


        if(ACCESSTOKEN != "null" && ACCESSTOKEN != "") {

            signaturePreference?.isVisible = true
            catPref?.isVisible = true

            if (signaturePreference != null) {
                signaturePreference.setOnPreferenceClickListener {

                    // Attempted logout using Github API,
                    // almost always fails.
                    var mainactivity : MainActivity
                    mainactivity = (activity as MainActivity?)!!
                    //mainactivity.mainViewModel.logOut()

                    // Patched logout, simply deletes token, restarts app.
                    if (sharedPreferences != null) {
                        sharedPreferences.edit().putString("access_token", "null").commit()
                    }
                    val refresh = Intent(activity, MainActivity::class.java)
                    startActivity(refresh)

                    true
                }
            }

        }

        val githubPref: Preference? = findPreference("Github")
        if (githubPref != null) {
            githubPref.setOnPreferenceClickListener {

                val url = "https://github.com/ryanhlewis/Central-Documentation";
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url.toString())))


                true
            }
        }

        val textPref : Preference? = findPreference("text")
        if(textPref != null) {
            textPref.setEnabled(false);
        }


        initializeThemePreference()
        initializeColorPreference()
        initializeTextPreference()

    }

    /*
    // Sets background color of navigation drawer
    navView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))

    // Sets background color of app pages
    drawerLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.black))

    // Sets toolbar color
    binding.appBarNavigationDrawer.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
*/


    private fun initializeThemePreference() {
        val themePreference: Preference? = findPreference("theme_color")
        if (themePreference != null) {
            themePreference.setOnPreferenceChangeListener(createThemeChangeListener())
        }
    }

    /**
     * Creates and returns a listener, which allows to adapt the app's theme, when the value of the
     * corresponding preference has been changed.
     *
     * @return The listener, which has been created, as an instance of the type [ ]
     */
    private fun createThemeChangeListener(): Preference.OnPreferenceChangeListener? {
        return object : Preference.OnPreferenceChangeListener {
            override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
                Log.e("",newValue.toString())

                var sharedPreferences =
                    context?.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
                if (sharedPreferences != null) {
                    sharedPreferences.edit().putString("theme_color", newValue.toString()).commit()
                };

                val darkModeString = newValue.toString()
                val darkModeValues = resources.getStringArray(R.array.themes_array)
                when (darkModeString) {
                    darkModeValues[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    darkModeValues[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    darkModeValues[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }


                activity!!.recreate()

                return true
            }
        }

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

    private fun initializeColorPreference() {
        val themePreference: Preference? = findPreference("changecolor")
        /*if (themePreference != null) {
            GlobalScope.launch {
                delay(2000)
                var mainactivity: MainActivity
                mainactivity = (activity as MainActivity?)!!
                var colorPickerView: ColorPickerView = mainactivity.findViewById(R.id.colorMenu)
                colorPickerView.setColor(R.color.black, true)
            }
        }*/
        if (themePreference != null) {
            themePreference.setOnPreferenceChangeListener(createColorChangeListener())
        }
    }

    /**
     * Creates and returns a listener, which allows to adapt the app's theme, when the value of the
     * corresponding preference has been changed.
     *
     * @return The listener, which has been created, as an instance of the type [ ]
     */
    private fun createColorChangeListener(): Preference.OnPreferenceChangeListener? {
        return object : Preference.OnPreferenceChangeListener {
            override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
                Log.e("",newValue.toString())

                var chosenColor = newValue as Int


                var sharedPreferences =
                    context?.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
                if (sharedPreferences != null) {
                    sharedPreferences.edit().putInt("color_color", chosenColor).commit()
                };


                var mainactivity : MainActivity
                mainactivity = (activity as MainActivity?)!!
                mainactivity.binding.appBarNavigationDrawer.toolbar.setBackgroundColor(chosenColor)
                mainactivity.binding.navView.getHeaderView(0).setBackgroundColor(chosenColor)

                mainactivity.getWindow().setStatusBarColor(chosenColor);

                // TODO
                // Make color choice update the whole theme of the app.
                // Seems near impossible to create a style theme programatically?

                if(isColorDark(newValue as Int)) {
                    mainactivity.binding.appBarNavigationDrawer.toolbar.setTitleTextColor(
                        ContextCompat.getColor(mainactivity, R.color.white)
                    )
                    mainactivity.findViewById<TextView>(R.id.textViewww).setTextColor(ContextCompat.getColor(mainactivity, R.color.white))
                    mainactivity.findViewById<TextView>(R.id.textVieww).setTextColor(ContextCompat.getColor(mainactivity, R.color.white))
    //                mainactivity.binding.appBarNavigationDrawer.toolbar.navigationIcon?.setTint(ContextCompat.getColor(mainactivity, R.color.white))

                    mainactivity.binding.appBarNavigationDrawer.toolbar.navigationIcon?.setColorFilter(-1,
                        PorterDuff.Mode.MULTIPLY)
                   // mainactivity.binding.appBarNavigationDrawer.toolbar.get(0).setBackgroundColor(-1)
                   // mainactivity.binding.appBarNavigationDrawer.toolbar.get(0).findViewById<Button>(R.id.sendButton).drawa

                    DrawableCompat.setTint(
                        DrawableCompat.wrap(mainactivity.binding.appBarNavigationDrawer.toolbar.get(0).findViewById<ImageButton>(R.id.sendButton).drawable),
                        ContextCompat.getColor(context!!, R.color.white)
                    );
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(mainactivity.binding.appBarNavigationDrawer.toolbar.get(1).findViewById<ImageButton>(R.id.editButton).drawable),
                        ContextCompat.getColor(context!!, R.color.white)
                    );
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(mainactivity.binding.appBarNavigationDrawer.toolbar.get(2).findViewById<ImageButton>(R.id.viewButton).drawable),
                        ContextCompat.getColor(context!!, R.color.white)
                    );
                    mainactivity.binding.appBarNavigationDrawer.toolbar.getNavigationIcon()
                        ?.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                    //activity!!.setTheme(R.style.ThemeToolbarWhite1)

                    //mainactivity.binding.navView.itemIconTintList = getResources().getColorStateList(R.color.white)
                    //mainactivity.binding.root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
                    //activity!!.setTheme(R.style.ThemeToolbarWhite)

                }
                else {
                    mainactivity.binding.appBarNavigationDrawer.toolbar.setTitleTextColor(
                        ContextCompat.getColor(mainactivity, R.color.black)
                    )
                    mainactivity.findViewById<TextView>(R.id.textViewww).setTextColor(ContextCompat.getColor(mainactivity, R.color.black))
                    mainactivity.findViewById<TextView>(R.id.textVieww).setTextColor(ContextCompat.getColor(mainactivity, R.color.black))
//                    mainactivity.binding.appBarNavigationDrawer.toolbar.navigationIcon?.setTint(ContextCompat.getColor(mainactivity, R.color.black))
                    //mainactivity.binding.appBarNavigationDrawer.toolbar.get(0).setBackgroundColor(0)
                    Log.e("navIcon",mainactivity.binding.appBarNavigationDrawer.toolbar.navigationIcon.toString())

                   // mainactivity.binding.appBarNavigationDrawer.toolbar.navigationIcon?.setTint(0)
                    mainactivity.binding.appBarNavigationDrawer.toolbar.navigationIcon?.setColorFilter(0,
                        PorterDuff.Mode.OVERLAY)

                    DrawableCompat.setTint(
                        DrawableCompat.wrap(mainactivity.binding.appBarNavigationDrawer.toolbar.get(0).findViewById<ImageButton>(R.id.sendButton).drawable),
                        ContextCompat.getColor(context!!, R.color.black)
                    );
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(mainactivity.binding.appBarNavigationDrawer.toolbar.get(1).findViewById<ImageButton>(R.id.editButton).drawable),
                        ContextCompat.getColor(context!!, R.color.black)
                    );
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(mainactivity.binding.appBarNavigationDrawer.toolbar.get(2).findViewById<ImageButton>(R.id.viewButton).drawable),
                        ContextCompat.getColor(context!!, R.color.black)
                    );
                    mainactivity.binding.appBarNavigationDrawer.toolbar.getNavigationIcon()
                        ?.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);

                    //activity!!.setTheme(R.style.ThemeToolbarBlack1)

                    //mainactivity.binding.navView.itemIconTintList = getResources().getColorStateList(R.color.black)
                    //mainactivity.binding.root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_DARK_NAVIGATION_BAR);
                    //activity!!.setTheme(R.style.ThemeToolbarBlack)
                    //mainactivity.binding.appBarNavigationDrawer.



                }

                //mainactivity.binding.appBarNavigationDrawer.toolbar.setBackgroundColor(ContextCompat.getColor(mainactivity, R.color.black))

                //activity!!.recreate()
                return true
            }
        }

    }


    private fun initializeTextPreference() {
        val themePreference: Preference? = findPreference("textsize")
        if (themePreference != null) {
            themePreference.setOnPreferenceChangeListener(createTextChangeListener())
        }
    }

    /**
     * Creates and returns a listener, which allows to adapt the app's theme, when the value of the
     * corresponding preference has been changed.
     *
     * @return The listener, which has been created, as an instance of the type [ ]
     */
    private fun createTextChangeListener(): Preference.OnPreferenceChangeListener? {
        return object : Preference.OnPreferenceChangeListener {
            override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
                Log.e("",newValue.toString())

                var mainactivity : MainActivity
                mainactivity = (activity as MainActivity?)!!

                var number = newValue as Int + 6


                Log.e("",number.toString())

                var stylee = when (number) {
                    6 -> R.style.FontSize6
                    7 -> R.style.FontSize7
                    8 -> R.style.FontSize8
                    9 -> R.style.FontSize9
                    10 -> R.style.FontSize10
                    11 -> R.style.FontSize11
                    12 -> R.style.FontSize12
                    13 -> R.style.FontSize13
                    14 -> R.style.FontSize14
                    15 -> R.style.FontSize15
                    16 -> R.style.FontSize16
                    17 -> R.style.FontSize17
                    18 -> R.style.FontSize18
                    else -> R.style.FontSize12
                }
                Log.e("",stylee.toString())

                var sharedPreferences =
                    context?.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
                if (sharedPreferences != null) {
                    sharedPreferences.edit().putInt("textsize", number).commit()
                };

                activity!!.setTheme(stylee)

                activity!!.recreate()

                //val themePreference: Preference? = findPreference("text")
                //themePreference.title.

                return true
            }
        }

    }


}