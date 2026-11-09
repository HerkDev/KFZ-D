package de.herk.kfzd

import de.herk.kfzd.ui.theme.ThemePreferenceStore
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemePreferenceTest {
    @Test
    fun followsSystemThemeWithoutManualSelection() {
        assertFalse(ThemePreferenceStore.resolveDarkTheme(systemIsDark = false, manualDarkTheme = null))
        assertTrue(ThemePreferenceStore.resolveDarkTheme(systemIsDark = true, manualDarkTheme = null))
    }

    @Test
    fun manualSelectionOverridesSystemTheme() {
        assertFalse(ThemePreferenceStore.resolveDarkTheme(systemIsDark = true, manualDarkTheme = false))
        assertTrue(ThemePreferenceStore.resolveDarkTheme(systemIsDark = false, manualDarkTheme = true))
    }
}
