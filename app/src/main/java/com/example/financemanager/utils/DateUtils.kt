package com.example.financemanager.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(date)
    }

    fun formatSimpleDate(date: Date): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(date)
    }
}