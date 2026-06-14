package com.example.a210139_fatin_drnazatul_project2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.a210139_fatin_drnazatul_project2.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [FoodEntity::class], version = 1, exportSchema = false)
abstract class ResQBiteDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao

    companion object {
        @Volatile
        private var INSTANCE: ResQBiteDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ResQBiteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ResQBiteDatabase::class.java,
                    "resqbite_database"
                )
                    // mock values when the database is built for the first time
                    .addCallback(ResQBiteDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class ResQBiteDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val foodDao = database.foodDao()
                    // add standard local public entries automatically
                    foodDao.insertInitialFoodItems(
                        listOf(
                            FoodEntity(title = "Extra Croissants", distance = "1.2km", user = "NearBakery", location = "Bangi", imageRes = R.drawable.croissant, isUserContribution = false),
                            FoodEntity(title = "Tin tuna", distance = "0.8km", user = "Mira", location = "KIY", imageRes = R.drawable.canned_tuna, isUserContribution = false),
                            FoodEntity(title = "Roti Gardenia Pandan", distance = "1.0km", user = "Amar", location = "KPZ", imageRes = R.drawable.roti, isUserContribution = false),
                            FoodEntity(title = "Nasi Ayam", distance = "0.3km", user = "Qila", location = "KUO", imageRes = R.drawable.nasiayam, isUserContribution = false)
                        )
                    )
                }
            }
        }
    }
}