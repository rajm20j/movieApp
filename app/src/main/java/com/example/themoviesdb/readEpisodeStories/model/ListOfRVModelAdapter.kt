package com.example.themoviesdb.readEpisodeStories.model

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.size
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.themoviesdb.R
import com.example.themoviesdb.readEpisodeStories.ReadEpisodeStoriesVM
import com.example.themoviesdb.utils.Utils
import com.google.android.material.button.MaterialButton
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class ListOfRVModelAdapter() : RecyclerView.Adapter<ListOfRVModelAdapter.ViewHolder>() {

    private val callNow: Boolean = false
    private lateinit var readEpisodeStoriesVM: ReadEpisodeStoriesVM
    private lateinit var context: Context
    private lateinit var listItems: MutableList<RecyclerView>
    private lateinit var backNav: MaterialButton
    private lateinit var frontNav: MaterialButton
    private lateinit var pageNoTv: TextView
    private lateinit var globalRecyclerView: RecyclerView

    private var listSize = 0
    private var pageNo: Int = 1
    private var isScrolling = false

    private lateinit var disposable: Disposable
    private var toDelay = Handler()
    var runnable: Runnable? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_for_recycler_view, parent, false) //YE DEH LENA
        return ViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        globalRecyclerView = holder.recyclerView
        globalRecyclerView.adapter = Utils.listOfRV[position].adapter

        listSize = (Utils.listOfRV[position].layoutManager as? LinearLayoutManager)!!.itemCount

        globalRecyclerView.layoutManager = GridLayoutManager(context, 1)
        globalRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        Utils.listOfRV[position].layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        if (listSize > 1) {
            globalRecyclerView.scrollToPosition(1)
        } else {
            pageNo = 0
            pageNoTv.text = "0"
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
            pageNo = listSize - 1
            pageNoTv.text = pageNo.toString()
            globalRecyclerView.scrollToPosition(pageNo)
            frontNav.visibility = View.INVISIBLE
            if (listSize >= 2) {
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
            if (listSize != 2)
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
                        Utils.smallListPosition = manager.findLastVisibleItemPosition()
                        recyclerView.smoothScrollToPosition(Utils.smallListPosition)
                        handleForwardPageNo(manager)
                    } else if (dx < 15) {
                        Utils.smallListPosition = manager.findFirstVisibleItemPosition()
                        recyclerView.smoothScrollToPosition(Utils.smallListPosition)
                        handleBackwardPageNo(manager)
                    }
                    readEpisodeStoriesVM.disposable.clear()

                    /*removeRange(
                        Utils.largeListPosition + 1,
                        Utils.currentPapaRV?.adapter?.itemCount!! - 1
                    )*/

                    val endIndex = Utils.currentPapaRV?.adapter?.itemCount!! - 1
                    Utils.listOfRV.subList(Utils.largeListPosition + 1, endIndex).clear()
                    Utils.currentPapaRV?.adapter?.notifyItemRangeRemoved(Utils.largeListPosition + 1, endIndex)
                    Utils.globalListOfList.subList(Utils.largeListPosition + 1, endIndex).clear()

                    if (Utils.smallListPosition != 0) {
                        if (runnable != null) {
                            toDelay.removeCallbacks(runnable!!)
                        }
                        Log.v(
                            "TEST1",
                            "ListOfRVSize: ${Utils.listOfRV.size}, GlobalListOfList: ${Utils.globalListOfList.size}, CurrentPapaRV: ${Utils.currentPapaRV?.adapter?.itemCount!!}"
                        )
                        readEpisodeStoriesVM.hitReadFurther(
                            Utils.titleForApi,
                            Utils.seasonForApi,
                            Utils.globalListOfList[Utils.largeListPosition][Utils.smallListPosition] .uid!!
                        )

                        runnable = Runnable() {
                        }
                        toDelay.postDelayed(runnable!!, 1500)
                    }

                    Log.v(
                        "TEST",
                        "LargeList: ${Utils.largeListPosition}, SmallList: ${Utils.smallListPosition}"
                    )
                    isScrolling = false
                }
            }

            fun removeRange(startIndex: Int, endIndex: Int) {
                var eIndex = endIndex
                if (startIndex <= endIndex) {
                    while (eIndex != startIndex - 1) {
                        Log.v("TEST1", eIndex.toString())
                        Utils.listOfRV.removeAt(eIndex)
                        Utils.currentPapaRV?.adapter?.notifyItemRemoved(eIndex)
                        Utils.globalListOfList.removeAt(eIndex)
                        eIndex--
                    }
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
//        return listItems.size
        return Utils.listOfRV.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView = itemView.findViewById(R.id.rv_for_list) as RecyclerView
    }

    constructor(
        context: Context,
        frontNav: MaterialButton,
        backNav: MaterialButton,
        pageNoTv: TextView,
        readEpisodeStoriesVM: ReadEpisodeStoriesVM
    ) : this() {
        this.context = context
        this.frontNav = frontNav
        this.backNav = backNav
        this.pageNoTv = pageNoTv
        this.readEpisodeStoriesVM = readEpisodeStoriesVM
    }
}