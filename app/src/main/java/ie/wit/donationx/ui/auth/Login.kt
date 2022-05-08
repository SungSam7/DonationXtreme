package ie.wit.donationx.ui.auth

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import ie.wit.donationx.R
import ie.wit.donationx.databinding.LoginBinding
import ie.wit.donationx.ui.home.Home
import ie.wit.donationx.ui.passwordreset.ForgotPasswordActivity
import ie.wit.donationx.ui.weather.WeatherActivity
import timber.log.Timber

class Login : AppCompatActivity() {

    private lateinit var loginRegisterViewModel : LoginRegisterViewModel
    private lateinit var loginBinding : LoginBinding
    private lateinit var startForResult : ActivityResultLauncher<Intent>

    lateinit var mfusedlocation: FusedLocationProviderClient
    private var myResquestCode=1010

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = LoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        mfusedlocation = LocationServices.getFusedLocationProviderClient(this)


        loginBinding.emailSignInButton.setOnClickListener {
            signIn(loginBinding.fieldEmail.text.toString(),
                    loginBinding.fieldPassword.text.toString())
        }
        loginBinding.emailCreateAccountButton.setOnClickListener {
            createAccount(loginBinding.fieldEmail.text.toString(),
                    loginBinding.fieldPassword.text.toString())
        }

        loginBinding.googleSignInButton.setSize(SignInButton.SIZE_WIDE)
        loginBinding.googleSignInButton.setColorScheme(0)

        loginBinding.googleSignInButton.setOnClickListener {
            googleSignIn()
        }

        loginBinding.weatherButton.setOnClickListener {
            getLastLocation()
        }

        loginBinding.forgotTv.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))

        }
    }


        @SuppressLint("MissingPermission")
         fun getLastLocation() {
            if(CheckPermission()) {
                if(LocationEnable()){
                    mfusedlocation.lastLocation.addOnCompleteListener{
                            task->
                        var location: Location?=task.result
                        if(location==null)
                        {
                            NewLocation()
                        }else{
                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent= Intent(this, WeatherActivity::class.java)
                                intent.putExtra("lat",location.latitude.toString())
                                intent.putExtra("long",location.longitude.toString())
                                startActivity(intent)
//                            finish()
                            },0)
                        }
                    }
                }else{
                    Toast.makeText(this,"Please Turn on your GPS location", Toast.LENGTH_LONG).show()
                }
            }else{
                RequestPermission()
            }
        }

        @SuppressLint("MissingPermission")
        private fun NewLocation() {
            var locationRequest= LocationRequest()
            locationRequest.priority= LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval=0
            locationRequest.fastestInterval=0
            locationRequest.numUpdates=1
            mfusedlocation= LocationServices.getFusedLocationProviderClient(this)

            //TODO DONT KNOW WHYLOOPER WANTS !!
            mfusedlocation.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper()!!)
        }
        private val locationCallback=object: LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                var lastLocation: Location =p0.lastLocation
            }
        }

        private fun LocationEnable(): Boolean {
            var locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER)
        }

        private fun RequestPermission() {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),myResquestCode)
        }

        private fun CheckPermission(): Boolean {
            if(
                ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
            ){
                return true
            }
            return false
        }


        @SuppressLint("MissingSuperCall")
        override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            if(requestCode==myResquestCode)
            {
                if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    getLastLocation()
                }
            }
        }



    private fun googleSignIn() {
        val signInIntent = loginRegisterViewModel.firebaseAuthManager
            .googleSignInClient.value!!.signInIntent

        startForResult.launch(signInIntent)
    }


    public override fun onStart() {
        super.onStart()

        loginRegisterViewModel = ViewModelProvider(this).get(LoginRegisterViewModel::class.java)

        loginRegisterViewModel.liveFirebaseUser.observe(this, Observer
        { firebaseUser -> if (firebaseUser != null)
            startActivity(Intent(this, Home::class.java)) })

        loginRegisterViewModel.firebaseAuthManager.errorStatus.observe(this, Observer
            { status -> checkStatus(status) })

        setupGoogleSignInCallback()
    }

    private fun setupGoogleSignInCallback() {
        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when(result.resultCode){
                    RESULT_OK -> {
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            val account = task.getResult(ApiException::class.java)
                            loginRegisterViewModel.authWithGoogle(account!!)
                        } catch (e: ApiException) {
                            // Google Sign In failed
                            Timber.i( "Google sign in failed $e")
                            Snackbar.make(loginBinding.loginLayout, "Authentication Failed.",
                                                                    Snackbar.LENGTH_SHORT).show()
                        }
                        Timber.i("DonationX Google Result $result.data")
                    }
                    RESULT_CANCELED -> {

                    } else -> { }
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Toast.makeText(this,"Click again to Close App...",Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun createAccount(email: String, password: String) {
        Timber.d("createAccount:$email")
        if (!validateForm()) { return }

        loginRegisterViewModel.register(email,password)
    }

    private fun signIn(email: String, password: String) {
        Timber.d("signIn:$email")
        if (!validateForm()) { return }

        loginRegisterViewModel.login(email,password)
    }

    private fun checkStatus(error:Boolean) {
            if (error)
                Toast.makeText(this,
                        getString(R.string.auth_failed),
                        Toast.LENGTH_LONG).show()
    }

    private fun validateForm(): Boolean {
        var valid = true

        val email = loginBinding.fieldEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            loginBinding.fieldEmail.error = "Required."
            valid = false
        } else {
            loginBinding.fieldEmail.error = null
        }

        val password = loginBinding.fieldPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            loginBinding.fieldPassword.error = "Required."
            valid = false
        } else {
            loginBinding.fieldPassword.error = null
        }
        return valid
    }
}
