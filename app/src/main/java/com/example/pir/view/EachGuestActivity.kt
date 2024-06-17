package com.example.pir.view

import Company
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
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

class EachGuestActivity : AppCompatActivity() {

    private lateinit var guestViewModel: GuestViewModel
    private lateinit var categorySpinner: Spinner
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var withSomeoneSwitch: Switch
    private lateinit var companyLayout: LinearLayout
    private lateinit var addCompanyButton: Button
    private lateinit var saveGuestButton: Button
    private lateinit var deleteGuestButton: Button

    private lateinit var guest: Guest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_each_guest)

        guestViewModel = ViewModelProvider(this).get(GuestViewModel::class.java)

        categorySpinner = findViewById(R.id.category_spinner)
        firstNameEditText = findViewById(R.id.first_name_edit_text)
        lastNameEditText = findViewById(R.id.last_name_edit_text)
        phoneNumberEditText = findViewById(R.id.phone_number_edit_text)
        withSomeoneSwitch = findViewById(R.id.with_someone_switch)
        companyLayout = findViewById(R.id.company_layout)
        addCompanyButton = findViewById(R.id.add_company_button)
        saveGuestButton = findViewById(R.id.save_guest_button)
        deleteGuestButton = findViewById(R.id.delete_guest_button)
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Postavljanje adaptera za Spinner koristeći kategorije iz strings.xml
        val categories = resources.getStringArray(R.array.guest_categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        // Preuzimanje Guest objekta iz intent-a
        guest = intent.getSerializableExtra("guest") as Guest

        firstNameEditText.setText(guest.firstName)
        lastNameEditText.setText(guest.lastName)
        phoneNumberEditText.setText(guest.phoneNumber)
        setSpinnerSelection(categorySpinner, guest.category)
        withSomeoneSwitch.isChecked = guest.withSomeone
        if (guest.withSomeone) {
            companyLayout.visibility = View.VISIBLE
            guest.company.forEach { addCompanyFields(it) }
        }

        withSomeoneSwitch.setOnCheckedChangeListener { _, isChecked ->
            companyLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        addCompanyButton.setOnClickListener { addCompanyFields() }
        saveGuestButton.setOnClickListener { saveGuest() }
        deleteGuestButton.setOnClickListener { deleteGuest() }

    }

    private fun addCompanyFields(company: Company? = null) {
        val companyView = layoutInflater.inflate(R.layout.item_company, null)
        val firstNameEditText = companyView.findViewById<EditText>(R.id.company_first_name)
        val lastNameEditText = companyView.findViewById<EditText>(R.id.company_last_name)
        val removeButton : ImageButton = companyView.findViewById<ImageButton>(R.id.remove_button)

        if (company != null) {
            firstNameEditText.setText(company.firstName)
            lastNameEditText.setText(company.lastName)
        }

        removeButton.setOnClickListener {
            companyLayout.removeView(companyView)
        }

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

        val updatedGuest = Guest(
            id = guest.id,
            category = category,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            withSomeone = withSomeone,
            company = companyList
        )

        guestViewModel.updateGuest(updatedGuest)
        finish()
    }

    private fun deleteGuest() {
        guest.id?.let {
            guestViewModel.deleteGuest(it)
        }
        finish()
    }

    private fun setSpinnerSelection(spinner: Spinner, value: String) {
        val adapter = spinner.adapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i) == value) {
                spinner.setSelection(i)
                break
            }
        }
    }
}
