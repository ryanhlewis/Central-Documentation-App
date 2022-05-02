package com.centraldocs.centraldocs

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
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


        initializeThemePreference()
        initializeColorPreference()

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
        return if (darkness < 0.5) {
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

                var sharedPreferences =
                    context?.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)
                if (sharedPreferences != null) {
                    sharedPreferences.edit().putString("color_color", newValue.toString()).commit()
                };

                var chosenColor = newValue as Int

                var mainactivity : MainActivity
                mainactivity = (activity as MainActivity?)!!
                mainactivity.binding.appBarNavigationDrawer.toolbar.setBackgroundColor(chosenColor)
                mainactivity.binding.navView.getHeaderView(0).setBackgroundColor(chosenColor)
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
                }
                else {
                    mainactivity.binding.appBarNavigationDrawer.toolbar.setTitleTextColor(
                        ContextCompat.getColor(mainactivity, R.color.black)
                    )
                    mainactivity.findViewById<TextView>(R.id.textViewww).setTextColor(ContextCompat.getColor(mainactivity, R.color.black))
                    mainactivity.findViewById<TextView>(R.id.textVieww).setTextColor(ContextCompat.getColor(mainactivity, R.color.black))
//                    mainactivity.binding.appBarNavigationDrawer.toolbar.navigationIcon?.setTint(ContextCompat.getColor(mainactivity, R.color.black))


                }

                //mainactivity.binding.appBarNavigationDrawer.toolbar.setBackgroundColor(ContextCompat.getColor(mainactivity, R.color.black))

                //activity!!.recreate()
                return true
            }
        }

    }


}