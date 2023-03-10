package com.hoang.memberie.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.hoang.memberie.R
import com.hoang.memberie.databinding.ActivityEventBinding
import com.hoang.memberie.models.Event
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track

class EventActivity : AppCompatActivity() {

    private val TAG = "EventActivity"
    private lateinit var binding: ActivityEventBinding
    private lateinit var event: Event

    private val launchCameraIntentLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { fileUri ->
            if (fileUri != null) {
                uploadFromUri(fileUri)
            } else {
                Log.w(TAG, "File URI is null")
            }
        }

    //SPOTIFY VALUES
    private val clientId = "CLIENT_KEY" //ADD OWN KEY
    private val redirectUri = "REDIRECT_URI" //ADD OWN KEY
    private var spotifyAppRemote: SpotifyAppRemote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getParcelableExtra<Event>("Event")?.apply {
            event = this
            // add an onListener to database
        }

        binding.flBtnAdd.setOnClickListener { v: View ->
            showMenu(v, R.menu.popup_menu_event_details)
        }

        binding.btnMute.setOnClickListener {
            muteMusic()
        }
        binding.btnUnmute.setOnClickListener {
            resumeMusic()
        }
    }

    private fun showMenu(v: View, menuRes: Int) {
        val popup = PopupMenu(this, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.option_1) {
                Toast.makeText(this, "Invite Friend TODO", Toast.LENGTH_SHORT).show()
            }
            if (it.itemId == R.id.option_2) {
                launchCameraIntentLauncher.launch(arrayOf("image/*"))
            }
            false
        }
        popup.show()
    }


    //SPOTIFY STARTUP
    override fun onStart() {
        super.onStart()
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })
    }

    //SPOTIFY PLAYING
    private fun connected() {
        spotifyAppRemote?.let {
            // Play a playlist
            val playlistURI = "PLAYLIST_URI" //ADD OWN KEY
            it.playerApi.setShuffle(true)
            it.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            it.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }

    }

    //mute music
    private fun muteMusic() {
        spotifyAppRemote?.let {
            it.playerApi.pause()
        }
    }

    //resume music
    private fun resumeMusic() {
        spotifyAppRemote?.let {
            it.playerApi.resume()
        }
    }

    //SPOTIFY ENDS
    override fun onPause() {
        super.onPause()
        spotifyAppRemote?.let {
            it.playerApi.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
        }
    }


    private fun uploadFromUri(fileUri: Uri) {
        var storageRef = FirebaseStorage.getInstance().reference
        fileUri.lastPathSegment?.let {
            val photoRef = storageRef.child("photos").child(it)

            // Upload file to Firebase Storage
            Log.d(TAG, "uploadFromUri:dst:" + photoRef.path)
            photoRef.putFile(fileUri).addOnProgressListener { (bytesTransferred, totalByteCount) ->
//                showProgressNotification(getString(R.string.progress_uploading),
//                    bytesTransferred,
//                    totalByteCount)
            }.continueWithTask { task ->
                // Forward any exceptions
                if (!task.isSuccessful) {
                    throw task.exception!!
                }

                Log.d(TAG, "uploadFromUri: upload success")

                // Request the public download URL
                photoRef.downloadUrl
            }.addOnSuccessListener { downloadUri ->
                // Upload succeeded
                Log.d(TAG, "uploadFromUri: getDownloadUri success: $downloadUri")

                val newPhotosUrls = event.photosUrls.toMutableList()
                newPhotosUrls.add(downloadUri.toString())
                val newEvent = event.copy(photosUrls = newPhotosUrls)

                val database = Firebase.firestore
                database.document("events/${event.id}")
                    .set(newEvent)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot added")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }.addOnFailureListener { exception ->
                // Upload failed
                Log.w(TAG, "uploadFromUri:onFailure", exception)

            }
        }
    }
}