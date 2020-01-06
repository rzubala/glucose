package com.zubala.rafal.glucose.spreadsheet

import android.R.attr.data
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.UpdateValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange
import com.zubala.rafal.glucose.logic.getCurrentDateTime
import com.zubala.rafal.glucose.logic.toString
import java.io.IOException


const val SPREADSHEET_ID = "1fLdEapcTUxaxoCN9YWW_LReRJJoQMbNR3bun4jQseDw"
const val SHEET_NAME = "Arkusz1"
const val VALUE_INPUT_OPTION = "RAW"
const val DATE_ROW_START = 3
const val LIMIT_ROWS = 250

class SpreadSheetService {

    fun getRowByDate(sheets: Sheets): Int {
        val date = getCurrentDateTime()
        val dateInString = date.toString("dd.MM.yyyy")

        try {
            val result: ValueRange = sheets.spreadsheets().values()
                .get(SPREADSHEET_ID, toRange("A", DATE_ROW_START, DATE_ROW_START + LIMIT_ROWS))
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

    fun insertValue(sheets: Sheets, row: Int, data: String) {
        try {
            val values: MutableList<List<Any>> = MutableList(1) { MutableList(1) { data } }
            val body: ValueRange = ValueRange().setValues(values)
            val range = toRange("C", row, row)
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

    fun test(sheets: Sheets, data: String) {
        try {
            val values: MutableList<List<Any>> = mutableListOf()
            repeat(10) {
                values.add(listOf("a$it $data"))
            }
            val body: ValueRange = ValueRange().setValues(values)

            val range = toRange("A", 1, 10)
            val result: UpdateValuesResponse =
                sheets.spreadsheets().values().update(SPREADSHEET_ID, range, body)
                    .setValueInputOption(VALUE_INPUT_OPTION)
                    .execute()
            Log.i("SpreadSheetService", result.toPrettyString())
        } catch (ex: UserRecoverableAuthIOException) {
            Log.e("SpreadSheetService", "UserRecoverableAuthIOException", ex)
        } catch (e: IOException) {
            Log.e("SpreadSheetService", "failure to get spreadsheet: ", e)
        }
    }

    private inline fun toRange(col: String, from: Int, to: Int): String = "'$SHEET_NAME'!$col$from:$col$to"
}