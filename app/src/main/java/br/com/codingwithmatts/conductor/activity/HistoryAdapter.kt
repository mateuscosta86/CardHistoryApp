package br.com.codingwithmatts.conductor.activity

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.codingwithmatts.conductor.R
import br.com.codingwithmatts.conductor.model.Purchase

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private val historySource : List<Purchase>

    constructor(source : List<Purchase>) {
        this.historySource = source
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): HistoryViewHolder {
        val layoutInflater : LayoutInflater = LayoutInflater.from(parent.context)
        val view : View = layoutInflater.inflate(R.layout.history_ticket, parent, false)

        return HistoryViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return historySource.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        var record : Purchase = historySource[position]
        var date = processDate(record.getDate())

        holder.dateText.text = date
        holder.actionText.text = record.getStore()
        holder.valueText.text = "${"%.2f".format(record.getValue())}"
    }

    private fun processDate(date : String) : String {
        var rawDate = date.split("T")
        rawDate = rawDate[0].split("-")
        var formattedDate = rawDate[2]

        when (rawDate[1]) {
            "01" -> formattedDate += " Jan"
            "02" -> formattedDate += " Fev"
            "03" -> formattedDate += " Mar"
            "04" -> formattedDate += " Abr"
            "05" -> formattedDate += " Mai"
            "06" -> formattedDate += " Jun"
            "07" -> formattedDate += " Jul"
            "08" -> formattedDate += " Ago"
            "09" -> formattedDate += " Set"
            "10" -> formattedDate += " Out"
            "11" -> formattedDate += " Nov"
            "12" -> formattedDate += " Dez"
        }
        return formattedDate
    }

    class HistoryViewHolder : RecyclerView.ViewHolder {

        val dateText : TextView
        val actionText : TextView
        val valueText : TextView

        constructor(itemView: View, context: Context) : super(itemView) {
            this.dateText = itemView.findViewById(R.id.txtViewDate)
            this.actionText = itemView.findViewById(R.id.txtViewAction)
            this.valueText = itemView.findViewById(R.id.txtViewPrice)
        }
    }
}