package com.example.todo.util

import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Utils {
    fun formatDate(date: Date?): String {
        return if (date != null) {
            val formatter = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
            formatter.format(date)
        } else {
            "Set Due Date"
        }
    }
}
object ThemeManager {
    private val _isDarkMode = mutableStateOf(false)

    val isDarkMode: State<Boolean> = _isDarkMode

    fun init(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        _isDarkMode.value = prefs.getBoolean("dark_mode_key", false)
    }

    fun setDarkMode(context: Context, isDark: Boolean) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putBoolean("dark_mode_key", isDark).apply()
        _isDarkMode.value = isDark
    }
}