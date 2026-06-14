package com.example.a210139_fatin_drnazatul_project2.data

import kotlinx.coroutines.flow.Flow

class FoodRepository(private val foodDao: FoodDao) {
    val publicFoodList: Flow<List<FoodEntity>> = foodDao.getPublicFoodItems()
    val myContributions: Flow<List<FoodEntity>> = foodDao.getMyContributions()

    suspend fun insert(foodItem: FoodEntity) { //route data
        foodDao.insertFoodItem(foodItem)
    }

    suspend fun deleteFood(food: FoodEntity) {
        foodDao.deleteFood(food)
    }
}