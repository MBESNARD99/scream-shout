package com.example.screamshout

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioRecord
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.edit

/*

Objet qui gère la logique centrale du jeu :

 - Le score
 - Le nom du joueur
 - Les sauvegardes de score
 - La détection de fin de partie

 */


object GameManager {

    var score: Int = 0
    private val countedPillarPairs = mutableSetOf<View>() //collection enregistrant les piliers déjà comptés
    private var currentPlayerName: String = "Inconnu"

    fun setPlayerName(name: String) {
        currentPlayerName = name
    }

    fun getPlayerName(): String = currentPlayerName

    fun resetScore() {
        score = 0
        countedPillarPairs.clear()
    }

    // Incrémentation du score
    fun updateScore(character: ImageView, pillars: List<View>) {
        for (i in 0 until pillars.size step 2) {
            val pillar = pillars[i]
            // Vérifie si le pilier est dépassé
            if (!countedPillarPairs.contains(pillar) && pillar.x + pillar.width < character.x) {
                score++
                countedPillarPairs.add(pillar)
            }
        }
    }

    // Sauvegarde du score
    fun saveScore(context: Context, score: Int) {
        val prefs = context.getSharedPreferences("leaderboard", Context.MODE_PRIVATE)
        val name = currentPlayerName
        val bestScore = prefs.getInt(name, 0)

        if (score > bestScore) {
            prefs.edit() { putInt(name, score) }
        }
    }

    // Récupérer le classement
    fun getLeaderboard(context: Context): List<Pair<String, Int>> {
        val prefs = context.getSharedPreferences("leaderboard", Context.MODE_PRIVATE)
        return prefs.all
            .mapNotNull { (name, value) -> if (value is Int) name to value else null }
            .sortedByDescending { it.second }
    }

    fun handleGameOver(
        activity: Activity,
        handler: Handler, //permet d'arrêter tous les runnable
        audioRecord: AudioRecord?,
        character: ImageView,
        pillars: MutableList<View>,
        gameLayout: FrameLayout,
        onCleanup: () -> Unit = {} //tâche de nettoyage
    ) {
        // Supprimer tous les messages/runnables en attente
        handler.removeCallbacksAndMessages(null)

        // Stopper l'enregistrement du micro
        audioRecord?.stop()
        audioRecord?.release()

        saveScore(activity.applicationContext, score)
        onCleanup()

        activity.runOnUiThread {
            val intent = Intent(activity, GameOverActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }
    }
}
