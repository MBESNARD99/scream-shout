package com.example.screamshout

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/*

Fichier principal de l'écran accueil du jeu

 */

class WelcomeActivity : AppCompatActivity() {

    private lateinit var backgroundMusic: MediaPlayer // Musique de fond pour le menu principal
    private var isMusicPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Initialisation et lancement de la musique de fond
        backgroundMusic = MediaPlayer.create(this, R.raw.menu_music)
        backgroundMusic.isLooping = true
        backgroundMusic.start()

        // Récupération des boutons de l'interface
        val playButton: Button = findViewById(R.id.startGameButton)
        val rulesButton: Button = findViewById(R.id.rulesButton)
        val creditsButton: Button = findViewById(R.id.creditsButton)
        val settingsButton: ImageButton = findViewById(R.id.settingsButton)
        val leaderboardButton: Button = findViewById(R.id.leaderboardButton)

        // Gestion des clics sur les différents boutons
        playButton.setOnClickListener { showPlayerNameDialog() }
        rulesButton.setOnClickListener { showRulesDialog() }
        creditsButton.setOnClickListener { showCreditsDialog() }
        leaderboardButton.setOnClickListener { showLeaderboardDialog() }

        // Animation de rotation sur le bouton paramètres
        val rotate = AnimationUtils.loadAnimation(this, R.anim.rotate)
        settingsButton.setOnClickListener {
            it.startAnimation(rotate)
            showMusicDialog()
        }
    }

    // Libération des ressources liées à la musique lors de la fermeture de l'activité
    override fun onDestroy() {
        super.onDestroy()
        if (::backgroundMusic.isInitialized) {
            backgroundMusic.stop()
            backgroundMusic.release()
        }
    }

    // Affiche la boîte de dialogue pour activer/désactiver la musique
    private fun showMusicDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_music, null)
        val toggleButton = dialogView.findViewById<ToggleButton>(R.id.musicToggleButton)
        val closeButton = dialogView.findViewById<Button>(R.id.closeMusicButton)

        // Met à jour l’état du bouton selon l’état de la musique
        toggleButton.isChecked = isMusicPlaying
        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            isMusicPlaying = isChecked
            if (isChecked) backgroundMusic.start()
            else backgroundMusic.pause()
        }

        // Création de la boîte de dialogue avec thème personnalisé
        val dialog = AlertDialog.Builder(this, R.style.BlurDialogTheme)
            .setView(dialogView)
            .create()

        // Animation de fermeture du dialogue
        closeButton.setOnClickListener {
            val animOut = AnimationUtils.loadAnimation(this, R.anim.dialog_exit)
            dialogView.startAnimation(animOut)
            dialogView.postDelayed({ dialog.dismiss() }, animOut.duration)
        }

        // Animation d’ouverture + son
        dialog.setOnShowListener {
            val animIn = AnimationUtils.loadAnimation(this, R.anim.dialog_enter)
            dialogView.startAnimation(animIn)
            val mp = MediaPlayer.create(this, R.raw.dialog_open)
            mp.setOnCompletionListener { it.release() }
            mp.start()
        }

        dialog.show()
    }

    // Affiche le classement des joueurs
    private fun showLeaderboardDialog() {
        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
            setBackgroundResource(R.drawable.dialog_background)
            layoutParams = LinearLayout.LayoutParams(
                resources.displayMetrics.density.times(320).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(32, 32, 32, 32)
            }
        }

        // Titre de la boîte
        val title = TextView(this).apply {
            text = "🏆 Classement"
            setTextColor(Color.WHITE)
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setPadding(0, 0, 0, 24)
        }

        dialogView.addView(title)

        val leaderboard = GameManager.getLeaderboard(applicationContext)
        val scrollContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // Affiche un message si aucun score n’est enregistré
        if (leaderboard.isEmpty()) {
            scrollContent.addView(TextView(this).apply {
                text = "Aucun score enregistré."
                setTextColor(Color.LTGRAY)
                textSize = 16f
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            })
        } else {
            // Affiche chaque entrée du classement avec une animation
            leaderboard.forEachIndexed { index, (name, score) ->
                val medal = when (index) {
                    0 -> "🥇"
                    1 -> "🥈"
                    2 -> "🥉"
                    else -> ""
                }

                val entry = TextView(this).apply {
                    text = "$medal ${index + 1}. $name — $score pts"
                    setTextColor(Color.LTGRAY)
                    textSize = 16f
                    textAlignment = View.TEXT_ALIGNMENT_CENTER
                    alpha = 0f
                }

                scrollContent.addView(entry)

                // Animation d’apparition décalée pour chaque score
                entry.postDelayed({
                    entry.animate().alpha(1f).setDuration(400).start()
                }, index * 150L)
            }
        }

        // Permet le scroll si le classement est long
        val scroll = ScrollView(this).apply {
            isVerticalScrollBarEnabled = false
            addView(scrollContent)
        }

        dialogView.addView(scroll)

        // Bouton pour fermer le classement
        lateinit var dialog: AlertDialog
        val closeButton = Button(this).apply {
            text = "FERMER"
            setBackgroundResource(R.drawable.rounded_button)
            setTextColor(Color.WHITE)
            textSize = 15f
            setOnClickListener { dialog.dismiss() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 24
            }
        }

        dialogView.addView(closeButton)

        // Création et affichage de la boîte de dialogue
        dialog = AlertDialog.Builder(this, R.style.BlurDialogTheme)
            .setView(dialogView)
            .create()

        dialog.setOnShowListener {
            val animIn = AnimationUtils.loadAnimation(this, R.anim.dialog_enter)
            dialogView.startAnimation(animIn)

            val mediaPlayer = MediaPlayer.create(this, R.raw.dialog_open)
            mediaPlayer.setOnCompletionListener { it.release() }
            mediaPlayer.start()
        }

        dialog.show()
    }

    // Affiche une boîte pour entrer le nom du joueur
    private fun showPlayerNameDialog() {
        val prefs = getSharedPreferences("leaderboard", MODE_PRIVATE)
        val previousPlayers = prefs.all.keys.toList() // Liste des anciens joueurs

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 30, 40, 0)
        }

        val input = EditText(this).apply {
            hint = "Nom du joueur"
        }

        layout.addView(input)

        // Si des joueurs précédents existent, on les affiche dans une liste déroulante
        if (previousPlayers.isNotEmpty()) {
            val spinner = Spinner(this)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, previousPlayers)
            spinner.adapter = adapter
            spinner.setSelection(0)
            spinner.setPadding(0, 20, 0, 0)

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    input.setText(previousPlayers[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }

            layout.addView(spinner)
        }

        // Boîte de dialogue pour commencer une partie
        AlertDialog.Builder(this)
            .setTitle("Entrez votre nom")
            .setView(layout)
            .setPositiveButton("Commencer") { _, _ ->
                val playerName = input.text.toString().trim()
                if (playerName.isNotEmpty()) {
                    GameManager.setPlayerName(playerName)
                    startActivity(Intent(this, MainActivity::class.java)) // Démarrer le jeu
                    finish()
                }
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    // Affiche la boîte de dialogue des crédits
    private fun showCreditsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_credits, null)

        val dialog = AlertDialog.Builder(this, R.style.BlurDialogTheme)
            .setView(dialogView)
            .create()

        dialog.setOnShowListener {
            val animIn = AnimationUtils.loadAnimation(this, R.anim.dialog_enter)
            dialogView.startAnimation(animIn)

            val mediaPlayer = MediaPlayer.create(this, R.raw.dialog_open)
            mediaPlayer.setOnCompletionListener { it.release() }
            mediaPlayer.start()
        }

        dialogView.findViewById<Button>(R.id.closeButton).setOnClickListener {
            val animOut = AnimationUtils.loadAnimation(this, R.anim.dialog_exit)
            dialogView.startAnimation(animOut)
            dialogView.postDelayed({ dialog.dismiss() }, animOut.duration)
        }

        dialog.show()
    }

    // Affiche la boîte de dialogue des règles du jeu
    private fun showRulesDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_rules, null)

        val dialog = AlertDialog.Builder(this, R.style.BlurDialogTheme)
            .setView(dialogView)
            .create()

        dialog.setOnShowListener {
            val animIn = AnimationUtils.loadAnimation(this, R.anim.dialog_enter)
            dialogView.startAnimation(animIn)

            val mediaPlayer = MediaPlayer.create(this, R.raw.dialog_open)
            mediaPlayer.setOnCompletionListener { it.release() }
            mediaPlayer.start()
        }

        dialogView.findViewById<Button>(R.id.closeRulesButton).setOnClickListener {
            val animOut = AnimationUtils.loadAnimation(this, R.anim.dialog_exit)
            dialogView.startAnimation(animOut)
            dialogView.postDelayed({ dialog.dismiss() }, animOut.duration)
        }

        dialog.show()
    }
}