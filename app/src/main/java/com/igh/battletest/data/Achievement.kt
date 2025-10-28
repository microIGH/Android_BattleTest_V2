package com.igh.battletest.data

import androidx.compose.ui.graphics.vector.ImageVector
import com.igh.battletest.manager.AchievementType
import com.igh.battletest.manager.AchievementCriteria
import java.util.Date

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val points: Int,
    val iconVector: ImageVector,
    val isUnlocked: Boolean = false,
    val earnedDate: Date? = null,

    // Nuevas propiedades para criterios
    val type: AchievementType? = null,
    val criteria: AchievementCriteria? = null,

    // Criterios específicos
    val maxSeconds: Double? = null,
    val minAccuracy: Double? = null,
    val requiredAccuracy: Double? = null,
    val minQuestions: Int? = null,
    val requiredStreak: Int? = null,
    val requiredDays: Int? = null,
    val requiredSubjects: Int? = null,
    val requiredPerfectQuizzes: Int? = null,
    val allowRetries: Boolean? = null,
    val maxSecondsPerQuestion: Double? = null
)