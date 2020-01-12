package com.zubala.rafal.glucose.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.services.sheets.v4.Sheets
import com.zubala.rafal.glucose.spreadsheet.SpreadSheetServiceConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(sheets: Sheets) : ViewModel() {

    private val sheetsService = SpreadSheetServiceConfig.service(sheets)

    private var _addMeasurementsEvent = MutableLiveData<Boolean>()

    val addMeasurementsEvent: LiveData<Boolean>
        get() = _addMeasurementsEvent

    private var _snackbarEvent = MutableLiveData<DataResult>()

    val snackbarEvent: LiveData<DataResult>
        get() = _snackbarEvent

    fun snackbarDone() {
        _snackbarEvent.value = DataResult.EMPTY
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
                sheetsService?.let {
                    row = sheetsService.getRowByDate()
                    if (row > 0) {
                        result = sheetsService.insertValue(row, data, type)
                    }
                }
            }
            if (!result) {
                _snackbarEvent.value = DataResult.DATA_EXISTS
            } else {
                if (row < 0) {
                    _snackbarEvent.value = DataResult.NO_ROW
                } else {
                    _snackbarEvent.value = DataResult.NEW_DATA
                }
            }
        }
    }
}

enum class DataResult {EMPTY, NO_ROW, NEW_DATA, DATA_EXISTS}

enum class Type {ON_EMPTY, BREAKFAST, DINNER, SUPPER}