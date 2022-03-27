package com.example.myapplicationnn.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplicationnn.R

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel

        val button = view?.findViewById<Button>(R.id.button)
        if (button != null) {
            button.setOnClickListener {
                val url = authorizationUrl
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url.toString())))
            }
        }



    }

    // Field from default config.
    val appId = "com.example.myapplicationnn"
    // Field from default config.
    val clientId = "60a1586f001e9e2a5be6"
    // Field from default config.
    val GITHUB_SECRET = "b2d158f949d3615078eaf570ff99eba81cfa1ff9"
    val redirectUrl = ""


    val authorizationUrl: Uri
        get() = Uri.Builder().scheme("https")
            .authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("redirect_uri", redirectUrl)
            .appendQueryParameter("scope", "public_repo")
            .appendQueryParameter("state", appId)
            .build()



}