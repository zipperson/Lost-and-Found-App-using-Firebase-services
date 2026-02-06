package com.example.actuallostofund.ui

import com.example.actuallostofund.utils.FirebaseUtils
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.actuallostofund.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.example.actuallostofund.R
import kotlin.jvm.java
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // LOGIN BUTTON
        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = FirebaseUtils.auth.currentUser!!
                        val userRef = FirebaseUtils.db.collection("users").document(user.uid)

                        userRef.get().addOnSuccessListener { doc ->
                            if (!doc.exists()) {
                                userRef.set(
                                    mapOf(
                                        "uid" to user.uid,
                                        "email" to user.email,
                                        "displayName" to "",
                                        "pfpKey" to "avatar_1",
                                        "notificationsEnabled" to true
                                    )
                                )
                            }

                            if (!doc.exists() || doc.getString("username").isNullOrBlank()) {
                                startActivity(Intent(this, ProfileSetupActivity::class.java))
                            } else {
                                startActivity(Intent(this, MainActivity::class.java))
                            }
                            finish()
                        }

                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // SIGNUP REDIRECT
        binding.signupRedirectText.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        // PRIVACY POPUP
        binding.txtPrivacy.setOnClickListener {
            showPrivacyPopup()
        }
    }

    // AUTO-LOGIN HERE 🚀
    override fun onStart() {
        super.onStart()

        val user = firebaseAuth.currentUser
        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun showPrivacyPopup() {
        val dialogView = layoutInflater.inflate(R.layout.popup_privacy_notice, null)

        val dialog = AlertDialog.Builder(this, R.style.DialogTransparent)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        dialogView.findViewById<TextView>(R.id.btnOkay).setOnClickListener {
            dialog.dismiss()
        }
    }
}

