package com.hoang.memberie.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import coil.load
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.hoang.memberie.R
import com.hoang.memberie.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        var currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            Toast.makeText(this, currentUser.displayName, Toast.LENGTH_SHORT).show()
        } else {
            launchSignIn()
        }

        setUserAvatar(currentUser)



        setOnClickLogOutButton()

        setOnClickDatabaseButton()
    }

    private fun setUserAvatar(currentUser: FirebaseUser?) {
        binding.ivUserAvatar.load(currentUser?.photoUrl)
    }

    private fun setOnClickDatabaseButton() {
        findViewById<Button>(R.id.btn_database).setOnClickListener {
            startActivity(Intent(this, DatabaseActivity::class.java))
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

    private fun setOnClickLogOutButton() {
        findViewById<Button>(R.id.btn_logout).setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    launchSignIn()
                }
        }

    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
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