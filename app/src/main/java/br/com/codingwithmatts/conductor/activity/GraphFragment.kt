package br.com.codingwithmatts.conductor.activity


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.codingwithmatts.conductor.model.PostManApi
import br.com.codingwithmatts.conductor.R
import br.com.codingwithmatts.conductor.model.Usage
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ViewPortHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class GraphFragment : Fragment() {

    lateinit var barChart : BarChart

    lateinit var retrofit : Retrofit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barChart = view.findViewById(R.id.barchart)

        retrofit = Retrofit.Builder()
            .baseUrl("https://2hm1e5siv9.execute-api.us-east-1.amazonaws.com/dev/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val postManApi = retrofit.create(PostManApi::class.java)

        loadBarChartData(postManApi)
    }

    private fun loadBarChartData(postManApi: PostManApi) {
        var call = postManApi.getCardUsage()
        call.enqueue(object : Callback<List<Usage>> {

            override fun onFailure(call: Call<List<Usage>>, t: Throwable) {
                Log.i("ERROR MESSAGE", t.message.toString())
            }

            override fun onResponse(call: Call<List<Usage>>, response: Response<List<Usage>>) {
                if ( !response.isSuccessful ) {
                    Log.i("CODE", response.code().toString())
                    return
                }

                if ( response.body() != null ) {
                    val cardUsage = response.body() as ArrayList<Usage>

                    fillBarChartData(cardUsage, barChart)
                }
            }
        })
    }

    private fun fillBarChartData(
        cardUsage: ArrayList<Usage>,
        barChartToFill: BarChart) {

        var dataValues : ArrayList<BarEntry> = ArrayList()
        var labels = ArrayList<String>()

        cardUsage.forEach {
            dataValues.add(BarEntry(it.getMonth().toFloat() - 1f, it.getValue().toFloat() ))
            labels.add(it.getName())
            Log.i("CODE", it.getName())
        }

        // Some Configurations
        barChartToFill.description.isEnabled = false
        barChartToFill.setDrawGridBackground(false)
        barChartToFill.axisRight.isEnabled = false
        barChartToFill.xAxis.isEnabled = true
        barChartToFill.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChartToFill.xAxis.labelCount = 12
        barChartToFill.xAxis.setCenterAxisLabels(false)
        barChartToFill.setFitBars(true)
        barChartToFill.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        barChartToFill.axisLeft.valueFormatter = MyYAxisFormatter()

        var barDataSet = BarDataSet(dataValues, "Consumo Mensal")

        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toMutableList()
        barDataSet.valueFormatter = MyValuesFormatter()

        var barData = BarData()
        barData.addDataSet(barDataSet)

        barChartToFill.data = barData
        barChartToFill.invalidate()
    }

    class MyValuesFormatter : IValueFormatter {
        override fun getFormattedValue(
            value: Float,
            entry: Entry?,
            dataSetIndex: Int,
            viewPortHandler: ViewPortHandler?
        ): String {
            return return "R$ ${"%,.2f".format(value)}"
        }
    }

    class MyYAxisFormatter : IAxisValueFormatter {
        override fun getFormattedValue(value: Float, axis: AxisBase?): String {

            return "R$ ${"%,.2f".format(value)}"
        }
    }
}