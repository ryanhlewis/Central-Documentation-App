package com.example.myapplicationyyy

import android.R.attr.button
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat


class SettingsFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        var sharedPreferences =
            context?.getSharedPreferences("MyPrefs", AppCompatActivity.MODE_PRIVATE);

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

    }


}