package com.igh.battletest.manager

import android.content.Context
import android.content.SharedPreferences
import com.igh.battletest.data.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserProgressManager {
    private const val PREFS_NAME = "battle_test_prefs"
    private const val KEY_STUDENT_NAME = "student_name"
    private const val KEY_STUDENT_EMAIL = "student_email"
    private const val KEY_TOTAL_POINTS = "total_points"
    private const val KEY_LEVEL = "level"
    private const val KEY_CURRENT_STREAK = "current_streak"

    private lateinit var prefs: SharedPreferences

    private val _currentStudent = MutableStateFlow<Student?>(null)
    val currentStudent: StateFlow<Student?> = _currentStudent.asStateFlow()

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadStudent()
    }

    private fun loadStudent() {
        val name = prefs.getString(KEY_STUDENT_NAME, "") ?: ""
        val email = prefs.getString(KEY_STUDENT_EMAIL, "") ?: ""

        if (name.isNotEmpty() && email.isNotEmpty()) {
            _currentStudent.value = Student(
                name = name,
                email = email,
                totalPoints = prefs.getInt(KEY_TOTAL_POINTS, 0),
                level = prefs.getInt(KEY_LEVEL, 1),
                currentStreak = prefs.getInt(KEY_CURRENT_STREAK, 0)
            )
        }
    }

    fun saveStudent(student: Student) {
        _currentStudent.value = student
        prefs.edit()
            .putString(KEY_STUDENT_NAME, student.name)
            .putString(KEY_STUDENT_EMAIL, student.email)
            .putInt(KEY_TOTAL_POINTS, student.totalPoints)
            .putInt(KEY_LEVEL, student.level)
            .putInt(KEY_CURRENT_STREAK, student.currentStreak)
            .apply()
    }
}