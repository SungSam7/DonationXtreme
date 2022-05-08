package ie.wit.donationx.ui.weather

import android.app.Application
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class WeatherData : Application() {

    lateinit var mfusedlocation: FusedLocationProviderClient
    private var myResquestCode=1010

    override fun onCreate() {
        super.onCreate()

        mfusedlocation = LocationServices.getFusedLocationProviderClient(this)




    }


}