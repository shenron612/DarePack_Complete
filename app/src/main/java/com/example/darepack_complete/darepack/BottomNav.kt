package com.example.darepack_complete.darepack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.darepack_complete.ui.theme.*

enum class NavTab { HOME, GROUPS, BUCKET, PROFILE }

@Composable
fun DarePackBottomNav(
    current: NavTab,
    onHome: () -> Unit,
    onGroups: () -> Unit,
    onBucket: () -> Unit,
    onProfile: () -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Brush.linearGradient(listOf(Color.Transparent, LightSurface, Color.Transparent)))
        )
        
        NavigationBar(
            containerColor = LightCard,
            tonalElevation = 0.dp,
            modifier = Modifier.height(72.dp)
        ) {
            NavigationBarItem(
                selected = current == NavTab.HOME,
                onClick  = onHome,
                icon     = { NavIcon(Icons.Default.Home, current == NavTab.HOME) },
                label    = { NavLabel("Home", current == NavTab.HOME) },
                colors   = navItemColors()
            )
            NavigationBarItem(
                selected = current == NavTab.GROUPS,
                onClick  = onGroups,
                icon     = { NavIcon(Icons.Default.Person, current == NavTab.GROUPS) },
                label    = { NavLabel("Groups", current == NavTab.GROUPS) },
                colors   = navItemColors()
            )
            NavigationBarItem(
                selected = current == NavTab.BUCKET,
                onClick  = onBucket,
                icon     = { NavIcon(Icons.Default.Favorite, current == NavTab.BUCKET) },
                label    = { NavLabel("Bucket", current == NavTab.BUCKET) },
                colors   = navItemColors()
            )
            NavigationBarItem(
                selected = current == NavTab.PROFILE,
                onClick  = onProfile,
                icon     = { NavIcon(Icons.Default.AccountCircle, current == NavTab.PROFILE) },
                label    = { NavLabel("Profile", current == NavTab.PROFILE) },
                colors   = navItemColors()
            )
        }
    }
}

@Composable
private fun NavIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Purple.copy(alpha = 0.1f) else Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isSelected) Purple else TextSecondary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun NavLabel(text: String, isSelected: Boolean) {
    Text(
        text,
        fontSize = 11.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        color = if (isSelected) Purple else TextSecondary
    )
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor   = Purple,
    selectedTextColor   = Purple,
    unselectedIconColor = TextSecondary,
    unselectedTextColor = TextSecondary,
    indicatorColor      = Color.Transparent
)
