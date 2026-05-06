package com.example.darepack_complete.darepack

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.viewmodel.ProfileViewModel
import com.example.darepack_complete.ui.components.*

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

@OptIn(ExperimentalMaterial3Api::class)
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
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { vm.updateProfileImage(context, it) }
    }

    Scaffold(
        containerColor = CyberDark,
        topBar = {
            TopAppBar(
                title = { Text("PROFILE", color = CyberText, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp, fontSize = 18.sp) },
                actions = {
                    IconButton(
                        onClick = { 
                            Log.d("ProfileScreen", "Logout clicked")
                            vm.signOut()
                            Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                            onSignOut() 
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sign out",
                            tint = Pink
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDark)
            )
        },
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
            CyberBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(20.dp))

                // Avatar with glow
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        Modifier
                            .size(110.dp)
                            .background(Brush.radialGradient(listOf(CyberBlue.copy(alpha = 0.2f), Color.Transparent)), CircleShape)
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
                                modifier = Modifier.fillMaxSize().background(CyberSurface)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.linearGradient(CyberGradient)),
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
                                CircularProgressIndicator(color = CyberBlue, modifier = Modifier.size(30.dp), strokeWidth = 3.dp)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(user?.name ?: "Loading...", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = CyberText)
                Text(user?.email ?: "", fontSize = 14.sp, color = CyberBlue.copy(alpha = 0.7f))
                
                Spacer(Modifier.height(12.dp))
                LuminousBadge(text = user?.gender ?: "User", color = CyberBlue)

                Spacer(Modifier.height(40.dp))

                // Stats card
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            value = (user?.totalCompleted ?: 0).toString(),
                            label = "Completed"
                        )
                        Box(Modifier.width(1.dp).height(40.dp).background(CyberBlue.copy(alpha = 0.2f)))
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
                    gradient = CyberGradient
                )
                
                Spacer(Modifier.height(16.dp))
                
                TextButton(onClick = { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                    Text("Change Profile Picture", color = CyberBlue, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(40.dp))
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
            color = CyberBlue
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = CyberText.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
    }
}
