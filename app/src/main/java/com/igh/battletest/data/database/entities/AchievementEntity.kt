package com.igh.battletest.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val points: Int,
    val isUnlocked: Boolean,
    val earnedDate: Date?,
    val studentId: String
)