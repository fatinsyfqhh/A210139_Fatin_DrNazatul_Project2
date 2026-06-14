package com.example.a210139_fatin_drnazatul_project2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    // fetches items populated as public local feeds
    @Query("SELECT * FROM food_items WHERE isUserContribution = 0 ORDER BY id DESC")
    fun getPublicFoodItems(): Flow<List<FoodEntity>>

    // fetches items added directly via the 'Give Away' screen
    @Query("SELECT * FROM food_items WHERE isUserContribution = 1 ORDER BY id DESC")
    fun getMyContributions(): Flow<List<FoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) //room process data into sql values
    suspend fun insertFoodItem(food: FoodEntity)

    // clear items or pre-seed operations
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInitialFoodItems(items: List<FoodEntity>)

    // delete item
    @Delete
    suspend fun deleteFood(food: FoodEntity)
}