package com.example.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिंदी"),
    HINGLISH("hi-Latn", "Hinglish")
}

class LanguageManager(private val context: Context) {
    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language_code")
        private val FIRST_LAUNCH_KEY = stringPreferencesKey("is_first_launch")
    }

    val languageFlow: Flow<Language> = context.dataStore.data.map { preferences ->
        val code = preferences[LANGUAGE_KEY] ?: Language.ENGLISH.code
        Language.values().find { it.code == code } ?: Language.ENGLISH
    }

    val isFirstLaunchFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH_KEY] == null
    }

    suspend fun saveLanguage(language: Language) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.code
            preferences[FIRST_LAUNCH_KEY] = "false"
        }
    }
}
