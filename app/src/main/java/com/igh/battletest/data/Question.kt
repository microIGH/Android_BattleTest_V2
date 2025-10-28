package com.igh.battletest.data

data class Question(
    val id: String = "",
    val text: String,
    val options: List<String>,
    val correctAnswer: String,
    val subjectId: String = ""
)