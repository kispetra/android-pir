package com.example.pir.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.example.pir.R
import com.example.pir.auth.Login
import com.example.pir.notif.NotificationUtils
import com.example.pir.viewModel.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var brideAndGroomNameTextView: TextView
    private lateinit var weddingDateTextView: TextView
    private lateinit var countdownTextView: TextView
    private var user: FirebaseUser? = null

    private val viewModel: MainViewModel by viewModels()

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        brideAndGroomNameTextView = findViewById(R.id.bride_and_groom_name)
        weddingDateTextView = findViewById(R.id.wedding_date)
        countdownTextView = findViewById(R.id.countdown)
        user = auth.currentUser

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQUEST_CODE)
            } else {
                // Permission already granted
                NotificationUtils.createNotificationChannels(this)
            }
        } else {
            NotificationUtils.createNotificationChannels(this)
        }

        if (user == null) {
            val intent = Intent(applicationContext, Login::class.java)
            startActivity(intent)
            finish()
        } else {
            viewModel.fetchUserData()
            viewModel.user.observe(this, Observer { user ->
                user?.let {
                    val brideName = it.brideName.toUpperCase(Locale.getDefault())
                    val groomName = it.groomName.toUpperCase(Locale.getDefault())
                    brideAndGroomNameTextView.text = "$brideName & $groomName"
                    weddingDateTextView.text = "DATE OF WEDDING:\n${it.weddingDate}"

                    // Start countdown timer
                    startCountdownTimer(it.weddingDate)
                } ?: run {
                    brideAndGroomNameTextView.text = "No data found"
                    weddingDateTextView.text = "No data found"
                }
            })
        }
    }


    private fun startCountdownTimer(weddingDate: String) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        try {
            val weddingDateMillis = dateFormat.parse(weddingDate)?.time ?: 0L
            val currentMillis = System.currentTimeMillis()
            val diffMillis = weddingDateMillis - currentMillis

            if (diffMillis > 0) {
                object : CountDownTimer(diffMillis, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val days = millisUntilFinished / (1000 * 60 * 60 * 24)
                        val hours = (millisUntilFinished / (1000 * 60 * 60)) % 24
                        val minutes = (millisUntilFinished / (1000 * 60)) % 60
                        val seconds = (millisUntilFinished / 1000) % 60

                        countdownTextView.text = "TIME UNTIL WEDDING:\n${days}D ${hours}H ${minutes}M ${seconds}S"
                    }

                    override fun onFinish() {
                        countdownTextView.text = "WEDDING DAY IS TODAY!"
                    }
                }.start()
                // Schedule notifications
                NotificationUtils.scheduleNotifications(this, weddingDateMillis)
            } else {
                countdownTextView.text = "THE WEDDING DATE IS IN THE PAST!"
            }
        } catch (e: Exception) {
            countdownTextView.text = "ERROR PARSING WEDDING DATE."
            Log.e("MainActivity", "Error parsing wedding date: ", e)
        }
    }
}
