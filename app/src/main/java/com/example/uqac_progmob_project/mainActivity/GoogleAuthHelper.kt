package com.example.uqac_progmob_project.mainActivity

import android.content.Context
import android.content.Intent
import android.util.Base64
import android.util.Log
import androidx.credentials.*
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.SecureRandom
import com.example.uqac_progmob_project.R
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleAuthHelper(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)
    private val firebaseAuth = FirebaseAuth.getInstance()

    // 🔹 Génère un nonce unique et sécurisé
    private fun generateNonce(): String {
        val nonce = ByteArray(32)
        SecureRandom().nextBytes(nonce)
        return Base64.encodeToString(nonce, Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE)
    }

    // 🔹 Hash le nonce avec SHA-256 pour la sécurité
    private fun hashNonce(nonce: String): String {
        val bytes = nonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return Base64.encodeToString(digest, Base64.NO_WRAP or Base64.NO_PADDING or Base64.URL_SAFE)
    }

    // 🔹 Démarre la connexion Google
    fun signInWithGoogle(scope: CoroutineScope) {
        val rawNonce = generateNonce()
        val hashedNonce = hashNonce(rawNonce)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(context.getString(R.string.web_client_id))
            .setNonce(hashedNonce) // 🔹 Sécurise le nonce
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch(Dispatchers.Main) {
            try {
                val result = credentialManager.getCredential(context, request)
                handleSignIn(result, rawNonce)
            } catch (e: GetCredentialException) {
                Log.e("GoogleSignIn", "Erreur de connexion Google : ${e.message}")
            }
        }
    }

    // 🔹 Gère la réponse de Google Credential Manager
    private fun handleSignIn(result: GetCredentialResponse, rawNonce: String) {
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken.toString(), rawNonce)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("GoogleSignIn", "Token ID Google invalide", e)
                    }
                } else {
                    Log.e("GoogleSignIn", "Type de credential inattendu")
                }
            }

            else -> {
                Log.e("GoogleSignIn", "Type de credential inconnu")
            }
        }
    }

    // 🔹 Authentifie l'utilisateur avec Firebase
    private fun firebaseAuthWithGoogle(idToken: String, rawNonce: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, rawNonce)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("GoogleSignIn", "Connexion réussie ✅")
                    Log.d("GoogleSignIn", "Connexion réussie ✅")
                    // changement d'activité pour aller sur le MainActivity
                    val intent = Intent(context, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    }
                    context.startActivity(intent)
                } else {
                    Log.e("GoogleSignIn", "Erreur Firebase ❌ : ${task.exception?.message}")
                }
            }
    }
}
