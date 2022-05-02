package com.centraldocs.centraldocs.ui.gallery

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import centraldocs.centraldocs.databinding.FragmentGalleryBinding
import com.centraldocs.centraldocs.GithubItem
import com.centraldocs.centraldocs.MainActivity
import io.noties.markwon.Markwon
import io.noties.markwon.image.ImagesPlugin
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class GalleryFragment : Fragment() {

private var _binding: FragmentGalleryBinding? = null

    lateinit var sendButton : View
    lateinit var viewButton : View
    lateinit var editButton : View


    lateinit var textView : TextView
    lateinit var markwon : Markwon
    lateinit var originalText : String
    lateinit var gitItem : GithubItem

  // This property is only valid between onCreateView and
  // onDestroyView.
  val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

    _binding = FragmentGalleryBinding.inflate(inflater, container, false)
    val root: View = binding.root


      textView = binding.textFunctionName

      // obtain an instance of Markwon
      //val markwon = Markwon.create(root.context)

      markwon = Markwon.builder(root.context)
          .usePlugin(ImagesPlugin.create())
          .build()

      originalText = ""

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

      }

      gitItem  = GithubItem()

      if(arguments?.get("gitItem") != null) {
          gitItem = arguments?.get("gitItem") as GithubItem
      }


      // Attach the bindings for the buttons,  0 -> edit button, 1 -> view button

      return root
  }


    override fun onStart() {
        super.onStart()

        //if(!galleryViewModel.ranOnce) {
        GlobalScope.launch {


            // DANGEROUS - but functional. Allows Kotlin to make the binding,
            // by saying the rest of this code is a coroutine so that it can continue
            // to make the binding.
            //delay(10)

            //val blockingQueue: BlockingQueue<Collection<ActivityNavigationDrawerBinding?>?> = ArrayBlockingQueue(1)
            //blockingQueue.add(MainActivity.getMainInstance())
            //blockingQueue.take()




            var mainactivity: MainActivity
            mainactivity = MainActivity.getMainInstance()

            mainactivity.runOnUiThread {

                sendButton = mainactivity.binding.appBarNavigationDrawer.toolbar.get(0)
                editButton = mainactivity.binding.appBarNavigationDrawer.toolbar.get(1)
                viewButton = mainactivity.binding.appBarNavigationDrawer.toolbar.get(2)

                // Disable view for view button when not editing
                viewButton.isVisible = false
                editButton.isVisible = true
                sendButton.isVisible = true

                editButton.setOnClickListener {

                    textView.isVisible = false
                    binding.editText.isVisible = true
                    viewButton.isVisible = true
                    editButton.isVisible = false

                    true
                }

                viewButton.setOnClickListener {

                    markwon.setMarkdown(textView, binding.editText.text.toString())
                    binding.editText.isVisible = false
                    textView.isVisible = true
                    editButton.isVisible = true
                    viewButton.isVisible = false

                    true
                }

                sendButton.setOnClickListener {

                    // Put down keyboard
                    mainactivity.hideSoftKeyboard()

                    // Switch to view mode
                    markwon.setMarkdown(textView, binding.editText.text.toString())
                    binding.editText.isVisible = false
                    textView.isVisible = true
                    editButton.isVisible = true
                    viewButton.isVisible = false


                    // Send pull request
                    mainactivity.mainViewModel.sendPullRequest(
                        binding.editText.text.toString(),
                        originalText, gitItem
                    )


                    true
                }
            }

        }


    }



override fun onDestroyView() {
        super.onDestroyView()

        _binding = null

    viewButton.isVisible = false
    editButton.isVisible = false
    sendButton.isVisible = false


}
}