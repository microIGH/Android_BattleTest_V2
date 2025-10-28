package com.igh.battletest.data.dto

data class QuizDto(
    val id: String,
    val title: String,
    val subjectId: String,
    val language: String,
    val questions: List<QuestionDto>,
    val minQuestionsNumber: Int
)