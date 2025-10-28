package com.igh.battletest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.igh.battletest.data.Subject
import com.igh.battletest.data.repository.QuizRepository
import com.igh.battletest.data.repository.Result

class SubjectsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = QuizRepository(application.applicationContext)

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val filteredSubjects: List<Subject>
        get() = _subjects.value.filter {
            it.name.contains(_searchQuery.value, ignoreCase = true)
        }

    init {
        repository.clearCache()
        loadSubjects()
    }

    fun loadSubjects(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val language = getSystemLanguage()

            // SIEMPRE llamar endpoint español (único con datos)
            when (val result = repository.getSubjects("es", forceRefresh)) {
                is Result.Success -> {
                    // Filtrar por idioma del dispositivo
                    val filteredSubjects = result.data
                        .map { subject ->
                            subject.copy(
                                quizzes = subject.quizzes.filter { it.language == language }
                            )
                        }
                        .filter { it.quizzes.isNotEmpty() }

                    _subjects.value = filteredSubjects
                    _error.value = null
                }
                is Result.Error -> {
                    val errorMsg = "ERROR: ${result.exception.message}"
                    android.util.Log.e("SubjectsViewModel", errorMsg, result.exception)
                    _error.value = errorMsg
                }
                is Result.Loading -> {}
            }

            _isLoading.value = false
        }
    }

    fun updateSearch(query: String) {
        _searchQuery.value = query
    }

    fun clearError() {
        _error.value = null
    }

    private fun getSystemLanguage(): String {
        val locale = getApplication<Application>().resources.configuration.locales[0]
        return when (locale.language) {
            "es" -> "es"
            "fr" -> "fr"
            else -> "en"
        }
    }

    fun navigateToQuizzes(subjectId: String) {
        // Navigation logic handled by Screen
    }
}