package com.igh.battletest.data.database.dao

import androidx.room.*
import com.igh.battletest.data.database.entities.QuizResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizResultDao {

    @Query("SELECT * FROM quiz_results WHERE studentId = :studentId ORDER BY completedAt DESC")
    fun getResultsByStudent(studentId: String): Flow<List<QuizResultEntity>>

    @Query("SELECT * FROM quiz_results WHERE studentId = :studentId AND subjectId = :subjectId")
    fun getResultsBySubject(studentId: String, subjectId: String): Flow<List<QuizResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(result: QuizResultEntity)

    @Query("DELETE FROM quiz_results WHERE studentId = :studentId")
    suspend fun deleteByStudent(studentId: String)
}