package com.dokugo.ui.moneymanage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dokugo.R
import com.dokugo.database.IncomeDatabaseHelper
import com.dokugo.database.ExpenseDatabaseHelper
import com.dokugo.databinding.FragmentMoneyBinding
import com.dokugo.ui.custom.CustomButtonDate
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.util.Locale
import org.threeten.bp.LocalDate

class MoneyFragment : Fragment() {

    private var _binding: FragmentMoneyBinding? = null
    private val binding get() = _binding!!

    private lateinit var tvIncome: TextView
    private lateinit var tvExpense: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvKeterangan: TextView
    private lateinit var tvIncomeDate: TextView
    private lateinit var tvExpenseDate: TextView
    private lateinit var tvMoney: TextView
    private lateinit var lineChart: com.github.mikephil.charting.charts.LineChart
    private lateinit var lineChartEx: com.github.mikephil.charting.charts.LineChart

    private lateinit var btWeek: CustomButtonDate
    private lateinit var btMonth: CustomButtonDate
    private lateinit var btYear: CustomButtonDate
    private lateinit var btAll: CustomButtonDate
    private lateinit var btDay: CustomButtonDate
    private lateinit var btIncomeAll: CustomButtonDate
    private lateinit var btIncomeSalary: CustomButtonDate
    private lateinit var btIncomeOthers: CustomButtonDate
    private lateinit var btExpenseAll: CustomButtonDate
    private lateinit var btExpenseFood: CustomButtonDate
    private lateinit var btExpenseTransport: CustomButtonDate
    private lateinit var btExpenseShopping: CustomButtonDate
    private lateinit var btExpenseOthers: CustomButtonDate

    private lateinit var btExpenseWeek: CustomButtonDate
    private lateinit var btExpenseMonth: CustomButtonDate
    private lateinit var btExpenseYear: CustomButtonDate
    private lateinit var btExpenseAllTime: CustomButtonDate
    private lateinit var btExpenseDay: CustomButtonDate

    private val incomeDetails = mutableListOf<View>()
    private val expenseDetails = mutableListOf<View>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoneyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize TextViews and Charts
        tvIncome = binding.incomeText
        tvExpense = binding.expenseText
        tvCategory = binding.incomeCategory
        tvKeterangan = binding.incomeKet
        tvIncomeDate = binding.incomeDate
        tvExpenseDate = binding.expenseDate
        tvMoney = binding.detailIncomeMoney
        lineChart = binding.lineChart
        lineChartEx = binding.lineChartEx

        // Initialize buttons
        btWeek = binding.btWeek
        btMonth = binding.btMonth
        btYear = binding.btYear
        btAll = binding.btAll
        btDay = binding.btDay
        btIncomeAll = binding.incomeAll
        btIncomeSalary = binding.incomeSalary
        btIncomeOthers = binding.incomeOthers
        btExpenseAll = binding.expenseAll
        btExpenseFood = binding.expenseFood
        btExpenseTransport = binding.expenseTransport
        btExpenseShopping = binding.expenseShopping
        btExpenseOthers = binding.expenseOthers


        btExpenseWeek = binding.btWeekEx
        btExpenseMonth = binding.btMonthEx
        btExpenseYear = binding.btYearEx
        btExpenseAllTime = binding.btAllEx
        btExpenseDay = binding.btDayEx

        // Set the default selected button to "Day" and update the text
        btDay.setSelectedState(true)
        btIncomeAll.setSelectedState(true)
        btExpenseAll.setSelectedState(true)
        btExpenseDay.setSelectedState(true)

        // Set up button click listeners
        btWeek.setOnClickListener { onDateButtonClicked(btWeek, "This Week") }
        btMonth.setOnClickListener { onDateButtonClicked(btMonth, "This Month") }
        btYear.setOnClickListener { onDateButtonClicked(btYear, "This Year") }
        btAll.setOnClickListener { onDateButtonClicked(btAll, "All Time") }
        btDay.setOnClickListener { onDateButtonClicked(btDay, "Day") }

        // Set up income category button click listeners
        // Set up income category button click listeners
        btIncomeAll.setOnClickListener { onIncomeButtonClicked(btIncomeAll, "All", currentTimeRange) }
        btIncomeSalary.setOnClickListener { onIncomeButtonClicked(btIncomeSalary, "Salary", currentTimeRange) }
        btIncomeOthers.setOnClickListener { onIncomeButtonClicked(btIncomeOthers, "Others", currentTimeRange) }

        btExpenseAll.setOnClickListener { onExpenseButtonClicked(btExpenseAll, "All", currentTimeRange) }
        btExpenseFood.setOnClickListener { onExpenseButtonClicked(btExpenseFood, "Food", currentTimeRange) }
        btExpenseTransport.setOnClickListener { onExpenseButtonClicked(btExpenseTransport, "Transport", currentTimeRange) }
        btExpenseShopping.setOnClickListener { onExpenseButtonClicked(btExpenseShopping, "Shopping", currentTimeRange) }
        btExpenseOthers.setOnClickListener { onExpenseButtonClicked(btExpenseOthers, "Others", currentTimeRange) }

        // Set up expense date button click listeners
        btExpenseWeek.setOnClickListener { onExpenseDateButtonClicked(btExpenseWeek, "This Week") }
        btExpenseMonth.setOnClickListener { onExpenseDateButtonClicked(btExpenseMonth, "This Month") }
        btExpenseYear.setOnClickListener { onExpenseDateButtonClicked(btExpenseYear, "This Year") }
        btExpenseAllTime.setOnClickListener { onExpenseDateButtonClicked(btExpenseAllTime, "All Time") }
        btExpenseDay.setOnClickListener { onExpenseDateButtonClicked(btExpenseDay, "Day") }

        // Set up frame_addincome click listener
        binding.frameAddincome.setOnClickListener {
            findNavController().navigate(R.id.incomeFragment)
        }

        // Set up frame_addexpense click listener
        binding.frameAddexpense.setOnClickListener {
            findNavController().navigate(R.id.expenseFragment)
        }

        // Update chart data and details with actual data
        updateIncomeChartData("Day")
        updateExpenseChartData("Day")
        updateIncomeDetails(null, "Day")
        updateExpenseDetails(null, "Day")

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Function to fetch income data from the database
    private fun fetchIncomeData(timeRange: String): List<Income> {
        val dbHelper = IncomeDatabaseHelper(requireContext())
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            IncomeDatabaseHelper.TABLE_NAME,
            null, null, null, null, null, null
        )

        val incomeList = mutableListOf<Income>()
        val currentDate = LocalDate.now()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(IncomeDatabaseHelper.COLUMN_ID))
                val date = getString(getColumnIndexOrThrow(IncomeDatabaseHelper.COLUMN_DATE))
                val amount = getString(getColumnIndexOrThrow(IncomeDatabaseHelper.COLUMN_AMOUNT))
                val category = getString(getColumnIndexOrThrow(IncomeDatabaseHelper.COLUMN_CATEGORY))
                val note = getString(getColumnIndexOrThrow(IncomeDatabaseHelper.COLUMN_NOTE))
                val incomeDate = LocalDate.parse(date)

                val isInRange = when (timeRange) {
                    "Day" -> incomeDate.isEqual(currentDate)
                    "This Week" -> incomeDate.isAfter(currentDate.minusDays(7)) && incomeDate.isBefore(currentDate.plusDays(1))
                    "This Month" -> incomeDate.month == currentDate.month && incomeDate.year == currentDate.year
                    "This Year" -> incomeDate.year == currentDate.year
                    "All Time" -> true
                    else -> false
                }

                if (isInRange) {
                    incomeList.add(Income(id, date, amount, category, note))
                }
            }
        }
        cursor.close()
        return incomeList.sortedBy { LocalDate.parse(it.date) }
    }

    // Function to fetch expense data from the database
    private fun fetchExpenseData(timeRange: String): List<Expense> {
        val dbHelper = ExpenseDatabaseHelper(requireContext())
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            ExpenseDatabaseHelper.TABLE_NAME,
            null, null, null, null, null, null
        )

        val expenseList = mutableListOf<Expense>()
        val currentDate = LocalDate.now()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(ExpenseDatabaseHelper.COLUMN_ID))
                val date = getString(getColumnIndexOrThrow(ExpenseDatabaseHelper.COLUMN_DATE))
                val amount = getString(getColumnIndexOrThrow(ExpenseDatabaseHelper.COLUMN_AMOUNT))
                val category = getString(getColumnIndexOrThrow(ExpenseDatabaseHelper.COLUMN_CATEGORY))
                val note = getString(getColumnIndexOrThrow(ExpenseDatabaseHelper.COLUMN_NOTE))
                val expenseDate = LocalDate.parse(date)

                val isInRange = when (timeRange) {
                    "Day" -> expenseDate.isEqual(currentDate)
                    "This Week" -> expenseDate.isAfter(currentDate.minusDays(7)) && expenseDate.isBefore(currentDate.plusDays(1))
                    "This Month" -> expenseDate.month == currentDate.month && expenseDate.year == currentDate.year
                    "This Year" -> expenseDate.year == currentDate.year
                    "All Time" -> true
                    else -> false
                }

                if (isInRange) {
                    expenseList.add(Expense(id, date, amount, category, note))
                }
            }
        }
        cursor.close()
        return expenseList.sortedBy { LocalDate.parse(it.date) }
    }

    // Data class to hold income data
    data class Income(val id: Int, val date: String, val amount: String, val category: String, val note: String)

    // Data class to hold expense data
    data class Expense(val id: Int, val date: String, val amount: String, val category: String, val note: String)

    // Update the income chart with actual data
    private fun updateIncomeChartData(timeRange: String) {
        val incomeData = fetchIncomeData(timeRange)
        val incomeEntries = mutableListOf<Entry>()

        if (incomeData.size == 1) {
            // Add a starting point with value 0
            incomeEntries.add(Entry(0f, 0f))
        }

        // Add actual income data points
        incomeEntries.addAll(incomeData.mapIndexed { index, income ->
            Entry((index + 1).toFloat(), income.amount.toFloat())
        })

        if (incomeData.size == 1) {
            // Add an ending point with value 0
            incomeEntries.add(Entry((incomeData.size + 1).toFloat(), 0f))
        }

        val incomeDataSet = LineDataSet(incomeEntries, "")
        incomeDataSet.color = ContextCompat.getColor(requireContext(), R.color.income_chart)
        incomeDataSet.setDrawCircles(true)
        incomeDataSet.setDrawValues(false)
        incomeDataSet.lineWidth = 5f

        val lineData = LineData(incomeDataSet)
        lineChart.data = lineData
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false

        // Configure the x-axis labels
        val xAxis: XAxis = lineChart.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value.toInt()) {
                    0 -> ""
                    incomeData.size + 1 -> ""
                    else -> incomeData.getOrNull(value.toInt() - 1)?.category ?: value.toString()
                }
            }
        }
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = incomeData.size + 2
        xAxis.isGranularityEnabled = true

        // Configure the y-axis
        val yAxis: YAxis = lineChart.axisLeft
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value == 0f) "0" else String.format(Locale("in", "ID"), "%.0f000", value / 1000)
            }
        }
        yAxis.axisMaximum = (Math.ceil((incomeData.maxOfOrNull { it.amount.toFloat() } ?: 0f).toDouble() / 1000) * 1000).toFloat()
        yAxis.axisMinimum = 0f
        lineChart.axisRight.isEnabled = false

        lineChart.invalidate()
    }

    private fun updateExpenseChartData(timeRange: String) {
        val expenseData = fetchExpenseData(timeRange)
        val expenseEntries = mutableListOf<Entry>()

        if (expenseData.size == 1) {
            // Add a starting point with value 0
            expenseEntries.add(Entry(0f, 0f))
        }

        // Add actual expense data points
        expenseEntries.addAll(expenseData.mapIndexed { index, expense ->
            Entry((index + 1).toFloat(), expense.amount.toFloat())
        })

        if (expenseData.size == 1) {
            // Add an ending point with value 0
            expenseEntries.add(Entry((expenseData.size + 1).toFloat(), 0f))
        }

        val expenseDataSet = LineDataSet(expenseEntries, "")
        expenseDataSet.color = ContextCompat.getColor(requireContext(), R.color.expense_chart)
        expenseDataSet.setDrawCircles(true)
        expenseDataSet.setDrawValues(false)
        expenseDataSet.lineWidth = 5f

        val lineData = LineData(expenseDataSet)
        lineChartEx.data = lineData
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false

        // Configure the x-axis labels
        val xAxis: XAxis = lineChartEx.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return when (value.toInt()) {
                    0 -> ""
                    expenseData.size + 1 -> ""
                    else -> expenseData.getOrNull(value.toInt() - 1)?.category ?: value.toString()
                }
            }
        }
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = expenseData.size + 2
        xAxis.isGranularityEnabled = true

        // Configure the y-axis
        val yAxis: YAxis = lineChartEx.axisLeft
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (value == 0f) "0" else String.format(Locale("in", "ID"), "%.0f000", value / 1000)
            }
        }
        yAxis.axisMaximum = (Math.ceil((expenseData.maxOfOrNull { it.amount.toFloat() } ?: 0f).toDouble() / 1000) * 1000).toFloat()
        yAxis.axisMinimum = 0f
        lineChartEx.axisRight.isEnabled = false

        lineChartEx.invalidate()
    }

    // Update income details with actual data
    private fun updateIncomeDetails(category: String? = null, timeRange: String): Boolean {
        val container = binding.allDetailIncomeContainer
        container.removeAllViews()
        val incomeData = fetchIncomeData(timeRange)

        val filteredIncomeData = if (category == null || category == "All") {
            incomeData
        } else {
            incomeData.filter { it.category == category }
        }

        if (filteredIncomeData.isEmpty()) {
            val noDataTextView = TextView(requireContext()).apply {
                text = "No data available"
                textSize = 16f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                gravity = View.TEXT_ALIGNMENT_CENTER
            }
            container.addView(noDataTextView)
            return false
        } else {
            var totalIncome = 0f
            val numberFormat = NumberFormat.getNumberInstance(Locale("in", "ID"))
            filteredIncomeData.forEach { income ->
                val incomeDetailView = layoutInflater.inflate(R.layout.layout_detail_income, null)
                val detailCategoryText = incomeDetailView.findViewById<TextView>(R.id.income_category)
                val detailKeteranganText = incomeDetailView.findViewById<TextView>(R.id.income_ket)
                val detailDateText = incomeDetailView.findViewById<TextView>(R.id.income_date)
                val detailMoneyText = incomeDetailView.findViewById<TextView>(R.id.detail_income_money)

                detailCategoryText.text = income.category
                detailKeteranganText.text = income.note
                detailDateText.text = income.date
                detailMoneyText.text = "Rp. ${numberFormat.format(income.amount.toFloat())}"

                totalIncome += income.amount.toFloat()

                container.addView(incomeDetailView)
            }

            tvIncome.text = "Rp. ${numberFormat.format(totalIncome)}"
            return true
        }
    }

    private fun updateExpenseDetails(category: String? = null, timeRange: String): Boolean {
        val container = binding.allDetailExpenseContainer
        container.removeAllViews()
        val expenseData = fetchExpenseData(timeRange)

        val filteredExpenseData = if (category == null || category == "All") {
            expenseData
        } else {
            expenseData.filter { it.category == category }
        }

        if (filteredExpenseData.isEmpty()) {
            val noDataTextView = TextView(requireContext()).apply {
                text = "No data available"
                textSize = 16f
                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                gravity = View.TEXT_ALIGNMENT_CENTER
            }
            container.addView(noDataTextView)
            return false
        } else {
            var totalExpense = 0f
            val numberFormat = NumberFormat.getNumberInstance(Locale("in", "ID"))
            filteredExpenseData.forEach { expense ->
                val expenseDetailView = layoutInflater.inflate(R.layout.layout_detail_expense, null)
                val detailCategoryText = expenseDetailView.findViewById<TextView>(R.id.expense_category)
                val detailKeteranganText = expenseDetailView.findViewById<TextView>(R.id.expense_ket)
                val detailDateText = expenseDetailView.findViewById<TextView>(R.id.expense_date)
                val detailMoneyText = expenseDetailView.findViewById<TextView>(R.id.detail_expense_money)

                detailCategoryText.text = expense.category
                detailKeteranganText.text = expense.note
                detailDateText.text = expense.date
                detailMoneyText.text = "Rp. ${numberFormat.format(expense.amount.toFloat())}"

                totalExpense += expense.amount.toFloat()

                container.addView(expenseDetailView)
            }

            tvExpense.text = "Rp. ${numberFormat.format(totalExpense)}"
            return true
        }
    }

    // Handle date button click and update data
    private var currentTimeRange: String = "Day"

    private fun onDateButtonClicked(selectedButton: CustomButtonDate, timeRange: String) {
        // Reset the state for all date buttons
        btWeek.setSelectedState(false)
        btMonth.setSelectedState(false)
        btYear.setSelectedState(false)
        btAll.setSelectedState(false)
        btDay.setSelectedState(false) // Reset day button state

        // Set the selected state for the clicked button
        selectedButton.setSelectedState(true)

        // Update the current time range
        currentTimeRange = timeRange

        // Reset category buttons to "All"
        btIncomeAll.setSelectedState(true)
        btIncomeSalary.setSelectedState(false)
        btIncomeOthers.setSelectedState(false)
        btExpenseAll.setSelectedState(true)
        btExpenseFood.setSelectedState(false)
        btExpenseTransport.setSelectedState(false)
        btExpenseShopping.setSelectedState(false)
        btExpenseOthers.setSelectedState(false)

        // Update chart data and details with actual data
        updateIncomeChartData(timeRange)
        updateExpenseChartData(timeRange)
        updateIncomeDetails("All", timeRange)
        updateExpenseDetails("All", timeRange)
    }

    private fun onExpenseDateButtonClicked(selectedButton: CustomButtonDate, timeRange: String) {
        // Reset the state for all expense date buttons
        btExpenseWeek.setSelectedState(false)
        btExpenseMonth.setSelectedState(false)
        btExpenseYear.setSelectedState(false)
        btExpenseAllTime.setSelectedState(false)
        btExpenseDay.setSelectedState(false) // Reset day button state

        // Set the selected state for the clicked button
        selectedButton.setSelectedState(true)

        // Update the current time range
        currentTimeRange = timeRange

        // Reset category buttons to "All"
        btExpenseAll.setSelectedState(true)
        btExpenseFood.setSelectedState(false)
        btExpenseTransport.setSelectedState(false)
        btExpenseShopping.setSelectedState(false)
        btExpenseOthers.setSelectedState(false)

        // Update expense chart data and details with actual data
        updateExpenseChartData(timeRange)
        updateExpenseDetails("All", timeRange)
    }

    private fun onIncomeButtonClicked(selectedButton: CustomButtonDate, category: String, timeRange: String) {
        // Reset the state for all income category buttons
        btIncomeAll.setSelectedState(false)
        btIncomeSalary.setSelectedState(false)
        btIncomeOthers.setSelectedState(false)

        // Set the selected state for the clicked button
        selectedButton.setSelectedState(true)

        // Update income details based on the selected category and time range
        updateIncomeDetails(category, timeRange)
    }

    private fun onExpenseButtonClicked(selectedButton: CustomButtonDate, category: String, timeRange: String) {
        // Reset the state for all expense category buttons
        btExpenseAll.setSelectedState(false)
        btExpenseFood.setSelectedState(false)
        btExpenseTransport.setSelectedState(false)
        btExpenseShopping.setSelectedState(false)
        btExpenseOthers.setSelectedState(false)

        // Set the selected state for the clicked button
        selectedButton.setSelectedState(true)

        // Update expense details based on the selected category and time range
        updateExpenseDetails(category, timeRange)
    }
}