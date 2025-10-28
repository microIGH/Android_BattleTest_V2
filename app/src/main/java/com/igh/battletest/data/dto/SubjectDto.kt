package com.igh.battletest.data.dto

data class SubjectDto(
    val id: String,
    val name: String,
    val color: String,
    val icon: String,
    val quizzes: List<QuizDto>
)