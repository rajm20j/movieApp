package com.example.themoviesdb.showDesc.model

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
import com.example.themoviesdb.epDesc.EpisodeDesc
import com.example.themoviesdb.utils.Utils

class ListAllSeasonGridAdapter() : RecyclerView.Adapter<ListAllSeasonGridAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var listItems: MutableList<SeasonsGridItemModel>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.season_list_item, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val seasonNo = listItems[position].seasonNo
        val airDate = listItems[position].airDate

        holder.seasonNo.text = "Season $seasonNo"
        holder.airDate.text = "First episode: $airDate"

        holder.llLayout.setOnClickListener{
            Utils.episodList = listItems[position].episodes
            Utils.currentShowSeason = seasonNo.toString()
            val intent = Intent(context, EpisodeDesc::class.java)
//            intent.putExtra("seasonNo", seasonNo)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llLayout = itemView.findViewById(R.id.the_big_view) as LinearLayout
        val seasonNo = itemView.findViewById(R.id.season_no) as TextView
        val airDate = itemView.findViewById(R.id.air_date) as TextView
    }

    constructor(context: Context, listItems: MutableList<SeasonsGridItemModel>) : this() {
        this.context = context
        this.listItems = listItems
    }
}
