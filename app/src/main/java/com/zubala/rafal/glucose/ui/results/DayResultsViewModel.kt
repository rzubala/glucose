package com.zubala.rafal.glucose.ui.results

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubala.rafal.glucose.domain.GlucoseDay
import com.zubala.rafal.glucose.logic.addDay
import com.zubala.rafal.glucose.logic.getCurrentDateTime
import com.zubala.rafal.glucose.spreadsheet.SpreadSheetServiceConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

const val ON_EMPTY_LIMIT = 90

const val AFTER_1H_LIMIT = 140

class DayResultsViewModel : ViewModel() {

    private val sheetsService = SpreadSheetServiceConfig.service(null)

    private var _resultEvent = MutableLiveData<GlucoseDay?>()

    var date: Date = getCurrentDateTime()

    val resultEvent: LiveData<GlucoseDay?>
        get() = _resultEvent

    private var _showProgressEvent = MutableLiveData<Boolean>()
    val showProgressEvent: LiveData<Boolean>
        get() = _showProgressEvent

    fun doneShowProgressEvent() {
        _showProgressEvent.value = false
    }

    fun getResults() {
        _showProgressEvent.value = true
        viewModelScope.launch {
            var results: GlucoseDay? = null
            withContext(Dispatchers.IO) {
                sheetsService?.let {
                    results = sheetsService.getDayResults(date)
                }
            }
            _resultEvent.value = results
        }
    }

    fun doneResults() {
        _resultEvent.value = null
    }

    fun onMinus() {
        date = addDay(date, -1)
        getResults()
    }

    fun onPlus() {
        date = addDay(date, 1)
        getResults()
    }
}