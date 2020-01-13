package com.zubala.rafal.glucose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        AppCenter.start(
            application, "f24dc627-a7a3-4a51-8a6c-d67fc2b4452c",
            Distribute::class.java, Analytics::class.java, Crashes::class.java
        )

        Distribute.setEnabled(true)
        Distribute.setEnabledForDebuggableBuild(true)
    }
}
