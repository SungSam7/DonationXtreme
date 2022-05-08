package ie.wit.donationx.ui.passwordreset

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import ie.wit.donationx.databinding.ActivityForgotPasswordBinding


class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityForgotPasswordBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Loading")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.submitBtn.setOnClickListener {
            confirmEmail()
        }


    }

    private var email = ""

    private fun confirmEmail() {
        email = binding.emailEt.text.toString().trim()


        if(email.isEmpty()){
            Toast.makeText(this, "Enter email for your account", Toast.LENGTH_LONG).show()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email, please enter a formal email", Toast.LENGTH_LONG).show()

        }
        else{
            resetPassword()
        }
    }

    private fun resetPassword() {
        progressDialog.setMessage("Sending rest email to ${email}")
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Sent successfully to ${email}, please follow instructions on to reset password", Toast.LENGTH_LONG).show()

            }
            .addOnFailureListener { e->
                progressDialog.dismiss()
                Toast.makeText(this,"Failed to = ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }
}