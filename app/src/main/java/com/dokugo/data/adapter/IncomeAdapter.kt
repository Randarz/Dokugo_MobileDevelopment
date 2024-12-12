package com.dokugo.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dokugo.data.response.IncomeResponseItem
import com.dokugo.R

class IncomeAdapter(private val incomeList: List<IncomeResponseItem>) : RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder>() {

    // ViewHolder untuk item di RecyclerView
    inner class IncomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val incomeCategory: TextView = itemView.findViewById(R.id.income_category)
        val incomeAmount: TextView = itemView.findViewById(R.id.detail_income_money)
        val incomeDate: TextView = itemView.findViewById(R.id.income_date)
    }

    // Menginflate layout dan mengembalikan ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_detail_income, parent, false)
        return IncomeViewHolder(itemView)
    }

    // Mengikat data ke ViewHolder
    override fun onBindViewHolder(holder: IncomeViewHolder, position: Int) {
        val currentItem = incomeList[position]
        holder.incomeCategory.text = "Category: ${currentItem.category}"  // Atau mapping sesuai kategori
        holder.incomeAmount.text = "Amount: ${currentItem.amount}"
        holder.incomeDate.text = "Date: ${currentItem.date}"
    }

    // Mengembalikan ukuran data
    override fun getItemCount(): Int = incomeList.size
}
