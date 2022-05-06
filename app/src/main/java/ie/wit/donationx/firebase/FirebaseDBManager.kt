package ie.wit.donationx.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import ie.wit.donationx.models.DonationModel
import ie.wit.donationx.models.DonationStore
import timber.log.Timber

object FirebaseDBManager : DonationStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAll(donationsList: MutableLiveData<List<DonationModel>>) {
        database.child("donations")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Donation error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<DonationModel>()
                    val children = snapshot.children
                    children.forEach {
                        val donation = it.getValue(DonationModel::class.java)
                        localList.add(donation!!)
                    }
                    database.child("donations")
                        .removeEventListener(this)

                    donationsList.value = localList
                }
            })
    }

    override fun findAll(userid: String, donationsList: MutableLiveData<List<DonationModel>>) {

        database.child("user-donations").child(userid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Donation error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<DonationModel>()
                    val children = snapshot.children
                    children.forEach {
                        val donation = it.getValue(DonationModel::class.java)
                        localList.add(donation!!)
                    }
                    database.child("user-donations").child(userid)
                        .removeEventListener(this)

                    donationsList.value = localList
                }
            })
    }

    override fun findById(userid: String, donationid: String, donation: MutableLiveData<DonationModel>) {

        database.child("user-donations").child(userid)
            .child(donationid).get().addOnSuccessListener {
                donation.value = it.getValue(DonationModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener{
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, donation: DonationModel) {
        Timber.i("Firebase DB Reference : $database")

        val uid = firebaseUser.value!!.uid
        val key = database.child("donations").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        donation.uid = key
        val donationValues = donation.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/donations/$key"] = donationValues
        childAdd["/user-donations/$uid/$key"] = donationValues

        database.updateChildren(childAdd)
    }

    override fun delete(userid: String, donationid: String) {

        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/donations/$donationid"] = null
        childDelete["/user-donations/$userid/$donationid"] = null

        database.updateChildren(childDelete)
    }

    override fun update(userid: String, donationid: String, donation: DonationModel) {

        val donationValues = donation.toMap()

        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["donations/$donationid"] = donationValues
        childUpdate["user-donations/$userid/$donationid"] = donationValues

        database.updateChildren(childUpdate)
    }

    fun updateImageRef(userid: String,imageUri: String) {

        val userDonations = database.child("user-donations").child(userid)
        val allDonations = database.child("donations")

        userDonations.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            //Update Users imageUri
                            it.ref.child("profilepic").setValue(imageUri)
                            //Update all donations that match 'it'
                            val donation = it.getValue(DonationModel::class.java)
                            allDonations.child(donation!!.uid!!)
                                         .child("profilepic").setValue(imageUri)
                        }
                    }
                })
    }
}