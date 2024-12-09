package com.accidentaldeveloper.autobright

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BrightnessViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BrightnessViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BrightnessViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
