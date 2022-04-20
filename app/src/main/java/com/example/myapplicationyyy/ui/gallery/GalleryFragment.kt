package com.example.myapplicationyyy.ui.gallery

import android.R
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplicationyyy.GithubItem
import com.example.myapplicationyyy.MainActivity
import com.example.myapplicationyyy.databinding.FragmentGalleryBinding
import io.noties.markwon.Markwon


class GalleryFragment : Fragment() {

private var _binding: FragmentGalleryBinding? = null

    lateinit var sendButton : View
    lateinit var viewButton : View
    lateinit var editButton : View

  // This property is only valid between onCreateView and
  // onDestroyView.
  public val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

    _binding = FragmentGalleryBinding.inflate(inflater, container, false)
    val root: View = binding.root


      val textView: TextView = binding.textFunctionName

      // obtain an instance of Markwon
      val markwon = Markwon.create(root.context)

      var originalText = ""

      // Disable the editText
      binding.editText.isVisible = false
      //textView.isEnabled = false

      // Now, we can use Markdown. Begin to index and download Markdown.

      // set markdown on edited text
      arguments?.getString("md")?.let {

          // set markdown
          markwon.setMarkdown(textView, it)

          // set markdown on edited text
          val editable: Editable = SpannableStringBuilder(it)
          binding.editText.text =  editable

          originalText = it

      };

      var gitItem: GithubItem = GithubItem()

      if(arguments?.get("gitItem") != null) {
          gitItem = arguments?.get("gitItem") as GithubItem
      }


      // Attach the bindings for the buttons,  0 -> edit button, 1 -> view button

      var mainactivity : MainActivity
      mainactivity = (activity as MainActivity?)!!

      sendButton = mainactivity.binding.appBarNavigationDrawer.toolbar.get(0)
      editButton = mainactivity.binding.appBarNavigationDrawer.toolbar.get(1)
      viewButton = mainactivity.binding.appBarNavigationDrawer.toolbar.get(2)

      // Disable view for view button when not editing
     viewButton.isVisible = false
      editButton.isVisible = true
      sendButton.isVisible = true

      editButton.setOnClickListener{

          textView.isVisible = false
          binding.editText.isVisible = true
          viewButton.isVisible = true
          editButton.isVisible = false

          true
      }

      viewButton.setOnClickListener{

          markwon.setMarkdown(textView, binding.editText.text.toString())
          binding.editText.isVisible = false
          textView.isVisible = true
          editButton.isVisible = true
          viewButton.isVisible = false

          true
      }

      sendButton.setOnClickListener {


          mainactivity.sendPullRequest(
                  binding.editText.text.toString(),
                  originalText, gitItem
              )

          true
      }



      return root
  }


override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    viewButton.isVisible = false
    editButton.isVisible = false
    sendButton.isVisible = false


}
}