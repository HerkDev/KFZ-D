package de.herk.kfzd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.herk.kfzd.R
import de.herk.kfzd.ui.DkfzApp
import de.herk.kfzd.ui.theme.DKFZTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DKFZTheme {
                var showLaunchSplash by remember { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(250L)
                    showLaunchSplash = false
                }
                if (showLaunchSplash) {
                    LaunchSplash()
                } else {
                    DkfzApp()
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
            .background(Color.White)
    ) {
        val logoTop = maxOf(0.dp, maxHeight * 0.32f - 79.5.dp)
        Image(
            painter = painterResource(R.drawable.splash_logo),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = logoTop)
        )
    }
}
