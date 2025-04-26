package com.example.uqac_progmob_project.gameChoose

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uqac_progmob_project.BaseActivity

class GameDetailActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val gameName = intent.getStringExtra("GAME_NAME") ?: ""
        val gameDescription = intent.getStringExtra("GAME_DESCRIPTION") ?: ""

        setContent {

            GameDetailScreen(
                gameName = gameName,
                gameDescription = gameDescription,
                onBackClick = { finish() },
                onPlayClick = {
                    val intent = Intent(this, GameSettingsActivity::class.java).apply {
                        putExtra("GAME_NAME", gameName)
                    }
                    startActivity(intent)
                }
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    gameName: String,
    gameDescription: String,
    gameIconResId: Int? = null,
    onBackClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    // 🎨 Palette personnalisée en nuances claires de gris
    val backgroundColor = Color(0xFFF5F5F5)
    val surfaceVariant = Color(0xFFEDEDED)
    val primaryTextColor = Color(0xFF333333)
    val labelTextColor = Color(0xFF666666)
    val buttonColor = Color(0xFFE0E0E0)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = gameName,
                        style = MaterialTheme.typography.titleLarge.copy(color = primaryTextColor),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = primaryTextColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = surfaceVariant
                )
            )
        }
    ) { innerPadding ->

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Image du jeu
            gameIconResId?.let { resId ->
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = "$gameName illustration",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(4.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Description dans une surface douce
            Surface(
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 2.dp,
                color = surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleMedium.copy(color = labelTextColor)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = gameDescription,
                        style = MaterialTheme.typography.bodyMedium.copy(color = primaryTextColor),
                        textAlign = TextAlign.Justify
                    )
                }
            }

            // Bouton Jouer
            Button(
                onClick = onPlayClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = primaryTextColor
                )
            ) {
                Text(text = "Jouer", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewGameDetailScreen() {
    GameDetailScreen(
        gameName = "Nom du Jeu",
        gameDescription = "Ceci est une description fictive du jeu pour l'aperçu. Elle peut inclure des détails sur le gameplay, les règles ou d'autres informations pertinentes.",
        gameIconResId = null, // Vous pouvez remplacer par une ressource drawable si nécessaire
        onBackClick = {},
        onPlayClick = {}
    )
}