package com.igh.battletest.data

data class Quiz(
    val id: String,
    val title: String,
    val subjectId: String,
    val language: String = "es",
    val questions: List<Question> = emptyList(),
    val isCompleted: Boolean = false,
    val bestScore: Int = 0,
    val questionsCount: Int = 0
)