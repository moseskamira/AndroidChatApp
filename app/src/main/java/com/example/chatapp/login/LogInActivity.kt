package com.example.chatapp.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R
import com.example.chatapp.mainPage.MainPageActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class LogInActivity : AppCompatActivity() {
    private lateinit var phoneNumberRequest: EditText
    private lateinit var generateCodeRequestButton: Button
    private lateinit var generatedVerificationCode: EditText
    private lateinit var mCallBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var mVerificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)
        phoneNumberRequest = phone_number_request
        generateCodeRequestButton = generate_code_button
        generatedVerificationCode = generated_code

        mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(firebaseException: FirebaseException?) {
                Toast.makeText(applicationContext, firebaseException!!.message, Toast.LENGTH_SHORT).show()
                Log.i("Error", firebaseException.message)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {
                super.onCodeSent(verificationId, token)
                mVerificationId = verificationId!!
                generatedVerificationCode.visibility = View.VISIBLE
                generateCodeRequestButton.text = getString(R.string.submit_code)
                phoneNumberRequest.focusable = View.NOT_FOCUSABLE
            }
        }
        generateCodeRequestButton.setOnClickListener {
            if (mVerificationId != null) {
                verifyPhoneNumberWithCode()
            } else
                startPhoneNumberVerification()
        }
    }

    private fun startPhoneNumberVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumberRequest.text.toString(), 60, TimeUnit.SECONDS, this, mCallBacks )
    }

    private fun signInWithPhoneCredential(phoneAuthCredential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener {
            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val userDatabase = FirebaseDatabase.getInstance().reference.child("user")
                    .child(currentUser.uid)
                userDatabase.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            val userMap = HashMap<String, Any>()
                            userMap["phoneNumber"] = currentUser.phoneNumber.toString()
                            userMap["userName"]= currentUser.displayName.toString()
                            userDatabase.updateChildren(userMap)
                        }
                        userIsLoggedIn()
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
            }
//            userIsLoggedIn()+256789608543
        }
    }

    private fun userIsLoggedIn() {
        FirebaseAuth.getInstance().currentUser
        startActivity(Intent(applicationContext, MainPageActivity::class.java))
        finish()
//        return
    }

    private fun verifyPhoneNumberWithCode() {
        val credential: PhoneAuthCredential = PhoneAuthProvider
            .getCredential(mVerificationId!!, generatedVerificationCode.text.toString())
        signInWithPhoneCredential(credential)
    }
}
