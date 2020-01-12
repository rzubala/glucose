package com.zubala.rafal.glucose.spreadsheet

import com.google.api.services.sheets.v4.Sheets

object SpreadSheetServiceConfig {

    private var service: SpreadSheetService? = null

    fun service(sheets: Sheets?): SpreadSheetService? {
        sheets?.let {
            if (service == null) {
                service = SpreadSheetService(sheets)
            }
        }
        return service
    }
}
