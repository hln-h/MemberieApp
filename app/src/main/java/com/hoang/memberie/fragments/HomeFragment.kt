package com.hoang.memberie.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.BlurTransformation
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hoang.memberie.R
import com.hoang.memberie.activities.DatabaseActivity
import com.hoang.memberie.databinding.FragmentHomeBinding

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
            startActivity(Intent(activity, DatabaseActivity::class.java))
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