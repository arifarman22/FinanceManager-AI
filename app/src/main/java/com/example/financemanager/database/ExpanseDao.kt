package com.example.financemanager.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.financemanager.models.Expense
import java.util.*

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getExpensesBetween(start: Date, end: Date): LiveData<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpenses(): LiveData<Double>

    @Query("SELECT category, SUM(amount) as total FROM expenses GROUP BY category")
    fun getCategoryTotals(): LiveData<Map<String, Double>>
}