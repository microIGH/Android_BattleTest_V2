package com.igh.battletest.manager

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import com.igh.battletest.data.Subject
import com.igh.battletest.data.Quiz
import com.igh.battletest.data.Question

object QuizDataManager {
    val subjects = listOf(
        Subject(
            id = "biologia",
            name = "Biología",
            color = Color(0xFF4CAF50),
            iconVector = Icons.Default.Star,
            completionPercentage = 0
        ),
        Subject(
            id = "fisica",
            name = "Física",
            color = Color(0xFF2196F3),
            iconVector = Icons.Default.Build,
            completionPercentage = 0
        ),
        Subject(
            id = "quimica",
            name = "Química",
            color = Color(0xFF9C27B0),
            iconVector = Icons.Default.Settings,
            completionPercentage = 0
        ),
        Subject(
            id = "matematicas",
            name = "Matemáticas",
            color = Color(0xFFFF9800),
            iconVector = Icons.Default.ThumbUp,
            completionPercentage = 0
        )
    )

    private val sampleQuestions = mapOf(
        "biologia" to listOf(
            Question(
                id = "bio_001",
                text = "¿Cuál es la unidad básica de la vida?",
                options = listOf("Átomo", "Célula", "Tejido", "Órgano"),
                correctAnswer = "Célula",  // ← Cambiado: valor en lugar de índice
                subjectId = "biologia"
            ),
            Question(
                id = "bio_002",
                text = "¿Qué proceso usan las plantas para producir energía?",
                options = listOf("Respiración", "Fotosíntesis", "Digestión", "Circulación"),
                correctAnswer = "Fotosíntesis",  // ← Cambiado: valor en lugar de índice
                subjectId = "biologia"
            )
        ),
        "fisica" to listOf(
            Question(
                id = "fis_001",
                text = "¿Cuál es la velocidad de la luz en el vacío?",
                options = listOf("300,000 km/s", "150,000 km/s", "450,000 km/s", "600,000 km/s"),
                correctAnswer = "300,000 km/s",  // ← Cambiado: valor en lugar de índice
                subjectId = "fisica"
            )
        )
    )

    fun getQuestionsForSubject(subjectId: String): List<Question> {
        return sampleQuestions[subjectId] ?: emptyList()
    }

    fun getQuizzesForSubject(subjectId: String): List<Quiz> {
        val questions = getQuestionsForSubject(subjectId)
        return if (questions.isNotEmpty()) {
            listOf(
                Quiz(
                    id = "${subjectId}_quiz_1",
                    title = "Quiz Básico",
                    subjectId = subjectId,
                    questions = questions,
                    questionsCount = questions.size
                )
            )
        } else emptyList()
    }
}