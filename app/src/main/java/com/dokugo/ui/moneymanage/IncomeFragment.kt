package com.dokugo.ui.moneymanage

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dokugo.R
import com.dokugo.data.network.RetrofitInstance
import com.dokugo.data.network.TransactionRetrofitInstance
import com.dokugo.data.response.IncomeRequest
import com.dokugo.databinding.FragmentIncomeBinding
import kotlinx.coroutines.launch
import java.util.Calendar

class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupCategorySpinner()
        fetchUserId()

        binding.etDatePicker.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnSave.setOnClickListener {
            saveIncome()
        }

        return root
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf("Other", "Salary", "Allowance", "Investment")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                binding.etDatePicker.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun fetchUserId() {
        val token = getTokenFromSharedPreferences()

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getProfile("Bearer $token")
                userId = response.user.id
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to fetch user ID: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveIncome() {
        Log.d("IncomeFragment", "saveIncome called")
        val amount = binding.etAmount.text.toString().toDoubleOrNull()
        val category = binding.spinnerCategory.selectedItemPosition
        val date = binding.etDatePicker.text.toString()
        val notes = binding.etNote.text.toString()

        if (amount == null || date.isEmpty() || notes.isEmpty() || userId == null) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val incomeRequest = IncomeRequest(userId!!, amount, category, date, notes)
        sendIncomeRequest(incomeRequest)
    }

    private fun sendIncomeRequest(incomeRequest: IncomeRequest) {
        Log.d("IncomeFragment", "sendIncomeRequest called with: $incomeRequest")
        val token = getTokenFromSharedPreferences()

        lifecycleScope.launch {
            try {
                val response = TransactionRetrofitInstance.api.postIncome("Bearer $token", incomeRequest)
                Log.d("IncomeFragment", "Response received: $response")
                if (response.message.contains("Income transaction added successfully")) {
                    findNavController().navigate(R.id.action_incomeFragment_to_moneyFragment)
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("IncomeFragment", "Exception: ${e.message}", e)
                Toast.makeText(requireContext(), "Failed to save income: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getTokenFromSharedPreferences(): String {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", "") ?: ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}