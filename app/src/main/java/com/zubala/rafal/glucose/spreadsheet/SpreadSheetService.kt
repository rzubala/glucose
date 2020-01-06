package com.zubala.rafal.glucose.spreadsheet

import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.UpdateValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange
import com.zubala.rafal.glucose.logic.getCurrentDateTime
import com.zubala.rafal.glucose.logic.toString
import com.zubala.rafal.glucose.ui.main.Type
import java.io.IOException

const val SPREADSHEET_ID = "18K6JOOSKQqWssptBUUdcSQF8kWpXTImBXiSRwXPyyuI"
//const val SPREADSHEET_ID = "1fLdEapcTUxaxoCN9YWW_LReRJJoQMbNR3bun4jQseDw"
const val SHEET_NAME = "pomiary"
const val VALUE_INPUT_OPTION = "USER_ENTERED"
const val DATE_ROW_START = 3
const val LIMIT_ROWS = 250

class SpreadSheetService {

    fun getRowByDate(sheets: Sheets): Int {
        val date = getCurrentDateTime()
        val dateInString = date.toString("dd.MM.yyyy")

        try {
            val result: ValueRange = sheets.spreadsheets().values()
                .get(SPREADSHEET_ID, toRange("A", "A", DATE_ROW_START, DATE_ROW_START + LIMIT_ROWS))
                .execute()
            Log.i("SpreadSheetService", result.toPrettyString())
            var cnt = 0
            for (r in result.getValues()) {
                if (dateInString == r[0]) {
                    break
                }
                cnt++
            }
            return DATE_ROW_START + cnt
        } catch (ex: UserRecoverableAuthIOException) {
            Log.e("SpreadSheetService", "UserRecoverableAuthIOException", ex)
        } catch (e: IOException) {
            Log.e("SpreadSheetService", "failure to get spreadsheet: ", e)
        }
        return -1
    }

    fun insertValue(sheets: Sheets, row: Int, data: String, type: Type) {
        val date = getCurrentDateTime()
        val timeString = date.toString("HH:mm")
        try {
            val values: MutableList<List<Any>> = MutableList(1) { listOf(timeString, data) }
            val body: ValueRange = ValueRange().setValues(values)
            val range = getRange(type, row)
            val result: UpdateValuesResponse = sheets.spreadsheets().values().update(SPREADSHEET_ID, range, body)
                    .setValueInputOption(VALUE_INPUT_OPTION)
                    .execute()
            Log.i("SpreadSheetService", result.toPrettyString())
        } catch (ex: UserRecoverableAuthIOException) {
            Log.e("SpreadSheetService", "UserRecoverableAuthIOException", ex)
        } catch (e: IOException) {
            Log.e("SpreadSheetService", "failure to get spreadsheet: ", e)
        }
    }

    private fun getRange(type: Type, row: Int): String {
        return when (type) {
            Type.ON_EMPTY -> {
                toRange("B", "C", row, row)
            }
            Type.BREAKFAST -> {
                toRange("D", "E", row, row)
            }
            Type.DINNER -> {
                toRange("F", "G", row, row)
            }
            Type.SUPPER -> {
                toRange("H", "I", row, row)
            }
        }
    }

    private fun toRange(col1: String, col2: String, from: Int, to: Int): String = "'$SHEET_NAME'!$col1$from:$col2$to"
}