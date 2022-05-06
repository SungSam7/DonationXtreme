package ie.wit.donationx.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ie.wit.donationx.firebase.FirebaseDBManager
import ie.wit.donationx.models.DonationModel
import timber.log.Timber
import java.lang.Exception

class DonationDetailViewModel : ViewModel() {
    private val donation = MutableLiveData<DonationModel>()

    var observableDonation: LiveData<DonationModel>
         get() = donation
         set(value) {donation.value = value.value}

    fun getDonation(userid:String, id: String) {
        try {
            //DonationManager.findById(email, id, donation)
            FirebaseDBManager.findById(userid, id, donation)
            Timber.i("Detail getDonation() Success : ${
                donation.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Detail getDonation() Error : $e.message")
        }
    }

    fun updateDonation(userid:String, id: String,donation: DonationModel) {
        try {
            //DonationManager.update(email, id, donation)
            FirebaseDBManager.update(userid, id, donation)
            Timber.i("Detail update() Success : $donation")
        }
        catch (e: Exception) {
            Timber.i("Detail update() Error : $e.message")
        }
    }
}

