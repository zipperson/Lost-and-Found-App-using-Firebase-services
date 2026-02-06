package com.example.actuallostofund.utils

import com.example.actuallostofund.R

object AvatarUtils {

    fun getAvatarRes(key: String): Int {
        return when (key) {
            "avatar_1" -> R.drawable.avatar_1
            "avatar_2" -> R.drawable.avatar_2
            "avatar_3" -> R.drawable.avatar_3
            "avatar_4" -> R.drawable.avatar_4
            "avatar_5" -> R.drawable.avatar_5
            "avatar_6" -> R.drawable.avatar_6
            "avatar_7" -> R.drawable.avatar_7
            "avatar_8" -> R.drawable.avatar_8
            "avatar_9" -> R.drawable.avatar_9
            "avatar_10" -> R.drawable.avatar_10
            else -> R.drawable.avatar_1
        }
    }

    val avatarKeys = listOf(
        "avatar_1",
        "avatar_2",
        "avatar_3",
        "avatar_4",
        "avatar_5",
        "avatar_6",
        "avatar_7",
        "avatar_8",
        "avatar_9",
        "avatar_10"
    )
}
