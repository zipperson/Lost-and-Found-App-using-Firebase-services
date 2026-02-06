package com.example.actuallostofund.utils.models

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val pfpKey: String = "avatar_1",
    val notificationsEnabled: Boolean = true,
    val fcmToken: String = ""
)
