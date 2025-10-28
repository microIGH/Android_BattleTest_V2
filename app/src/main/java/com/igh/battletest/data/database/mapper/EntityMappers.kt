package com.igh.battletest.data.database.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import com.igh.battletest.data.*
import com.igh.battletest.data.database.entities.*
import java.util.*

// StudentEntity → Student
fun StudentEntity.toStudent(
    achievements: List<Achievement> = emptyList(),
    quizResults: List<QuizResult> = emptyList()
): Student {
    return Student(
        id = id,
        name = name,
        email = email,
        registrationDate = registrationDate,
        totalPoints = totalPoints,
        level = level,
        experiencePoints = experiencePoints,
        achievements = achievements,
        lastAchievementDate = lastAchievementDate,
        achievementPointsToday = achievementPointsToday,
        currentStreak = currentStreak,
        bestStreak = bestStreak,
        lastActivityDate = lastActivityDate,
        lastStreakRewardDate = lastStreakRewardDate,
        totalStudyTime = totalStudyTime,
        completedQuizzes = completedQuizzes,
        quizResults = quizResults,
        subjectsExplored = subjectsExplored,
        dailyQuizCount = dailyQuizCount,
        lastQuizDate = lastQuizDate
    )
}

// Student → StudentEntity
fun Student.toEntity(): StudentEntity {
    return StudentEntity(
        id = id,
        name = name,
        email = email,
        registrationDate = registrationDate,
        totalPoints = totalPoints,
        level = level,
        experiencePoints = experiencePoints,
        currentStreak = currentStreak,
        bestStreak = bestStreak,
        lastActivityDate = lastActivityDate,
        lastStreakRewardDate = lastStreakRewardDate,
        totalStudyTime = totalStudyTime,
        completedQuizzes = completedQuizzes,
        subjectsExplored = subjectsExplored,
        dailyQuizCount = dailyQuizCount,
        lastQuizDate = lastQuizDate,
        lastAchievementDate = lastAchievementDate,
        achievementPointsToday = achievementPointsToday
    )
}

// AchievementEntity → Achievement (reconstrucción básica)
fun AchievementEntity.toAchievement(): Achievement {
    return Achievement(
        id = id,
        title = title,
        description = description,
        category = "general",
        points = points,
        iconVector = Icons.Default.Star, // Default icon
        isUnlocked = isUnlocked,
        earnedDate = earnedDate
    )
}

// Achievement → AchievementEntity (solo campos persistibles)
fun Achievement.toEntity(studentId: String): AchievementEntity {
    return AchievementEntity(
        id = id,
        title = title,
        description = description,
        icon = category, // Guardamos category como icon
        points = points,
        isUnlocked = isUnlocked,
        earnedDate = earnedDate,
        studentId = studentId
    )
}

// QuizResultEntity → QuizResult
fun QuizResultEntity.toQuizResult(): QuizResult {
    val percentage = (correctAnswers.toFloat() / totalQuestions.toFloat()) * 100f
    val completionTimeSec = timeSpent / 1000.0

    return QuizResult(
        quizId = quizId,
        subjectId = subjectId,
        score = score,
        totalQuestions = totalQuestions,
        scorePercentage = percentage,
        completionTime = completionTimeSec,
        timeElapsed = timeSpent,
        errorsCount = totalQuestions - correctAnswers,
        pointsEarned = score,
        timestamp = completedAt.time
    )
}

// QuizResult → QuizResultEntity
fun QuizResult.toEntity(studentId: String): QuizResultEntity {
    return QuizResultEntity(
        id = "$quizId-$timestamp",
        quizId = quizId,
        subjectId = subjectId,
        score = score,
        totalQuestions = totalQuestions,
        correctAnswers = totalQuestions - errorsCount,
        timeSpent = timeElapsed,
        completedAt = date,
        studentId = studentId
    )
}