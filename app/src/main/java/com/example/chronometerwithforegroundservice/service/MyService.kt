package com.example.chronometerwithforegroundservice.service

import android.animation.ObjectAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.chronometerwithforegroundservice.R
import com.example.chronometerwithforegroundservice.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@AndroidEntryPoint
class MyService : Service() {
    private var timer: Timer? = null
    private var viewShowing = false
    private var isLogin = false

    val handler = Handler(Looper.getMainLooper())


    // overlay
    private var overlayView: View? = null
    private var windowManager: WindowManager? = null
    private var params: WindowManager.LayoutParams? = null

    private var myContext: Context? = null

    @Inject
    lateinit var notificationManager: NotificationManagerCompat

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        myContext = this


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTimer()
        return super.onStartCommand(intent, flags, startId)

//        return START_STICKY
    }

    private fun startTimer() {
        timer = Timer()
        val timerTask = timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
                val time = System.currentTimeMillis()
                val usageEvents = usageStatsManager.queryEvents(time - 1000 * 5, time)
                val event = UsageEvents.Event()

                while (usageEvents.hasNextEvent()) {
                    usageEvents.getNextEvent(event)

                    if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED && !event.packageName.startsWith(
                            "com.sec"
                        )
                    ) {
                        Timber.e("ACTIVITY_RESUMED packageName : ${event.packageName}")

                        if (event.packageName.equals("com.spotify.music")) {
                            handler.post {
                                overlayView = LayoutInflater.from(myContext)
                                    .inflate(R.layout.overlay_layout, null)

                                // WindowManager oluştur
                                windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
                                params = WindowManager.LayoutParams(
                                    WindowManager.LayoutParams.MATCH_PARENT,
                                    WindowManager.LayoutParams.MATCH_PARENT,
                                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                                    PixelFormat.TRANSLUCENT
                                )
                                params?.gravity = Gravity.CENTER

                                // OverlayView'i ekrana ekle
                                if (!viewShowing && !isLogin) {
                                    viewShowing = true
                                    Timber.e("eklendi")
                                    windowManager?.addView(overlayView, params)

                                    overlayView?.let { overlayView ->


                                        overlayView.setOnKeyListener { v, keyCode, event ->
                                            Timber.e("keyCode : $keyCode")
                                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                                // Geri tuşuna basıldığında yapılacak işlemler
                                                Timber.e("back basıldı")
                                                // ...
                                                true
                                            } else {
                                                Timber.e("else basıldı")
                                                false
                                            }
                                        }
                                        val etPassword =
                                            overlayView.findViewById<EditText>(R.id.etPassword)
                                        val cardConfirm =
                                            overlayView.findViewById<CardView>(R.id.cardConfirm)

                                        val tv0 = overlayView.findViewById<TextView>(R.id.tv0)
                                        val tv1 = overlayView.findViewById<TextView>(R.id.tv1)
                                        val tv2 = overlayView.findViewById<TextView>(R.id.tv2)
                                        val tv3 = overlayView.findViewById<TextView>(R.id.tv3)
                                        val tv4 = overlayView.findViewById<TextView>(R.id.tv4)
                                        val tv5 = overlayView.findViewById<TextView>(R.id.tv5)
                                        val tv6 = overlayView.findViewById<TextView>(R.id.tv6)
                                        val tv7 = overlayView.findViewById<TextView>(R.id.tv7)
                                        val tv8 = overlayView.findViewById<TextView>(R.id.tv8)
                                        val tv9 = overlayView.findViewById<TextView>(R.id.tv9)
                                        val tvDelete =
                                            overlayView.findViewById<TextView>(R.id.tvDelete)

                                        tv0.setOnClickListener {
                                            val currentText = etPassword.text.toString().trim()
                                            etPassword.setText(currentText + "0")
                                        }
                                        tv1.setOnClickListener {
                                            val currentText = etPassword.text.toString().trim()
                                            etPassword.setText(currentText + "1")
                                        }
                                        tv2.setOnClickListener {
                                            val currentText = etPassword.text.toString().trim()
                                            etPassword.setText(currentText + "2")
                                        }
                                        tv3.setOnClickListener {
                                            val currentText = etPassword.text.toString().trim()
                                            etPassword.setText(currentText + "3")
                                        }
                                        tv4.setOnClickListener {
                                            val currentText = etPassword.text.toString().trim()
                                            etPassword.setText(currentText + "4")
                                        }
                                        tv5.setOnClickListener {
                                            val currentText = etPassword.text.toString().trim()
                                            etPassword.setText(currentText + "5")
                                        }
                                        tv6.setOnClickListener {
                                            val currentText = etPassword.text.toString().trim()
                                            etPassword.setText(currentText + "6")
                                        }
                                        tv7.setOnClickListener {
                                            val currentText = etPassword.text.toString().trim()
                                            etPassword.setText(currentText + "7")
                                        }
                                        tv8.setOnClickListener {
                                            val currentText = etPassword.text.toString().trim()
                                            etPassword.setText(currentText + "8")
                                        }
                                        tv9.setOnClickListener {
                                            val currentText = etPassword.text.toString().trim()
                                            etPassword.setText(currentText + "9")
                                        }
                                        tvDelete.setOnClickListener {
                                            val currentText = etPassword.text.toString().trim()
                                            if (currentText.isNotEmpty())
                                                etPassword.setText(
                                                    currentText.substring(
                                                        0,
                                                        currentText.length - 1
                                                    )
                                                )
                                        }



                                        cardConfirm.setOnClickListener {
                                            val password = etPassword.text.toString().trim()
                                            if (password == "123") {
                                                viewShowing = false
                                                isLogin = true
                                                windowManager?.removeView(overlayView)
                                            } else {
                                                val shakeAnimation = ObjectAnimator.ofFloat(
                                                    etPassword,
                                                    "translationX",
                                                    -10f,
                                                    10f
                                                )
                                                shakeAnimation.duration = 100
                                                shakeAnimation.repeatCount = 3
                                                shakeAnimation.start()

                                                // vibration
                                                val vibrator =
                                                    getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    vibrator.vibrate(
                                                        VibrationEffect.createOneShot(
                                                            1000,
                                                            VibrationEffect.DEFAULT_AMPLITUDE
                                                        )
                                                    )
                                                } else {
                                                    vibrator.vibrate(1000)
                                                }

                                                Toast.makeText(
                                                    myContext, "Hatalı Giriş", Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                    }

                                }
                            }
                        }
                    }
                }


//                val startTime = System.currentTimeMillis() - 1000 * 60 // Son bir dakikadaki etkinlikleri almak için
//                val endTime = System.currentTimeMillis()
//
//                val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
//                val usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
//
//                if (usageStatsList.isNotEmpty()) {
//                    for (lastUsedApp in usageStatsList){
//                        val packageName = lastUsedApp.packageName
//                        Timber.e("package name : $packageName")
//                    }
//                    Timber.e("---------------------------------------------------------")
//                    // Açılan uygulamanın paket adını burada kullanabilirsiniz
//                }
            }
        }, 0, 1000 * 1) // Her 10 saniyede bir çalışacak şekilde ayarlanmıştır

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        startForeground(Constants.NOTIFICATION_ID, notificationBuilder.build())
    }


    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    /** Notification channel oluşturulur **/
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}