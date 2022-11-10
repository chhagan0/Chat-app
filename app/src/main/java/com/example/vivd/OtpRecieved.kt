package com.example.vivd

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_otp_recieved.*
import java.util.concurrent.TimeUnit

class OtpRecieved : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var Otp1: EditText
    private lateinit var Otp2: EditText
    private lateinit var Otp3: EditText
    private lateinit var Otp4: EditText
    private lateinit var Otp5: EditText
    private lateinit var Otp6: EditText
    private lateinit var OTP: String
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var phoneNuber: String
    var dialog: ProgressDialog?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialog= ProgressDialog(this)

        setContentView(R.layout.activity_otp_recieved)
        auth = FirebaseAuth.getInstance()


        OTP = intent.getStringExtra("OTP").toString()
        resendToken = intent.getParcelableExtra("resendToken")!!
        phoneNuber = intent.getStringExtra("PhoneNumber")!!


        backbtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        resendotp.setOnClickListener { resendd()
        dialog!!.show()}

        verify.setOnClickListener {


            dialog!!.show()


            val otpcode =
                (otpEditText1.text.toString() + otpEditText2.text.toString() + otpEditText3.text.toString() + otpEditText4.text.toString() + otpEditText5.text.toString() + otpEditText6.text.toString())
            if (otpcode.isNotEmpty()) {
                if (otpcode.length == 6) {
                    dialog!!.dismiss()
                    val credential: PhoneAuthCredential =
                        PhoneAuthProvider.getCredential(OTP, otpcode)
                    signInWithPhoneAuthCredential(credential)
                } else {
                    dialog!!.dismiss()
                    Toast.makeText(this, "Enter Valid OTP", Toast.LENGTH_SHORT).show()

                }
            } else {
                dialog!!.dismiss()
                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show()

            }
        }

        initt()
        addtextchagnelistner()
    }

    private fun resendd() {

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNuber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    sendd()

                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun sendd() {
        startActivity(Intent(this, SetipProfile::class.java))
    }

    fun addtextchagnelistner() {
        Otp1.addTextChangedListener(EditClassWatcher(Otp1))
        Otp2.addTextChangedListener(EditClassWatcher(Otp2))
        Otp3.addTextChangedListener(EditClassWatcher(Otp3))
        Otp4.addTextChangedListener(EditClassWatcher(Otp4))
        Otp5.addTextChangedListener(EditClassWatcher(Otp5))
        Otp6.addTextChangedListener(EditClassWatcher(Otp6))

    }


    private fun initt() {
        Otp1 = findViewById(R.id.otpEditText1)
        Otp2 = findViewById(R.id.otpEditText2)
        Otp3 = findViewById(R.id.otpEditText3)
        Otp4 = findViewById(R.id.otpEditText4)
        Otp5 = findViewById(R.id.otpEditText5)
        Otp6 = findViewById(R.id.otpEditText6)
    }

    inner class EditClassWatcher(private var view: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            return
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            return
        }

        override fun afterTextChanged(p0: Editable?) {
            val text = p0.toString()
            when (view.id) {
                R.id.otpEditText1 -> if (text.length == 1) Otp2.requestFocus()
                R.id.otpEditText2 -> if (text.length == 1) Otp3.requestFocus() else if (text.isEmpty()) Otp1.requestFocus()
                R.id.otpEditText3 -> if (text.length == 1) Otp4.requestFocus() else if (text.isEmpty()) Otp2.requestFocus()
                R.id.otpEditText4 -> if (text.length == 1) Otp5.requestFocus() else if (text.isEmpty()) Otp3.requestFocus()
                R.id.otpEditText5 -> if (text.length == 1) Otp6.requestFocus() else if (text.isEmpty()) Otp4
                R.id.otpEditText6 -> if (text.isEmpty()) Otp5.requestFocus()
            }
        }

    }

    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }
        }
    }
}


