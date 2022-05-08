package ie.wit.donationx.ui.donate

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseUser
import ie.wit.donationx.R
import ie.wit.donationx.databinding.FragmentDonateBinding
import ie.wit.donationx.models.DonationModel
import ie.wit.donationx.ui.auth.LoggedInViewModel
import ie.wit.donationx.ui.map.MapsViewModel
import ie.wit.donationx.ui.report.ReportViewModel

class DonateFragment : Fragment() {

    var totalDonated = 0
    private var _fragBinding: FragmentDonateBinding? = null
    private val fragBinding get() = _fragBinding!!
    private lateinit var donateViewModel: DonateViewModel
    private val loggedInViewModel : LoggedInViewModel by activityViewModels()
    private val reportViewModel: ReportViewModel by activityViewModels()
    private val mapsViewModel: MapsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _fragBinding = FragmentDonateBinding.inflate(inflater, container, false)
        val root = fragBinding.root

        donateViewModel = ViewModelProvider(this).get(DonateViewModel::class.java)
        donateViewModel.observableStatus.observe(viewLifecycleOwner, Observer {
                status -> status?.let { render(status) }
        })

        fragBinding.progressBar.max = 10000
        fragBinding.amountPicker.minValue = 1
        fragBinding.amountPicker.maxValue = 1000

        fragBinding.amountPicker.setOnValueChangedListener { _, _, newVal ->

        }
        setButtonListener(fragBinding)

        return root;
       // start()
    }

    private fun render(status: Boolean) {
        when (status) {
            true -> {
                view?.let {

                }
            }
            false -> Toast.makeText(context,getString(R.string.donationError),Toast.LENGTH_LONG).show()
        }
    }
//    fun start(){
//
//        loggedInViewModel = ViewModelProvider(this).get(LoggedInViewModel::class.java)
//        loggedInViewModel.liveFirebaseUser.observe(viewLifecycleOwner, { firebaseUser ->
//            if (firebaseUser != null) {
//                nameUpdate(firebaseUser)
//            }
//        })

   // }
//    private fun nameUpdate(currentUser: FirebaseUser)
//    {
//
//        if(currentUser.displayName != null)
//            fragBinding.donateSubtitle.text = currentUser.displayName
//        else
//            fragBinding.donateSubtitle.text = "TEST"
//    }

    fun setButtonListener(layout: FragmentDonateBinding) {
        layout.donateButton.setOnClickListener {
            val amount = if (layout.paymentAmount.text.isNotEmpty())
                layout.paymentAmount.text.toString().toInt() else layout.amountPicker.value
            if(totalDonated >= layout.progressBar.max)
                Toast.makeText(context,"Donate Amount Exceeded!", Toast.LENGTH_LONG).show()
            else {
                val paymentmethod = if(layout.paymentMethod.checkedRadioButtonId == R.id.Direct) "Direct" else "Paypal"
                totalDonated += amount
                layout.totalSoFar.text = String.format(getString(R.string.totalSoFar),totalDonated)
                layout.progressBar.progress = totalDonated
                donateViewModel.addDonation(loggedInViewModel.liveFirebaseUser,
                    DonationModel(paymentmethod = paymentmethod,amount = amount,
                        email = loggedInViewModel.liveFirebaseUser.value?.email!!,
                        latitude = mapsViewModel.currentLocation.value!!.latitude,
                        longitude = mapsViewModel.currentLocation.value!!.longitude))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_donate, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item,
                requireView().findNavController()) || super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragBinding = null
    }

    override fun onResume() {
        super.onResume()
        totalDonated = reportViewModel.observableDonationsList.value!!.sumOf { it.amount }
        fragBinding.progressBar.progress = totalDonated
        fragBinding.totalSoFar.text = String.format(getString(R.string.totalSoFar),totalDonated)
    }
}