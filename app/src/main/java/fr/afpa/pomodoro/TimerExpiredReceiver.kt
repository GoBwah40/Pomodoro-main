package fr.afpa.pomodoro

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import fr.afpa.pomodoro.util.NotificationUtil
import fr.afpa.pomodoro.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)

        //MainActivity.timerType = !MainActivity.timerType
        PrefUtil.setTimerState(MainActivity.TimerState.Stopped,context)
        PrefUtil.setAlarmSetTime(context,0)
    }
}