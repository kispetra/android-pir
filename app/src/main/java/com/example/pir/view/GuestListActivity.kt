package com.example.pir.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pir.R
import com.example.pir.model.Guest
import com.example.pir.viewModel.GuestViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.Serializable

class GuestListActivity : AppCompatActivity() {

    private lateinit var guestViewModel: GuestViewModel
    private lateinit var guestListLayout: LinearLayout
    private lateinit var categorySpinner: Spinner
    private lateinit var guestCountText: TextView
    private lateinit var totalGuestCountText: TextView
    private var currentCategory: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guest_list)

        guestViewModel = ViewModelProvider(this).get(GuestViewModel::class.java)
        guestListLayout = findViewById(R.id.guest_list_layout)
        categorySpinner = findViewById(R.id.category_spinner)
        guestCountText = findViewById(R.id.guest_count_text)

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentCategory = parent?.getItemAtPosition(position).toString()
                guestViewModel.fetchGuests(currentCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Set the default category for Spinner
        currentCategory = categorySpinner.selectedItem.toString()
        guestViewModel.fetchGuests(currentCategory)

        findViewById<Button>(R.id.add_guest_button).setOnClickListener {
            val intent = Intent(this, AddGuestActivity::class.java)
            startActivity(intent)
        }

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim().toLowerCase()
                guestViewModel.searchGuests(currentCategory, searchText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        guestViewModel.guests.observe(this, Observer { guests ->
            guestListLayout.removeAllViews()
            Log.d("GuestListActivity", "Guests updated: $guests")
            guests.forEach { guest ->
                addGuestToLayout(guest)
            }
            updateGuestCount(guests)
        })

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_tips
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

    private fun addGuestToLayout(guest: Guest) {
        val guestView = layoutInflater.inflate(R.layout.item_guest, guestListLayout, false)
        val guestNameTextView = guestView.findViewById<TextView>(R.id.guest_name)
        val guestPhoneNumberTextView = guestView.findViewById<TextView>(R.id.guest_phone)
        val guestCategoryTextView = guestView.findViewById<TextView>(R.id.guest_category)
        val guestWithSomeoneTextView = guestView.findViewById<TextView>(R.id.guest_with_someone)
        val guestTotalCountTextView = guestView.findViewById<TextView>(R.id.guest_total_count)

        val totalGuestCount = 1 + guest.company.size
        guestNameTextView.text = "${guest.firstName} ${guest.lastName}"
        guestTotalCountTextView.text = "Total: $totalGuestCount"
        guestCategoryTextView.text = guest.category
        guestPhoneNumberTextView.text = guest.phoneNumber
        guestWithSomeoneTextView.text = if (guest.withSomeone) {
            guest.company.joinToString(", ") { "${it.firstName} ${it.lastName}" }
        } else {
            "No company"
        }

        // Set OnClickListener for the guest view
        guestView.setOnClickListener {
            val intent = Intent(this, EachGuestActivity::class.java).apply {
                putExtra("guest", guest as Serializable)
            }
            startActivity(intent)
        }

        guestListLayout.addView(guestView)
    }

    private fun updateGuestCount(guests: List<Guest>) {
        val count = guests.size + guests.sumOf { it.company.size }
        guestCountText.text = "Total of $currentCategory: $count guests"
    }
}
