package ie.wit.donationx.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseUser
import ie.wit.donationx.R
import ie.wit.donationx.databinding.HomeBinding
import ie.wit.donationx.databinding.NavHeaderBinding
import ie.wit.donationx.firebase.FirebaseImageManager
import ie.wit.donationx.ui.auth.LoggedInViewModel
import ie.wit.donationx.ui.auth.Login
import ie.wit.donationx.ui.map.MapsViewModel
import ie.wit.donationx.utils.checkLocationPermissions
import ie.wit.donationx.utils.isPermissionGranted
import ie.wit.donationx.utils.readImageUri
import ie.wit.donationx.utils.showImagePicker
import timber.log.Timber
import ie.wit.donationx.ui.weather.WeatherActivity as WeatherActivity1

class Home : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var homeBinding : HomeBinding
    private lateinit var navHeaderBinding : NavHeaderBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var loggedInViewModel : LoggedInViewModel
    private lateinit var headerView : View
    private lateinit var intentLauncher : ActivityResultLauncher<Intent>
    private val mapsViewModel : MapsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homeBinding = HomeBinding.inflate(layoutInflater)
        setContentView(homeBinding.root)
        drawerLayout = homeBinding.drawerLayout
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager.
        findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController


        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.aboutFragment, R.id.donateFragment, R.id.reportFragment, R.id.mapsFragment), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView = homeBinding.navView
        navView.setupWithNavController(navController)

        initNavHeader()

        if(checkLocationPermissions(this)) {
            mapsViewModel.updateCurrentLocation()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (isPermissionGranted(requestCode, grantResults))
            mapsViewModel.updateCurrentLocation()
        else {
            mapsViewModel.currentLocation.value = Location("Default").apply {
                latitude = 52.245696
                longitude = -7.139102
            }
        }
        Timber.i("LOC : %s", mapsViewModel.currentLocation.value)
    }

    public override fun onStart() {
        super.onStart()
        loggedInViewModel = ViewModelProvider(this).get(LoggedInViewModel::class.java)
        loggedInViewModel.liveFirebaseUser.observe(this, { firebaseUser ->
            if (firebaseUser != null) {
                updateNavHeader(firebaseUser)
            }
        })

        loggedInViewModel.loggedOut.observe(this, { loggedout ->
            if (loggedout) {
                startActivity(Intent(this, Login::class.java))
            }
        })

        registerImagePickerCallback()
    }

    private fun initNavHeader() {
        Timber.i("DX Init Nav Header")
        headerView = homeBinding.navView.getHeaderView(0)
        navHeaderBinding = NavHeaderBinding.bind(headerView)

        navHeaderBinding.navHeaderImage.setOnClickListener {
            showImagePicker(intentLauncher)
        }
    }

    private fun updateNavHeader(currentUser: FirebaseUser) {
        FirebaseImageManager.imageUri.observe(this, { result ->
            if(result == Uri.EMPTY) {
                Timber.i("DX NO Existing imageUri")
                if (currentUser.photoUrl != null) {
                    //if you're a google user
                    FirebaseImageManager.updateUserImage(
                            currentUser.uid,
                            currentUser.photoUrl,
                            navHeaderBinding.navHeaderImage,
                            false)
                }
                else
                {
                    Timber.i("DX Loading Existing Default imageUri")
                    FirebaseImageManager.updateDefaultImage(
                            currentUser.uid,
                            R.drawable.logo,
                            navHeaderBinding.navHeaderImage)
                }
            }
            else // load existing image from firebase
            {
                Timber.i("DX Loading Existing imageUri")
                FirebaseImageManager.updateUserImage(
                        currentUser.uid,
                        FirebaseImageManager.imageUri.value,
                        navHeaderBinding.navHeaderImage, false)
            }
        })
        navHeaderBinding.navHeaderEmail.text = currentUser.email
        if(currentUser.displayName != null)
            navHeaderBinding.navHeaderName.text = currentUser.displayName
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun signOut(item: MenuItem) {
        loggedInViewModel.logOut()
        val intent = Intent(this, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun registerImagePickerCallback() {
        intentLauncher =
           registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
               when(result.resultCode){
                   RESULT_OK -> {
                       if (result.data != null) {
                           Timber.i("DX registerPickerCallback() ${readImageUri(result.resultCode, result.data).toString()}")
                           FirebaseImageManager
                               .updateUserImage(loggedInViewModel.liveFirebaseUser.value!!.uid,
                                    readImageUri(result.resultCode, result.data),
                                    navHeaderBinding.navHeaderImage,
                                    true)
                            } // end of if
                        }
                        RESULT_CANCELED -> { } else -> { }
                    }
                }
    }
}