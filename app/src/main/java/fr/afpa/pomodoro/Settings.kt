package fr.afpa.pomodoro

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class Settings : AppCompatActivity() {
    var contextSettings: Context = this
    var contextOfMain : Context = this
    var contextoDuMain : Context = MainActivity.contextDuMain





    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //Textview et seekbar travail
        val tempsTravail: TextView = findViewById(R.id.textViewSeekBarNumberTravail)
        val tempsTravailSeekbar: SeekBar = findViewById(R.id.seekBarTravail)

        tempsTravailSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tempsTravail.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    var startPoint = seekBar.progress
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    var endPoint = seekBar.progress
                }
            }

        })

        //Textview et seekbar pause

        val tempsPause: TextView = findViewById(R.id.textViewSeekBarNumberPause)
        val tempsPauseSeekbar: SeekBar = findViewById(R.id.seekBarPause)

        tempsPauseSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tempsPause.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    var startPoint = seekBar.progress
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    var endPoint = seekBar.progress
                }
            }

        })


        //Textview et seekbar répétition
        val nombreRepetition: TextView = findViewById(R.id.textViewSeekBarNumberRepetition)
        val nombreRepetitonSeekbar: SeekBar = findViewById(R.id.seekBarRepetition)

        nombreRepetitonSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                nombreRepetition.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    var startPoint = seekBar.progress
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    var endPoint = seekBar.progress
                }
            }

        })


        //desactiver seekbars
        tempsTravailSeekbar.isEnabled = false
        nombreRepetitonSeekbar.isEnabled = false
        tempsPauseSeekbar.isEnabled = false
        val switchSeancePerso: Switch = findViewById(R.id.switchPerso)
        switchSeancePerso.setOnClickListener { activerDesactiverSeancePersonnalisé() }


        //Séances prédéfinis
        val firstSeance: RadioButton = findViewById(R.id.firtSeance)
        val secondSeance: RadioButton = findViewById(R.id.secondSeance)
        val thirdSeance: RadioButton = findViewById(R.id.thirdSeance)

        firstSeance.setOnClickListener { premiereSeance() }
        secondSeance.setOnClickListener { secondeSeance() }
        thirdSeance.setOnClickListener { troisiemeSeance() }


        //Alerte son
        var alerteSonSettings = sonOuNon()


        //Darkmode
        var switchMode: Switch = findViewById(R.id.switchNightMode)

        //Retour MainActivity
        val returnMain: Button = findViewById(R.id.btnRetour)
        returnMain.setOnClickListener { retourMain() }

        variableImportante.contextSettings = contextSettings
    }

    private fun sonOuNon(): Boolean {
        var switchSonAlerte: Switch = findViewById(R.id.switchSonAlerte)
        return switchSonAlerte.isChecked

    }

    private fun activerDesactiverSeancePersonnalisé() {
        switchListeSeance()
        switchSeekbarSeancePerso()
    }

    private fun switchListeSeance() {
        val switchSeancePerso: Switch = findViewById(R.id.switchPerso)
        val seanceUn: RadioButton = findViewById(R.id.firtSeance)
        val seanceDeux: RadioButton = findViewById(R.id.secondSeance)
        val seanceTrois: RadioButton = findViewById(R.id.thirdSeance)

        if (switchSeancePerso.isChecked) {
            seanceUn.isEnabled = false
            seanceDeux.isEnabled = false
            seanceTrois.isEnabled = false
        } else {
            seanceUn.isEnabled = true
            seanceDeux.isEnabled = true
            seanceTrois.isEnabled = true
        }
    }

    private fun switchSeekbarSeancePerso() {
        val switchSeancePerso: Switch = findViewById(R.id.switchPerso)
        val tempsTravailSeekbar: SeekBar = findViewById(R.id.seekBarTravail)
        val tempsPauseSeekbar: SeekBar = findViewById(R.id.seekBarPause)
        val nombreRepetitonSeekbar: SeekBar = findViewById(R.id.seekBarRepetition)
        if (switchSeancePerso.isChecked) {
            tempsTravailSeekbar.setProgress(60)
            nombreRepetitonSeekbar.setProgress(2)
            tempsPauseSeekbar.setProgress(15)

            tempsPauseSeekbar.isEnabled = true
            tempsTravailSeekbar.isEnabled = true
            nombreRepetitonSeekbar.isEnabled = true
            Toast.makeText(this, "Veuillez remplir les champs de votre séance", Toast.LENGTH_SHORT).show()
        } else {
            tempsPauseSeekbar.isEnabled = false
            tempsTravailSeekbar.isEnabled = false
            nombreRepetitonSeekbar.isEnabled = false
        }
    }

    private fun premiereSeance() {
        val tempsPauseSeekbar: SeekBar = findViewById(R.id.seekBarPause)
        val tempsTravailSeekbar: SeekBar = findViewById(R.id.seekBarTravail)
        val nombreRepetitonSeekbar: SeekBar = findViewById(R.id.seekBarRepetition)
        tempsTravailSeekbar.progress = 55
        nombreRepetitonSeekbar.progress = 4
        tempsPauseSeekbar.progress = 5
    }

    private fun secondeSeance() {
        val tempsPauseSeekbar: SeekBar = findViewById(R.id.seekBarPause)
        val tempsTravailSeekbar: SeekBar = findViewById(R.id.seekBarTravail)
        val nombreRepetitonSeekbar: SeekBar = findViewById(R.id.seekBarRepetition)
        tempsTravailSeekbar.progress = 30
        nombreRepetitonSeekbar.progress = 4
        tempsPauseSeekbar.progress = 2
    }

    private fun troisiemeSeance() {
        val tempsPauseSeekbar: SeekBar = findViewById(R.id.seekBarPause)
        val tempsTravailSeekbar: SeekBar = findViewById(R.id.seekBarTravail)
        val nombreRepetitonSeekbar: SeekBar = findViewById(R.id.seekBarRepetition)
        tempsTravailSeekbar.progress = 80
        nombreRepetitonSeekbar.progress = 4
        tempsPauseSeekbar.progress = 10
    }


    private fun retourMain() {
        preferencePersist()
        val retourMainActivity = Intent(this, MainActivity::class.java)
        startActivity(retourMainActivity)

    }

    fun preferencePersist() {

        var seekRepet: SeekBar = findViewById(R.id.seekBarRepetition)
        variableImportante.repetRecup = seekRepet.progress

        var seekbarPause: SeekBar = findViewById(R.id.seekBarPause)
        variableImportante.PausePref = seekbarPause.progress

        var seekbarTrvail: SeekBar = findViewById(R.id.seekBarTravail)
        variableImportante.TravailPref = seekbarTrvail.progress

        PreferenceManager.getDefaultSharedPreferences(contextOfMain).edit().putInt(MainActivity().REPET_RECUP,variableImportante.repetRecup).apply() // nbr rounds
        PreferenceManager.getDefaultSharedPreferences(contextOfMain).edit().putInt(MainActivity().TIME_PAUSE_ROUND,variableImportante.PausePref).apply() // temps pause
        PreferenceManager.getDefaultSharedPreferences(contextOfMain).edit().putInt(MainActivity().TIME_WORK_ROUND,variableImportante.TravailPref).apply() // temps travail

    }

    class variableImportante {
        companion object {

            var repetRecup = 4
            var TravailPref: Int = 4
            var PausePref: Int = 3
            var contextSettings: Context? = null // TODO A REVOIR CAR MEMORY LEAK

        }
    }
}