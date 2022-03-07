package com.hoang.memberie.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hoang.memberie.adapter.EventsAdapter
import com.hoang.memberie.databinding.ActivityDatabaseBinding
import com.hoang.memberie.models.Event

class DatabaseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDatabaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatabaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = Firebase.firestore

        binding.btnGoToAddEvent.setOnClickListener {
            startActivity(Intent(this, AddEventActivity::class.java))
        }

        val recyclerView = binding.rvEvents
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false )

        val eventsAdapter = EventsAdapter()
        recyclerView.adapter = eventsAdapter

        eventsAdapter.onItemClicked = {
            val intent = Intent(this, EventActivity::class.java)
            intent.putExtra("Event", it)
            startActivity(intent)
        }



        // Create a new user with a first and last name
//        val user = hashMapOf(
//            "first" to "Ada",
//            "last" to "Lovelace",
//            "born" to 2000
//        )

//        val event = Event(
//            "Ibiza",
//            listOf(
//                "http://34.77.228.118/export/sites/segtur/.content/imagenes/cabeceras-grandes/baleares/ibiza-cala-s1534753385.jpg",
//                "https://cdn2.civitatis.com/espana/ibiza/guia/ibiza-ciudad-grid-m.jpg"
//            ),
//            listOf("carolinakakefuku@gmail.com")
//        )
//
//        val event2 = Event(
//            "Caro's birthday",
//            listOf(),
//            listOf("federicotrimboli@gmail.com")
//        )
//
//        // Add a new document with an auto-generated ID (string)
//        database.collection("events")
//            .add(event2)
//            .addOnSuccessListener { documentReference ->
//                Log.d(
//                    "Successful Add Message",
//                    "DocumentSnapshot added with ID: ${documentReference.id}"
//                )
//            }
//            .addOnFailureListener { e ->
//                Log.w("Failure Add Message", "Error adding document", e)
//            }

//        database.document("events/ada")
//            .set(user)
//            .addOnSuccessListener {
//                Log.d(
//                    "Successful Add Message",
//                    "DocumentSnapshot added"
//                )
//            }
//            .addOnFailureListener { e ->
//                Log.w("Failure Add Message", "Error adding document", e)
//            }


        // get events for the logged in user and set the id for each event
        database.collection("events")
            .whereArrayContains("usersEmails", FirebaseAuth.getInstance().currentUser?.email!!)
            .get()
            .addOnSuccessListener { documents ->
//                val events = documents.toObjects(Event::class.java)
                val events = documents.map {
                    val event = it.toObject(Event::class.java)
                    event.id = it.id
                    return@map event
                }

                eventsAdapter.setData(events)
            }


    }
}