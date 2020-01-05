package com.zubala.rafal.glucose.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.api.services.sheets.v4.Sheets

class MainViewModelFactory(private val sheets: Sheets) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(sheets) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
