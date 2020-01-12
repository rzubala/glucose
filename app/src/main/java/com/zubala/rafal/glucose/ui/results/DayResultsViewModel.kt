package com.zubala.rafal.glucose.ui.results

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubala.rafal.glucose.domain.GlucoseDay
import com.zubala.rafal.glucose.spreadsheet.SpreadSheetServiceConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val ON_EMPTY_LIMIT = 90

const val AFTER_1H_LIMIT = 140

class DayResultsViewModel : ViewModel() {

    private val sheetsService = SpreadSheetServiceConfig.service(null)

    private var _resultEvent = MutableLiveData<GlucoseDay?>()

    val resultEvent: LiveData<GlucoseDay?>
        get() = _resultEvent


    fun getResults() {
        viewModelScope.launch {
            var results: GlucoseDay? = null
            withContext(Dispatchers.IO) {
                sheetsService?.let {
                    results = sheetsService.getDayResults()
                }
            }
            _resultEvent.value = results
        }
    }

    fun doneResults() {
        _resultEvent.value = null
    }
}