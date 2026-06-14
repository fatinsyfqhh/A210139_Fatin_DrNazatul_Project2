package com.example.a210139_fatin_drnazatul_project2

import androidx.annotation.DrawableRes

// resqbiteviewmodel.kt
data class CommunityPost(
    val id: String,
    val userName: String,
    val action: String,
    val time: String,
    val likes: Int,
    val isLiked: Boolean = false
)

// messages.kt
data class ChatPreview(
    val name: String,
    val lastMsg: String,
    val time: String,
    val isUnread: Boolean = false
)