package com.example.themoviesdb.readEpisodeStories.model

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.themoviesdb.R
import com.google.android.material.button.MaterialButton

class ListOfRVModelAdapter() : RecyclerView.Adapter<ListOfRVModelAdapter.ViewHolder>()  {

    lateinit var context: Context
    lateinit var listItems: MutableList<RecyclerView>
    lateinit var backNav: MaterialButton
    lateinit var frontNav: MaterialButton
    lateinit var pageNoTv: TextView
    lateinit var globalRecyclerView: RecyclerView
//    lateinit var globalRVManager: LinearLayoutManager

    var listSize = 0
    var pageNo: Int = 1
    var isScrolling = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_for_recycler_view, parent, false) //YE DEH LENA
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.recyclerView.adapter = listItems[position].adapter

        globalRecyclerView = holder.recyclerView
//        globalRVManager = holder.recyclerView.layoutManager as LinearLayoutManager
        listSize = (listItems[position].layoutManager as? LinearLayoutManager)!!.itemCount

        holder.recyclerView.layoutManager = GridLayoutManager(context, 1)
        holder.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        listItems[position].layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        if (listSize > 1) {
            globalRecyclerView.scrollToPosition(1)
        }
        else {
            pageNo = 0
            pageNoTv.text = "0"
//            navigationLinearLayout.visibility = View.INVISIBLE
        }

        loadMore()
        implementButtons()
        Log.v("TEST", listSize.toString())
    }

    private fun implementButtons() {

        if (listSize == 2)
            frontNav.visibility = View.INVISIBLE

        if (listSize == 1)
            backNav.visibility = View.INVISIBLE

        frontNav.setOnClickListener {
            if (pageNo < listSize - 1) {
                if (backNav.visibility == View.INVISIBLE)
                    backNav.visibility = View.VISIBLE

                if (pageNoTv.visibility == View.INVISIBLE)
                    pageNoTv.visibility = View.VISIBLE

                pageNo++
                pageNoTv.text = pageNo.toString()
                if (pageNo == listSize - 1) {
                    frontNav.visibility = View.INVISIBLE
                }
                globalRecyclerView.smoothScrollToPosition(pageNo)
            }
        }

        frontNav.setOnLongClickListener {
            pageNo = listSize-1
            pageNoTv.text = pageNo.toString()
            globalRecyclerView.scrollToPosition(pageNo)
            frontNav.visibility = View.INVISIBLE
            if(listSize >= 2)
            {
                backNav.visibility = View.VISIBLE
                pageNoTv.visibility = View.VISIBLE
            }
            return@setOnLongClickListener true
        }

        backNav.setOnClickListener {
            if (pageNo > 0) {
                if (frontNav.visibility == View.INVISIBLE)
                    frontNav.visibility = View.VISIBLE
                pageNo--
                pageNoTv.text = pageNo.toString()
                if (pageNo == 0) {
                    backNav.visibility = View.INVISIBLE
                    pageNoTv.visibility = View.INVISIBLE
                }
                globalRecyclerView.smoothScrollToPosition(pageNo)
            }
        }

        backNav.setOnLongClickListener {
            if(listSize != 2)
                frontNav.visibility = View.VISIBLE
            pageNo = 1
            pageNoTv.text = "1"
            globalRecyclerView.scrollToPosition(1)
            return@setOnLongClickListener true
        }
    }

    private fun loadMore() {
        globalRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING)
                    isScrolling = true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager = recyclerView.layoutManager as LinearLayoutManager
                if (isScrolling) {
                    if (dx > 15) {
                        recyclerView.smoothScrollToPosition(manager.findLastVisibleItemPosition())
                        handleForwardPageNo(manager)
                    } else if (dx < 15) {
                        recyclerView.smoothScrollToPosition(manager.findFirstVisibleItemPosition())
                        handleBackwardPageNo(manager)
                    }
                    isScrolling = false
                }
            }

            private fun handleBackwardPageNo(manager: LinearLayoutManager) {
                pageNo = manager.findFirstVisibleItemPosition()
                if (pageNo >= 0) {
                    if (frontNav.visibility == View.INVISIBLE)
                        frontNav.visibility = View.VISIBLE
                    pageNoTv.text = pageNo.toString()
                    if (pageNo == 0) {
                        backNav.visibility = View.INVISIBLE
                        pageNoTv.visibility = View.INVISIBLE
                    }
                }
            }

            private fun handleForwardPageNo(manager: LinearLayoutManager) {
                pageNo = manager.findLastVisibleItemPosition()

                if (pageNo <= listSize) {
                    if (backNav.visibility == View.INVISIBLE)
                        backNav.visibility = View.VISIBLE

                    if (pageNoTv.visibility == View.INVISIBLE)
                        pageNoTv.visibility = View.VISIBLE

                    pageNoTv.text = pageNo.toString()
                    if (pageNo == listSize - 1) {
                        frontNav.visibility = View.INVISIBLE
                    }
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView = itemView.findViewById(R.id.rv_for_list) as RecyclerView
    }

    constructor(context: Context, listItems: MutableList<RecyclerView>, frontNav: MaterialButton, backNav: MaterialButton, pageNoTv: TextView) : this() {
        this.context = context
        this.listItems = listItems
        this.frontNav = frontNav
        this.backNav = backNav
        this.pageNoTv = pageNoTv
    }
}