package pl.piotrgorny.gymondo

import android.app.Application
import timber.log.Timber

class GymondoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }
}