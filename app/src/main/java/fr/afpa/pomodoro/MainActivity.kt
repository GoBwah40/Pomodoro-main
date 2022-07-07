package fr.afpa.pomodoro

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import fr.afpa.pomodoro.databinding.ActivityMainBinding
import fr.afpa.pomodoro.util.NotificationUtil
import fr.afpa.pomodoro.util.PrefUtil
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        fun setAlarm(context:Context, nowSeconds:Long, secondsRemaining: Long):Long{
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000 // c'est le temps dans le futur pour savoir le moement ou le timer se termine
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context,0,intent,0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,wakeUpTime,pendingIntent)
            PrefUtil.setAlarmSetTime(context, nowSeconds)
            return wakeUpTime
        }

        fun removeAlarm(context:Context){
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context,0,intent,0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(context,0)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000

        var timerType : Boolean = true
        lateinit var contextDuMain : Context
    }

    enum class TimerState{
        Stopped,Paused,Running
    }


    var compteurTravail = 0
    var compteurPause = 0
    var compteurTot = 0
    val REPET_RECUP = "fr.afpa.pomodoro.timer.repet_recup"
    val TIME_WORK_ROUND = "fr.afpa.pomodoro.timer.time_work_round"
    val TIME_PAUSE_ROUND = "fr.afpa.pomodoro.timer.time_pause_round"
    private var NBR_REPET_TOTAL : Int = 1
    private var TIME_WORK : Int = 2
    private var TIME_PAUSE : Int = 1
    private lateinit var timer : CountDownTimer
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped
    private var secondsRemaining = 0L
    private lateinit var binding: ActivityMainBinding
    private var reinitialTriche = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val btnStartStop : Button = binding.buttonStart

        setAppContext()  // défini le context du main


        val pauseRounds : TextView = findViewById(R.id.breakRounds)
        val travailRounds : TextView = findViewById(R.id.workRounds)


        //TODO test

        TIME_WORK =  PreferenceManager.getDefaultSharedPreferences(contextDuMain).getInt(TIME_WORK_ROUND,50)
        TIME_PAUSE = PreferenceManager.getDefaultSharedPreferences(contextDuMain).getInt(TIME_PAUSE_ROUND,10)
        NBR_REPET_TOTAL = PreferenceManager.getDefaultSharedPreferences(contextDuMain).getInt(REPET_RECUP,1)


        val stringWorkRounds = "Work rounds :"
        val stringBreakRounds = "Break rounds :"


        //TODO Créer une fonction dans une nouvelle class pour update l'UI des valeurs des rounds

        pauseRounds.text = "$stringBreakRounds $compteurPause/$NBR_REPET_TOTAL"
        travailRounds.text = "$stringWorkRounds $compteurTravail/$NBR_REPET_TOTAL"

        //on set le timer sur le mode working rounds
        PrefUtil.setTimerLength(this, TIME_WORK)

        val btnrestart = binding.restartbutton

        btnrestart.setOnClickListener{ v ->
            if(timerState == TimerState.Running || timerState == TimerState.Paused){
                timerType = false
                reinitialTriche = true
                timer.cancel()
                timerState = TimerState.Stopped
                onTimerFinished()
            }
        }

        btnStartStop.setOnClickListener { v ->
            if(timerState == TimerState.Stopped || timerState == TimerState.Paused){ // boutton play
                startTimer()
                timerState = TimerState.Running
                updateButton()
            } // TODO IMPLEMENTER UN BOUTTON STOP !!! avec timer.cancel() et onTimerFinished qui va call updateButton() de lui mếme.
            else if(timerState == TimerState.Running){ // Boutton pause
                timer.cancel()
                timerState = TimerState.Paused
                updateButton()
            }
        }
    }


    //test

    fun setAppContext(){
        contextDuMain = this
    }

    fun getAppContext():Context{
        return contextDuMain
    }

//Code pour aller dans les préferences -------------------------------------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.item_preference)
            goToSettingsActivity()
        return super.onOptionsItemSelected(item)
    }
    private fun goToSettingsActivity () {
        startActivity(Intent(this,Settings::class.java))
    }
//-----------------------------------------------------------------------------------------

    override fun onResume() {
        super.onResume()

        initTimer()

        removeAlarm(this)
        NotificationUtil.hideTimerNotification(this)
    }

    override fun onPause() {
        super.onPause()

        if(timerState == MainActivity.TimerState.Running){
            timer.cancel()
            val wakeUpTime = setAlarm(this, nowSeconds,secondsRemaining)
            NotificationUtil.showTimerRunning(this,wakeUpTime)
        }
        else if(timerState == MainActivity.TimerState.Paused){
            NotificationUtil.showTimerPaused(this)
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds,this)
        PrefUtil.setSecondsRemaining(secondsRemaining,this)
        PrefUtil.setTimerState(timerState,this)
    }

    private fun initTimer(){
        timerState = PrefUtil.getTimerState(this)
        PrefUtil.setTimerState(timerState,this)

        if(timerState == MainActivity.TimerState.Stopped) {
            setNewTimerLength()
        }
        else{
            setPreviousTimerLength()
        }


        secondsRemaining = if(timerState == MainActivity.TimerState.Running || timerState == MainActivity.TimerState.Paused)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
        if(alarmSetTime > 0){  // ça veut dire que le timer est en cours parce qu'il est réinitialisé à 0

            println("ligne 145 du main timer en cours alarmsettime >0")
            secondsRemaining -= nowSeconds - alarmSetTime
        }
        if (secondsRemaining <=0){
            onTimerFinished()
        }
        else if(timerState == MainActivity.TimerState.Running){
            startTimer()
        }

        updateButton()
        updateCountDownUI()
    }

    private fun changeTimerType():Boolean{
        if (timerType){
            timerType = false
            return timerType
        }
        else{
            timerType = true
            return timerType
        }
        //TODO incrémenter une variable qui augmente jusqu'à la valeur de nbr de répèt
        //TODO Si ce nombre >= nbr max mettre un toast
    }

    private fun incrementCompteur(){
        val stringWorkRounds = "Work rounds :"
        val stringBreakRounds = "Break rounds :"
        if(compteurTot < NBR_REPET_TOTAL*2) {
            if (compteurTot % 2 == 1)
                compteurTravail += 1
            else
                compteurPause += 1
            binding.workRounds.text = "$stringWorkRounds $compteurTravail/$NBR_REPET_TOTAL"
            binding.breakRounds.text = "$stringBreakRounds $compteurPause/$NBR_REPET_TOTAL"
        }
        else{
            compteurTot = 0
            compteurPause = 0
            compteurTravail = 0
            binding.workRounds.text = "$stringWorkRounds $compteurTravail/$NBR_REPET_TOTAL"
            binding.breakRounds.text = "$stringBreakRounds $compteurPause/$NBR_REPET_TOTAL"
            Toast.makeText(this, "Séance terminée, Felicitation !", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onTimerFinished(){
        if(!reinitialTriche){
            compteurTot += 1
            incrementCompteur()
        }
        else{
            reinitialTriche = false
            val stringWorkRounds = "Work rounds :"
            val stringBreakRounds = "Break rounds :"
            compteurTot = 0
            compteurPause = 0
            compteurTravail = 0
            binding.workRounds.text = "$stringWorkRounds $compteurTravail/$NBR_REPET_TOTAL"
            binding.breakRounds.text = "$stringBreakRounds $compteurPause/$NBR_REPET_TOTAL"
        }



        timerState = MainActivity.TimerState.Stopped

        //changement du type de timer i.e. pair ou impair pour pause et work et on set le new timer accordingly
        changeTimerType()
        if(timerType){
            PrefUtil.setTimerLength(this,TIME_WORK)
            setNewTimerLength()
        }
        else{
            PrefUtil.setTimerLength(this, TIME_PAUSE)
            setNewTimerLength()
        }

        // TODO Alerter l'utilisateur d'un manière ou d'une autre
        // TODO progress bar à 0 ici

        PrefUtil.setSecondsRemaining(timerLengthSeconds,this)
        secondsRemaining = timerLengthSeconds
        updateButton()
        updateCountDownUI()
    }

    private fun startTimer(){
        timerState = MainActivity.TimerState.Running

        timer = object : CountDownTimer(secondsRemaining*1000,1000){
            override fun onFinish() {
                onTimerFinished()
                binding.buttonStart.text = "fini"
            }

            override fun onTick(millisUntilFinished : Long){
                secondsRemaining = millisUntilFinished/1000
                updateCountDownUI()
            }

        }.start()
    }

    private fun setNewTimerLength(){
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSeconds = (lengthInMinutes*60L)
        //TODO faire avancer la progress bar
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
        //TODO Progress bar
    }

    private fun updateCountDownUI(){
        val minutesUntilFinished = secondsRemaining/60
        val secondsInMinutesUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = prettyTimerForm(secondsInMinutesUntilFinished.toString())
        val minutesStr = prettyTimerForm(minutesUntilFinished.toString())


        binding.clockSeconds.text = secondsStr
        binding.clockMinutes.text = minutesStr
        //TODO Progress bar
    }

    private fun updateButton(){
        when(timerState){
            MainActivity.TimerState.Running ->  binding.buttonStart.text = "Pause"
            MainActivity.TimerState.Stopped ->  binding.buttonStart.text = "Lancer"
            MainActivity.TimerState.Paused ->  binding.buttonStart.text = "Play"
        }

    }

    fun prettyTimerForm(number : String):String{
        if(number.toInt() < 10){
            return "0$number"
        }
        return number
    }


}