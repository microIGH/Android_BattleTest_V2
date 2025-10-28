package com.igh.battletest.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.igh.battletest.R
import com.igh.battletest.viewmodel.ProfileViewModel
import com.igh.battletest.data.Subject
import com.igh.battletest.manager.QuizDataManager

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val student by viewModel.student.collectAsState()
    val subjectProgress by viewModel.subjectProgress.collectAsState()

    if (student == null) {
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
        item { UserProfileCard(student!!) }
        item { SubjectProgressSection(QuizDataManager.subjects, subjectProgress) }
        item { StatisticsSection(student!!) }
    }
}

@Composable
fun UserProfileCard(student: com.igh.battletest.data.Student) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = student.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = student.email,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.level, student.level),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.level_points, student.level, student.totalPoints),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun SubjectProgressSection(subjects: List<Subject>, progress: Map<String, Int>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.progress_by_subject),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            subjects.forEach { subject ->
                // Mapear IDs español → inglés para buscar progreso
                val progressKey = when(subject.id) {
                    "biologia" -> "biology"
                    "fisica" -> "physics"
                    "quimica" -> "chemistry"
                    "matematicas" -> "mathematics"
                    else -> subject.id
                }
                val subjectProgress = progress[progressKey] ?: progress[subject.id] ?: 0
                SubjectProgressItem(subject, subjectProgress)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SubjectProgressItem(subject: Subject, progress: Int) {
    // Mapear IDs a strings localizados
    val localizedName = when(subject.id) {
        "biologia" -> stringResource(R.string.biology)
        "fisica" -> stringResource(R.string.physics)
        "quimica" -> stringResource(R.string.chemistry)
        "matematicas" -> stringResource(R.string.mathematics)
        else -> subject.name
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = localizedName,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$progress%",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = progress / 100f,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun StatisticsSection(student: com.igh.battletest.data.Student) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.achievements),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            StatisticRow(
                stringResource(R.string.streak_day, student.currentStreak),
                stringResource(R.string.activity_days)
            )
            StatisticRow(
                stringResource(R.string.streak_day, student.bestStreak),
                stringResource(R.string.activity_days)
            )
            StatisticRow(
                stringResource(R.string.completed),
                student.completedQuizzes.size.toString()
            )
            StatisticRow(
                stringResource(R.string.subjects_title),
                student.subjectsExplored.size.toString()
            )
        }
    }
}

@Composable
fun StatisticRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Text(text = value)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelector() {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage by remember {
        mutableStateOf(com.igh.battletest.manager.LanguageManager.getCurrentLanguage(context))
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Idioma / Language / Langue",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = when(selectedLanguage) {
                        "es" -> "Español"
                        "en" -> "English"
                        "fr" -> "Français"
                        else -> "Español"
                    },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    com.igh.battletest.manager.LanguageManager.getAvailableLanguages().forEach { (code, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                selectedLanguage = code
                                com.igh.battletest.manager.LanguageManager.setLocale(context, code)
                                expanded = false
                                // Mostrar mensaje que debe reiniciar manualmente
                                android.widget.Toast.makeText(
                                    context,
                                    "Reinicia la app para ver cambios completos",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                }
            }
        }
    }
}