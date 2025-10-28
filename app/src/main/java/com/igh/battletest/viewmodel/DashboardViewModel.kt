package com.igh.battletest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.igh.battletest.data.Student
import com.igh.battletest.data.Achievement
import com.igh.battletest.data.repository.StudentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StudentRepository(application.applicationContext)

    val student: StateFlow<Student?> = repository.student
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val achievements: StateFlow<List<Achievement>> = student
        .map { it?.getEarnedAchievements() ?: emptyList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val studentName: String get() = student.value?.name ?: "Usuario"
    val totalPoints: Int get() = student.value?.totalPoints ?: 0
    val currentLevel: Int get() = student.value?.level ?: 1
    val progressToNext: Float get() = student.value?.progressToNextLevel ?: 0f
    val weeklyStreak: Int get() = student.value?.currentStreak ?: 0

    init {
        viewModelScope.launch {
            // Esperar a que Flow emita primer valor
            val existingStudent = repository.student.first()
            if (existingStudent == null) {
                repository.createStudent("Usuario", "test@example.com")
            }
        }
    }

    fun refresh() {
        // Forzar re-colección del flow
        viewModelScope.launch {
            repository.student.collect { }
        }
    }
}