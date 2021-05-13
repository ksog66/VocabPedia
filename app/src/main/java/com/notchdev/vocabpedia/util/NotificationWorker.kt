package com.notchdev.vocabpedia.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.notchdev.vocabpedia.MainActivity
import com.notchdev.vocabpedia.R

class NotificationWorker(
    val context: Context,
    params: WorkerParameters
) : Worker(context, params) {



    private fun sendNotification() {

        val notificationIntent = Intent(applicationContext,MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(context,121,notificationIntent,0)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "VocabPedia",
                "revisionNc",
                NotificationManager.IMPORTANCE_DEFAULT)

            channel.apply {
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
        val vocabNotification = NotificationCompat
            .Builder(context,"VocabPedia")
            .setContentTitle("Revise Your Vocab")
            .setContentText("Revise,Add new word & take a quiz")
            .setSmallIcon(R.drawable.book_nature)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1234,vocabNotification)
    }

    override fun doWork(): Result {
        sendNotification()
        return Result.success()
    }
}