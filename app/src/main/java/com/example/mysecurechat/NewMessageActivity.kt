package com.example.mysecurechat

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.mysecurechat.databinding.ActivityNewMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import javax.crypto.SecretKey
import android.util.Base64

data class Message(
    val senderId: String? = null,
    val receiverId: String? = null,
    val messageText: String = ""
    //val iv: String? = null
)

class NewMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewMessageBinding

    private lateinit var messagesAdapter: ArrayAdapter<String>
    private lateinit var messagesList: MutableList<String>

    //private val cipherClass = CipherClass()
    //private lateinit var aesKey: SecretKey
    private val database = FirebaseDatabase.getInstance()
    private val messagesRef = database.getReference("messages")
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    private var receiverId = "xSDzLoXkKxM7NmNS8maY4p8t9oY2" // Установите ID получателя
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("MyMessengerActivity", "start")
        if (currentUserId==" xSDzLoXkKxM7NmNS8maY4p8t9oY2") {
            receiverId = "5caHzcdS78dprEkOlHAM3omT1H52"
        }
        //aesKey = cipherClass.generateAESKey()
        messagesList = mutableListOf()
        messagesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messagesList)
        binding.messListView.adapter = messagesAdapter
        Log.d("MyMessengerActivity", "???")
        binding.sendMessBtn.setOnClickListener {
            val messageText = binding.messInput.text.toString()
            if (messageText.isNotEmpty()) {
                Log.d("MyMessengerActivity", "send")
                sendMessage(messageText)
                binding.messInput.text.clear()
            }
        }

        // Подписываемся на получение новых сообщений
        listenForMessages()
    }

    private fun sendMessage(messageText: String) {

        //val (encryptedMessage, ivBase64) = cipherClass.encryptMessage(messageText, aesKey)
        val messageId = messagesRef.push().key ?: return

        val message = mapOf(
            "senderId" to currentUserId,
            "receiverId" to receiverId,
            "messageText" to messageText
            //"iv" to ivBase64
        )

        messagesRef.child(messageId).setValue(message)
            .addOnSuccessListener {
                Log.d("MyMessengerActivity", "Message sent successfully")
            }
            .addOnFailureListener {
                Log.e("MyMessengerActivity", "Failed to send message", it)
            }
    }

    private fun listenForMessages() {
        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("MyMessengerActivity", "help")
                val message = snapshot.getValue(Message::class.java)
                Log.d("MyMessengerActivity", "aaaaaa")
                message?.let {
                    //if (it.senderId == currentUserId || it.receiverId == currentUserId) {
                        //val decryptMessage = cipherClass.decryptMessage(it.messageText, aesKey, it.iv!!)
                    messagesList.add(it.messageText)
                    Log.d("MyMessengerActivity", "deeead")
                    messagesAdapter.notifyDataSetChanged()
                   // }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("MyMessengerActivity", "Failed to listen for messages", error.toException())
            }
        })
    }
}

