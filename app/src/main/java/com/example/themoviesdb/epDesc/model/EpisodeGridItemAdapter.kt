package com.example.themoviesdb.epDesc.model

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.themoviesdb.R
import com.example.themoviesdb.readEpisodeStories.ReadEpisodeStories


class EpisodeGridItemAdapter() : RecyclerView.Adapter<EpisodeGridItemAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var listItems: MutableList<EpisodeGridItemModel>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.episode_list_item, parent, false) //YE DEH LENA
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val episodeNo = listItems[position].episodeNo
        val airDate = listItems[position].airDate
        val overview = listItems[position].overview
        val episodeName = listItems[position].episodeName

        holder.episodeNo.text = "Episode $episodeNo"
        holder.airDate.text = "Air date: $airDate"
        holder.overview.text = overview
        holder.episodeName.text = episodeName

        holder.llLayout.setOnClickListener{
//            context.startActivity(Intent(context, ReadEpisodeStories::class.java))
        }

    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llLayout = itemView.findViewById(R.id.the_episode_big_view) as LinearLayout
        val episodeNo = itemView.findViewById(R.id.episode_no) as TextView
        val airDate = itemView.findViewById(R.id.episode_air_date) as TextView
        val overview = itemView.findViewById(R.id.episode_overview) as TextView
        val episodeName = itemView.findViewById(R.id.episode_name) as TextView
    }

    constructor(context: Context, listItems: MutableList<EpisodeGridItemModel>) : this() {
        this.context = context
        this.listItems = listItems
    }
}