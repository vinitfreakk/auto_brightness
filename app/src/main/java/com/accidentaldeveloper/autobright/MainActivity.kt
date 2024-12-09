package com.accidentaldeveloper.autobright

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.CheckBox
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: BrightnessViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val brightnessSeekBar = findViewById<SeekBar>(R.id.brightnessSeekBar)
        val autoBrightnessCheckBox = findViewById<CheckBox>(R.id.autoBrightnessCheckBox)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this, BrightnessViewModelFactory(this))[BrightnessViewModel::class.java]

        // Observe brightness value and update SeekBar
        viewModel.brightness.observe(this) { value ->
            brightnessSeekBar.progress = value
        }

        // Observe auto-brightness state and manage SeekBar behavior
        viewModel.isAutoBrightness.observe(this) { isAuto ->
            autoBrightnessCheckBox.isChecked = isAuto
            brightnessSeekBar.isEnabled = !isAuto
        }

        // Sync SeekBar state initially
        brightnessSeekBar.isEnabled = !autoBrightnessCheckBox.isChecked

        // SeekBar change listener
        brightnessSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    viewModel.setBrightness(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // CheckBox toggle listener
        autoBrightnessCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleAutoBrightness(isChecked)
        }

        // Check for WRITE_SETTINGS permission
        if (!Settings.System.canWrite(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        }
    }
}