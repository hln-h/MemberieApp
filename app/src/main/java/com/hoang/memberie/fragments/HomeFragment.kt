package com.hoang.memberie.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.BlurTransformation
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hoang.memberie.R
import com.hoang.memberie.databinding.FragmentHomeBinding
import com.hoang.memberie.models.Event

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentHomeBinding.bind(view)


        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            Toast.makeText(activity, currentUser.displayName, Toast.LENGTH_SHORT).show()
        } else {
            launchSignIn()
        }

        setUserProfileInfo(currentUser, binding)

        setBackgroundImg(currentUser, binding)

        setOnAddButtonClicked(binding)

        setOnMenuButtonClicked(binding)
    }

    private fun setUserProfileInfo(currentUser: FirebaseUser?, binding: FragmentHomeBinding) {
        binding.ivUserAvatar.load(currentUser?.photoUrl)
        binding.tvHello.text = "Hello ${currentUser?.displayName}!"
    }

    private fun setOnAddButtonClicked(binding: FragmentHomeBinding) {
        binding.btnMore.setOnClickListener {
            val database = Firebase.firestore
            val inflater = LayoutInflater.from(activity)
            val addEventView = inflater.inflate(R.layout.dialog_add_event, null)

            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Add a new event")
//                .setMessage("Choose a title")
                .setView(addEventView)
                .setNeutralButton("cancel") { dialog, which ->
                    // Respond to neutral button press
                }

                .setPositiveButton("ADD") { dialog, which ->
                    val event = Event(
                        "",
                        addEventView.findViewById<EditText>(R.id.et_event_title).text.toString(),
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
                .show()
        }
    }

    private fun setBackgroundImg(currentUser: FirebaseUser?, binding: FragmentHomeBinding) {
        binding.ivImgBackground.load(currentUser?.photoUrl) {
            transformations(BlurTransformation(requireContext(), 4f))
        }
    }

    private fun setOnMenuButtonClicked(binding: FragmentHomeBinding) {
        binding.imBtnMenu.setOnClickListener { v: View ->
            showMenu(v, R.menu.popup_menu)
        }
    }

    private fun showMenu(v: View, menuRes: Int) {
        val popup = PopupMenu(activity, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener {
            if (it.itemId == R.id.option_1) {
                signOutAndLaunchSignIn()
            }
            if (it.itemId == R.id.option_2) {
                Toast.makeText(activity, "Notifications", Toast.LENGTH_SHORT).show()
            }
            false
        }
        popup.show()
    }

    private fun signOutAndLaunchSignIn() {
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnCompleteListener {
                launchSignIn()
            }
    }


    private fun launchSignIn() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            // ...
            return
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            return
        }
    }
}