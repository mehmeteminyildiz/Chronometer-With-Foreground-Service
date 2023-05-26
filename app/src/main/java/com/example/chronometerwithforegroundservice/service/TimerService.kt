package com.example.chronometerwithforegroundservice.service

import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.chronometerwithforegroundservice.model.TimerEvent
import com.example.chronometerwithforegroundservice.util.Constants

class TimerService : LifecycleService() {

    companion object {
        val timerEvent = MutableLiveData<TimerEvent>()
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                Constants.ACTION_START_SERVICE -> {
                    timerEvent.postValue(TimerEvent.START)
                }

                Constants.ACTION_STOP_SERVICE -> {
                    timerEvent.postValue(TimerEvent.END)
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}