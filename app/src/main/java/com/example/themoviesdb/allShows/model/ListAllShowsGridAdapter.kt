package com.example.themoviesdb.allShows.model

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.themoviesdb.R
import com.example.themoviesdb.extras.Constants
import com.example.themoviesdb.showDesc.ShowDesc

class ListAllShowsGridAdapter() : RecyclerView.Adapter<ListAllShowsGridAdapter.ViewHolder>() {

    lateinit var context: Context
    lateinit var listItems: MutableList<GridItemModel>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.show_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listPositionItem = listItems[position]
        val posterPath = listPositionItem.posterPath
        Glide.with(context)
            .load(Constants.IMAGE_BASE_URL + posterPath)
            .placeholder(R.drawable.movie_place_holder)
            .into(holder.banner)

        holder.rlLayout.setOnClickListener{
            val intent = Intent(context, ShowDesc::class.java)
            intent.putExtra("overview", listPositionItem.overview)
            intent.putExtra("backdrop", listPositionItem.backdropPath)
            intent.putExtra("title", listPositionItem.title)
            intent.putExtra("tvId", listPositionItem.tvId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rlLayout = itemView.findViewById(R.id.theBigView) as LinearLayout
        val banner = itemView.findViewById(R.id.show_banner) as ImageView
    }

    constructor(context: Context, listItems: MutableList<GridItemModel>) : this() {
        this.context = context
        this.listItems = listItems
    }
}
