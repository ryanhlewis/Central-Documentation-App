package com.centraldocs.centraldocs

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import centraldocs.centraldocs.R


class SettingsFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        var sharedPreferences =
            context?.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE)

        val ACCESSTOKEN : String? = sharedPreferences?.getString("access_token","null")

        if(ACCESSTOKEN != "null") {
            val signaturePreference: EditTextPreference? = findPreference("logout")
            signaturePreference?.isVisible = true

            if (signaturePreference != null) {
                signaturePreference.setOnPreferenceClickListener {
                    var mainactivity : MainActivity
                    mainactivity = (activity as MainActivity?)!!
                    mainactivity.logOut()
                    true
                }
            }

        }

        initializeThemePreference()

    }


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


}