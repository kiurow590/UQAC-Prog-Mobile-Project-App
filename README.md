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
├── mainActivity/
│   ├── MainActivity.kt — Connexion utilisateur
│   ├── GoogleAuthHelper.kt — Gestion authentification Google
│   ├── MainActivityComposableLayout.kt — Écran principal
├── gameChoose/
│   ├── GameChoose.kt — Sélection de jeux
│   ├── GameDetailActivity.kt — Détail d'un jeu
│   ├── GameSettingsActivity.kt — Paramétrage des parties
│   ├── FinalResult.kt — Résultats et sauvegarde
│   └── AccountDialogActivity.kt — Affichage de compte
├── gameHistory/
│   ├── GameHistoryActivity.kt — Historique des parties
├── games/
│   ├── bomberGames/ — *Et Boom!* (jeu de mots et reconnaissance vocale)
│   ├── triMann/ — *TriMann* (jeu de dés)
│   ├── werewolfGame/ — *Bleiz-Garou* (jeu de rôle)
├── settings/
│   ├── SettingsDialogFragment.kt — Langue, Volume
├── utils/
│   ├── LanguageManager.kt — Gestion multilingue
│   ├── SpeechRecognizerHelper.kt — Reconnaissance vocale
├── ui/theme/
│   ├── Theme.kt — Thème clair/sombre
│   └── Type.kt — Styles de texte
└── BaseActivity.kt — Support multilingue global
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
Licence à préciser selon diffusion.

---

# Vidéo Compilation

https://github.com/user-attachments/assets/07491da3-b742-49c7-88bc-ace2d20a8447

