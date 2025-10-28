package com.igh.battletest.manager

import com.igh.battletest.data.Quiz
import com.igh.battletest.data.Question
import com.igh.battletest.data.QuizResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import android.content.Context
import com.igh.battletest.data.repository.StudentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class QuizEngineResult {
    CONTINUE_QUIZ,
    RESTART_REQUIRED,
    QUIZ_COMPLETED
}

object QuizEngine {
    private val _currentQuiz = MutableStateFlow<Quiz?>(null)
    val currentQuiz: StateFlow<Quiz?> = _currentQuiz.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    private val _penaltyCount = MutableStateFlow(0)
    val penaltyCount: StateFlow<Int> = _penaltyCount.asStateFlow()

    private val _timeStarted = MutableStateFlow(0L)
    private val _selectedAnswers = MutableStateFlow<MutableList<Int>>(mutableListOf())
    private val _processedQuestions = MutableStateFlow<MutableList<Boolean>>(mutableListOf())

    private var studentRepository: StudentRepository? = null

    fun initialize(context: Context) {
        studentRepository = StudentRepository(context)
    }

    fun startQuiz(quiz: Quiz) {
        _currentQuiz.value = quiz
        _currentQuestionIndex.value = 0
        _score.value = 0
        _penaltyCount.value = 0
        _timeStarted.value = System.currentTimeMillis()

        // Inicializar arrays con tamaño fijo
        _selectedAnswers.value = MutableList(quiz.questions.size) { -1 }
        _processedQuestions.value = MutableList(quiz.questions.size) { false }
    }

    fun getCurrentQuestion(): Question? {
        return _currentQuiz.value?.questions?.getOrNull(_currentQuestionIndex.value)
    }

    fun processAnswer(selectedAnswerIndex: Int): QuizEngineResult {
        val currentQuestion = getCurrentQuestion() ?: return QuizEngineResult.CONTINUE_QUIZ

        // Convertir índice a valor para comparar
        val correctAnswerIndex = currentQuestion.options.indexOf(currentQuestion.correctAnswer)
        val currentIndex = _currentQuestionIndex.value

        if (!_processedQuestions.value[currentIndex]) {
            // Primera vez respondiendo esta pregunta
            _processedQuestions.value[currentIndex] = true
            _selectedAnswers.value[currentIndex] = selectedAnswerIndex

            if (selectedAnswerIndex == correctAnswerIndex) {
                _score.value += 1
            } else {
                _penaltyCount.value += 1
            }
        } else {
            // Cambiando respuesta existente
            val previousAnswer = _selectedAnswers.value[currentIndex]

            if (previousAnswer != selectedAnswerIndex) {
                if (previousAnswer == correctAnswerIndex) {
                    _score.value -= 1  // Quitar punto si anterior era correcta
                }

                if (selectedAnswerIndex == correctAnswerIndex) {
                    _score.value += 1  // Dar punto si nueva es correcta
                } else {
                    _penaltyCount.value += 1  // Penalizar si nueva es incorrecta
                }

                _selectedAnswers.value[currentIndex] = selectedAnswerIndex
            }
        }

        if (_penaltyCount.value > 3) {
            return QuizEngineResult.RESTART_REQUIRED
        }

        return QuizEngineResult.CONTINUE_QUIZ
    }

    fun processBackNavigation() {
        if (_currentQuestionIndex.value > 0) {
            val currentIndex = _currentQuestionIndex.value
            val previouslySelectedAnswer = _selectedAnswers.value[currentIndex]

            if (_processedQuestions.value[currentIndex]) {
                _processedQuestions.value[currentIndex] = false

                val currentQuestion = getCurrentQuestion()
                val correctAnswerIndex = currentQuestion?.options?.indexOf(currentQuestion.correctAnswer) ?: -1

                if (previouslySelectedAnswer == correctAnswerIndex) {
                    _score.value -= 1
                }
            }

            _currentQuestionIndex.value -= 1
        }
    }

    fun moveToNextQuestion(): QuizEngineResult {
        val nextIndex = _currentQuestionIndex.value + 1
        val totalQuestions = _currentQuiz.value?.questions?.size ?: 0

        if (nextIndex < totalQuestions) {
            _currentQuestionIndex.value = nextIndex
            return QuizEngineResult.CONTINUE_QUIZ
        } else {
            return QuizEngineResult.QUIZ_COMPLETED
        }
    }

    fun canNavigateBack(): Boolean {
        return _currentQuestionIndex.value > 0
    }

    fun canNavigateForward(): Boolean {
        val currentIndex = _currentQuestionIndex.value
        val totalQuestions = _currentQuiz.value?.questions?.size ?: 0
        return currentIndex < totalQuestions - 1
    }

    fun getPreviousAnswer(): Int? {
        val currentIndex = _currentQuestionIndex.value
        val answer = _selectedAnswers.value.getOrNull(currentIndex)
        return if (answer != null && answer != -1) answer else null
    }

    fun getShuffledOptions(): List<String> {
        return getCurrentQuestion()?.options?.shuffled() ?: emptyList()
    }

    fun finishQuiz(): QuizResult? {
        val quiz = _currentQuiz.value ?: return null
        val timeElapsed = System.currentTimeMillis() - _timeStarted.value
        val scorePercentage = (_score.value.toFloat() / quiz.questions.size * 100)
        val completionTime = timeElapsed / 1000.0

        val result = QuizResult(
            quizId = quiz.id,
            subjectId = quiz.subjectId,
            score = _score.value,
            totalQuestions = quiz.questions.size,
            scorePercentage = scorePercentage,
            completionTime = completionTime,
            timeElapsed = timeElapsed,
            errorsCount = _penaltyCount.value,
            pointsEarned = calculatePoints(_score.value, quiz.questions.size, timeElapsed),
            timestamp = System.currentTimeMillis()
        )

        // Guardar en Room
        studentRepository?.let { repo ->
            CoroutineScope(Dispatchers.IO).launch {
                repo.addQuizResult(result)
            }
        }

        return result
    }

    private fun calculatePoints(score: Int, totalQuestions: Int, timeElapsed: Long): Int {
        val basePoints = score * 2
        val bonusPoints = if (score == totalQuestions) 10 else 0

        // Bonus por velocidad (menos de 30 segundos por pregunta en promedio)
        val avgTimePerQuestion = timeElapsed / totalQuestions
        val speedBonus = if (avgTimePerQuestion < 30000) 5 else 0

        return basePoints + bonusPoints + speedBonus
    }

    fun restartQuiz() {
        val quiz = _currentQuiz.value ?: return
        startQuiz(quiz)
    }

    fun reset() {
        _currentQuiz.value = null
        _currentQuestionIndex.value = 0
        _score.value = 0
        _penaltyCount.value = 0
        _timeStarted.value = 0L
        _selectedAnswers.value = mutableListOf<Int>()
        _processedQuestions.value = mutableListOf()
    }
}