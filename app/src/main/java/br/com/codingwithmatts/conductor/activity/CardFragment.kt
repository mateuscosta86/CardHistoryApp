package br.com.codingwithmatts.conductor.activity


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.codingwithmatts.conductor.model.PostManApi
import br.com.codingwithmatts.conductor.R
import br.com.codingwithmatts.conductor.model.History
import br.com.codingwithmatts.conductor.model.Purchase
import br.com.codingwithmatts.conductor.model.Resume
import br.com.codingwithmatts.conductor.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class CardFragment : Fragment() {

    lateinit var nameTxtView : TextView
    lateinit var cardNumberTxtView : TextView
    lateinit var balanceTxtView : TextView
    lateinit var totalExpendedTxtView : TextView

    var nextPage = 1

    var history = ArrayList<Purchase>()

    lateinit var retrofit : Retrofit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        history.clear()
        nextPage = 1

        nameTxtView = view.findViewById(R.id.txtViewName)
        cardNumberTxtView = view.findViewById(R.id.txtViewCardNumber)
        balanceTxtView = view.findViewById(R.id.txtViewBalance)
        totalExpendedTxtView = view.findViewById(R.id.txtViewTotalExpended)

        retrofit = Retrofit.Builder()
            .baseUrl("https://2hm1e5siv9.execute-api.us-east-1.amazonaws.com/dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val postManApi = retrofit.create(PostManApi::class.java)

        loadProfileData(postManApi)
        loadBalanceData(postManApi)

        processTotalExpenses(postManApi)
    }

    private fun processTotalExpenses(postManApi: PostManApi) {

        val current = LocalDateTime.now().toString().split('T')[0].split('-')
        val year = current[0].toInt()
        val month = current[1].toInt()

        var call = postManApi.getPurchases(month, year, nextPage.toString().padStart(2,'0'))

        Log.i("Scrolling", "Loading $nextPage")

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

                    var purchaseHistory = response.body() as History

                    //Add new data do datasource
                    history.addAll(purchaseHistory.getHistory())

                    Log.i("Scrolling", "$nextPage loaded")
                    nextPage++
                    if (nextPage > purchaseHistory.getLastPage()) {
                        totalExpendedTxtView.text= "R$ ${"%,.2f".format(calculateTotal(history))}"
                    } else {
                        processTotalExpenses(postManApi)
                    }
                }
            }
        })
    }

    private fun calculateTotal(history: ArrayList<Purchase>) : Double {
        var total = 0.0
        history.forEach {
            total += it.getValue()
        }
        return total
    }

    private fun loadBalanceData(postManApi : PostManApi) {

        var call = postManApi.getResume()
        call.enqueue(object : Callback<Resume> {

            override fun onFailure(call: Call<Resume>, t: Throwable) {
                Log.i("ERROR MESSAGE", t.message.toString())
            }

            override fun onResponse(call: Call<Resume>, response: Response<Resume>) {
                if ( !response.isSuccessful ) {
                    Log.i("CODE", response.code().toString())
                    return
                }

                if ( response.body() != null ) {
                    Log.i("CODE", response.code().toString())
                    val result = response.body() as Resume
                    balanceTxtView.text = "R$ ${"%,.2f".format(result.getBalance())}"
                }
            }
        })
    }

    private fun loadProfileData(postManApi : PostManApi) {

        var call = postManApi.getProfile()
        call.enqueue(object : Callback<User> {

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.i("ERROR MESSAGE", t.message.toString())
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if ( !response.isSuccessful ) {
                    Log.i("CODE", response.code().toString())
                    return
                }

                if ( response.body() != null ) {

                    val userProfile = response.body() as User
                    nameTxtView.text = userProfile.getName()
                    cardNumberTxtView.text = processCardNumber(userProfile.getCardNumber())
                }
            }
        })
    }

    private fun processCardNumber(cardNumber: String): String {
        var s1 = cardNumber.substring(0, 4)
        var s2 = cardNumber.substring(4, 8)
        var s3 = cardNumber.substring(8, 12)
        var s4 = cardNumber.substring(12, 16)

        return "$s1 $s2 $s3 $s4"
    }
}