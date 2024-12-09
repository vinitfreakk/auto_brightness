package com.accidentaldeveloper.autobright

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BrightnessViewModel(private val context: Context) : ViewModel() {

    private val _brightness = MutableLiveData<Int>()
    val brightness: LiveData<Int> get() = _brightness

    private val _isAutoBrightness = MutableLiveData<Boolean>()
    val isAutoBrightness: LiveData<Boolean> get() = _isAutoBrightness

    private val resolver = context.contentResolver
    private var observer: ContentObserver? = null

    init {
        loadCurrentSettings()
        observeBrightnessChanges()
    }

    private fun loadCurrentSettings() {
        // Auto-brightness state
        val isAuto = Settings.System.getInt(
            resolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        ) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        _isAutoBrightness.value = isAuto

        // Current brightness level
        val currentBrightness = Settings.System.getInt(
            resolver,
            Settings.System.SCREEN_BRIGHTNESS,
            125 // Default value
        )
        _brightness.value = currentBrightness
    }

    fun setBrightness(value: Int) {
        if (!Settings.System.canWrite(context)) return
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, value)
        _brightness.value = value
    }

    fun toggleAutoBrightness(enabled: Boolean) {
        if (!Settings.System.canWrite(context)) return
        val mode = if (enabled) {
            Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        } else {
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        }
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE, mode)
        _isAutoBrightness.value = enabled
    }

    private fun observeBrightnessChanges() {
        observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                loadCurrentSettings()
            }
        }
        resolver.registerContentObserver(
            Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
            false,
            observer!!
        )
    }

    override fun onCleared() {
        super.onCleared()
        observer?.let { resolver.unregisterContentObserver(it) }
    }
}
