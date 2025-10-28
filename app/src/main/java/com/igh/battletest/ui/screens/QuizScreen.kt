package com.igh.battletest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.igh.battletest.R
import com.igh.battletest.data.Question
import com.igh.battletest.manager.QuizEngine
import com.igh.battletest.manager.QuizEngineResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onNavigateBack: () -> Unit,
    onQuizComplete: (quizId: String, score: Int, total: Int) -> Unit
) {
    val currentQuestion by QuizEngine.currentQuiz.collectAsState()
    val questionIndex by QuizEngine.currentQuestionIndex.collectAsState()
    val score by QuizEngine.score.collectAsState()
    val penaltyCount by QuizEngine.penaltyCount.collectAsState()

    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    val question = QuizEngine.getCurrentQuestion()
    val totalQuestions = currentQuestion?.questions?.size ?: 0

    // Restaurar respuesta previa al cambiar de pregunta
    LaunchedEffect(questionIndex) {
        selectedAnswer = QuizEngine.getPreviousAnswer()
        showFeedback = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.question, questionIndex + 1, totalQuestions))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        QuizEngine.reset()
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Progreso y puntuación
            Column {
                LinearProgressIndicator(
                    progress = (questionIndex + 1).toFloat() / totalQuestions,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.points_label, score, totalQuestions))
                    Text(
                        stringResource(R.string.errors_label, penaltyCount, 3),
                        color = if (penaltyCount > 2) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Pregunta y opciones
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                question?.let { q ->
                    Text(
                        text = q.text,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    q.options.forEachIndexed { index, option ->
                        OptionButton(
                            text = option,
                            isSelected = selectedAnswer == index,
                            isCorrect = if (showFeedback) {
                                q.options.indexOf(q.correctAnswer) == index
                            } else null,
                            onClick = {
                                if (!showFeedback) {
                                    selectedAnswer = index
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (showFeedback) {
                        Spacer(modifier = Modifier.height(16.dp))
                        FeedbackCard(isCorrect = isCorrect)
                    }
                }
            }

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botón Anterior
                OutlinedButton(
                    onClick = {
                        if (QuizEngine.canNavigateBack()) {
                            QuizEngine.processBackNavigation()
                            showFeedback = false
                        }
                    },
                    enabled = QuizEngine.canNavigateBack()
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.previous))
                }

                // Botón Validar/Siguiente
                Button(
                    onClick = {
                        selectedAnswer?.let { answer ->
                            if (!showFeedback) {
                                // Validar respuesta
                                val result = QuizEngine.processAnswer(answer)
                                val correctIndex = question?.options?.indexOf(question.correctAnswer) ?: -1
                                isCorrect = answer == correctIndex
                                showFeedback = true

                                if (result == QuizEngineResult.RESTART_REQUIRED) {
                                    // Reiniciar quiz por exceso de errores
                                    QuizEngine.restartQuiz()
                                    selectedAnswer = null
                                    showFeedback = false
                                }
                            } else {
                                // Siguiente pregunta
                                val result = QuizEngine.moveToNextQuestion()

                                if (result == QuizEngineResult.QUIZ_COMPLETED) {
                                    val quizResult = QuizEngine.finishQuiz()
                                    quizResult?.let {
                                        onQuizComplete(it.quizId, it.score, it.totalQuestions)
                                    }
                                    QuizEngine.reset()
                                } else {
                                    showFeedback = false
                                    selectedAnswer = QuizEngine.getPreviousAnswer()
                                }
                            }
                        }
                    },
                    enabled = selectedAnswer != null
                ) {
                    Text(
                        if (showFeedback)
                            stringResource(R.string.next)
                        else
                            stringResource(R.string.validate)
                    )
                    if (showFeedback) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun OptionButton(
    text: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCorrect == true -> MaterialTheme.colorScheme.primaryContainer
        isCorrect == false && isSelected -> MaterialTheme.colorScheme.errorContainer
        isSelected -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isCorrect == true -> MaterialTheme.colorScheme.primary
        isCorrect == false && isSelected -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.outline
    }

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun FeedbackCard(isCorrect: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(
            text = if (isCorrect) "¡Correcto! ✓" else "Incorrecto ✗",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}