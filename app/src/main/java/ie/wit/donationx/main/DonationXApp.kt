package ie.wit.donationx.main

import android.app.Application
import timber.log.Timber

class DonationXApp : Application() {

    //lateinit var donationsStore: DonationStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
      //  donationsStore = DonationManager()
        Timber.i("DonationX Application Started")
    }
}