package br.com.codingwithmatts.conductor.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log

import android.widget.ImageButton
import android.widget.Toast
import br.com.codingwithmatts.conductor.model.PostManApi
import br.com.codingwithmatts.conductor.R
import br.com.codingwithmatts.conductor.model.History
import br.com.codingwithmatts.conductor.model.Purchase

import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime


class MainActivity : AppCompatActivity() {

    lateinit var cardButton : ImageButton
    lateinit var graphButton : ImageButton

    lateinit var cardFragment : CardFragment
    lateinit var graphFragment : GraphFragment

    lateinit var retrofit : Retrofit

    lateinit var recyclerView : RecyclerView
    lateinit var historyAdapter : HistoryAdapter
    lateinit var history : ArrayList<Purchase>
    lateinit var purchaseHistory : History

    private var isGraphActive : Boolean = true
    private var isCardActive : Boolean = false


    private var isEndOfList = false

    var nextPage : Int = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cardButton = findViewById(R.id.walltetBtn)
        graphButton = findViewById(R.id.graphBtn)

        cardFragment = CardFragment()
        graphFragment = GraphFragment()

        setFragment(graphFragment)
        graphButton.setImageDrawable(resources.getDrawable(R.drawable.tab2, null))

        ////////////////////////////////////
        // Setting Button Click Listeners //
        ////////////////////////////////////
        cardButton.setOnClickListener {
            if (!isCardActive) {
                isCardActive = true
                isGraphActive = false
                cardButton.setImageDrawable(resources.getDrawable(R.drawable.tabb2, null))
                graphButton.setImageDrawable(resources.getDrawable(R.drawable.tabb1, null))

                setFragment(cardFragment)
            }
        }

        graphButton.setOnClickListener {
            if (!isGraphActive) {
                isGraphActive = true
                isCardActive = false
                graphButton.setImageDrawable(resources.getDrawable(R.drawable.tab2, null))
                cardButton.setImageDrawable(resources.getDrawable(R.drawable.tab1, null))

                setFragment(graphFragment)
            }
        }

        //////////////////////////
        // Setting RecyclerView //
        //////////////////////////
        val layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.recyclerView)
        (recyclerView as RecyclerView).layoutManager = layoutManager

        /////////////////////////////////
        // Retrieving Data from Server //
        /////////////////////////////////
        retrofit = Retrofit.Builder()
            .baseUrl("https://2hm1e5siv9.execute-api.us-east-1.amazonaws.com/dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val postManApi = retrofit.create(PostManApi::class.java)

        history = ArrayList()

        historyAdapter = HistoryAdapter(history)
        recyclerView.adapter = historyAdapter

        loadData(history, postManApi)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isLastItemDisplaying(recyclerView)) {
                    if( !isEndOfList ) {
                        Toast.makeText(this@MainActivity, "Loading next page...", Toast.LENGTH_LONG).show()
                        loadData(history, postManApi)
                    } else {
                        Toast.makeText(this@MainActivity, "End of the list", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun loadData(history: ArrayList<Purchase>, postManApi: PostManApi) {
        val current = LocalDateTime.now().toString().split('T')[0].split('-')
        val year = current[0].toInt()
        val month = current[1].toInt()

        var call = postManApi.getPurchases(month, year, nextPage.toString().padStart(2,'0'))

        call.enqueue(object : Callback<History> {

            override fun onFailure(call: Call<History>, t: Throwable) {
                Log.i("ERROR MESSAGE", t.message.toString())
            }

            override fun onResponse(call: Call<History>, response: Response<History>) {
                if ( !response.isSuccessful ) {
                    Log.i("CODE", response.code().toString())
                    return
                }

                if ( response.body() != null ) {
                    purchaseHistory = response.body() as History

                    //Add new data do datasource
                    history.addAll(purchaseHistory.getHistory())
                    historyAdapter.notifyDataSetChanged()
                    Log.i("Scrolling", "$nextPage loaded")
                    nextPage++
                    if (nextPage > purchaseHistory.getLastPage()) {
                        isEndOfList = true
                    }
                }
            }
        })
    }

    private fun isLastItemDisplaying(myRecycler: RecyclerView): Boolean {
        if (myRecycler.adapter != null ) {
            if ( (myRecycler.adapter as HistoryAdapter).itemCount != 0 ) {
                var lastItemVisible = (myRecycler.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

                if (lastItemVisible != RecyclerView.NO_POSITION && lastItemVisible == (myRecycler.adapter as HistoryAdapter).itemCount - 1) {
                    return true
                }
            }
        }
        return false
    }

    private fun setFragment(fragment: Fragment) {
        var fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}