package com.example.chronometerwithforegroundservice.ui

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.example.chronometerwithforegroundservice.databinding.ActivityForegroundAppTestBinding
import com.example.chronometerwithforegroundservice.service.MyService
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class ForegroundAppTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForegroundAppTestBinding

    private val REQUEST_PACKAGE_USAGE_STATS = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForegroundAppTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!checkPackageUsageStatsPermission()) {
            gotoPermission()
        } else {
            Timber.e("izin zaten verildi")
            observeForegroundApplications()
            startServiceProcess()
        }
    }

    private fun startServiceProcess() {

        val intent = Intent(this, MyService::class.java)
        startService(intent)
    }

    private fun observeForegroundApplications() {
        val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager

        val startTime =
            System.currentTimeMillis() - 1000 * 60 // Son bir dakikadaki etkinlikleri almak için

        val endTime = System.currentTimeMillis()

        val event = UsageEvents.Event()
        val usageEvents: UsageEvents = usageStatsManager.queryEvents(startTime, endTime)

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                val packageName = event.packageName
                Timber.e("Açılan uygulama : $packageName")
                // Açılan uygulamanın paket adını burada kullanabilirsiniz
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.e("onDestroy foregroundAppTestActivity")
        val intent = Intent(this, MyService::class.java)
        stopService(intent)
    }

    private fun checkPackageUsageStatsPermission(): Boolean {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun gotoPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PACKAGE_USAGE_STATS) {
            Timber.e("izin verildi")
            // Kullanıcı izinleri verdiğinde yapılacak işlemleri burada gerçekleştirebilirsiniz
        }
        super.onActivityResult(requestCode, resultCode, data)

    }
}