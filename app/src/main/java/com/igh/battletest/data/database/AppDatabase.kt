package com.igh.battletest.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.igh.battletest.data.database.dao.*
import com.igh.battletest.data.database.entities.*

@Database(
    entities = [
        StudentEntity::class,
        AchievementEntity::class,
        QuizResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun achievementDao(): AchievementDao
    abstract fun quizResultDao(): QuizResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "battle_test_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}