package com.centraldocs.centraldocs.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import centraldocs.centraldocs.R
import com.centraldocs.centraldocs.MainActivity
import com.centraldocs.centraldocs.fragments.LanguageFragment
import com.centraldocs.centraldocs.model.Topic
import com.centraldocs.centraldocs.ui.home.HomeFragment

class ItemAdapter(private val context: LanguageFragment,
                  private val dataset: List<Topic>, private val activity: MainActivity): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {


    class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.item_topic)
        val subTopicRecyclerView: RecyclerView = view.findViewById(R.id.sub_recyclerView)
        var isClicked: Boolean = false
    }

    // takes list_item xml and puts it on main xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        // finds position in list
        val item = dataset[position]
        holder.textView.text = context.resources.getString(item.stringID)
        holder.subTopicRecyclerView.isVisible = false

        holder.textView.setOnClickListener {
            if (!holder.isClicked) {
                holder.subTopicRecyclerView.isVisible = true
                holder.subTopicRecyclerView.adapter = SubAdapter(context, item.subtopic_list, activity)
            }
            else {
                holder.subTopicRecyclerView.isVisible = false
            }
            holder.isClicked = !holder.isClicked
        }
    }

    override fun getItemCount() = dataset.size
}
