package com.example.darepack_complete.darepack

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.darepack_complete.ui.theme.DarkSurface
import com.example.darepack_complete.ui.theme.Purple


enum class NavTab { HOME, GROUPS, BUCKET, PROFILE }

@Composable
fun DarePackBottomNav(
    current: NavTab,
    onHome: () -> Unit,
    onGroups: () -> Unit,
    onBucket: () -> Unit,
    onProfile: () -> Unit
) {
    NavigationBar(containerColor = DarkSurface) {
        NavigationBarItem(
            selected = current == NavTab.HOME,
            onClick  = onHome,
            icon     = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label    = { Text("Home") },
            colors   = navItemColors()
        )
        NavigationBarItem(
            selected = current == NavTab.GROUPS,
            onClick  = onGroups,
            icon     = { Icon(Icons.Default.Person, contentDescription = "Groups") },
            label    = { Text("Groups") },
            colors   = navItemColors()
        )
        NavigationBarItem(
            selected = current == NavTab.BUCKET,
            onClick  = onBucket,
            icon     = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Bucket") },
            label    = { Text("Bucket") },
            colors   = navItemColors()
        )
        NavigationBarItem(
            selected = current == NavTab.PROFILE,
            onClick  = onProfile,
            icon     = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label    = { Text("Profile") },
            colors   = navItemColors()
        )
    }
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor   = Purple,
    selectedTextColor   = Purple,
    unselectedIconColor = Color.Gray,
    unselectedTextColor = Color.Gray,
    indicatorColor      = Color.Transparent
)