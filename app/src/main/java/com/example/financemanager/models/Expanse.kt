package com.example.financemanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val description: String,
    val category: String,
    val date: Date = Date(),
    val createdAt: Date = Date()
)

enum class ExpenseCategory(val displayName: String) {
    FOOD("Food"),
    TRANSPORT("Transport"),
    SHOPPING("Shopping"),
    ENTERTAINMENT("Entertainment"),
    HEALTH("Health"),
    BILLS("Bills"),
    GROCERIES("Groceries"),
    OTHER("Other")
}