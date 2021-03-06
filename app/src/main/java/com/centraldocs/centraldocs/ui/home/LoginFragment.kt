package com.centraldocs.centraldocs.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import centraldocs.centraldocs.R
import centraldocs.centraldocs.databinding.FragmentLoginBinding
import com.centraldocs.centraldocs.MainActivity

class LoginFragment : Fragment() {

private var _binding: FragmentLoginBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val homeViewModel =
            ViewModelProvider(this).get(LoginViewModel::class.java)

    _binding = FragmentLoginBinding.inflate(inflater, container, false)
    val root: View = binding.root

    //val textView: TextView = binding.textHome
    //homeViewModel.text.observe(viewLifecycleOwner) {
    //  textView.text = it
    //}
    return root
  }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val button = binding.button
        if (button != null) {
            button.setOnClickListener {

                // BUG - This goes to Github and errors out.

                val url = authorizationUrl
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url.toString())))

                // FUTURE- Webview in-app like how Github does its

                // Fix- FORCE the web browser.
                // From https://stackoverflow.com/questions/58800240/android-app-link-open-a-url-from-app-in-browser-without-triggering-app-link?msclkid=dc41876bc0c911ec961bd04171eb055d
                //val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
                //defaultBrowser.data = Uri.parse(url.toString())
                //startActivity(defaultBrowser)
            }
        }

        val button2 = binding.button2
        if (button2 != null) {
            button2.setOnClickListener {

                view?.findNavController()?.navigate(R.id.nav_home)


                var mainactivity : MainActivity
                mainactivity = (activity as MainActivity?)!!
                mainactivity.binding.appBarNavigationDrawer.toolbar.isVisible = true

                mainactivity.mainViewModel.noAuthGetRepos()


            }
        }


        // Future- move Login Code specifically to this fragment, NOT mainactivity.kt.


    }

    // Field from default config.
    val appId = "com.centraldocs.centraldocs"
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
            .appendQueryParameter("scope", "public_repo") // usually "repo"
            .appendQueryParameter("state", appId)
            .build()



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}