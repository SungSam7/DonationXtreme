package ie.wit.donationx.main

import android.app.Application
import timber.log.Timber

class DonationXApp : Application() {


    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Timber.i("DonationX Application Started")



    }

}