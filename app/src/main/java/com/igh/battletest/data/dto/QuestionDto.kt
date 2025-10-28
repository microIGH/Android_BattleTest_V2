package com.igh.battletest.data.dto

data class QuestionDto(
    val question: String,
    val options: List<String>,
    val correctAnswer: String
)