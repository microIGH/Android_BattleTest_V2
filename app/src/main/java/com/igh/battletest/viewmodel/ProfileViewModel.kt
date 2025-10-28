package com.igh.battletest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.igh.battletest.data.Student
import com.igh.battletest.data.Achievement
import com.igh.battletest.data.repository.StudentRepository
import kotlinx.coroutines.flow.*

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = StudentRepository(application.applicationContext)

    val student: StateFlow<Student?> = repository.student
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val subjectProgress: StateFlow<Map<String, Int>> = student
        .map { student ->
            val progress = student?.quizResults
                ?.groupBy { it.subjectId }
                ?.mapValues { (_, results) ->
                    val avg = results.map { it.scorePercentage }.average()
                    android.util.Log.d("ProfileVM", "Subject: ${results.first().subjectId}, Avg: ${avg.toInt()}%")
                    avg.toInt()
                }
                ?: emptyMap()
            android.util.Log.d("ProfileVM", "Progress map: $progress")
            progress
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val allAchievements: StateFlow<List<Achievement>> = student
        .map { it?.achievements ?: emptyList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}