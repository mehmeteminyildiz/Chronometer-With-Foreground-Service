package com.example.chronometerwithforegroundservice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.chronometerwithforegroundservice.databinding.ActivityMainBinding
import com.example.chronometerwithforegroundservice.model.TimerEvent
import com.example.chronometerwithforegroundservice.service.TimerService
import com.example.chronometerwithforegroundservice.util.Constants
import com.example.chronometerwithforegroundservice.util.TimerUtil

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleClickEvents()
        setObservers()
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