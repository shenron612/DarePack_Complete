package com.example.darepack_complete.darepack

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.darepack_complete.models.DarePackModel as Dare
import androidx.compose.ui.graphics.Brush
import com.example.darepack_complete.ui.components.LuminousBadge
import com.example.darepack_complete.ui.theme.DarePackTheme
import com.example.darepack_complete.ui.theme.LightBg
import com.example.darepack_complete.ui.theme.LightCard
import com.example.darepack_complete.ui.theme.LightSurface
import com.example.darepack_complete.ui.theme.Pink
import com.example.darepack_complete.ui.theme.Purple
import com.example.darepack_complete.ui.theme.PurpleDark
import com.example.darepack_complete.ui.theme.PurpleGradient
import com.example.darepack_complete.ui.theme.Teal
import com.example.darepack_complete.ui.theme.TealGradient
import com.example.darepack_complete.ui.theme.TextPrimary
import com.example.darepack_complete.ui.theme.TextSecondary
import com.example.darepack_complete.viewmodel.HomeViewModel
import java.security.Timestamp
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
        containerColor = LightBg,
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
                        color      = PurpleDark
                    )
                    Text(
                        "Challenge your friends today",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }

            item { SectionHeader("Dares for you", pendingDares.size, PurpleGradient) }

            if (pendingDares.isEmpty()) {
                item { EmptyState("No pending dares — dare a friend!") }
            } else {
                items(pendingDares) { dare ->
                    DareCard(dare = dare, onClick = { onDareClick(dare.dareId) })
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                SectionHeader("Dares you sent", sentDares.size, TealGradient)
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

@Composable
fun SectionHeader(title: String, count: Int, gradient: List<Color> = PurpleGradient) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
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
        "completed" -> Teal
        "expired"   -> Pink
        else        -> Purple
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors   = CardDefaults.cardColors(containerColor = LightCard),
        shape    = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(Color.Transparent, LightSurface)))
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Text(
                    dare.title,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                    modifier   = Modifier.weight(1f)
                )
                LuminousBadge(text = dare.status, color = statusColor)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).background(statusColor.copy(alpha = 0.5f), RoundedCornerShape(2.dp)))
                Spacer(Modifier.width(8.dp))
                Text(
                    "From: ${dare.daredByName}  •  To: ${dare.daredToName}",
                    fontSize = 13.sp,
                    color    = TextSecondary
                )
            }
            
            if (dare.status == "pending") {
                val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    .format(dare.deadline.toDate())
                Divider(Modifier.padding(vertical = 12.dp), color = LightSurface, thickness = 1.dp)
                Text(
                    "Expires on: $date",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color    = Purple
                )
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .background(LightCard, RoundedCornerShape(12.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, fontSize = 13.sp, color = TextSecondary)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DarePackTheme {
        Box(Modifier.background(LightBg).fillMaxSize()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader("Dares for you", 2)
                DareCard(
                    dare = Dare(
                        title = "Run 5km",
                        daredByName = "Alex",
                        daredToName = "Me",
                        status = "pending",
                        deadline = Timestamp.now()
                    ),
                    onClick = {}
                )
                EmptyState("No pending dares — dare a friend!")
            }
        }
    }
}
