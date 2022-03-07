package com.hoang.memberie.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hoang.memberie.databinding.ActivityAddEventBinding
import com.hoang.memberie.models.Event

class AddEventActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = Firebase.firestore


        // Add a new document with an auto-generated ID (string)
        binding.btnAddEvent.setOnClickListener {
            val event = Event(
                "",
                binding.etEventTitle.text.toString(),
                listOf(),
                listOf(
                    FirebaseAuth.getInstance().currentUser?.email!!, // current user should always be in the list of users
                    // The rest of the users could be added in the event screen (one by one to not overcomplicate)
                )
            )

            database.collection("events")
                .add(event)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        "Successful Add Message",
                        "DocumentSnapshot added with ID: ${documentReference.id}"
                    )
                }
                .addOnFailureListener { e ->
                    Log.w("Failure Add Message", "Error adding document", e)
                }
        }

    }
}