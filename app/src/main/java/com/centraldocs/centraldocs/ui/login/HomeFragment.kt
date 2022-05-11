package com.centraldocs.centraldocs.ui.home

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import centraldocs.centraldocs.R
import centraldocs.centraldocs.databinding.FragmentHomeBinding
import com.centraldocs.centraldocs.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}