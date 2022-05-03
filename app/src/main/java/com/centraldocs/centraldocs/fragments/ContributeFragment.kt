package com.centraldocs.centraldocs.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import centraldocs.centraldocs.R
import com.centraldocs.centraldocs.adapter.ItemAdapter
import com.centraldocs.centraldocs.data.Datasource

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


// ADD CONTRIBUTIONS TO A RUNNING LIST IN REPOSITORY

class ContributeFragment : Fragment(R.layout.fragment_contribute) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contribute, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        val name : EditText = itemView.findViewById(R.id.enter_name)
        val language : EditText = itemView.findViewById(R.id.enter_language)
        val subtopic : EditText = itemView.findViewById(R.id.enter_topic)

        /*
        if (language.length() > 1 && subtopic.length() > 1) {
        }

         */
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ContributeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContributeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}