package com.example.chronometerwithforegroundservice.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.chronometerwithforegroundservice.MainActivity
import com.example.chronometerwithforegroundservice.R
import com.example.chronometerwithforegroundservice.model.TimerEvent
import com.example.chronometerwithforegroundservice.util.Constants
import com.example.chronometerwithforegroundservice.util.Constants.NOTIFICATION_CHANNEL_ID
import com.example.chronometerwithforegroundservice.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.chronometerwithforegroundservice.util.TimerUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : LifecycleService() {

    companion object {
        val timerEvent = MutableLiveData<TimerEvent>()
        val timerInMillis = MutableLiveData<Long>()
    }

    private var isServiceStopped = false

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    @Inject
    lateinit var notificationBuilder : NotificationCompat.Builder
    override fun onCreate() {
        super.onCreate()
        initValues()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                Constants.ACTION_START_SERVICE -> startForegroundService()

                Constants.ACTION_STOP_SERVICE -> stopForegroundService()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun startForegroundService() {
        timerEvent.postValue(TimerEvent.START)
        startTimer()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        startForeground(Constants.NOTIFICATION_ID, notificationBuilder.build())

        timerInMillis.observe(this, Observer {
            if (!isServiceStopped) {
                val builder = notificationBuilder.setContentText(
                    TimerUtil.getFormattedTime(it, false)
                )
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return@Observer
                }
                notificationManager.notify(Constants.NOTIFICATION_ID, builder.build())
            }
        })

    }


    private fun initValues() {
        timerEvent.postValue(TimerEvent.END)
        timerInMillis.postValue(0L)
    }

    private fun stopForegroundService() {
        isServiceStopped = true
        initValues()
        notificationManager.cancel(Constants.NOTIFICATION_ID)
        stopForeground(true)
        stopSelf()
    }

    private fun startTimer() {
        val timeStarted = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch {
            while (!isServiceStopped && timerEvent.value == TimerEvent.START) {
                val lapTime = System.currentTimeMillis() - timeStarted
                timerInMillis.postValue(lapTime)
                delay(10L)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }




}