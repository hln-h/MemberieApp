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

    private lateinit var event: Event

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
            .addSnapshotListener{ documents, e ->
                if (documents == null) { return@addSnapshotListener }

                val events = documents.map {
                    val event = it.toObject(Event::class.java)
                    event.id = it.id
                    return@map event
                }
                eventsAdapter.setData(events)
            }

    }
}