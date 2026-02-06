package com.example.actuallostofund.utils.models

data class LostItem(
    val id: String = "",
    val userId: String = "",
    val itemName: String = "",
    val locationLost: String = "",
    val dateLost: String = "",
    val imageKey: String = "",
    val status: String = "pending"
)
