package com.zubala.rafal.glucose.ui.results

import androidx.lifecycle.ViewModel
import com.google.api.services.sheets.v4.Sheets
import com.zubala.rafal.glucose.spreadsheet.SpreadSheetService

class DayResultsViewModel(private val sheets: Sheets) : ViewModel() {

    private val sheetsService = SpreadSheetService()

    fun getResults() = sheetsService.getDayResults(sheets)
}