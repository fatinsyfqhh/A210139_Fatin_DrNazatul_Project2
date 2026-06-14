package com.example.a210139_fatin_drnazatul_project2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val distance: String,
    val user: String,
    val location: String,
    val imageRes: Int,
    val isUserContribution: Boolean = false
)