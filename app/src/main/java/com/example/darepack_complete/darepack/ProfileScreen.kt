package com.example.darepack_complete.darepack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.viewmodel.ProfileViewModel
import com.example.darepack_complete.ui.components.GradientButton
import com.example.darepack_complete.ui.components.LuminousBadge

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

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
    val uploading by vm.uploading.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { vm.updateProfileImage(it) }
    }

    Scaffold(
        containerColor = LightBg,
        bottomBar = {
            DarePackBottomNav(
                current = NavTab.PROFILE,
                onHome = onNavHome,
                onGroups = onNavGroups,
                onBucket = onNavBucket,
                onProfile = {}
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            
            // Background decoration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Brush.verticalGradient(listOf(Purple.copy(alpha = 0.1f), Color.Transparent)))
            )

            // Sign Out Button (Top-Right)
            IconButton(
                onClick = { vm.signOut(); onSignOut() },
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign out", tint = Pink)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(60.dp))

                // Avatar with glow
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        Modifier
                            .size(110.dp)
                            .background(Brush.radialGradient(listOf(Purple.copy(alpha = 0.2f), Color.Transparent)), CircleShape)
                    )
                    
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .clickable { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                    ) {
                        if (user?.photoUrl?.isNotBlank() == true) {
                            AsyncImage(
                                model = user!!.photoUrl,
                                contentDescription = "Profile photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize().background(LightCard)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.linearGradient(PurpleGradient)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    fontSize = 36.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                        
                        if (uploading) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(30.dp), strokeWidth = 3.dp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(user?.name ?: "Loading...", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                Text(user?.email ?: "", fontSize = 14.sp, color = TextSecondary)
                
                Spacer(Modifier.height(12.dp))
                LuminousBadge(text = user?.gender ?: "User", color = Teal)

                Spacer(Modifier.height(40.dp))

                // Stats card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = LightCard),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(LightSurface, Color.Transparent)))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            value = (user?.totalCompleted ?: 0).toString(),
                            label = "Completed"
                        )
                        Box(Modifier.width(1.dp).height(40.dp).background(LightSurface))
                        StatItem(
                            value = "Legend",
                            label = "Rank"
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Memories button
                GradientButton(
                    text = "View Memories Archive",
                    onClick = onMemories,
                    gradient = PurpleGradient
                )
                
                Spacer(Modifier.height(16.dp))
                
                TextButton(onClick = { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                    Text("Change Profile Picture", color = PurpleDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Purple
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}
