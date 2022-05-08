package ie.wit.donationx.ui.weather

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import ie.wit.donationx.R
import ie.wit.donationx.databinding.ActivityWeatherBinding

import org.json.JSONObject
import kotlin.math.ceil


class WeatherActivity : AppCompatActivity() {

    val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "channelName"
    val NOTIFICATION_ID = 0

    lateinit var mfusedlocation: FusedLocationProviderClient
    private var myResquestCode=1010


    private lateinit var binding : ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mfusedlocation = LocationServices.getFusedLocationProviderClient(this)


        createNotificationChannel()
      //  getLastLocation()



        val lat=intent.getStringExtra("lat")
        var long=intent.getStringExtra("long")




        //TODO CHANGE COLOR
        window.statusBarColor= Color.parseColor("#C8BBDF")
        getJsonData(lat,long)




    }



    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lightColor = Color.RED
                enableLights(true)
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }





    private fun notificationsHigh()
    {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WARNING!")
            .setContentText("It is getting HOT!")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun notificationsLow()
    {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("WARNING!")
            .setContentText("It is getting COLD!")
            .setSmallIcon(R.drawable.logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)

        notificationManager.notify(NOTIFICATION_ID, notification)
    }


    private fun getJsonData(lat:String?,long:String?)
    {

        val API_KEY="d782c3641dcc9cbcb8528dfb0245b3e4"
        val queue = Volley.newRequestQueue(this)

        val url ="https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${long}&appid=${API_KEY}"
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            Response.Listener { response ->
                setValues(response)
            },
            Response.ErrorListener { Toast.makeText(this,"ERROR HERE",Toast.LENGTH_LONG).show() })


        queue.add(jsonRequest)
    }

    private fun setValues(response:JSONObject){
        binding.city.text=response.getString("name")
        var lat = response.getJSONObject("coord").getString("lat")
        var long=response.getJSONObject("coord").getString("lon")
        binding.coordinates.text="${lat} , ${long}"
        binding.weather.text=response.getJSONArray("weather").getJSONObject(0).getString("main")
        var tempr=response.getJSONObject("main").getString("temp")
        tempr=((((tempr).toFloat()-273.15)).toInt()).toString()
        binding.temp.text="${tempr}°C"
        var mintemp=response.getJSONObject("main").getString("temp_min")
        mintemp=((((mintemp).toFloat()-273.15)).toInt()).toString()
        binding.minTemp1.text=mintemp+"°C"
        var maxtemp=response.getJSONObject("main").getString("temp_max")
        maxtemp=((ceil((maxtemp).toFloat()-273.15)).toInt()).toString()
        binding.maxTemp1.text=maxtemp+"°C"

        binding.pressure.text=response.getJSONObject("main").getString("pressure")
        binding.humidity.text=response.getJSONObject("main").getString("humidity")+"%"
        binding.wind.text=response.getJSONObject("wind").getString("speed")
        binding.degree.text="Degree : "+response.getJSONObject("wind").getString("deg")+"°"



        val imageView = findViewById<ImageView>(R.id.iconIv)
        if(tempr.toFloat() >= 13){
            notificationsHigh()


        }


        else if(tempr.toFloat() <= 12){
            notificationsLow()

        }
        else if(tempr.toFloat() <= 9){
            notificationsLow()

        }
        else if(tempr.toFloat() <= 8){
            notificationsLow()

        }
        else if(tempr.toFloat() <= 7){
            notificationsLow()

        }
        else if(tempr.toFloat() <= 6){
            notificationsLow()

        }
        else if(tempr.toFloat() <= 5){
            notificationsLow()

        }

        if(binding.weather.text == "Rain")
        {
            imageView.setImageResource(R.drawable.rain)
        }
        else if(binding.weather.text == "Clouds"){
            imageView.setImageResource(R.drawable.clouds)
        }
        else if(binding.weather.text == "Clear"){
            imageView.setImageResource(R.drawable.sunny)
        }
    }

//    @SuppressLint("MissingPermission")
//    private fun getLastLocation() {
//        if(CheckPermission()) {
//            if(LocationEnable()){
//                mfusedlocation.lastLocation.addOnCompleteListener{
//                        task->
//                    var location: Location?=task.result
//                    if(location==null)
//                    {
//                        NewLocation()
//                    }else{
//                        Handler(Looper.getMainLooper()).postDelayed({
//                            val intent= Intent(this, WeatherActivity::class.java)
//                            intent.putExtra("lat",location.latitude.toString())
//                            intent.putExtra("long",location.longitude.toString())
//                            startActivity(intent)
//
//                        },0)
//                    }
//                }
//            }else{
//                Toast.makeText(this,"Please Turn on your GPS location", Toast.LENGTH_LONG).show()
//            }
//        }else{
//            RequestPermission()
//        }
//    }
//
//    @SuppressLint("MissingPermission")
//    private fun NewLocation() {
//        var locationRequest= LocationRequest()
//        locationRequest.priority= LocationRequest.PRIORITY_HIGH_ACCURACY
//        locationRequest.interval=0
//        locationRequest.fastestInterval=0
//        locationRequest.numUpdates=1
//        mfusedlocation= LocationServices.getFusedLocationProviderClient(this)
//
//        //TODO DONT KNOW WHYLOOPER WANTS !!
//        mfusedlocation.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper()!!)
//    }
//    private val locationCallback=object: LocationCallback(){
//        override fun onLocationResult(p0: LocationResult) {
//            var lastLocation: Location =p0.lastLocation
//        }
//    }
//
//    private fun LocationEnable(): Boolean {
//        var locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER)
//    }
//
//    private fun RequestPermission() {
//        ActivityCompat.requestPermissions(this, arrayOf(
//            Manifest.permission.ACCESS_COARSE_LOCATION,
//            Manifest.permission.ACCESS_FINE_LOCATION),myResquestCode)
//    }
//
//    private fun CheckPermission(): Boolean {
//        if(
//            ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED ||
//            ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
//        ){
//            return true
//        }
//        return false
//    }
//
//
//    @SuppressLint("MissingSuperCall")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        if(requestCode==myResquestCode)
//        {
//            if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED)
//            {
//                getLastLocation()
//            }
//        }
//    }

}