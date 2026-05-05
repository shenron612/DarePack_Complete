package com.example.darepack_complete.darepack

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.darepack_complete.models.DarePackModel as Dare
import androidx.compose.ui.graphics.Brush
import com.example.darepack_complete.ui.components.*
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.viewmodel.HomeViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(
    onDareClick: (String) -> Unit,
    onNavGroups: () -> Unit,
    onNavBucket: () -> Unit,
    onNavProfile: () -> Unit,
    vm: HomeViewModel = viewModel()
) {
    val pendingDares by vm.pendingDares.collectAsState()
    val sentDares    by vm.sentDares.collectAsState()

    Scaffold(
        containerColor = CyberDark,
        bottomBar = {
            DarePackBottomNav(
                current   = NavTab.HOME,
                onHome    = {},
                onGroups  = onNavGroups,
                onBucket  = onNavBucket,
                onProfile = onNavProfile
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            CyberBackground()
            
            LazyColumn(
                modifier            = Modifier.fillMaxSize().padding(padding),
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)) {
                        Text(
                            "DarePack",
                            fontSize   = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = CyberText
                        )
                        Text(
                            "Challenge your friends today",
                            fontSize = 14.sp,
                            color = CyberBlue.copy(alpha = 0.7f)
                        )
                    }
                }

                item { SectionHeader("Dares for you", pendingDares.size, CyberGradient) }

                if (pendingDares.isEmpty()) {
                    item { EmptyState("No pending dares — dare a friend!") }
                } else {
                    items(pendingDares) { dare ->
                        DareCard(dare = dare, onClick = { onDareClick(dare.dareId) })
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    SectionHeader("Dares you sent", sentDares.size, CyberGradient)
                }

                if (sentDares.isEmpty()) {
                    item { EmptyState("You haven't dared anyone yet.") }
                } else {
                    items(sentDares) { dare ->
                        DareCard(dare = dare, onClick = { onDareClick(dare.dareId) })
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, count: Int, gradient: List<Color>) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CyberText)
        Spacer(Modifier.width(10.dp))
        Box(
            Modifier
                .background(Brush.linearGradient(gradient), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text("$count", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun DareCard(dare: Dare, onClick: () -> Unit) {
    val statusColor = when (dare.status) {
        "completed" -> CyberBlue
        "expired"   -> Pink
        else        -> CyberBlue
    }
    
    CyberCard(onClick = onClick) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top
        ) {
            Text(
                dare.title,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                color      = CyberText,
                modifier   = Modifier.weight(1f)
            )
            LuminousBadge(text = dare.status, color = statusColor)
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(8.dp).background(statusColor.copy(alpha = 0.5f), CircleShape))
            Spacer(Modifier.width(8.dp))
            Text(
                "From: ${dare.daredByName}  •  To: ${dare.daredToName}",
                fontSize = 13.sp,
                color    = CyberText.copy(alpha = 0.6f)
            )
        }
        
        if (dare.status == "pending") {
            val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                .format(dare.deadline.toDate())
            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = CyberBlue.copy(alpha = 0.1f), thickness = 1.dp)
            Text(
                "Expires on: $date",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color    = CyberBlue
            )
        }
    }
}

@Composable
fun EmptyState(message: String) {
    CyberCard {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(message, fontSize = 13.sp, color = CyberText.copy(alpha = 0.5f))
        }
    }
}
