package com.example.uqac_progmob_project

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val language = LanguageManager.getLanguage(newBase)
        val context = LanguageManager.setLocale(newBase, language)
        super.attachBaseContext(context)
    }
}
