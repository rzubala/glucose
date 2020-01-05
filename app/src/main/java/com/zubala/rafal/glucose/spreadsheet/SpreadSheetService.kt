package com.zubala.rafal.glucose.spreadsheet

import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.UpdateValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange
import java.io.IOException

const val SPREADSHEET_ID = "1fLdEapcTUxaxoCN9YWW_LReRJJoQMbNR3bun4jQseDw"
const val SHEET_NAME = "Arkusz1"
const val VALUE_INPUT_OPTION = "RAW"

class SpreadSheetService {
    fun test(sheets: Sheets, data: String) {
        try {
            val values: MutableList<List<Any>> = mutableListOf()
            repeat(10) {
                values.add(listOf("a$it $data", "b$it"))
            }
            val body: ValueRange = ValueRange().setValues(values)

            val range = "'$SHEET_NAME'!A1:B10"
            val result: UpdateValuesResponse =
                sheets.spreadsheets().values().update(SPREADSHEET_ID, range, body)
                    .setValueInputOption(VALUE_INPUT_OPTION)
                    .execute()
            Log.i("MainViewModel", result.toPrettyString())
        } catch (ex: UserRecoverableAuthIOException) {
            Log.e("MainViewModel", "UserRecoverableAuthIOException", ex)
        } catch (e: IOException) {
            Log.e("MainViewModel", "failure to get spreadsheet: ", e)
        }
    }
}