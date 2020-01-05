package com.zubala.rafal.glucose.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val SHEET_URL = "https://docs.google.com/spreadsheets/d/1fLdEapcTUxaxoCN9YWW_LReRJJoQMbNR3bun4jQseDw/edit?usp=sharing"

class MainViewModel : ViewModel() {

    fun onSubmit() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                Log.i("ViewModel", "Submit")
            }
        }
    }
}
