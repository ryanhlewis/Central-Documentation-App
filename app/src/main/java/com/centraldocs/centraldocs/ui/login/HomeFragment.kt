package com.centraldocs.centraldocs.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.AnimatorRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import centraldocs.centraldocs.R
import centraldocs.centraldocs.databinding.FragmentHomeBinding
import com.centraldocs.centraldocs.adapter.ItemAdapter
import com.centraldocs.centraldocs.data.Datasource
import com.centraldocs.centraldocs.fragments.ContributeFragment
import com.centraldocs.centraldocs.fragments.LanguageFragment
import com.centraldocs.centraldocs.fragments.ReviewFragment
import com.centraldocs.centraldocs.ui.login.InitialFragment

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
    return root
  }

  override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
    super.onViewCreated(itemView, savedInstanceState)

    val language = itemView.findViewById<Button>(R.id.languageBtn)
    language?.setOnClickListener {
      childFragmentManager.commit {
        replace<LanguageFragment>(R.id.fragmentContainerView)
        setReorderingAllowed(true)
        addToBackStack("name") // name can be null
      }
    }

    val contribute = itemView.findViewById<Button>(R.id.contributeBtn)
    contribute?.setOnClickListener {
      childFragmentManager.commit {
        replace<ContributeFragment>(R.id.fragmentContainerView)
        setReorderingAllowed(true)
        addToBackStack("name") // name can be null
      }
    }

    val review = itemView.findViewById<Button>(R.id.reviewBtn)
    review?.setOnClickListener {
      childFragmentManager.commit {
        replace<ReviewFragment>(R.id.fragmentContainerView)
        setReorderingAllowed(true)
        addToBackStack("name") // name can be null
      }
    }

    val image = itemView.findViewById<ImageView>(R.id.imageView)
    image?.setOnClickListener {
      childFragmentManager.commit{
        /*
        setCustomAnimations(
          @AnimatorRes enterTransition =  R.anim.slide_in,
          @AnimatorRes R.anim.slide
        )

         */
        replace<InitialFragment>(R.id.fragmentContainerView)
        setReorderingAllowed(true)
        addToBackStack("name")
      }
    }
  }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}