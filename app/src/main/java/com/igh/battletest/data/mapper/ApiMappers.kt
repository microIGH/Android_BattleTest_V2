package com.igh.battletest.data.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.igh.battletest.data.*
import com.igh.battletest.data.dto.*
import java.util.UUID

// Question: DTO → Domain
fun QuestionDto.toQuestion(subjectId: String): Question {
    return Question(
        id = UUID.randomUUID().toString(),
        text = question,
        options = options,
        correctAnswer = correctAnswer,
        subjectId = subjectId
    )
}

// Quiz: DTO → Domain
fun QuizDto.toQuiz(): Quiz {
    return Quiz(
        id = id,
        title = title,
        subjectId = subjectId,
        language = language,
        questions = questions.map { it.toQuestion(subjectId) },
        isCompleted = false,
        bestScore = 0,
        questionsCount = minQuestionsNumber
    )
}

// Subject: DTO → Domain
fun SubjectDto.toSubject(): Subject {
    return Subject(
        id = id,
        name = name,
        color = parseColor(color),
        iconVector = parseIcon(icon),
        quizzes = quizzes.map { it.toQuiz() },
        completionPercentage = 0
    )
}

// Helper: Hex String → Compose Color
private fun parseColor(hexColor: String): Color {
    return try {
        val colorInt = android.graphics.Color.parseColor(hexColor)
        Color(colorInt)
    } catch (e: Exception) {
        Color(0xFF2196F3)  // Default blue
    }
}

// Helper: Emoji → Material Icon (usando iconos disponibles)
private fun parseIcon(emoji: String): ImageVector {
    return when(emoji) {
        "🔢" -> Icons.Default.Star
        "🧪" -> Icons.Default.Build
        "⚛️" -> Icons.Default.Settings
        "🧬" -> Icons.Default.Favorite
        else -> Icons.Default.Info
    }
}