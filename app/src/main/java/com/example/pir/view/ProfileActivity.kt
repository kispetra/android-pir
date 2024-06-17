package com.example.pir.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.pir.R
import com.example.pir.auth.Login
import com.example.pir.viewModel.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var emailTextView: TextView
    private lateinit var brideNameEditText: EditText
    private lateinit var groomNameEditText: EditText
    private lateinit var weddingDateEditText: EditText
    private lateinit var updateButton: Button
    private lateinit var logoutButton: Button

    private val viewModel: ProfileViewModel by viewModels()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.title = "Profile"

        emailTextView = findViewById(R.id.emailTextView)
        brideNameEditText = findViewById(R.id.brideNameEditText)
        groomNameEditText = findViewById(R.id.groomNameEditText)
        weddingDateEditText = findViewById(R.id.weddingDateEditText)
        updateButton = findViewById(R.id.saveButton)
        logoutButton = findViewById(R.id.logout)

        viewModel.getUserProfile()

        viewModel.userProfile.observe(this, Observer { userProfile ->
            userProfile?.let {
                emailTextView.text = auth.currentUser?.email
                brideNameEditText.setText(it.brideName)
                groomNameEditText.setText(it.groomName)
                weddingDateEditText.setText(it.weddingDate)
            }
        })

        updateButton.setOnClickListener {
            val newBrideName = brideNameEditText.text.toString()
            val newGroomName = groomNameEditText.text.toString()
            val newWeddingDate = weddingDateEditText.text.toString()

            if (newBrideName.isNotBlank() && newGroomName.isNotBlank() && newWeddingDate.isNotBlank()) {
                viewModel.updateUserProfile(newBrideName, newGroomName, newWeddingDate)
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent1 = Intent(this, MainActivity::class.java)
                    startActivity(intent1)
                    true
                }
                R.id.navigation_todo -> {
                    val intent2 = Intent(this, TaskActivity::class.java)
                    startActivity(intent2)
                    true
                }
                R.id.navigation_tips -> {
                    val intent3 = Intent(this, GuestListActivity::class.java)
                    startActivity(intent3)
                    true
                }
                R.id.navigation_profile -> {
                    val intent4 = Intent(this, ProfileActivity::class.java)
                    startActivity(intent4)
                    true
                }
                else -> false
            }
        }
    }
}
