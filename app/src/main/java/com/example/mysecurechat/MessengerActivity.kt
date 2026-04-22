package com.example.mysecurechat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mysecurechat.databinding.ActivityMainBinding
import com.example.mysecurechat.databinding.ActivityMessengerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.util.Base64

class MessengerActivity : AppCompatActivity() {

    companion object {
        lateinit var auth: FirebaseAuth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMessengerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Messenger"
        auth = FirebaseAuth.getInstance()
        getPublicKey()

        binding.btnLogout.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.btnNewMessage.setOnClickListener {
            startActivity(Intent(this, NewMessageActivity::class.java))
        }
    }

    fun getPublicKey(){
        val userId = auth.currentUser?.uid ?: return
        val keyPair = CipherClass().generateRSAKeyPair()
        val publicKeyString = Base64.encodeToString(keyPair.public.encoded, Base64.DEFAULT)

        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("users").child(userId)
        userRef.child("publicKey").setValue(publicKeyString)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("Cipher", "Public key saved successfully")
                } else {
                    Log.e("Cipher", "Failed to save public key", it.exception)
                }
            }

    }
}