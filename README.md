# Scream & Shout

Un mini-jeu Android inspiré de *Flappy Bird*, où vous contrôlez le personnage... en criant !

## Concept

Dans ce jeu, le personnage saute en fonction du volume de votre voix, capté par le microphone de votre appareil. Le but est d'éviter les piliers avec un trou central qui défilent à l'écran. Si vous touchez un pilier ou les bords supérieur ou inférieur de l'écran, la partie se termine (Game Over).

## Fonctionnalités

- **Contrôle par la voix** : Utilise le microphone pour détecter le volume de votre voix et faire sauter le personnage.
- **Gravité et saut dynamiques** : Le saut du personnage est influencé par l'intensité du son, créant une dynamique de jeu unique.
- **Génération automatique de piliers** : Les piliers avec un trou central défilent de manière aléatoire à l'écran.
- **Détection instantanée de collision** : Si le personnage touche un pilier ou les bords de l'écran, la partie se termine immédiatement.
- **Écrans du jeu** :
  - **WelcomeActivity** : Écran d'accueil avec un bouton pour démarrer le jeu.
  - **MainActivity** : Écran de la partie active où se déroule le jeu.
  - **GameOverActivity** : Écran de fin de partie avec un bouton pour rejouer.

## Prérequis

- **Android Studio Arctic Fox** ou version supérieure
- Un appareil Android ou émulateur avec microphone
- **API cible** : 31 (Android 12) ou plus récent

## Installation

1. Clonez ce repository sur votre machine locale :

   ```bash
   git clone https://github.com/MBESNARD99/scream-shout.git
