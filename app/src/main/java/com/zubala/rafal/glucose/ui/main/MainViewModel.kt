package com.zubala.rafal.glucose.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    fun onSubmit() {
        Log.i("ViewModel", "Submit")
    }
}
