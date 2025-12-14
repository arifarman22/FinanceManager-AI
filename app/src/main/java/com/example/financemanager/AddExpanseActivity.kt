package com.example.financemanager

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.financemanager.database.AppDatabase
import com.example.financemanager.databinding.ActivityAddExpanseBinding
import com.example.financemanager.models.Expense
import com.example.financemanager.models.ExpenseCategory
import com.example.financemanager.viewmodels.MainViewModel
import com.example.financemanager.viewmodels.MainViewModelFactory
import java.text.SimpleDateFormat
import java.util.*



class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpanseBinding
    private lateinit var viewModel: MainViewModel
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpanseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupUI()
        setupClickListeners()
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val factory = MainViewModelFactory(database.expenseDao())
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    private fun setupUI() {
        // Setup category dropdown
        val categories = ExpenseCategory.values().map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)

        // Set current date
        updateDateInView()
    }

    private fun setupClickListeners() {
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            saveExpense()
        }
    }

    private fun showDatePicker() {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateInView() {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.etDate.setText(format.format(calendar.time))
    }

    private fun saveExpense() {
        val amountText = binding.etAmount.text.toString()
        val description = binding.etDescription.text.toString().trim()
        val category = binding.actvCategory.text.toString().trim()

        if (amountText.isEmpty()) {
            binding.etAmount.error = "Please enter amount"
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.etAmount.error = "Please enter valid amount"
            return
        }

        if (description.isEmpty()) {
            binding.etDescription.error = "Please enter description"
            return
        }

        if (category.isEmpty()) {
            binding.actvCategory.error = "Please select category"
            return
        }

        val expense = Expense(
            amount = amount,
            description = description,
            category = category,
            date = calendar.time
        )

        viewModel.insertExpense(expense)

        Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}