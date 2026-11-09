package de.herk.kfzd.ui.theme

import android.content.Context

/** Stores the user's explicit theme choice. A missing value means "follow system". */
class ThemePreferenceStore(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    val manualDarkTheme: Boolean?
        get() = if (preferences.contains(KEY_MANUAL_DARK_THEME)) {
            preferences.getBoolean(KEY_MANUAL_DARK_THEME, false)
        } else {
            null
        }

    fun setManualDarkTheme(isDark: Boolean) {
        preferences.edit().putBoolean(KEY_MANUAL_DARK_THEME, isDark).apply()
    }

    companion object {
        private const val PREFERENCES_NAME = "theme_preferences"
        private const val KEY_MANUAL_DARK_THEME = "manual_dark_theme"

        fun resolveDarkTheme(systemIsDark: Boolean, manualDarkTheme: Boolean?): Boolean {
            return manualDarkTheme ?: systemIsDark
        }
    }
}
