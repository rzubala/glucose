package com.zubala.rafal.glucose.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private var _savedEvent = MutableLiveData<Boolean>()
    val savedEvent: LiveData<Boolean>
        get() = _savedEvent

    fun onSave() {
        _savedEvent.value = true
    }

    fun onSaved() {
        _savedEvent.value = false
    }
}