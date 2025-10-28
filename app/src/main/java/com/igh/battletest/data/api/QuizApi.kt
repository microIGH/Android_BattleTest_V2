package com.igh.battletest.data.api

import com.igh.battletest.data.dto.SubjectDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QuizApi {
    @GET("api/quizzes/{language}")
    suspend fun getQuizzesByLanguage(
        @Path("language") language: String
    ): Response<List<SubjectDto>>
}