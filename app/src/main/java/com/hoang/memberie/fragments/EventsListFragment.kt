package com.hoang.memberie.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hoang.memberie.R
import com.hoang.memberie.activities.EventActivity
import com.hoang.memberie.adapter.EventsAdapter
import com.hoang.memberie.databinding.FragmentEventsListBinding
import com.hoang.memberie.models.Event

class EventsListFragment : Fragment(R.layout.fragment_events_list) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentEventsListBinding.bind(view)

        val database = Firebase.firestore

        val recyclerView = binding.rvEvents
        recyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        val eventsAdapter = EventsAdapter()
        recyclerView.adapter = eventsAdapter

        eventsAdapter.onItemClicked = {
            val intent = Intent(activity, EventActivity::class.java)
            intent.putExtra("Event", it)
            startActivity(intent)
        }

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

//------------------------ DO NOT DELETE THIS CODE YET--------------------------

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
    }
}