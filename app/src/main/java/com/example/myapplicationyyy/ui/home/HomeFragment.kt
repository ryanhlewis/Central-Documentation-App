package com.example.myapplicationyyy.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplicationyyy.MainActivity
import com.example.myapplicationyyy.R
import com.example.myapplicationyyy.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

private var _binding: FragmentHomeBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val textView: TextView = binding.textHome
    homeViewModel.text.observe(viewLifecycleOwner) {
      textView.text = it
    }
    return root
  }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val button = binding.button
        if (button != null) {
            button.setOnClickListener {
                val url = authorizationUrl
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url.toString())))
            }
        }

        // Future- move Login Code specifically to this fragment, NOT mainactivity.kt.


    }

    // Field from default config.
    val appId = "com.example.myapplicationyyy"
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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}