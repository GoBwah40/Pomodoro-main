package fr.afpa.pomodoro.util

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import fr.afpa.pomodoro.AppConstants
import fr.afpa.pomodoro.MainActivity
import fr.afpa.pomodoro.R
import fr.afpa.pomodoro.TimerNotificationActionReceiver
import java.text.SimpleDateFormat
import java.util.*

class NotificationUtil {
    companion object{
        private const val CHANNEL_ID_TIMER = "menu_timer"
        private const val CHANNEL_NAME_TIMER = "Timer App Timer"
        private const val TIMER_ID = 0



        fun showTimerExpired(context : Context){
            val startIntent = Intent(context,TimerNotificationActionReceiver::class.java)
            startIntent.action = AppConstants.ACTION_START
            val startPendingIntent = PendingIntent.getBroadcast(context,0,startIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val notifBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER,true)
            notifBuilder.setContentTitle("Timer terminé !")
                    .setContentText("commencer à nouveau ?")
                    .setContentIntent(getPendingIntentWithStack(context,MainActivity::class.java))
                    .addAction(R.drawable.ic_play, "Start", startPendingIntent) // TODO ajouter un drawable pour le boutton play

            val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true) // son à activer si jamais

            notifManager.notify(TIMER_ID,notifBuilder.build())
        }

        fun showTimerRunning(context : Context, wakeUpTime : Long){


            val stopIntent = Intent(context,TimerNotificationActionReceiver::class.java)
            stopIntent.action = AppConstants.ACTION_STOP
            val stopPendingIntent = PendingIntent.getBroadcast(context,0,stopIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val pauseIntent = Intent(context,TimerNotificationActionReceiver::class.java)
            pauseIntent.action = AppConstants.ACTION_PAUSE
            val pausePendingIntent = PendingIntent.getBroadcast(context,0,pauseIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val prettyDateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

            val notifBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER,true)
            notifBuilder.setContentTitle("Le timer est en cours")
                    .setContentText("Le timer se termine à : ${prettyDateFormat.format(Date(wakeUpTime))}")
                    .setContentIntent(getPendingIntentWithStack(context,MainActivity::class.java))
                    .setOngoing(true)  // Pour que l'utilisateur ne puisse pas cancel la notif ça doit être cancel par le code
                    .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent) // TODO ajouter un drawable pour les bouttons pause et et stop
                    .addAction(R.drawable.ic_pause, "Pause",pausePendingIntent)// TODO
            val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, false) // Ici si jamais on veut rajouter le son plus tard mais je ne gère que le son par défaut

            notifManager.notify(TIMER_ID,notifBuilder.build())

        }

        fun showTimerPaused(context : Context){
            val resumeIntent = Intent(context,TimerNotificationActionReceiver::class.java)
            resumeIntent.action = AppConstants.ACTION_RESUME
            val resumePendingIntent = PendingIntent.getBroadcast(context,0,resumeIntent,PendingIntent.FLAG_UPDATE_CURRENT)

            val notifBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER,true)
            notifBuilder.setContentTitle("Le timer est en pause")
                    .setContentText("continuer ?")
                    .setContentIntent(getPendingIntentWithStack(context,MainActivity::class.java))
                    .addAction(R.drawable.ic_play, "continuer", resumePendingIntent) // TODO ajouter un drawable pour le boutton play

            val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, false) // activer son si besoin

            notifManager.notify(TIMER_ID,notifBuilder.build())

        }

        fun hideTimerNotification(context:Context){
            val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.cancel(TIMER_ID)
        }

        private fun getBasicNotificationBuilder(context: Context, channelId: String, playSound: Boolean): NotificationCompat.Builder {
            val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notifBuilder = NotificationCompat.Builder(context,channelId)
                    .setSmallIcon(R.drawable.pomodorobackground) //TODO changer l'image pour un vrai timer
                    .setAutoCancel(true)
                    .setDefaults(0)
            if(playSound) notifBuilder.setSound(notificationSound)
            return notifBuilder
        }

        private fun <T> getPendingIntentWithStack(context:Context, javaClass : Class<T>):PendingIntent{
            val resultIntent = Intent(context,javaClass)
            resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(javaClass)
            stackBuilder.addNextIntent(resultIntent)

            return stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT)
        }

        @TargetApi(26)
        private fun NotificationManager.createNotificationChannel(channelID: String, channelName:String, playSound: Boolean){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){ //.O c'est la version Oreo du SDK
                val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
                else NotificationManager.IMPORTANCE_LOW
                val notifChannel = NotificationChannel(channelID,channelName,channelImportance)
                notifChannel.enableLights(true)
                notifChannel.lightColor = Color.RED
                this.createNotificationChannel(notifChannel)
            }
        }
    }


}