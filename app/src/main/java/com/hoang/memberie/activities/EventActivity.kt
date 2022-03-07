package com.hoang.memberie.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.hoang.memberie.databinding.ActivityEventBinding
import com.hoang.memberie.models.Event

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)



        intent.getParcelableExtra<Event>("Event")?.apply {
            event = this
            // add an onListener to database
        }

        binding.flBtnAdd.setOnClickListener {
            launchCameraIntentLauncher.launch(arrayOf("image/*"))
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