package com.example.darepack_complete.darepack

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.darepack_complete.models.UserModel
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.viewmodel.InviteFriendsViewModel
import com.example.darepack_complete.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteFriendsScreen(
    groupId: String,
    onBack: () -> Unit,
    vm: InviteFriendsViewModel = viewModel()
) {
    val group by vm.group.collectAsState()
    val searchResults by vm.searchResults.collectAsState()
    var query by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    LaunchedEffect(groupId) { vm.loadGroup(groupId) }

    Scaffold(
        containerColor = CyberDark,
        topBar = {
            TopAppBar(
                title = { Text("Invite Friends", color = CyberText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = CyberText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDark)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            CyberBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(16.dp))
                
                // Invite Code Card
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Share Invite Code", color = CyberBlue.copy(alpha = 0.7f), fontSize = 14.sp)
                        Text(
                            group?.inviteCode ?: "......",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberText,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = { 
                                    group?.inviteCode?.let { 
                                        clipboard.setText(AnnotatedString(it))
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberBlue.copy(alpha = 0.1f),
                                    contentColor = CyberBlue
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = CyberBlue.copy(alpha = 0.3f))
                            ) {
                                Text("Copy Code")
                            }
                            
                            GradientButton(
                                text = "Share Link",
                                onClick = {
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Join my DarePack crew! Use code: ${group?.inviteCode}")
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                    context.startActivity(shareIntent)
                                },
                                modifier = Modifier.weight(1f),
                                gradient = CyberGradient
                            )
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))
                
                Text("Find on DarePack", color = CyberText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Invite friends who already have an account.", color = CyberBlue.copy(alpha = 0.7f), fontSize = 14.sp)
                
                Spacer(Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = query,
                    onValueChange = { 
                        query = it
                        vm.searchUsers(it)
                    },
                    placeholder = { Text("Search by email...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = CyberBlue.copy(alpha = 0.6f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberBlue,
                        unfocusedBorderColor = CyberBlue.copy(alpha = 0.3f),
                        focusedTextColor = CyberText,
                        unfocusedTextColor = CyberText,
                        focusedLabelColor = CyberBlue,
                        unfocusedLabelColor = CyberBlue.copy(alpha = 0.6f),
                        cursorColor = CyberBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(Modifier.height(16.dp))
                
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(searchResults) { user ->
                        val isMember = group?.members?.contains(user.userId) == true
                        UserInviteRow(
                            user = user, 
                            isMember = isMember,
                            onInvite = { vm.inviteUser(user.userId) }
                        )
                    }
                }
                
                if (query.isNotEmpty() && searchResults.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No users found", color = CyberBlue.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun UserInviteRow(user: UserModel, isMember: Boolean, onInvite: () -> Unit) {
    CyberCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(CyberBlue.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(user.name.firstOrNull()?.toString() ?: "?", color = CyberBlue, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(user.name, color = CyberText, fontWeight = FontWeight.Medium)
                Text(user.email, color = CyberBlue.copy(alpha = 0.6f), fontSize = 12.sp)
            }
            
            if (isMember) {
                LuminousBadge(text = "Member", color = CyberBlue)
            } else {
                TextButton(onClick = onInvite) {
                    Text("Add", color = CyberBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
