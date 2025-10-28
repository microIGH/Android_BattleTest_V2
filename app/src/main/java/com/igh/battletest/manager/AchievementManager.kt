package com.igh.battletest.manager

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.igh.battletest.data.Achievement
import com.igh.battletest.data.QuizResult
import com.igh.battletest.data.Student
import java.util.*

enum class AchievementType {
    VELOCITY, PRECISION, CONSISTENCY, EXPLORER, PERFECTIONIST
}

enum class AchievementCriteria {
    VELOCITY,
    PRECISION,
    PRECISION_STREAK,
    PRECISION_ALL_SUBJECTS,
    CONSISTENCY,
    EXPLORER,
    PERFECTIONIST,
    PERFECT_ABSOLUTE
}

object AchievementManager {
    private const val MAX_DAILY_ACHIEVEMENT_POINTS = 50
    private const val MAX_DAILY_QUIZ_COUNT = 20

    val allAchievements = listOf(
        // Velocidad (3)
        Achievement(
            id = "sonic",
            title = "Sonic",
            description = "Responde en menos de 15 segundos por pregunta con 70% aciertos",
            category = "Velocidad",
            points = 10,
            iconVector = Icons.Default.DateRange,
            type = AchievementType.VELOCITY,
            criteria = AchievementCriteria.VELOCITY,
            maxSeconds = 15.0,
            minAccuracy = 70.0
        ),
        Achievement(
            id = "flash",
            title = "Flash",
            description = "Responde en menos de 10 segundos por pregunta con 70% aciertos",
            category = "Velocidad",
            points = 15,
            iconVector = Icons.Default.Done,
            type = AchievementType.VELOCITY,
            criteria = AchievementCriteria.VELOCITY,
            maxSeconds = 10.0,
            minAccuracy = 70.0
        ),
        Achievement(
            id = "lightning",
            title = "Lightning",
            description = "Responde en menos de 7 segundos por pregunta con 70% aciertos",
            category = "Velocidad",
            points = 20,
            iconVector = Icons.Default.Add,
            type = AchievementType.VELOCITY,
            criteria = AchievementCriteria.VELOCITY,
            maxSeconds = 7.0,
            minAccuracy = 70.0
        ),

        // Precisión (3)
        Achievement(
            id = "sniper",
            title = "Sniper",
            description = "Obtén 100% en un quiz de mínimo 5 preguntas",
            category = "Precisión",
            points = 10,
            iconVector = Icons.Default.Warning,
            type = AchievementType.PRECISION,
            criteria = AchievementCriteria.PRECISION,
            requiredAccuracy = 100.0,
            minQuestions = 5
        ),
        Achievement(
            id = "eagle_eye",
            title = "Eagle Eye",
            description = "Obtén 100% en 3 quizzes consecutivos",
            category = "Precisión",
            points = 15,
            iconVector = Icons.Default.Face,
            type = AchievementType.PRECISION,
            criteria = AchievementCriteria.PRECISION_STREAK,
            requiredStreak = 3,
            requiredAccuracy = 100.0
        ),
        Achievement(
            id = "perfect_shot",
            title = "Perfect Shot",
            description = "Obtén 100% en todas las materias",
            category = "Precisión",
            points = 20,
            iconVector = Icons.Default.Star,
            type = AchievementType.PRECISION,
            criteria = AchievementCriteria.PRECISION_ALL_SUBJECTS,
            requiredAccuracy = 100.0
        ),

        // Consistencia (3)
        Achievement(
            id = "steady",
            title = "Steady",
            description = "Estudia 3 días consecutivos",
            category = "Consistencia",
            points = 10,
            iconVector = Icons.Default.DateRange,
            type = AchievementType.CONSISTENCY,
            criteria = AchievementCriteria.CONSISTENCY,
            requiredDays = 3
        ),
        Achievement(
            id = "reliable",
            title = "Reliable",
            description = "Estudia 7 días consecutivos",
            category = "Consistencia",
            points = 15,
            iconVector = Icons.Default.List,
            type = AchievementType.CONSISTENCY,
            criteria = AchievementCriteria.CONSISTENCY,
            requiredDays = 7
        ),
        Achievement(
            id = "unstoppable",
            title = "Unstoppable",
            description = "Estudia 14 días consecutivos",
            category = "Consistencia",
            points = 20,
            iconVector = Icons.Default.ArrowForward,
            type = AchievementType.CONSISTENCY,
            criteria = AchievementCriteria.CONSISTENCY,
            requiredDays = 14
        ),

        // Explorador (3)
        Achievement(
            id = "curious",
            title = "Curious",
            description = "Explora 2 materias diferentes",
            category = "Explorador",
            points = 5,
            iconVector = Icons.Default.ExitToApp,
            type = AchievementType.EXPLORER,
            criteria = AchievementCriteria.EXPLORER,
            requiredSubjects = 2
        ),
        Achievement(
            id = "explorer",
            title = "Explorer",
            description = "Explora 3 materias diferentes",
            category = "Explorador",
            points = 10,
            iconVector = Icons.Default.Home,
            type = AchievementType.EXPLORER,
            criteria = AchievementCriteria.EXPLORER,
            requiredSubjects = 3
        ),
        Achievement(
            id = "scholar",
            title = "Scholar",
            description = "Explora todas las materias",
            category = "Explorador",
            points = 15,
            iconVector = Icons.Default.Create,
            type = AchievementType.EXPLORER,
            criteria = AchievementCriteria.EXPLORER,
            requiredSubjects = 4
        ),

        // Perfeccionista (3)
        Achievement(
            id = "perfectionist",
            title = "Perfectionist",
            description = "Obtén 100% en 5 quizzes diferentes",
            category = "Perfeccionista",
            points = 25,
            iconVector = Icons.Default.Lock,
            type = AchievementType.PERFECTIONIST,
            criteria = AchievementCriteria.PERFECTIONIST,
            requiredPerfectQuizzes = 5,
            allowRetries = false
        ),
        Achievement(
            id = "master",
            title = "Master",
            description = "Obtén 100% en 10 quizzes diferentes",
            category = "Perfeccionista",
            points = 30,
            iconVector = Icons.Default.AccountCircle,
            type = AchievementType.PERFECTIONIST,
            criteria = AchievementCriteria.PERFECTIONIST,
            requiredPerfectQuizzes = 10,
            allowRetries = false
        ),
        Achievement(
            id = "legend",
            title = "Legend",
            description = "Obtén 100% en menos de 5 segundos por pregunta",
            category = "Perfeccionista",
            points = 50,
            iconVector = Icons.Default.Favorite,
            type = AchievementType.PERFECTIONIST,
            criteria = AchievementCriteria.PERFECT_ABSOLUTE,
            requiredAccuracy = 100.0,
            maxSecondsPerQuestion = 5.0
        )
    )

    fun evaluateAchievements(result: QuizResult, student: Student): List<Achievement> {
        val newAchievements = mutableListOf<Achievement>()

        if (!canEarnMoreAchievements(student)) {
            return newAchievements
        }

        // Evaluar cada tipo de achievement
        evaluateVelocityAchievements(result, student)?.let { newAchievements.add(it) }
        evaluatePrecisionAchievements(result, student)?.let { newAchievements.add(it) }
        evaluateConsistencyAchievements(student)?.let { newAchievements.add(it) }
        evaluateExplorerAchievements(result, student)?.let { newAchievements.add(it) }
        evaluatePerfectionistAchievements(result, student)?.let { newAchievements.add(it) }

        return newAchievements
    }

    private fun canEarnMoreAchievements(student: Student): Boolean {
        // Verificar límite diario de puntos
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val lastAchievementDay = student.lastAchievementDate?.let { date ->
            Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }

        if (lastAchievementDay == today && student.achievementPointsToday >= MAX_DAILY_ACHIEVEMENT_POINTS) {
            return false
        }

        if (student.dailyQuizCount > MAX_DAILY_QUIZ_COUNT) {
            return false
        }

        return true
    }

    private fun hasEarnedAchievement(achievementId: String, student: Student): Boolean {
        return student.achievements.any { it.id == achievementId && it.isUnlocked }
    }

    private fun canEarnVelocityAchievement(quizId: String, student: Student): Boolean {
        val velocityAchievementsForQuiz = student.quizResults.filter { result ->
            result.quizId == quizId && hasVelocityAchievementForResult(result, student.getEarnedAchievements())
        }
        return velocityAchievementsForQuiz.isEmpty()
    }

    private fun hasVelocityAchievementForResult(result: QuizResult, achievements: List<Achievement>): Boolean {
        val velocityAchievements = achievements.filter { it.type == AchievementType.VELOCITY }

        for (achievement in velocityAchievements) {
            if (achievement.criteria == AchievementCriteria.VELOCITY) {
                if (result.averageTimePerQuestion <= achievement.maxSeconds!!) {
                    return true
                }
            }
        }
        return false
    }

    private fun evaluateVelocityAchievements(result: QuizResult, student: Student): Achievement? {
        if (result.scorePercentage < 70.0) return null
        if (result.totalQuestions < 5) return null
        if (!canEarnVelocityAchievement(result.quizId, student)) return null
        if (result.averageTimePerQuestion < 5.0) return null // Anti-bot

        val velocityAchievements = allAchievements.filter { it.type == AchievementType.VELOCITY }
            .sortedByDescending { it.points }

        for (achievement in velocityAchievements) {
            if (hasEarnedAchievement(achievement.id, student)) continue

            if (result.averageTimePerQuestion <= achievement.maxSeconds!! &&
                result.scorePercentage >= achievement.minAccuracy!!) {
                return achievement
            }
        }

        return null
    }

    private fun evaluatePrecisionAchievements(result: QuizResult, student: Student): Achievement? {
        if (result.scorePercentage != 100.0f) return null

        val precisionAchievements = allAchievements.filter { it.type == AchievementType.PRECISION }

        for (achievement in precisionAchievements) {
            if (hasEarnedAchievement(achievement.id, student)) continue

            when (achievement.criteria) {
                AchievementCriteria.PRECISION -> {
                    if (result.scorePercentage >= achievement.requiredAccuracy!! &&
                        result.totalQuestions >= achievement.minQuestions!! &&
                        canEarnPrecisionAchievement(result.quizId, student)) {
                        return achievement
                    }
                }
                AchievementCriteria.PRECISION_STREAK -> {
                    if (checkPrecisionStreak(achievement.requiredStreak!!, achievement.requiredAccuracy!!, student)) {
                        return achievement
                    }
                }
                AchievementCriteria.PRECISION_ALL_SUBJECTS -> {
                    if (checkPrecisionAllSubjects(achievement.requiredAccuracy!!, student)) {
                        return achievement
                    }
                }
                else -> continue
            }
        }

        return null
    }

    private fun canEarnPrecisionAchievement(quizId: String, student: Student): Boolean {
        val oneHourAgo = Date(System.currentTimeMillis() - 3600000)
        val recentSameQuiz = student.quizResults.filter {
            it.quizId == quizId && it.date.after(oneHourAgo)
        }
        return recentSameQuiz.size <= 1
    }

    private fun checkPrecisionStreak(requiredStreak: Int, requiredAccuracy: Double, student: Student): Boolean {
        val recentResults = student.quizResults.takeLast(requiredStreak)
        if (recentResults.size < requiredStreak) return false

        return recentResults.all { it.scorePercentage >= requiredAccuracy }
    }

    private fun checkPrecisionAllSubjects(requiredAccuracy: Double, student: Student): Boolean {
        val subjectIds = listOf("biology", "physics", "chemistry", "mathematics")

        return subjectIds.all { subjectId ->
            student.quizResults.any { result ->
                result.subjectId == subjectId && result.scorePercentage >= requiredAccuracy
            }
        }
    }

    private fun evaluateConsistencyAchievements(student: Student): Achievement? {
        val consistencyAchievements = allAchievements.filter { it.type == AchievementType.CONSISTENCY }

        for (achievement in consistencyAchievements) {
            if (hasEarnedAchievement(achievement.id, student)) continue

            if (student.currentStreak >= achievement.requiredDays!! && canEarnStreakReward(student)) {
                return achievement
            }
        }

        return null
    }

    private fun canEarnStreakReward(student: Student): Boolean {
        val lastReward = student.lastStreakRewardDate ?: return true

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val lastRewardDay = Calendar.getInstance().apply {
            time = lastReward
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return today.after(lastRewardDay)
    }

    private fun evaluateExplorerAchievements(result: QuizResult, student: Student): Achievement? {
        val explorerAchievements = allAchievements.filter { it.type == AchievementType.EXPLORER }

        for (achievement in explorerAchievements) {
            if (hasEarnedAchievement(achievement.id, student)) continue

            if (student.subjectsExploredCount >= achievement.requiredSubjects!!) {
                return achievement
            }
        }

        return null
    }

    private fun evaluatePerfectionistAchievements(result: QuizResult, student: Student): Achievement? {
        if (result.scorePercentage != 100.0f) return null

        val perfectionistAchievements = allAchievements.filter { it.type == AchievementType.PERFECTIONIST }

        for (achievement in perfectionistAchievements) {
            if (hasEarnedAchievement(achievement.id, student)) continue

            when (achievement.criteria) {
                AchievementCriteria.PERFECTIONIST -> {
                    if (checkPerfectionistCriteria(achievement.requiredPerfectQuizzes!!, achievement.allowRetries!!, student)) {
                        return achievement
                    }
                }
                AchievementCriteria.PERFECT_ABSOLUTE -> {
                    if (result.scorePercentage >= achievement.requiredAccuracy!! &&
                        result.averageTimePerQuestion <= achievement.maxSecondsPerQuestion!!) {
                        return achievement
                    }
                }
                else -> continue
            }
        }

        return null
    }

    private fun checkPerfectionistCriteria(requiredPerfectQuizzes: Int, allowRetries: Boolean, student: Student): Boolean {
        return if (allowRetries) {
            val perfectResults = student.quizResults.filter { it.scorePercentage == 100.0f }
            perfectResults.size >= requiredPerfectQuizzes
        } else {
            val perfectQuizIds = student.quizResults.filter { it.scorePercentage == 100.0f }.map { it.quizId }.toSet()
            perfectQuizIds.size >= requiredPerfectQuizzes
        }
    }

    fun getAchievementProgress(student: Student): Triple<Int, Int, Float> {
        val earned = student.getEarnedAchievements().size
        val total = allAchievements.size
        val percentage = earned.toFloat() / total * 100

        return Triple(earned, total, percentage)
    }

    fun getRecentAchievements(student: Student, limit: Int = 3): List<Achievement> {
        return student.getEarnedAchievements()
            .sortedByDescending { it.earnedDate ?: Date(0) }
            .take(limit)
    }
}