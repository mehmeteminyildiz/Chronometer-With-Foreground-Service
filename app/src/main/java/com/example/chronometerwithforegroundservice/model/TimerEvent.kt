package com.example.chronometerwithforegroundservice.model

sealed class TimerEvent {
    object START : TimerEvent()
    object END : TimerEvent()

}