package com.example.pir.model

import Company
import java.io.Serializable

data class Guest(
    var id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val category: String = "",
    val withSomeone: Boolean = false,
    val company: List<Company> = emptyList()
) : Serializable


