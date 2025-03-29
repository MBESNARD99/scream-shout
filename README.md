# 🎮 Scream & Shout

Un mini-jeu Android inspiré de Flappy Bird, où tu contrôles le personnage... **en criant !** 🎤

---

## 🧠 Concept

Le personnage saute en fonction du **volume de ta voix** captée par le micro.  
Le but est d’éviter les **piliers** avec un trou central, qui défilent à l’écran.  
Si tu touches un pilier ou le haut/bas de l'écran : **Game Over**.

---

## 📱 Fonctionnalités

- 🎙️ Contrôle par la voix (via le micro)
- 📉 Gravité et saut dynamiques
- 🧱 Génération automatique de piliers avec collisions
- 🚫 Détection de collision instantanée (Game Over direct)
- 🎬 Écrans :
  - **WelcomeActivity** : écran d’accueil avec bouton *Jouer*
  - **MainActivity** : partie active
  - **GameOverActivity** : fin de partie avec bouton *Rejouer*

---

## 🔧 Prérequis

- Android Studio Arctic Fox ou +
- Un appareil (ou émulateur) avec **micro**
- API cible : 31 (Android 12) ou plus récent
