package com.example.mysecurechat

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mysecurechat.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.security.KeyPair

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    companion object {
        lateinit var auth: FirebaseAuth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Log.d("AAAA", "$auth ${auth.uid}")
        if (auth.currentUser != null) {
            startActivity(Intent(this, MessengerActivity::class.java))
            finish()
        }

        binding.registerTxt.setOnClickListener{
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.emailField.text.toString()
            val pswd = binding.passwordField.text.toString()
            if (email.isNotEmpty() && pswd.isNotEmpty()) {

                auth.signInWithEmailAndPassword(email, pswd).addOnCompleteListener {
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
        val userId = auth.currentUser?.uid ?: return

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