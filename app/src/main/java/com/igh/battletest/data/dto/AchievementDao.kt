package com.igh.battletest.data.database.dao

import androidx.room.*
import com.igh.battletest.data.database.entities.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {

    @Query("SELECT * FROM achievements WHERE studentId = :studentId")
    fun getAchievementsByStudent(studentId: String): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE studentId = :studentId AND isUnlocked = 1")
    fun getUnlockedAchievements(studentId: String): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(achievement: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Update
    suspend fun update(achievement: AchievementEntity)

    @Delete
    suspend fun delete(achievement: AchievementEntity)
}