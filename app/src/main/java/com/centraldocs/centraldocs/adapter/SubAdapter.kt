package com.centraldocs.centraldocs.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import centraldocs.centraldocs.R
import com.centraldocs.centraldocs.model.Subtopic
import com.centraldocs.centraldocs.ui.home.HomeFragment

class SubAdapter(private val context: HomeFragment,
                 private val dataset: List<Subtopic>): RecyclerView.Adapter<SubAdapter.SubItemViewHolder>(){

    class SubItemViewHolder(private val view: View): RecyclerView.ViewHolder(view){
        val textView: TextView = view.findViewById(R.id.item_sub)
    }

    // takes list_item xml and puts it on main xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_topic, parent, false)
        return SubItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: SubItemViewHolder, position: Int) {
        // finds position in list
        val item = dataset[position]
        // tells how to format on screen
        holder.textView.text = context.resources.getString(item.stringID)

    }

    override fun getItemCount() = dataset.size
}