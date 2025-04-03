package com.example.uqac_progmob_project.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uqac_progmob_project.LanguageManager
import com.example.uqac_progmob_project.R
import com.example.uqac_progmob_project.mainActivity.MainActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    val context = LocalContext.current
    var selectedLanguage by remember { mutableStateOf(getSavedLanguage(context)) }
    var volumeLevel by remember { mutableIntStateOf(getSavedVolumeLevel(context)) }
    var expanded by remember { mutableStateOf(false) } // ✅ Gère l'ouverture du menu déroulant

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(id = R.string.settings)) },
        text = {
            Column {
                // Language Dropdown
                val languages = listOf("English", "Français")

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedLanguage,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        label = { Text(text = stringResource(id = R.string.language)) },
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.public_32dp_e3e3e3_fill0_wght400_grad0_opsz40),
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        languages.forEachIndexed { index, language ->
                            DropdownMenuItem(
                                text = { Text(text = language) },
                                onClick = {
                                    selectedLanguage = language
                                    expanded = false

                                    if (language != getSavedLanguage(context)) {
                                        changeLocale(context, if (index == 0) "en" else "fr")
                                        saveLanguage(context, language)
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Volume Slider with Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.volume_up_32dp_e3e3e3_fill0_wght400_grad0_opsz40),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "${volumeLevel}%")
                        Slider(
                            value = volumeLevel.toFloat(),
                            onValueChange = { volumeLevel = it.toInt() },
                            valueRange = 0f..100f,
                            onValueChangeFinished = {
                                saveVolumeLevel(context, volumeLevel)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "OK")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsDialog() {
    SettingsDialog(onDismissRequest = {}, onConfirm = {})
}

/**
 * Gère la langue de l'application
 */
private fun getSavedLanguage(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("app_settings", 0)
    return sharedPreferences.getString("language", "English") ?: "English"
}

/**
 * Gère la langue de l'application
 */
private fun saveLanguage(context: Context, language: String) {
    val sharedPreferences = context.getSharedPreferences("app_settings", 0)
    with(sharedPreferences.edit()) {
        putString("language", language)
        apply()
    }
}

/**
 * Gère le niveau de volume de l'application
 */
private fun getSavedVolumeLevel(context: Context): Int {
    val sharedPreferences = context.getSharedPreferences("app_settings", 0)
    return sharedPreferences.getInt("volume_level", 50)
}

/**
 * Gère le niveau de volume de l'application
 */
private fun saveVolumeLevel(context: Context, level: Int) {
    val sharedPreferences = context.getSharedPreferences("app_settings", 0)
    with(sharedPreferences.edit()) {
        putInt("volume_level", level)
        apply()
    }
}

/**
 * Change la langue de l'application
 */
fun changeLocale(context: Context, language: String) {
    LanguageManager.saveLanguage(context, language)
    LanguageManager.setLocale(context, language)
    restartApp(context)
}


/**
 * Redémarre l'application
 */
fun restartApp(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(intent)
    if (context is Activity) {
        context.finish()
    }
}