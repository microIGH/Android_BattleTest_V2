package com.igh.battletest.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val registrationDate: Long,

    val totalPoints: Int,
    val level: Int,
    val experiencePoints: Int,

    val currentStreak: Int,
    val bestStreak: Int,
    val lastActivityDate: Date?,
    val lastStreakRewardDate: Date?,

    val totalStudyTime: Long,
    val completedQuizzes: List<String>,
    val subjectsExplored: Set<String>,

    val dailyQuizCount: Int,
    val lastQuizDate: Date?,
    val lastAchievementDate: Date?,
    val achievementPointsToday: Int
)