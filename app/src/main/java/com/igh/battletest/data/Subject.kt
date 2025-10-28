package com.igh.battletest.data

import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class Subject(
    val id: String,
    val name: String,
    val color: Color,
    val iconVector: ImageVector,
    val quizzes: List<Quiz> = emptyList(),
    val completionPercentage: Int = 0
)