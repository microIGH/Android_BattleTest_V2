package com.igh.battletest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.igh.battletest.R
import com.igh.battletest.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    val student by viewModel.student.collectAsState()

    // Refresh cuando regresa a pantalla
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    if (student == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (student == null) {
        // Loading state
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { UserGreeting(student!!.name) }
        item { TotalPointsCard(student!!.totalPoints) }
        item { ProgressLevelCard(student!!.level, viewModel.progressToNext) }
        item { MetricsRow(viewModel) }
        item { WeeklyStreakCard(student!!.currentStreak) }
    }
}

@Composable
fun UserGreeting(name: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.hello_user, name),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = stringResource(R.string.continue_learning),  // CAMBIAR
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Composable
fun TotalPointsCard(points: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.points),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = points.toString(),
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@Composable
fun ProgressLevelCard(level: Int, progress: Float) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.level, level),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MetricsRow(viewModel: DashboardViewModel) {
    val student = viewModel.student.collectAsState().value

    val quizzesCompleted = student?.completedQuizzes?.size ?: 0
    val averageScore = student?.quizResults?.let { results ->
        if (results.isEmpty()) 0
        else results.map { it.scorePercentage }.average().toInt()
    } ?: 0

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Card(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.quizzes))
                Text(quizzesCompleted.toString())
            }
        }
        Card(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(R.string.average))
                Text("$averageScore%")
            }
        }
    }
}

@Composable
fun WeeklyStreakCard(streak: Int) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.activity_streak, streak),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.activity_days),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}