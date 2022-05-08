package ie.wit.donationx.firebase

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import ie.wit.donationx.utils.customTransformation
import timber.log.Timber
import java.io.ByteArrayOutputStream

object FirebaseImageManager {

    var storage = FirebaseStorage.getInstance().reference
    var imageUri = MutableLiveData<Uri>()



    fun checkStorageForExistingProfilePic(userid: String) {
        val imageRef = storage.child("photos").child("${userid}.jpg")

        imageRef.metadata.addOnSuccessListener { //File Exists
            imageRef.downloadUrl.addOnCompleteListener { task ->
                imageUri.value = task.result!!
            }
        }.addOnFailureListener {
            imageUri.value = Uri.EMPTY
        }
    }

    fun uploadImageToFirebase(userid: String, bitmap: Bitmap, updating : Boolean) {
        val imageRef = storage.child("photos").child("${userid}.jpg")
        val baos = ByteArrayOutputStream()
        lateinit var uploadTask: UploadTask

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        imageRef.metadata.addOnSuccessListener {
            if(updating)
            {
                uploadTask = imageRef.putBytes(data)
                uploadTask.addOnSuccessListener { ut ->
                    ut.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
                        imageUri.value = task.result!!
                        FirebaseDBManager.updateImageRef(userid,imageUri.value.toString())
                    }
                }
            }
        }.addOnFailureListener {
            uploadTask = imageRef.putBytes(data)
            uploadTask.addOnSuccessListener { ut ->
                ut.metadata!!.reference!!.downloadUrl.addOnCompleteListener { task ->
                    imageUri.value = task.result!!
                }
            }
        }
    }

    fun updateUserImage(userid: String, imageUri : Uri?, imageView: ImageView, updating : Boolean) {
        Picasso.get().load(imageUri)
            .resize(200, 200)
            .transform(customTransformation())
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .centerCrop()
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?,
                                            from: Picasso.LoadedFrom?
                ) {
                    Timber.i("DX onBitmapLoaded $bitmap")
                    uploadImageToFirebase(userid, bitmap!!,updating)
                    imageView.setImageBitmap(bitmap)
                }

                override fun onBitmapFailed(e: java.lang.Exception?,
                                            errorDrawable: Drawable?) {
                    Timber.i("DX onBitmapFailed $e")
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    Timber.i("DX onPrepareLoad $placeHolderDrawable")
                }
            })
    }

    fun updateDefaultImage(userid: String, resource: Int, imageView: ImageView) {
        Picasso.get().load(resource)
                .resize(200, 200)
                .transform(customTransformation())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .centerCrop()
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?,
                                                from: Picasso.LoadedFrom?
                    ) {
                        Timber.i("DX onBitmapLoaded $bitmap")
                        uploadImageToFirebase(userid, bitmap!!,false)
                        imageView.setImageBitmap(bitmap)
                    }

                    override fun onBitmapFailed(e: java.lang.Exception?,
                                                errorDrawable: Drawable?) {
                        Timber.i("DX onBitmapFailed $e")
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        Timber.i("DX onPrepareLoad $placeHolderDrawable")
                    }
                })
    }
}