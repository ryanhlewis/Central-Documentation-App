package com.example.myapplicationyyy.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplicationyyy.databinding.FragmentGalleryBinding
import io.noties.markwon.Markwon

class GalleryFragment : Fragment() {

private var _binding: FragmentGalleryBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

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

      // Now, we can use Markdown. Begin to index and download Markdown.

        // set markdown
      markwon.setMarkdown(textView, "**Hello there!**");


      val textView2: TextView = binding.textFunctionDesc
      galleryViewModel.text2.observe(viewLifecycleOwner) {
          textView2.text = it
      }
    return root
  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}