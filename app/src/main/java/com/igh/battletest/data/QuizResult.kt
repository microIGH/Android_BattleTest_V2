package com.igh.battletest.data

import java.util.Date

data class QuizResult(
    val quizId: String,
    val subjectId: String,
    val score: Int,
    val totalQuestions: Int,
    val scorePercentage: Float,
    val completionTime: Double, // En segundos para AchievementManager
    val timeElapsed: Long, // Milisegundos para UI
    val errorsCount: Int,
    val pointsEarned: Int,
    val timestamp: Long,
    val date: Date = Date(timestamp)
) {
    // Propiedades computadas para compatibilidad iOS
    val averageTimePerQuestion: Double
        get() = completionTime / totalQuestions.toDouble()
}