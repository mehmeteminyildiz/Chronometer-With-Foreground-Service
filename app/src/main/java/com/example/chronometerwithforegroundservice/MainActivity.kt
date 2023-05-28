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

    private fun handleClickEvents() {
        binding.apply {
            fab.setOnClickListener {
                toggleTimer()
            }
        }
    }

    private fun setObservers() {
        TimerService.timerEvent.observe(this, Observer {
            when (it) {
                is TimerEvent.START -> {
                    isTimerRunning = true
                    binding.fab.setImageResource(R.drawable.baseline_timer_off_24)
                }

                is TimerEvent.END -> {
                    isTimerRunning = false
                    binding.fab.setImageResource(R.drawable.baseline_timer_24)
                }
            }
        })

        TimerService.timerInMillis.observe(this, Observer {
            binding.apply {
                tvTimer.text = TimerUtil.getFormattedTime(it, true)
            }
        })
    }

    private fun toggleTimer() {
        if (isTimerRunning) {
            sendCommandToService(Constants.ACTION_STOP_SERVICE)
        } else {
            sendCommandToService(Constants.ACTION_START_SERVICE)
        }
    }

    private fun sendCommandToService(action: String) {
        startService(
            Intent(
                this,
                TimerService::class.java
            ).apply {
                this.action = action
            }
        )
    }
}