package com.example.uqac_progmob_project.mainActivity

import MainScreen
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.uqac_progmob_project.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // hide the action bar
        supportActionBar?.hide()
        setContent {
            MainScreen()
        }
    }
}