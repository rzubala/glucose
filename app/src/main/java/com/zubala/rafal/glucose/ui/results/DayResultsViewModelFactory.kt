package com.zubala.rafal.glucose.ui.results

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.api.services.sheets.v4.Sheets

class DayResultsViewModelFactory(private val sheets: Sheets) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DayResultsViewModel::class.java)) {
            return DayResultsViewModel(sheets) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
