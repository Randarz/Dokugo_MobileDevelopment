package com.dokugo.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dokugo.R
import com.dokugo.databinding.FragmentDashboardBinding
import com.dokugo.ui.custom.CustomButtonDate
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var btWeek: CustomButtonDate
    private lateinit var btMonth: CustomButtonDate
    private lateinit var btYear: CustomButtonDate
    private lateinit var btAll: CustomButtonDate
    private lateinit var tvDate: TextView
    private lateinit var lineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize the LineChart
        lineChart = binding.lineChart

        // Initialize the buttons and TextViews
        btWeek = binding.btWeek
        btMonth = binding.btMonth
        btYear = binding.btYear
        btAll = binding.btAll
        tvDate = binding.tvDate  // Get the reference to the TextView

        // Set the default selected button (1W) as selected
        btWeek.setSelectedState(true)
        tvDate.text = "This Week"  // Default text

        // Set up click listeners for the buttons
        btWeek.setOnClickListener { onButtonClicked(btWeek, "This Week") }
        btMonth.setOnClickListener { onButtonClicked(btMonth, "This Month") }
        btYear.setOnClickListener { onButtonClicked(btYear, "January - December") }
        btAll.setOnClickListener { onButtonClicked(btAll, "All Time") }

        // Initialize random data for the default button (1W) at the start
        updateChartData("This Week")

        return root
    }

    // Handle button click and update tv_date text
    private fun onButtonClicked(selectedButton: CustomButtonDate, buttonText: String) {
        // Reset the state for all buttons
        btWeek.setSelectedState(false)
        btMonth.setSelectedState(false)
        btYear.setSelectedState(false)
        btAll.setSelectedState(false)

        // Set the selected state for the clicked button
        selectedButton.setSelectedState(true)

        // Set the correct text for tv_date based on the button clicked
        when (buttonText) {
            "This Week" -> tvDate.text = "This Week"
            "This Month" -> tvDate.text = getCurrentMonth()
            "January - December" -> tvDate.text = "January - December"
            "All Time" -> tvDate.text = "All Time"
        }

        // Update the chart with new random data
        updateChartData(buttonText)
    }

    // Function to generate random data for the chart
    private fun updateChartData(period: String) {
        val incomeEntries = mutableListOf<Entry>()
        val expenseEntries = mutableListOf<Entry>()
        val xAxisLabels = mutableListOf<String>()

        when (period) {
            "This Week" -> {
                for (i in 0..6) {
                    val incomeValue = generateRandomAmount().toFloat()
                    val expenseValue = generateRandomAmount().toFloat()
                    incomeEntries.add(Entry(i.toFloat(), incomeValue))
                    expenseEntries.add(Entry(i.toFloat(), expenseValue))
                    xAxisLabels.add("Day ${i + 1}")
                }
            }
            "This Month" -> {
                for (i in 0..3) {
                    val incomeValue = generateRandomAmount().toFloat()
                    val expenseValue = generateRandomAmount().toFloat()
                    incomeEntries.add(Entry(i.toFloat(), incomeValue))
                    expenseEntries.add(Entry(i.toFloat(), expenseValue))
                    xAxisLabels.add("Week ${i + 1}")
                }
            }
            "January - December" -> {
                val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                for (i in months.indices) {
                    val incomeValue = generateRandomAmount().toFloat()
                    val expenseValue = generateRandomAmount().toFloat()
                    incomeEntries.add(Entry(i.toFloat(), incomeValue))
                    expenseEntries.add(Entry(i.toFloat(), expenseValue))
                    xAxisLabels.add(months[i])
                }
            }
            "All Time" -> {
                for (i in 0..11) {
                    val incomeValue = generateRandomAmount().toFloat()
                    val expenseValue = generateRandomAmount().toFloat()
                    incomeEntries.add(Entry(i.toFloat(), incomeValue))
                    expenseEntries.add(Entry(i.toFloat(), expenseValue))
                    xAxisLabels.add((i + 1).toString())
                }
            }
        }

        // Create the datasets for Expense (Income is not needed here)
        val expenseDataSet = LineDataSet(expenseEntries, "Expense")
        expenseDataSet.color = ContextCompat.getColor(requireContext(), R.color.expense_text)
        expenseDataSet.setDrawCircles(true)
        expenseDataSet.setDrawValues(false) // Hide values
        expenseDataSet.lineWidth = 5f

        // Create a list of datasets
        val dataSets: MutableList<ILineDataSet> = mutableListOf()
        dataSets.add(expenseDataSet)

        // Create LineData and set it to the chart
        val lineData = LineData(dataSets)
        lineChart.data = lineData
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false

        // Customize chart axes
        val xAxis: XAxis = lineChart.xAxis
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return xAxisLabels.getOrNull(value.toInt()) ?: value.toString()
            }
        }
        xAxis.labelCount = xAxisLabels.size

        val yAxis: YAxis = lineChart.axisLeft
        yAxis.setAxisMaximum(10_000_000f)
        yAxis.setAxisMinimum(0f)
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value >= 1_000_000) {
                    "${DecimalFormat("#,###", DecimalFormatSymbols(Locale("id", "ID"))).format(value / 1_000)}K"
                } else {
                    DecimalFormat("#,###", DecimalFormatSymbols(Locale("id", "ID"))).format(value)
                }
            }
        }
        lineChart.axisRight.isEnabled = false

        // Refresh chart
        lineChart.invalidate()
    }

    // Function to generate a random amount between 10,000 and 10,000,000
    private fun generateRandomAmount(): Int {
        val randomValue = Random.nextInt(10_000, 10_000_000)
        return (randomValue / 1000) * 1000  // Ensure the result is a multiple of 1,000
    }

    // Get the current month name (e.g., "March")
    private fun getCurrentMonth(): String {
        val currentDate = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        return monthFormat.format(currentDate.time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
