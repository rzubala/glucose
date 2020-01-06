package com.zubala.rafal.glucose.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.services.sheets.v4.Sheets
import com.zubala.rafal.glucose.spreadsheet.SpreadSheetService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(private val sheets: Sheets) : ViewModel() {

    private val sheetsService = SpreadSheetService()

    private var _addMeasurementsEvent = MutableLiveData<Boolean>()

    val addMeasurementsEvent: LiveData<Boolean>
        get() = _addMeasurementsEvent


    fun onSubmit() {
        Log.i("ViewModel", "Submit")
        _addMeasurementsEvent.value = true
    }

    fun doneSubmit() {
        _addMeasurementsEvent.value = false
    }

    fun sendMeasurements(data: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val row = sheetsService.getRowByDate(sheets)
                if (row > 0) {
                    sheetsService.insertValue(sheets, row, data)
                }
            }
        }
    }
}
