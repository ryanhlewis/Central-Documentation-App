package com.centraldocs.centraldocs.adapter

import android.content.Context
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import centraldocs.centraldocs.R
import com.centraldocs.centraldocs.GithubItem
import com.centraldocs.centraldocs.MainActivity
import com.centraldocs.centraldocs.fragments.LanguageFragment
import com.centraldocs.centraldocs.model.Subtopic
import com.centraldocs.centraldocs.model.Topic
import com.centraldocs.centraldocs.ui.home.HomeFragment
import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.security.AccessController.getContext

class SubAdapter(private val context: LanguageFragment,
                 private val dataset: List<Subtopic>, private val activity: MainActivity): RecyclerView.Adapter<SubAdapter.SubItemViewHolder>(){


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

        holder.textView.setOnClickListener {
            var link : String = when(holder.textView.text) {
                "\t\t\tNext.md" -> "https://raw.githubusercontent.com/ryanhlewis/Central-Documentation/main/C%23/Classes/Random/Next.md"
                "\t\t\tSystem.Object.md" -> "https://raw.githubusercontent.com/ryanhlewis/Central-Documentation/main/C%23/Classes/System.Object.md"
                "\t\t\tIGeneric.md" -> "https://raw.githubusercontent.com/ryanhlewis/Central-Documentation/main/C%23/Interfaces/Generic/IGeneric.md"
                "\t\t\tIObject.md" -> "https://raw.githubusercontent.com/ryanhlewis/Central-Documentation/main/C%23/Interfaces/Generic/IObject.md"
                "\t\t\tObject.md" -> "https://raw.githubusercontent.com/ryanhlewis/Central-Documentation/main/Java/Classes/Object.md"
                else -> "https://raw.githubusercontent.com/ryanhlewis/Central-Documentation/main/Kotlin/Classes/Any.md" // "Any.md"
            }

            var item = GithubItem()
            item.name = holder.textView.text.substring(6)
            item.download_url = link

            GlobalScope.launch{
                Log.e("e" ,"HI")
                activity.mainViewModel.getRawText(link, holder.textView.text.substring(6), null, item )
            }



        }
    }

    override fun getItemCount() = dataset.size
}