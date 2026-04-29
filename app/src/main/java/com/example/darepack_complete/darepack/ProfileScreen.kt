package com.example.darepack_complete.darepack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.darepack_complete.darepack.DarePackBottomNav
import com.example.darepack_complete.darepack.NavTab
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onMemories: () -> Unit,
    onSignOut: () -> Unit,
    onNavHome: () -> Unit,
    onNavGroups: () -> Unit,
    onNavBucket: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    val user by vm.user.collectAsState()

    Scaffold(
        containerColor = DarkBg,
        bottomBar = {
            DarePackBottomNav(
                current   = NavTab.PROFILE,
                onHome    = onNavHome,
                onGroups  = onNavGroups,
                onBucket  = onNavBucket,
                onProfile = {}
            )
        }
    ) { padding ->
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // Avatar
            if (user?.photoUrl?.isNotBlank() == true) {
                AsyncImage(
                    model              = user!!.photoUrl,
                    contentDescription = "Profile photo",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier         = Modifier
                        .size(88.dp)
                        .background(Purple.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = user?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        fontSize   = 32.sp,
                        color      = Purple,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text       = user?.name ?: "Loading...",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )
            Text(
                text     = user?.email ?: "",
                fontSize = 14.sp,
                color    = Color.Gray
            )

            Spacer(Modifier.height(28.dp))

            // Stats card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = DarkCard),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(
                        value = "${user?.totalCompleted ?: 0}",
                        label = "Dares Completed"
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Memories button
            Button(
                onClick  = onMemories,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Purple),
                shape    = RoundedCornerShape(12.dp)
            ) {
                Text("View Memories Archive", fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(12.dp))

            // Sign out
            OutlinedButton(
                onClick  = { vm.signOut(); onSignOut() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFE24B4A)
                )
            ) {
                Text("Sign out", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Purple)
        Text(label, fontSize = 13.sp, color = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    DarePackTheme {
        Column(
            modifier            = Modifier
                .background(DarkBg)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier         = Modifier
                    .size(88.dp)
                    .background(Purple.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "J",
                    fontSize   = 32.sp,
                    color      = Purple,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(16.dp))
            Text("Jane Doe", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(28.dp))
            StatItem(value = "12", label = "Dares Completed")
        }
    }
}
