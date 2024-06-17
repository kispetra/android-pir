package com.example.pir.view

import Company
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pir.R
import com.example.pir.model.Guest
import com.example.pir.viewModel.GuestViewModel

class AddGuestActivity : AppCompatActivity() {

    private lateinit var guestViewModel: GuestViewModel
    private lateinit var categorySpinner: Spinner
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var withSomeoneSwitch: Switch
    private lateinit var companyLayout: LinearLayout
    private lateinit var addCompanyButton: Button
    private lateinit var saveGuestButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_guest)

        guestViewModel = ViewModelProvider(this).get(GuestViewModel::class.java)

        categorySpinner = findViewById(R.id.category_spinner)
        firstNameEditText = findViewById(R.id.first_name_edit_text)
        lastNameEditText = findViewById(R.id.last_name_edit_text)
        phoneNumberEditText = findViewById(R.id.phone_number_edit_text)
        withSomeoneSwitch = findViewById(R.id.with_someone_switch)
        companyLayout = findViewById(R.id.company_layout)
        addCompanyButton = findViewById(R.id.add_company_button)

        withSomeoneSwitch.setOnCheckedChangeListener { _, isChecked ->
            companyLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        addCompanyButton.setOnClickListener {
            addCompanyFields()
        }
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            // Povratak na TaskActivity
            val intent = Intent(this, GuestListActivity::class.java)
            startActivity(intent)
            finish()
        }
        findViewById<Button>(R.id.save_guest_button).setOnClickListener {
            saveGuest()
        }
    }

    private fun addCompanyFields() {
        val companyView = layoutInflater.inflate(R.layout.item_company, null)
        companyLayout.addView(companyView)
    }

    private fun saveGuest() {
        val category = categorySpinner.selectedItem.toString()
        val firstName = firstNameEditText.text.toString()
        val lastName = lastNameEditText.text.toString()
        val phoneNumber = phoneNumberEditText.text.toString()
        val withSomeone = withSomeoneSwitch.isChecked

        val companyList = mutableListOf<Company>()
        if (withSomeone) {
            for (i in 0 until companyLayout.childCount) {
                val companyView = companyLayout.getChildAt(i)
                val companyFirstNameEditText = companyView.findViewById<EditText>(R.id.company_first_name)
                val companyLastNameEditText = companyView.findViewById<EditText>(R.id.company_last_name)

                if (companyFirstNameEditText != null && companyLastNameEditText != null) {
                    val companyFirstName = companyFirstNameEditText.text.toString()
                    val companyLastName = companyLastNameEditText.text.toString()

                    if (companyFirstName.isNotEmpty() && companyLastName.isNotEmpty()) {
                        val company = Company(firstName = companyFirstName, lastName = companyLastName)
                        companyList.add(company)
                    }
                }
            }
        }

        val guest = Guest(
            id = "",
            category = category,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            withSomeone = withSomeone,
            company = companyList
        )

        guestViewModel.addGuest(guest)
        finish()
    }
}
