package com.centraldocs.centraldocs.fragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import centraldocs.centraldocs.R
import android.widget.Toast


class ReviewFragment : Fragment(R.layout.fragment_review) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        val ratingBar: RatingBar = itemView.findViewById(R.id.rating_bar)
        ratingBar.rating = 3f
        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            Toast.makeText(context, "Rating:$rating", Toast.LENGTH_SHORT).show()
        }

        val submitButton: Button = itemView.findViewById(R.id.submitBtn)
        submitButton.setOnClickListener {
            Toast.makeText(context, "Thank you!", Toast.LENGTH_SHORT).show()
        }

        val likeResponse : EditText = itemView.findViewById(R.id.like_response)
        val dislikeResponse : EditText = itemView.findViewById(R.id.dislike_response)


        fun ObjectAnimator.disabledViewDuringAnimation(view: View) {
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    view.isEnabled = false
                }

                override fun onAnimationEnd(animation: Animator?) {
                    view.isEnabled = true
                }
            })
        }

        fun translator() {
            val animator = ObjectAnimator.ofFloat(ratingBar,View.TRANSLATION_Y, 200f)
            animator.disabledViewDuringAnimation(ratingBar)
            animator.start()
        }
    }
}