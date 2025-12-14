package com.example.financemanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.financemanager.database.ExpenseDao
import com.example.financemanager.models.Expense
import kotlinx.coroutines.launch

class MainViewModel(private val expenseDao: ExpenseDao) : ViewModel() {

    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    fun insertExpense(expense: Expense) {
        viewModelScope.launch {
            expenseDao.insert(expense)
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            expenseDao.update(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            expenseDao.delete(expense)
        }
    }

    fun getTotalExpenses(): LiveData<Double> = expenseDao.getTotalExpenses()

    fun getCategoryTotals(): LiveData<Map<String, Double>> = expenseDao.getCategoryTotals()
}

class MainViewModelFactory(private val expenseDao: ExpenseDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(expenseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}