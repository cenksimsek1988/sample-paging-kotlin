package com.cenk.scorp

import DataSource
import FetchCompletionHandler
import FetchError
import FetchResponse
import Person
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cenk.scorp.data.PagingPersonAdapter
import com.cenk.scorp.data.PaginationScrollListener
import com.cenk.scorp.data.PersonComparator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.util.ArrayList


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerViewPeople: RecyclerView
    private lateinit var layoutSwipeRefresh: SwipeRefreshLayout
    private lateinit var textViewNoOne: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var adapterPerson: PagingPersonAdapter
    private val dataSource: DataSource = DataSource()
    private val handler = MyFetchDoneHandler ()
    private var next: String? = "0";
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()
        setViews()

        refreshPeople()
    }

    private fun findViews() {
        recyclerViewPeople = findViewById(R.id.recycler_view)
        textViewNoOne = findViewById(R.id.empty_view)
        layoutSwipeRefresh = findViewById(R.id.layout_swipe_refresh)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setViews() {
        progressBar.visibility = VISIBLE
        val llm = LinearLayoutManager(this)
        recyclerViewPeople.layoutManager = llm
        recyclerViewPeople.addOnScrollListener(object : PaginationScrollListener(llm) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                progressBar.visibility = VISIBLE
                dataSource.fetch(next, handler)
            }
        })
        recyclerViewPeople.setHasFixedSize(true)
        adapterPerson = PagingPersonAdapter()
        adapterPerson.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkEmpty()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                checkEmpty()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                checkEmpty()
            }

            fun checkEmpty() {
                textViewNoOne.visibility = if (adapterPerson.itemCount == 0) VISIBLE else GONE
            }
        })

        recyclerViewPeople.adapter = adapterPerson
        layoutSwipeRefresh.setOnRefreshListener {
            refreshPeople()
        }
        layoutSwipeRefresh.setColorSchemeResources(R.color.design_default_color_primary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)
    }

    private suspend fun setPeople(people: ArrayList<Person>) {
        if (people.isNotEmpty()) {
            adapterPerson.addAll(people)
        }
        if(next==null){
            isLastPage = true
        }
    }

    inner class MyFetchDoneHandler: FetchCompletionHandler {
        override fun invoke(resp: FetchResponse?, err: FetchError?) {
            CoroutineScope(Main).launch  {
                if(resp!=null){
                    next = resp.next;
                    setPeople(resp.people)
                }
                if (err!=null){
                    Toast.makeText(baseContext, err.errorDescription, Toast.LENGTH_SHORT).show()
                }
                isLoading = false
                progressBar.visibility = GONE
                layoutSwipeRefresh.isRefreshing = false
            }
        }
    }

    private fun refreshPeople() {
        layoutSwipeRefresh.isRefreshing = true
        CoroutineScope(Main).launch  {
            adapterPerson.clear()
            isLastPage = false;
            dataSource.fetch(next, handler)
        }


    }
}