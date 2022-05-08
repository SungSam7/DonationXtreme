package ie.wit.donationx.ui.about

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ie.wit.donationx.R
import ie.wit.donationx.databinding.FragmentAboutBinding
import ie.wit.donationx.databinding.FragmentDonateBinding
import ie.wit.donationx.ui.auth.Login
import ie.wit.donationx.ui.weather.WeatherActivity



class AboutFragment : Fragment() {

    private lateinit var aboutViewModel: AboutViewModel

    private var _fragBinding: FragmentAboutBinding? = null
    private val fragBinding get() = _fragBinding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        aboutViewModel =
                ViewModelProvider(this).get(AboutViewModel::class.java)
        _fragBinding = FragmentAboutBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        aboutViewModel.text.observe(viewLifecycleOwner, Observer {
            fragBinding.weatherButton.setOnClickListener {


                val intent = Intent(activity, WeatherActivity::class.java)
                startActivity(intent)
            }
        })
        return root
    }




}