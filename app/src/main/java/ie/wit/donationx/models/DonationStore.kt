package ie.wit.donationx.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface DonationStore {
    fun findAll(donationsList:
                MutableLiveData<List<DonationModel>>)
    fun findAll(userid:String,
                donationsList:
                MutableLiveData<List<DonationModel>>)
    fun findById(userid:String, donationid: String,
                 donation: MutableLiveData<DonationModel>)
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, donation: DonationModel)
    fun delete(userid:String, donationid: String)
    fun update(userid:String, donationid: String, donation: DonationModel)
}


