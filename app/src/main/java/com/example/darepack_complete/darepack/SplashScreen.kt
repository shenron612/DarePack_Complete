package com.example.darepack_complete.darepack

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.darepack_complete.R
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.ui.components.*
import kotlinx.coroutines.delay
import kotlin.math.sin

@Composable
fun SplashScreen(onNext: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000) // 3 seconds splash
        onNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDark),
        contentAlignment = Alignment.Center
    ) {
        CyberBackground()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(CyberBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "DarePack",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = CyberText,
                letterSpacing = 4.sp
            )

            Spacer(Modifier.height(48.dp))

            // Dot Wave Animation
            DotWaveAnimation()
        }
    }
}

@Composable
fun DotWaveAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val colors = listOf(Color.Red, Color.Yellow, Color.Blue)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        colors.forEachIndexed { index, color ->
            val yOffset = sin(phase + index * 0.8f) * 15f
            val scale = 0.8f + (sin(phase + index * 0.8f) + 1f) * 0.2f

            Box(
                modifier = Modifier
                    .offset(y = yOffset.dp)
                    .size(12.dp)
                    .scale(scale)
                    .background(color, CircleShape)
            )
        }
    }
}
