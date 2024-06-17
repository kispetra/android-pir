package com.example.pir.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pir.view.MainActivity
import com.example.pir.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {
    private lateinit var editTextBrideName: TextInputEditText
    private lateinit var editTextGroomName: TextInputEditText
    private lateinit var editTextWeddingDate: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var buttonReg: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var textView: TextView
    private lateinit var db: FirebaseFirestore

    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            Toast.makeText(applicationContext, "Login Successful", Toast.LENGTH_SHORT).show()
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editTextBrideName = findViewById(R.id.bride_name)
        editTextGroomName = findViewById(R.id.groom_name)
        editTextWeddingDate = findViewById(R.id.wedding_date)
        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        buttonReg = findViewById(R.id.btn_register)
        mAuth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)
        textView = findViewById(R.id.loginNow)
        db = FirebaseFirestore.getInstance()

        textView.setOnClickListener {
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        }

        buttonReg.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val brideName = editTextBrideName.text.toString()
            val groomName = editTextGroomName.text.toString()
            val weddingDate = editTextWeddingDate.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            if (TextUtils.isEmpty(brideName) || TextUtils.isEmpty(groomName) ||
                TextUtils.isEmpty(weddingDate) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)
            ) {
                Toast.makeText(this@Register, "Fill in all fields", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser

                        // Create a HashMap to store the user details
                        val userMap = HashMap<String, Any>()
                        userMap["brideName"] = brideName
                        userMap["groomName"] = groomName
                        userMap["weddingDate"] = weddingDate

                        // Add user details to Firestore
                        user?.uid?.let {
                            db.collection("users").document(it)
                                .set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        applicationContext,
                                        "Registration Successful",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(applicationContext, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        applicationContext,
                                        "Error: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        Toast.makeText(
                            this@Register,
                            "Authentication failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}
