package com.danielys.storyapp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val nameKey = stringPreferencesKey("name_key_pref")
    private val useridKey = stringPreferencesKey("id_key_pref")
    private val tokenKey = stringPreferencesKey("token_key_pref")

    fun getName(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[nameKey] ?: ""
        }
    }

    fun getUserId(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[useridKey] ?: ""
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[tokenKey] ?: ""
        }
    }

    suspend fun setName(name: String) {
        dataStore.edit { preferences ->
            preferences[nameKey] = name
        }
    }

    suspend fun setUserId(userid: String) {
        dataStore.edit { preferences ->
            preferences[useridKey] = userid
        }
    }

    suspend fun setToken(token: String) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}