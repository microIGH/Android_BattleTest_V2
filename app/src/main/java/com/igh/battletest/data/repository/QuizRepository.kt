package com.igh.battletest.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.igh.battletest.data.Subject
import com.igh.battletest.data.api.RetrofitClient
import com.igh.battletest.data.dto.SubjectDto
import com.igh.battletest.data.mapper.toSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}

class NetworkException(message: String) : Exception(message)
class ServerException(message: String) : Exception(message)

class QuizRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("quiz_cache", Context.MODE_PRIVATE)

    private val gson = Gson()
    private val api = RetrofitClient.quizApi

    companion object {
        private fun getCacheKey(language: String) = "cached_subjects_$language"
        private const val KEY_CACHE_TIMESTAMP = "cache_timestamp"
        private const val CACHE_VALIDITY_MS = 24 * 60 * 60 * 1000L // 24 horas
    }

    suspend fun getSubjects(language: String, forceRefresh: Boolean = false): Result<List<Subject>> {
        return withContext(Dispatchers.IO) {
            try {

                if (!forceRefresh) {
                    val cached = getCachedSubjects(language)
                    if (cached != null && isCacheValid()) {
                        return@withContext Result.Success(cached)
                    }
                }

                val response = api.getQuizzesByLanguage(language)

                if (response.isSuccessful) {
                    val subjectsDto = response.body() ?: emptyList()

                    if (subjectsDto.isNotEmpty()) {

                    }

                    val subjects = subjectsDto.map { it.toSubject() }
                        .also {
                            //android.util.Log.d("QuizRepo", "Subjects sin filtrar: ${it.size}")
                        }

                    cacheSubjects(subjects, "es")  // Cambiar language por "es" hardcoded
                    Result.Success(subjects)
                } else {
                    android.util.Log.e("QuizRepo", "API falló con código: ${response.code()}")
                    val cached = getCachedSubjects(language)
                    if (cached != null) {
                        Result.Success(cached)
                    } else {
                        Result.Error(ServerException("Error del servidor: ${response.code()}"))
                    }
                }
            } catch (e: java.io.IOException) {
                //
                android.util.Log.e("QuizRepo", "IOException: ${e.message}", e)
                val cached = getCachedSubjects(language)
                if (cached != null) {
                    Result.Success(cached)
                } else {
                    Result.Error(NetworkException("Sin conexión a internet"))
                }
            } catch (e: Exception) {
                //
                android.util.Log.e("QuizRepo", "Exception: ${e.message}", e)
                Result.Error(Exception("Error desconocido: ${e.message}"))
            }
        }
    }

    private fun getCachedSubjects(language: String): List<Subject>? {
        val json = prefs.getString(getCacheKey(language), null) ?: return null
        val cached = getCachedSubjects(language)
        return try {
            val type = object : TypeToken<List<Subject>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    private fun cacheSubjects(subjects: List<Subject>, language: String) {
        //android.util.Log.d("QuizRepo", "Guardando en caché ($language): ${subjects.size} subjects")
        val json = gson.toJson(subjects)
        prefs.edit()
            .putString(getCacheKey(language), json)
            .putLong(KEY_CACHE_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }

    private fun isCacheValid(): Boolean {
        val timestamp = prefs.getLong(KEY_CACHE_TIMESTAMP, 0)
        val now = System.currentTimeMillis()
        return (now - timestamp) < CACHE_VALIDITY_MS
    }

    fun clearCache() {
        prefs.edit().clear().apply()
    }
}