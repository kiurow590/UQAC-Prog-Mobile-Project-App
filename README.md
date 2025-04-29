---

# 🎲 La Taverne au Jeu

Bienvenue dans **La Taverne au Jeu**, une application mobile Android de mini-jeux multijoueurs développée avec **Jetpack Compose** et **Firebase** !

---

## 📱 Fonctionnalités principales

- **Connexion Google**  
  Authentification sécurisée via Google grâce à Firebase Authentication.

- **Choix de jeux**  
  Accédez à plusieurs mini-jeux originaux :
  - **TriMann** : Jeu de dés avec règles personnalisées.
  - **Bleiz-Garou** : Adaptation du célèbre jeu "Loup-Garou" en mode numérique.
  - **Et Boom !** : Jeu d'association de mots rapide sous la pression d'une bombe virtuelle.
  - **Course Épic** *(prochainement disponible)*.

- **Gestion de la session**  
  - Création de parties personnalisées (noms des joueurs, session).
  - Paramétrage du nombre de joueurs.
  - Historique des parties sauvegardé dans Firestore.

- **Résultats de jeu**  
  - Affichage dynamique des scores.
  - Sauvegarde automatique dans Firestore avec horodatage.

- **Multilingue & Accessibilité**  
  - Langues supportées : Français 🇫🇷, Anglais 🇬🇧.
  - Sélecteur de langue et réglage du volume via une interface dédiée.

---

## 🛠️ Stack technique

- **Android** — Kotlin, Jetpack Compose
- **Firebase** — Authentication, Firestore Database
- **Speech Recognition** — Utilisé dans *Et Boom !* pour capturer les mots des joueurs.
- **Material Design 3** — Interface moderne, support clair/sombre.

---

## 🔥 Architecture du projet

```plaintext
.
├── README.md
├── app
│   └── src
│       ├── main
│           ├── AndroidManifest.xml
│           ├── java
│           │   └── com
│           │       └── example
│           │           └── uqac_progmob_project
│           │               ├── BaseActivity.kt
│           │               ├── LanguageManager.kt
│           │               ├── gameChoose
│           │               │   ├── AccountDialogActivity.kt
│           │               │   ├── FinalResult.kt
│           │               │   ├── GameChoose.kt
│           │               │   ├── GameDetailActivity.kt
│           │               │   └── GameSettingsActivity.kt
│           │               ├── gameHistory
│           │               │   └── GameHistoryActivity.kt
│           │               ├── games
│           │               │   ├── bomberGames
│           │               │   │   ├── BombGames.kt
│           │               │   │   └── SpeechRecognizerHelper.kt
│           │               │   ├── triMann
│           │               │   │   └── TriMannGame.kt
│           │               │   └── werewolfGame
│           │               │       └── WerewolfGame.kt
│           │               ├── mainActivity
│           │               │   ├── GoogleAuthHelper.kt
│           │               │   ├── MainActivity.kt
│           │               │   └── MainActivityComposableLayout.kt
│           │               ├── settings
│           │               │   └── SettingsDialogFragment.kt
│           │               └── ui
│           │                   └── theme
│           │                       ├── Color.kt
│           │                       ├── Theme.kt
│           │                       └── Type.kt
│           └── res
│               ├── drawable
│               │   ├── baseline_close_24.xml
│               │   ├── baseline_keyboard_backspace_24.xml
│               │   ├── baseline_person_24.xml
│               │   ├── bomb_character_o_explode0.png
│               │   ├── bomb_character_o_explode1.png
│               │   ├── bomb_character_o_explode2.png
│               │   ├── bomb_character_o_explode3.png
│               │   ├── bomb_character_o_idle.png
│               │   ├── dice1.png
│               │   ├── dice2.png
│               │   ├── dice3.png
│               │   ├── dice4.png
│               │   ├── dice5.png
│               │   ├── dice6.png
│               │   ├── ic_arrow_down.xml
│               │   ├── ic_arrow_up.xml
│               │   ├── ic_launcher_background.xml
│               │   ├── ic_launcher_foreground.xml
│               │   ├── manage_search_32dp_e3e3e3_fill0_wght400_grad0_opsz40.xml
│               │   ├── ouest_forge_game.png
│               │   ├── public_32dp_e3e3e3_fill0_wght400_grad0_opsz40.xml
│               │   ├── taverne_au_jeux.png
│               │   ├── trophee.xml
│               │   └── volume_up_32dp_e3e3e3_fill0_wght400_grad0_opsz40.xml
│               ├── mipmap-anydpi-v26
│               │   ├── ic_launcher.xml
│               │   └── ic_launcher_round.xml
│               ├── mipmap-hdpi
│               │   ├── ic_launcher.webp
│               │   └── ic_launcher_round.webp
│               ├── mipmap-mdpi
│               │   ├── ic_launcher.webp
│               │   └── ic_launcher_round.webp
│               ├── mipmap-xhdpi
│               │   ├── ic_launcher.webp
│               │   └── ic_launcher_round.webp
│               ├── mipmap-xxhdpi
│               │   ├── ic_launcher.webp
│               │   └── ic_launcher_round.webp
│               ├── mipmap-xxxhdpi
│               │   ├── ic_launcher.webp
│               │   └── ic_launcher_round.webp
│               ├── values
│               │   ├── colors.xml
│               │   ├── strings.xml
│               │   └── themes.xml
│               ├── values-fr
│               │   └── string.xml
│               └── xml
│                   ├── backup_rules.xml
│                   └── data_extraction_rules.xml
├── assets
    └── demo.webm
```

---

## 🚀 Lancement du projet

1. **Prérequis**  
   - Android Studio Giraffe ou plus récent.
   - API minimum : Android 8.0 (Oreo) — `minSdk 26`.
   - Configuration Firebase (fichier `google-services.json`).

2. **Installation**
   - Cloner le projet :
     ```bash
     git clone [<lien-du-repo>](https://github.com/kiurow590/UQAC-Prog-Mobile-Project-App)
     ```
   - Ouvrir dans Android Studio.
   - Synchroniser Gradle.
   - Exécuter sur un émulateur ou appareil physique.

3. **Notes importantes**
   - Activer l'authentification Google dans Firebase Console.
   - Ajouter la clé SHA-1 de votre projet dans la configuration Firebase.

---

## ✨ Améliorations futures possibles

- Implémentation du jeu **Course Épic**.
- Ajout de mini-jeux supplémentaires.
- Système de notifications entre joueurs.

---

## 📜 Licence

Projet développé dans le cadre de **Programmation Mobile UQAC** — Usage académique.  

---

# Vidéo Compilation

https://github.com/user-attachments/assets/07491da3-b742-49c7-88bc-ace2d20a8447

