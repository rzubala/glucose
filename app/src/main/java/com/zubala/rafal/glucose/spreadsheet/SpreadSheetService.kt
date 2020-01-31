package com.zubala.rafal.glucose.spreadsheet

import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.UpdateValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange
import com.zubala.rafal.glucose.domain.GlucoseDay
import com.zubala.rafal.glucose.domain.toGlucose
import com.zubala.rafal.glucose.logic.getCurrentDateTime
import com.zubala.rafal.glucose.logic.toString
import com.zubala.rafal.glucose.ui.main.Type
import java.io.IOException
import java.util.*

const val SHEET_NAME = "glucose"
const val VALUE_INPUT_OPTION = "USER_ENTERED"
const val DATE_ROW_START = 3
const val LIMIT_ROWS = 250
const val ROW_NOT_FOUND = -1
const val SPREADSHEET_EXCEPTION_ROW = -10

class SpreadSheetService(private val sheets: Sheets, var spreadsheetId: String = "") {

    fun getRowByDate(date: Date = getCurrentDateTime()): Int {
        val dateInString = date.toString("dd.MM.yyyy")

        try {
            val result: ValueRange = sheets.spreadsheets().values()
                .get(spreadsheetId, toRange("A", "A", DATE_ROW_START, DATE_ROW_START + LIMIT_ROWS))
                .execute()
            Log.i("SpreadSheetService", result.toPrettyString())
            var cnt = 0
            var found = false
            for (r in result.getValues()) {
                if (r.isNotEmpty() && dateInString == r[0]) {
                    found = true
                    break
                }
                cnt++
            }
            if (!found) {
                return ROW_NOT_FOUND
            }
            return DATE_ROW_START + cnt
        } catch (ex: UserRecoverableAuthIOException) {
            Log.e("SpreadSheetService", "UserRecoverableAuthIOException", ex)
            return SPREADSHEET_EXCEPTION_ROW
        } catch (e: IOException) {
            Log.e("SpreadSheetService", "failure to get spreadsheet: ", e)
            return SPREADSHEET_EXCEPTION_ROW
        }
    }

    fun getValue(range: String): String {
        try {
            val result: ValueRange = sheets.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute()
            Log.i("SpreadSheetService", result.toPrettyString())
            result.getValues()?.let {
                for (r in result.getValues()) {
                    if (r.size > 1) {
                        return r[1] as String
                    }
                }
            }
        } catch (e: IOException) {
            Log.e("SpreadSheetService", "failure to get spreadsheet: ", e)
        }
        return ""
    }

    fun getDayResults(date: Date): GlucoseDay {
        val row = getRowByDate(date)
        if (row < 0) {
            return GlucoseDay.error()
        }
        return try {
            val result: ValueRange = sheets.spreadsheets().values()
                .get(spreadsheetId, toRange("B", "I", row, row))
                .execute()
            Log.i("SpreadSheetService", result.toPrettyString())
            result.getValues()?.toGlucose() ?: GlucoseDay.empty()
        } catch (e: IOException) {
            Log.e("SpreadSheetService", "failure to get spreadsheet: ", e)
            GlucoseDay.error()
        }
    }

    fun insertValue(row: Int, data: String, type: Type): Boolean {
        val date = getCurrentDateTime()
        val timeString = date.toString("HH:mm")
        try {
            val values: MutableList<List<Any>> = MutableList(1) { listOf(timeString, data) }
            val body: ValueRange = ValueRange().setValues(values)
            val range = getRange(type, row)

            val oldValue = getValue(range)
            Log.i("SpreadSheetService", "Old value: $oldValue")
            if (oldValue.isNotEmpty()) {
                return false
            }

            val result: UpdateValuesResponse = sheets.spreadsheets().values().update(spreadsheetId, range, body)
                    .setValueInputOption(VALUE_INPUT_OPTION)
                    .execute()
            Log.i("SpreadSheetService", result.toPrettyString())
            return true
        } catch (ex: UserRecoverableAuthIOException) {
            Log.e("SpreadSheetService", "UserRecoverableAuthIOException", ex)
            return false
        } catch (e: IOException) {
            Log.e("SpreadSheetService", "failure to get spreadsheet: ", e)
            return false
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