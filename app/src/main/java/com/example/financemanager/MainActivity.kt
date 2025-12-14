package com.example.financemanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financemanager.adapters.ExpenseAdapter
import com.example.financemanager.database.AppDatabase
import com.example.financemanager.databinding.ActivityMainBinding
import com.example.financemanager.models.Expense
import com.example.financemanager.models.ExpenseCategory
import com.example.financemanager.viewmodels.MainViewModel
import com.example.financemanager.viewmodels.MainViewModelFactory
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var viewModel: MainViewModel
    private val REQUEST_CAMERA_PERMISSION = 100
    private val REQUEST_VOICE_RECOGNITION = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        observeData()
        loadSampleData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val factory = MainViewModelFactory(database.expenseDao())
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter { expense ->
            showExpenseDetails(expense)
        }

        binding.rvRecentExpenses.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = expenseAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnScanReceipt.setOnClickListener {
            checkCameraPermission()
        }

        binding.btnVoiceExpense.setOnClickListener {
            startVoiceRecognition()
        }

        binding.fabAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }

        binding.tvViewAll.setOnClickListener {
            Toast.makeText(this, "Showing all expenses", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            startCameraActivity()
        }
    }

    private fun startCameraActivity() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivityForResult(intent, REQUEST_CAMERA_PERMISSION)
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Say expense amount and description")
        }

        try {
            startActivityForResult(intent, REQUEST_VOICE_RECOGNITION)
        } catch (e: Exception) {
            Toast.makeText(this, "Voice recognition not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeData() {
        viewModel.allExpenses.observe(this) { expenses ->
            if (expenses.isEmpty()) {
                binding.tvNoExpenses.visibility = android.view.View.VISIBLE
                binding.rvRecentExpenses.visibility = android.view.View.GONE
            } else {
                binding.tvNoExpenses.visibility = android.view.View.GONE
                binding.rvRecentExpenses.visibility = android.view.View.VISIBLE
                expenseAdapter.submitList(expenses.take(5))

                // Update totals
                updateTotals(expenses)
                updateChart(expenses)
            }
        }
    }

    private fun updateTotals(expenses: List<Expense>) {
        val totalExpenses = expenses.sumOf { it.amount }
        val totalIncome = 2000.00 // Mock income
        val balance = totalIncome - totalExpenses

        val format = NumberFormat.getCurrencyInstance(Locale.US)

        binding.tvBalance.text = format.format(balance)
        binding.tvExpenses.text = format.format(totalExpenses)
        binding.tvIncome.text = format.format(totalIncome)
    }

    private fun updateChart(expenses: List<Expense>) {
        val categoryMap = expenses.groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }

        val entries = categoryMap.map { (category, amount) ->
            PieEntry(amount.toFloat(), category)
        }

        if (entries.isNotEmpty()) {
            val dataSet = PieDataSet(entries, "Spending by Category").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                valueTextSize = 12f
                valueTextColor = android.graphics.Color.WHITE
            }

            val data = PieData(dataSet)
            binding.pieChart.apply {
                this.data = data
                description.isEnabled = false
                legend.isEnabled = true
                setEntryLabelColor(android.graphics.Color.BLACK)
                animateY(1000)
                invalidate()
            }
        }
    }

    private fun showExpenseDetails(expense: Expense) {
        // For now, just show a toast
        Toast.makeText(this, "Selected: ${expense.description}", Toast.LENGTH_SHORT).show()
    }

    private fun loadSampleData() {
        // Load initial sample data only if database is empty
        viewModel.allExpenses.value?.let { expenses ->
            if (expenses.isEmpty()) {
                val sampleExpenses = listOf(
                    Expense(
                        amount = 25.50,
                        description = "Lunch at Restaurant",
                        category = ExpenseCategory.FOOD.displayName
                    ),
                    Expense(
                        amount = 45.00,
                        description = "Fuel for car",
                        category = ExpenseCategory.TRANSPORT.displayName
                    ),
                    Expense(
                        amount = 120.00,
                        description = "Monthly groceries",
                        category = ExpenseCategory.GROCERIES.displayName
                    ),
                    Expense(
                        amount = 15.99,
                        description = "Netflix subscription",
                        category = ExpenseCategory.ENTERTAINMENT.displayName
                    )
                )

                sampleExpenses.forEach { expense ->
                    viewModel.insertExpense(expense)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCameraActivity()
                } else {
                    Toast.makeText(
                        this,
                        "Camera permission denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VOICE_RECOGNITION && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            results?.firstOrNull()?.let { processVoiceInput(it) }
        }
    }

    private fun processVoiceInput(text: String) {
        // Simple voice input parsing
        val words = text.lowercase(Locale.US).split(" ")
        var amount = 0.0
        var description = ""
        var category = ExpenseCategory.OTHER

        // Find amount (look for numbers)
        words.find { it.matches(Regex("\\d+(\\.\\d+)?")) }?.toDoubleOrNull()?.let {
            amount = it
        }

        // Simple category detection
        when {
            text.contains("food") || text.contains("lunch") || text.contains("dinner") ->
                category = ExpenseCategory.FOOD
            text.contains("fuel") || text.contains("gas") || text.contains("uber") ->
                category = ExpenseCategory.TRANSPORT
            text.contains("grocery") || text.contains("market") ->
                category = ExpenseCategory.GROCERIES
        }

        description = text

        val expense = Expense(
            amount = amount,
            description = description,
            category = category.displayName
        )

        viewModel.insertExpense(expense)
        Toast.makeText(this, "Expense added via voice!", Toast.LENGTH_SHORT).show()
    }
}