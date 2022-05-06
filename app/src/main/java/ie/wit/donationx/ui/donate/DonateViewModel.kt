package ie.wit.donationx.ui.donate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import ie.wit.donationx.firebase.FirebaseDBManager
import ie.wit.donationx.firebase.FirebaseImageManager
import ie.wit.donationx.models.DonationModel

class DonateViewModel : ViewModel() {

    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addDonation(firebaseUser: MutableLiveData<FirebaseUser>,
                    donation: DonationModel) {
        status.value = try {
            donation.profilepic = FirebaseImageManager.imageUri.value.toString()
            FirebaseDBManager.create(firebaseUser,donation)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

}