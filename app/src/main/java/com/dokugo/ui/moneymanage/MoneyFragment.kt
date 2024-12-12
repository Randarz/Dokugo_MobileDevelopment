package com.dokugo.ui.moneymanage

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dokugo.R
import com.dokugo.data.network.TransactionRetrofitInstance
import com.dokugo.data.response.ExpenseGetResponse
import com.dokugo.databinding.FragmentMoneyBinding
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class MoneyFragment : Fragment() {

    private var _binding: FragmentMoneyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoneyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        fetchAndDisplayExpenses()

        binding.frameAddexpense.setOnClickListener {
            findNavController().navigate(R.id.action_moneyFragment_to_expenseFragment)
        }

        binding.frameAddincome.setOnClickListener {
            findNavController().navigate(R.id.action_moneyFragment_to_incomeFragment)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchAndDisplayExpenses() {
        val token = getTokenFromSharedPreferences()
        val userId = getUserIdFromSharedPreferences()
        lifecycleScope.launch {
            try {
                val response = TransactionRetrofitInstance.api.getExpenses("Bearer $token")
                updateExpenseDetails(response)
            } catch (e: Exception) {
                val errorMessage = parseErrorResponse(e)
                Toast.makeText(requireContext(), "Failed to load expenses: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateExpenseDetails(expenseData: List<ExpenseGetResponse>) {
        val container = binding.allDetailExpenseContainer
        container.removeAllViews()

        if (expenseData.isEmpty()) {
            val noDataTextView = TextView(requireContext()).apply {
                text = "No data available"
                textSize = 16f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                gravity = View.TEXT_ALIGNMENT_CENTER
            }
            container.addView(noDataTextView)
        } else {
            expenseData.forEach { expense ->
                val expenseDetailView = layoutInflater.inflate(R.layout.layout_detail_expense, null)
                val detailCategoryText = expenseDetailView.findViewById<TextView>(R.id.expense_category)
                val detailKeteranganText = expenseDetailView.findViewById<TextView>(R.id.expense_ket)
                val detailDateText = expenseDetailView.findViewById<TextView>(R.id.expense_date)
                val detailMoneyText = expenseDetailView.findViewById<TextView>(R.id.detail_expense_money)

                detailCategoryText.text = getCategoryName(expense.category)
                detailKeteranganText.text = expense.notes
                detailDateText.text = expense.date
                detailMoneyText.text = "Rp. ${expense.amount}"

                container.addView(expenseDetailView)
            }
        }
    }

    private fun getCategoryName(category: Int): String {
        return when (category) {
            0 -> "Bills & Fees"
            1 -> "Food & Drinks"
            2 -> "Other"
            3 -> "Transport"
            else -> "Unknown"
        }
    }

    private fun getTokenFromSharedPreferences(): String {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", "") ?: ""
    }

    private fun getUserIdFromSharedPreferences(): String {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_id", "") ?: ""
    }

    private fun parseErrorResponse(exception: Exception): String {
        return try {
            val errorBody = (exception as? HttpException)?.response()?.errorBody()?.string()
            if (errorBody != null) {
                val jsonObject = JSONObject(errorBody)
                jsonObject.getString("error")
            } else {
                exception.message ?: "Unknown error"
            }
        } catch (e: Exception) {
            exception.message ?: "Unknown error"
        }
    }
}