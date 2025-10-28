package com.igh.battletest.manager

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguageManager {
    private const val PREF_LANGUAGE = "app_language"

    fun setLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)

        // APLICAR CAMBIOS AL CONTEXTO ACTUAL
        resources.updateConfiguration(config, resources.displayMetrics)

        // Guardar preferencia
        val prefs = context.getSharedPreferences("battle_test_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString(PREF_LANGUAGE, language).apply()
    }

    fun getCurrentLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("battle_test_prefs", Context.MODE_PRIVATE)
        return prefs.getString(PREF_LANGUAGE, "es") ?: "es"
    }

    fun getAvailableLanguages(): List<Pair<String, String>> {
        return listOf(
            "es" to "Español",
            "en" to "English",
            "fr" to "Français"
        )
    }
}