package com.centraldocs.centraldocs.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import centraldocs.centraldocs.R
import com.centraldocs.centraldocs.MainActivity
import com.centraldocs.centraldocs.adapter.ItemAdapter
import com.centraldocs.centraldocs.data.Datasource

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class LanguageFragment : Fragment(R.layout.fragment_language) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_language, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        val myDataset = Datasource().loadTopics()
        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.apply {
            //layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView

            var mainactivity : MainActivity
            mainactivity = (activity as MainActivity?)!!

            if (recyclerView != null) {
                recyclerView.adapter = ItemAdapter(this@LanguageFragment, myDataset, mainactivity)
                recyclerView.setHasFixedSize(true)
            }
        }
    }

}