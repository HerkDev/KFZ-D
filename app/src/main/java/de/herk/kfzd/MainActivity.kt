package de.herk.kfzd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsControllerCompat
import de.herk.kfzd.R
import de.herk.kfzd.ui.DkfzApp
import de.herk.kfzd.ui.theme.DKFZTheme
import de.herk.kfzd.ui.theme.ThemePreferenceStore
import kotlinx.coroutines.delay

private const val LAUNCH_SPLASH_DURATION_MILLIS = 2_500L

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val themePreferences = remember(context) { ThemePreferenceStore(context) }
            var manualDarkTheme by remember { mutableStateOf(themePreferences.manualDarkTheme) }
            val systemDarkTheme = isSystemInDarkTheme()
            val darkTheme = ThemePreferenceStore.resolveDarkTheme(systemDarkTheme, manualDarkTheme)

            SideEffect {
                WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = !darkTheme
            }

            DKFZTheme(darkTheme = darkTheme) {
                var showLaunchSplash by remember { mutableStateOf(savedInstanceState == null) }
                LaunchedEffect(Unit) {
                    if (showLaunchSplash) {
                        delay(LAUNCH_SPLASH_DURATION_MILLIS)
                    }
                    showLaunchSplash = false
                }
                if (showLaunchSplash) {
                    LaunchSplash()
                } else {
                    DkfzApp(
                        isDarkTheme = darkTheme,
                        onThemeChange = { isDark ->
                            themePreferences.setManualDarkTheme(isDark)
                            manualDarkTheme = isDark
                        }
                    )
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun LaunchSplash() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
    ) {
        val logoTop = maxOf(0.dp, maxHeight * 0.32f - 79.5.dp)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = logoTop)
        ) {
            Image(
                painter = painterResource(R.drawable.splash_logo),
                contentDescription = null
            )
            Image(
                painter = painterResource(R.drawable.splash_logo_title),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
        }
    }
}
