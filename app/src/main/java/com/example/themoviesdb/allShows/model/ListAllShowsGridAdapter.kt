package com.example.themoviesdb.allShows.model

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.themoviesdb.R
import com.example.themoviesdb.extras.Constants
import com.example.themoviesdb.showDesc.ShowDesc
import com.example.themoviesdb.utils.Utils

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
//            intent.putExtra("title", listPositionItem.title)
            Utils.currentShowTitle = listPositionItem.title.toString()
            intent.putExtra("tvId", listPositionItem.tvId)
            context.startActivity(intent)
        }
    }

    fun submitList(newList: MutableList<GridItemModel>)
    {
//        Log.v("TEST2", "submitList")
        val oldList = listItems
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            ReadItemDiffUtil(oldList, newList)
        )
//        listItems.clear()
        listItems = newList
        for(items in listItems)
            Log.v("TEST2", items.title.toString())

        diffResult.dispatchUpdatesTo(this)
    }

    class ReadItemDiffUtil(
        var oldList: MutableList<GridItemModel>,
        var newList: MutableList<GridItemModel>
    ): DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//            Log.v("TEST2", "areItemsTheSame")
            return (oldList[oldItemPosition].tvId == newList[newItemPosition].tvId)
        }

        override fun getOldListSize(): Int {
//            Log.v("TEST2", "getOldListSize")
            return oldList.size
        }

        override fun getNewListSize(): Int {
//            Log.v("TEST2", "getNewListSize")
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//            Log.v("TEST2", "areContentsTheSame")
            if(oldList[oldItemPosition].tvId != newList[newItemPosition].tvId)
                return false
            if(oldList[oldItemPosition].posterPath != newList[newItemPosition].posterPath)
                return false
            if(oldList[oldItemPosition].backdropPath != newList[newItemPosition].backdropPath)
                return false
            if(oldList[oldItemPosition].overview != newList[newItemPosition].overview)
                return false
            if(oldList[oldItemPosition].title != newList[newItemPosition].title)
                return false
//            Log.v("TEST2", "areContentsTheSame is true")
            return true
        }

        @Nullable
        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return super.getChangePayload(oldItemPosition, newItemPosition)
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
