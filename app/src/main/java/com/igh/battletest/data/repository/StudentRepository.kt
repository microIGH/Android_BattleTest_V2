package com.igh.battletest.data.repository

import android.content.Context
import com.igh.battletest.data.*
import com.igh.battletest.data.database.AppDatabase
import com.igh.battletest.data.database.mapper.*
import kotlinx.coroutines.flow.*
import java.util.*

class StudentRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val studentDao = database.studentDao()
    private val achievementDao = database.achievementDao()
    private val quizResultDao = database.quizResultDao()

    // Flow de estudiante completo con relaciones
    val student: Flow<Student?> = studentDao.getStudent()
        .combine(achievementDao.getAchievementsByStudent("default")) { studentEntity, achievements ->
            studentEntity to achievements
        }
        .combine(quizResultDao.getResultsByStudent("default")) { (studentEntity, achievements), results ->
            studentEntity?.toStudent(
                achievements = achievements.map { it.toAchievement() },
                quizResults = results.map { it.toQuizResult() }
            )
        }

    suspend fun createStudent(name: String, email: String): Student {
        val newStudent = Student(
            id = "default",
            name = name,
            email = email,
            registrationDate = System.currentTimeMillis()
        )

        studentDao.insert(newStudent.toEntity())

        // Verificar que se insertó
        val verificacion = studentDao.getStudentById("default")

        return newStudent
    }

    suspend fun updateStudent(student: Student) {
        studentDao.update(student.toEntity())
    }

    suspend fun addQuizResult(result: QuizResult) {
        // Guardar resultado
        quizResultDao.insert(result.toEntity("default"))

        // Actualizar student
        val currentStudent = studentDao.getStudentById("default")
        if (currentStudent != null) {
            val student = currentStudent.toStudent()

            // Recalcular datos
            val newTotalPoints = student.totalPoints + result.pointsEarned
            val newCompletedQuizzes = student.completedQuizzes + result.quizId
            val newSubjectsExplored = student.subjectsExplored + result.subjectId

            // Actualizar racha
            val today = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.time

            val yesterday = java.util.Calendar.getInstance().apply {
                time = today
                add(java.util.Calendar.DAY_OF_YEAR, -1)
            }.time

            val lastActivityDay = student.lastActivityDate?.let { date ->
                java.util.Calendar.getInstance().apply {
                    time = date
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }.time
            }

            val newStreak = when (lastActivityDay) {
                today -> student.currentStreak
                yesterday -> student.currentStreak + 1
                else -> 1
            }

            val newBestStreak = maxOf(newStreak, student.bestStreak)

            // Calcular nuevo nivel
            val newLevel = calculateLevelFromPoints(newTotalPoints)

            val updatedStudent = student.copy(
                totalPoints = newTotalPoints,
                level = newLevel,
                completedQuizzes = newCompletedQuizzes,
                subjectsExplored = newSubjectsExplored,
                currentStreak = newStreak,
                bestStreak = newBestStreak,
                lastActivityDate = java.util.Date(),
                lastQuizDate = java.util.Date()
            )

            studentDao.update(updatedStudent.toEntity())
        }
    }

    private fun calculateLevelFromPoints(points: Int): Int {
        var level = 1
        while (calculateExperienceForLevel(level + 1) <= points) {
            level++
        }
        return level
    }

    private fun calculateExperienceForLevel(level: Int): Int {
        if (level <= 1) return 0
        return ((level - 1) * (level - 1) * 75) + 150
    }

    suspend fun unlockAchievement(achievement: Achievement) {
        achievementDao.insert(achievement.toEntity("default"))

        // Actualizar student
        val currentStudent = studentDao.getStudentById("default")
        if (currentStudent != null) {
            val student = currentStudent.toStudent()
            student.addAchievement(achievement)
            studentDao.update(student.toEntity())
        }
    }
}