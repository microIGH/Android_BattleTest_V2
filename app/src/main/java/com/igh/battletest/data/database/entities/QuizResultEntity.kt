package com.igh.battletest.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey val id: String,
    val quizId: String,
    val subjectId: String,
    val score: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val timeSpent: Long,
    val completedAt: Date,
    val studentId: String
)