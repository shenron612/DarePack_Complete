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
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.darepack.DarePackBottomNav
import com.example.darepack_complete.darepack.NavTab
import com.example.darepack_complete.viewmodel.HomeViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

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
        containerColor = DarkBg,
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    "DarePack",
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White,
                    modifier   = Modifier.padding(bottom = 4.dp, top = 8.dp)
                )
            }

            item { SectionHeader("Dares for you", pendingDares.size) }

            if (pendingDares.isEmpty()) {
                item { EmptyState("No pending dares — dare a friend!") }
            } else {
                items(pendingDares) { dare ->
                    DareCard(dare = dare, onClick = { onDareClick(dare.dareId) })
                }
            }

            item {
                Spacer(Modifier.height(4.dp))
                SectionHeader("Dares you sent", sentDares.size)
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
fun SectionHeader(title: String, count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
        Spacer(Modifier.width(8.dp))
        Box(
            Modifier
                .background(Purple, RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text("$count", fontSize = 11.sp, color = Color.White)
        }
    }
}

@Composable
fun DareCard(dare: Dare, onClick: () -> Unit) {
    val statusColor = when (dare.status) {
        "completed" -> Teal
        "expired"   -> Color(0xFFE24B4A)
        else        -> Purple
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors   = CardDefaults.cardColors(containerColor = DarkCard),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    dare.title,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color      = Color.White,
                    modifier   = Modifier.weight(1f)
                )
                Box(
                    Modifier
                        .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(dare.status, fontSize = 11.sp, color = statusColor)
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "From: ${dare.daredByName}  •  To: ${dare.daredToName}",
                fontSize = 12.sp,
                color    = Color.Gray
            )
            if (dare.status == "pending") {
                val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    .format(dare.deadline.toDate())
                Text(
                    "Deadline: $date",
                    fontSize = 12.sp,
                    color    = Color(0xFFFAC775),
                    modifier = Modifier.padding(top = 4.dp)
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
            .background(DarkCard, RoundedCornerShape(12.dp))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, fontSize = 13.sp, color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    DarePackTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
