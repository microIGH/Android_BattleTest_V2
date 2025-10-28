package com.igh.battletest.data

import java.util.*

data class Student(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val registrationDate: Long = System.currentTimeMillis(),

    var totalPoints: Int = 0,
    var level: Int = 1,
    var experiencePoints: Int = 0,

    var achievements: List<Achievement> = emptyList(),
    var lastAchievementDate: Date? = null,
    var achievementPointsToday: Int = 0,

    var currentStreak: Int = 0,
    var bestStreak: Int = 0,
    var lastActivityDate: Date? = null,
    var lastStreakRewardDate: Date? = null,

    var totalStudyTime: Long = 0L,
    var completedQuizzes: List<String> = emptyList(),
    var quizResults: List<QuizResult> = emptyList(),
    var subjectsExplored: Set<String> = emptySet(),

    var dailyQuizCount: Int = 0,
    var lastQuizDate: Date? = null
) {
    val progressToNextLevel: Float
        get() {
            val currentLevelExp = calculateExperienceForLevel(level)
            val nextLevelExp = calculateExperienceForLevel(level + 1)
            val progressExp = totalPoints - currentLevelExp
            val neededExp = nextLevelExp - currentLevelExp
            return if (neededExp > 0) progressExp.toFloat() / neededExp.toFloat() else 0f
        }

    val subjectsExploredCount: Int
        get() = subjectsExplored.size

    private fun calculateExperienceForLevel(level: Int): Int {
        if (level <= 1) return 0
        return ((level - 1) * (level - 1) * 75) + 150
    }

    fun getEarnedAchievements(): List<Achievement> {
        return achievements.filter { it.isUnlocked }
    }

    fun addAchievement(achievement: Achievement) {
        val updatedAchievement = achievement.copy(
            isUnlocked = true,
            earnedDate = Date()
        )
        achievements = achievements + updatedAchievement

        // Actualizar puntos
        totalPoints += achievement.points
        achievementPointsToday += achievement.points
        lastAchievementDate = Date()

        // Actualizar nivel si es necesario
        updateLevel()
    }

    private fun updateLevel() {
        val newLevel = calculateLevelFromPoints(totalPoints)
        if (newLevel > level) {
            level = newLevel
        }
    }

    private fun calculateLevelFromPoints(points: Int): Int {
        var level = 1
        while (calculateExperienceForLevel(level + 1) <= points) {
            level++
        }
        return level
    }

    fun addSubjectExplored(subjectId: String) {
        subjectsExplored = subjectsExplored + subjectId
    }

    fun addQuizResult(result: QuizResult) {
        quizResults = quizResults + result
        completedQuizzes = completedQuizzes + result.quizId
        addSubjectExplored(result.subjectId)

        // Actualizar contadores diarios
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val lastQuizDay = lastQuizDate?.let { date ->
            Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }

        if (lastQuizDay == today) {
            dailyQuizCount += 1
        } else {
            dailyQuizCount = 1
        }

        lastQuizDate = Date()
        updateStreak()
    }

    private fun updateStreak() {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val yesterday = Calendar.getInstance().apply {
            time = today
            add(Calendar.DAY_OF_YEAR, -1)
        }.time

        val lastActivityDay = lastActivityDate?.let { date ->
            Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }

        when (lastActivityDay) {
            today -> {
                // Ya estudió hoy, mantener streak
            }
            yesterday -> {
                // Estudió ayer, incrementar streak
                currentStreak += 1
            }
            else -> {
                // No estudió ayer, resetear streak
                currentStreak = 1
            }
        }

        if (currentStreak > bestStreak) {
            bestStreak = currentStreak
        }

        lastActivityDate = Date()
    }
}