package com.example.uqac_progmob_project

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import com.example.uqac_progmob_project.databinding.DialogSettingsBinding

class SettingsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogSettingsBinding.inflate(layoutInflater)

        // Configure language spinner
        val languages = arrayOf("English", "Français")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_dropdown_item, languages)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.languageSpinner.adapter = adapter

        // Load saved language
        val savedLanguage = getSavedLanguage()
        val languageIndex = languages.indexOf(savedLanguage)
        if (languageIndex >= 0) {
            binding.languageSpinner.setSelection(languageIndex)
        }

        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLanguage = languages[position]
                if (selectedLanguage != savedLanguage) {
                    context?.let {
                        changeLocale(it, if (position == 0) "en" else "fr")
                        saveLanguage(selectedLanguage) // Save the selected language
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

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
            .setTitle(getString(R.string.settings))
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
    }

    private fun getSavedLanguage(): String {
        val sharedPreferences = requireContext().getSharedPreferences("app_settings", 0)
        return sharedPreferences.getString("language", "English") ?: "English"
    }

    private fun saveLanguage(language: String) {
        val sharedPreferences = requireContext().getSharedPreferences("app_settings", 0)
        with(sharedPreferences.edit()) {
            putString("language", language)
            apply()
        }
    }

    private fun getSavedVolumeLevel(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("app_settings", 0)
        return sharedPreferences.getInt("volume_level", 50)
    }

    private fun saveVolumeLevel(level: Int) {
        val sharedPreferences = requireContext().getSharedPreferences("app_settings", 0)
        with(sharedPreferences.edit()) {
            putInt("volume_level", level)
            apply()
        }
    }

    fun changeLocale(context: Context, language: String) {
        LanguageManager.saveLanguage(context, language)
        LanguageManager.setLocale(context, language)
        restartApp(context)
    }

    fun restartApp(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }
    }
}