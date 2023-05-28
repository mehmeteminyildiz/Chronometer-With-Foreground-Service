package com.example.chronometerwithforegroundservice

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import com.example.chronometerwithforegroundservice.databinding.ActivityMainBinding
import com.example.chronometerwithforegroundservice.model.TimerEvent
import com.example.chronometerwithforegroundservice.service.TimerService
import com.example.chronometerwithforegroundservice.util.Constants
import com.example.chronometerwithforegroundservice.util.TimerUtil
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleClickEvents()
        setObservers()

        checkNotificationPermissionGranted(context = this)
    }

    private fun checkNotificationPermissionGranted(context: MainActivity) {

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val isEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.areNotificationsEnabled()
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }

        if (isEnabled) {
            Timber.e("İzin verilmiş")
        } else {
            Timber.e("İzin verilmemiş")
            notificationPermissionRequest()
        }
    }

    private fun notificationPermissionRequest() {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)

    }

    /** Tıklama dinleyicileri **/
    private fun handleClickEvents() {
        binding.apply {
            fab.setOnClickListener {
                toggleTimer()
            }
        }
    }

    /** dinleme işlemlerinin aktif hale getirilmesi **/
    private fun setObservers() {
        observeTimerEvent()
        observeTimerInMillis()
    }

    /** timerEvent değeri dinlenir **/
    private fun observeTimerEvent() {
        TimerService.timerEvent.observe(this, Observer {
            when (it) {
                is TimerEvent.START -> {
                    timerStarted()
                }

                is TimerEvent.END -> {
                    timerStopped()
                }
            }
        })
    }

    /** Timer başladığında **/
    private fun timerStarted() {
        isTimerRunning = true
        binding.fab.setImageResource(R.drawable.baseline_timer_off_24)
    }

    /** Timer sonlandırıldığında **/
    private fun timerStopped() {
        isTimerRunning = false
        binding.fab.setImageResource(R.drawable.baseline_timer_24)
    }

    /** timer değeri observe edilir, değer formatlanır ve arayüz güncellemesi yapılır **/
    private fun observeTimerInMillis() {
        TimerService.timerInMillis.observe(this, Observer {
            binding.apply {
                tvTimer.text = TimerUtil.getFormattedTime(it, true)
            }
        })
    }


    /** isTimerRunning değerine göre servisi durdurmak veya çalıştırmak için kullanılır **/
    private fun toggleTimer() {
        if (isTimerRunning) {
            sendCommandToService(Constants.ACTION_STOP_SERVICE)
        } else {
            sendCommandToService(Constants.ACTION_START_SERVICE)
        }
    }

    /** startService metoduna Intent (action ile birlikte) göndererek servis durum güncellemesi yapılır **/
    private fun sendCommandToService(action: String) {
        startService(Intent(
            this, TimerService::class.java
        ).apply {
            this.action = action
        })
    }
}