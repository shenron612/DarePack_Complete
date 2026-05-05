package com.example.darepack_complete.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.darepack_complete.ui.theme.*

@Composable
fun CyberBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 5)
        
        // Draw concentric circles (radar style)
        for (i in 1..8) {
            drawCircle(
                color = CyberBlue.copy(alpha = 0.08f),
                radius = i * 120f,
                center = center,
                style = Stroke(width = 1.5f)
            )
        }
        
        // Draw grid
        val step = 80.dp.toPx()
        for (x in 0..(size.width / step).toInt()) {
            drawLine(
                color = CyberBlue.copy(alpha = 0.03f),
                start = Offset(x * step, 0f),
                end = Offset(x * step, size.height),
                strokeWidth = 1f
            )
        }
        for (y in 0..(size.height / step).toInt()) {
            drawLine(
                color = CyberBlue.copy(alpha = 0.03f),
                start = Offset(0f, y * step),
                end = Offset(size.width, y * step),
                strokeWidth = 1f
            )
        }
    }
}

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier
    
    Box(
        modifier = cardModifier
            .clip(RoundedCornerShape(16.dp))
            .background(CyberSurface.copy(alpha = 0.4f))
            .border(1.dp, CyberBlue.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradient: List<Color> = PurpleGradient
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Brush.linearGradient(gradient), RoundedCornerShape(16.dp))
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
fun LuminousBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text.uppercase(),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
