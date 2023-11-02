package android.example.myapplication002

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

class WaterCalcApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }
}