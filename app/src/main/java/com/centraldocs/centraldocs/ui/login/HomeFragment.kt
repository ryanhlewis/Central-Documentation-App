package com.centraldocs.centraldocs.ui.home

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import centraldocs.centraldocs.R
import centraldocs.centraldocs.databinding.FragmentHomeBinding
import com.centraldocs.centraldocs.MainActivity
import io.noties.markwon.Markwon
import io.noties.markwon.image.ImagesPlugin
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

private var _binding: FragmentHomeBinding? = null
  private lateinit var markwon : Markwon
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


    markwon = Markwon.builder(root.context)
      .usePlugin(ImagesPlugin.create())
      .build()


    /* THIS IS FOR THE RECYCLER VIEW; DIDN'T KNOW WHERE TO PUT IT */
   /* val myDataset = Datasource().loadTopics()
    val recyclerView = binding.recycler
    Log.e("", recyclerView.toString())
    if (recyclerView != null) {
      recyclerView.adapter = ItemAdapter(this, myDataset)
    }
    if (recyclerView != null) {
      recyclerView.setHasFixedSize(true)
    }*/
    //val textView: TextView = binding.textHome
    //homeViewModel.text.observe(viewLifecycleOwner) {
    //  textView.text = it
    //}
    return root
  }


  override fun onStart() {
    super.onStart()

    // Send the network request
    var mainactivity: MainActivity
    mainactivity = MainActivity.getMainInstance()

    var downX = 0f
    var upX = 0f
    var downY = 0f
    var upY = 0f
    var bool = true
    binding.scrollViewwww.setOnTouchListener(object : View.OnTouchListener {
      override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
          MotionEvent.ACTION_DOWN -> {
            downX = event.x
            downY = event.y
          }
          MotionEvent.ACTION_MOVE -> {
            // Since ACTION_DOWN is being swallowed by another view-
            if(bool) {
              bool = false
              downX = event.x
              downY = event.y
            }
          }
          MotionEvent.ACTION_UP -> {
            upX = event.x
            upY = event.y
            val deltaX: Float = downX - upX
            val deltaY: Float = downY - upY
            Log.e("","swiped up"  + downY.toString() + ' ' + upY.toString())
            Log.e("","swiped up"  + deltaY.toString())
            Log.e("","swiped left"  + deltaX.toString())
            bool = true
            return if (deltaX <= -50 && Math.abs(deltaY) < 200) {
              mainactivity.binding.drawerLayout.open()
              true
            } else {
              false
            }
          }
        }
        return false
      }
    })


    var hey = binding.howto
    GlobalScope.launch {
      var words = mainactivity.mainViewModel.getSuperRawTextInitial("https://raw.githubusercontent.com/ryanhlewis/Central-Documentation/main/.howto.md")
      if (words != null) {
        mainactivity.runOnUiThread {
          markwon.setMarkdown(hey, words + "\n \n \n \u200B")
        }
      }
    }


  }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}