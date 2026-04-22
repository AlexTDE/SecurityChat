package com.example.mysecurechat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mysecurechat.databinding.ActivityMainBinding
import com.example.mysecurechat.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Registration"

        binding.loginTxt.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.btnRegister.setOnClickListener {
            val email = binding.emailField.text.toString()
            val pswd = binding.passwordField.text.toString()
            val pswd2 = binding.passwordRepeatField.text.toString()
            if (email.isNotEmpty() && pswd.isNotEmpty() && pswd == pswd2) {
                MainActivity.auth.createUserWithEmailAndPassword(email, pswd).addOnCompleteListener {
                    if (it.isSuccessful) {
                        saveEmail(email)
                        startActivity(Intent(this, MessengerActivity::class.java))
                        finish()
                    }
                }.addOnFailureListener{
                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun saveEmail(email: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users").child(userId)
        userRef.child("email").setValue(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Cipher", "Email saved successfully")
                } else {
                    Log.e("Cipher", "Email public key", it.exception)
                }
            }
    }
}