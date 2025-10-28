package com.igh.battletest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.igh.battletest.ui.screens.DashboardScreen
import com.igh.battletest.ui.screens.SubjectsScreen
import com.igh.battletest.ui.screens.ProfileScreen
import androidx.compose.ui.res.stringResource
import com.igh.battletest.ui.screens.QuizScreen
import com.igh.battletest.ui.screens.ResultsScreen
import com.igh.battletest.ui.screens.QuizzesListScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.igh.battletest.viewmodel.SubjectsViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        com.igh.battletest.manager.UserProgressManager.initialize(this)
        com.igh.battletest.manager.QuizEngine.initialize(this)

        setContent {
            BattleTestTheme {
                BattleTestApp()
            }
        }
    }
}

@Preview
@Composable
fun BattleTestApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        NavigationHost(navController, paddingValues)
    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("dashboard") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Dashboard"
                )
            },
            label = { Text(stringResource(R.string.dashboard_title)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("subjects") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Subjects"
                )
            },
            label = { Text(stringResource(R.string.subjects_title)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("profile") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text(stringResource(R.string.profile_title)) }
        )
    }
}


@Composable
fun NavigationHost(navController: NavHostController, paddingValues: androidx.compose.foundation.layout.PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = "dashboard",
        modifier = Modifier.padding(paddingValues)
    ) {
        composable("dashboard") {
            DashboardScreen()
        }

        composable("subjects") {
            SubjectsScreen(
                onSubjectSelected = { subjectId ->
                    navController.navigate("quizzes/$subjectId")
                }
            )
        }

        composable("profile") {
            ProfileScreen()
        }

        composable("quizzes/{subjectId}") { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId")
            val viewModel: SubjectsViewModel = viewModel()
            val subjects by viewModel.subjects.collectAsState()

            val subject = subjects.firstOrNull { it.id == subjectId }
            val quizzes = subject?.quizzes ?: emptyList()

            subject?.let {
                QuizzesListScreen(
                    subject = it,
                    quizzes = quizzes,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onQuizSelected = { quiz ->
                        navController.navigate("quiz/${quiz.id}")
                    }
                )
            }
        }

        composable("quiz/{quizId}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")
            val viewModel: SubjectsViewModel = viewModel()
            val subjects by viewModel.subjects.collectAsState()

            // Buscar quiz en todos los subjects
            val quiz = subjects
                .flatMap { it.quizzes }
                .firstOrNull { it.id == quizId }

            quiz?.let {
                com.igh.battletest.manager.QuizEngine.startQuiz(it)

                QuizScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onQuizComplete = { quizId, score, total ->
                        navController.navigate("results/$quizId/$score/$total") {
                            popUpTo("subjects") { inclusive = false }
                        }
                    }
                )
            }
        }

        composable("results/{quizId}/{score}/{total}") { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
            val score = backStackEntry.arguments?.getString("score")?.toIntOrNull() ?: 0
            val total = backStackEntry.arguments?.getString("total")?.toIntOrNull() ?: 0

            ResultsScreen(
                quizId = quizId,
                score = score,
                totalQuestions = total,
                onNavigateHome = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
    }
}


@Composable
fun BattleTestTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}