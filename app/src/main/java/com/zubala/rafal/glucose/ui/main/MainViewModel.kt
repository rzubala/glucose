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

    private var _snackbarEvent = MutableLiveData<String>()

    val snackbarEvent: LiveData<String>
        get() = _snackbarEvent

    fun snackbarDone() {
        _snackbarEvent.value = ""
    }

    fun onSubmit() {
        Log.i("ViewModel", "Submit")
        _addMeasurementsEvent.value = true
    }

    fun doneSubmit() {
        _addMeasurementsEvent.value = false
    }

    fun sendMeasurements(data: String, type: Type) {
        var row: Int = -1
        var result = false
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                row = sheetsService.getRowByDate(sheets)
                if (row > 0) {
                    result = sheetsService.insertValue(sheets, row, data, type)
                }
            }
            if (!result) {
                _snackbarEvent.value = "ERROR: Value already exists!"
            } else {
                if (row < 0) {
                    _snackbarEvent.value = "Can not find row"
                } else {
                    _snackbarEvent.value = "New data at row: $row"
                }
            }
        }
    }
}

enum class Type {ON_EMPTY, BREAKFAST, DINNER, SUPPER}