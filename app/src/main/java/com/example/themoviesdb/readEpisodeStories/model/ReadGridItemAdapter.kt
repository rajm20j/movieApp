package com.example.themoviesdb.readEpisodeStories.model

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.themoviesdb.R
import com.example.themoviesdb.readEpisodeStories.ReadEpisodeStoriesVM
import com.example.themoviesdb.utils.Utils
import com.example.themoviesdb.writeAStory.WriteAStory

class ReadGridItemAdapter() : RecyclerView.Adapter<ReadGridItemAdapter.ViewHolder>()  {

    lateinit var context: Context
    lateinit var listItems: MutableList<ReadEpisodeGridItemModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.read_episode_list_item, parent, false) //YE DEH LENA
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position != 0)
        {
            val listItemAtPosition = listItems[position]
            holder.episodeNo.text = "S${Utils.currentShowSeason}E${Utils.currentShowEpisode}"
            holder.episodeName.text = listItemAtPosition.title
            holder.overview.text = listItemAtPosition.descr
        }
        else
        {
            holder.llLayout.setBackgroundResource(R.drawable.plus_symbol)
            holder.overview.visibility = View.GONE
            holder.episodeName.visibility = View.GONE
            holder.episodeNo.visibility= View.GONE
            holder.linearLaoyutLine.visibility = View.GONE
            holder.llLayout.setOnClickListener {
                context.startActivity(Intent(context, WriteAStory::class.java))
                (context as Activity).finish()
            }
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llLayout = itemView.findViewById(R.id.read_the_episode_big_view) as LinearLayout
        val episodeNo = itemView.findViewById(R.id.read_episode_no) as TextView
        val overview = itemView.findViewById(R.id.read_episode_overview) as TextView
        val episodeName = itemView.findViewById(R.id.read_episode_name) as TextView
        var linearLaoyutLine = itemView.findViewById(R.id.read_linear_layout_line) as LinearLayout
    }

    constructor(context: Context, listItems: MutableList<ReadEpisodeGridItemModel>) : this() {
        this.context = context
        this.listItems = listItems
    }
}