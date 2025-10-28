package com.igh.battletest.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.igh.battletest.data.Quiz
import com.igh.battletest.data.Question
import com.igh.battletest.data.QuizResult

class QuizViewModel : ViewModel() {
    private val _currentQuiz = MutableStateFlow<Quiz?>(null)
    val currentQuiz: StateFlow<Quiz?> = _currentQuiz.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _timeElapsed = MutableStateFlow(0L)
    val timeElapsed: StateFlow<Long> = _timeElapsed.asStateFlow()

    fun startQuiz(quiz: Quiz) {
        _currentQuiz.value = quiz
        _currentQuestionIndex.value = 0
        _score.value = 0
        _timeElapsed.value = 0L
    }

    fun answerQuestion(selectedAnswer: Int): Boolean {
        val question = _currentQuiz.value?.questions?.get(_currentQuestionIndex.value)

        // Convertir índice a valor para comparar
        val correctAnswerIndex = question?.options?.indexOf(question.correctAnswer) ?: -1
        val isCorrect = correctAnswerIndex == selectedAnswer

        if (isCorrect) {
            _score.value += 1
        }
        return isCorrect
    }

    fun nextQuestion() {
        _currentQuestionIndex.value += 1
    }

    fun finishQuiz(): QuizResult {
        val quiz = _currentQuiz.value!!
        return QuizResult(
            quizId = quiz.id,
            subjectId = quiz.subjectId,  // AGREGAR
            score = _score.value,
            totalQuestions = quiz.questions.size,
            scorePercentage = (_score.value.toFloat() / quiz.questions.size * 100),  // AGREGAR
            completionTime = _timeElapsed.value / 1000.0,  // AGREGAR
            timeElapsed = _timeElapsed.value,
            errorsCount = quiz.questions.size - _score.value,
            pointsEarned = _score.value * 2,
            timestamp = System.currentTimeMillis()
        )
    }
}