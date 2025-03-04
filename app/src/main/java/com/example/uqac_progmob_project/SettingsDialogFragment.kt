package com.example.uqac_progmob_project

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import com.example.uqac_progmob_project.databinding.DialogSettingsBinding

class SettingsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogSettingsBinding.inflate(layoutInflater)

        // Configure language spinner
        val languages = arrayOf("Français", "English")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_dropdown_item, languages)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter

        // Configure volume seekbar
        binding.volumeSeekbar.progress = getSavedVolumeLevel()
        binding.volumePercentage.text = "${binding.volumeSeekbar.progress}%"
        binding.volumeSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Save the volume level
                saveVolumeLevel(progress)
                binding.volumePercentage.text = "$progress%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle("Settings")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
    }

    private fun getSavedVolumeLevel(): Int {
        // Retrieve the saved volume level from shared preferences or other storage
        val sharedPreferences = requireContext().getSharedPreferences("app_settings", 0)
        return sharedPreferences.getInt("volume_level", 50) // Default volume level is 50
    }

    private fun saveVolumeLevel(level: Int) {
        // Save the volume level to shared preferences or other storage
        val sharedPreferences = requireContext().getSharedPreferences("app_settings", 0)
        with(sharedPreferences.edit()) {
            putInt("volume_level", level)
            apply()
        }
    }
}