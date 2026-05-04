package com.example.darepack_complete.darepack

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
        containerColor = LightBg,
        topBar = {
            TopAppBar(
                title = { Text("Invite Friends", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBg)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            
            // Invite Code Card
            Card(
                colors = CardDefaults.cardColors(containerColor = LightCard),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Share Invite Code", color = TextSecondary, fontSize = 14.sp)
                    Text(
                        group?.inviteCode ?: "......",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
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
                                containerColor = Purple.copy(alpha = 0.2f),
                                contentColor = Purple
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Copy Code")
                        }
                        
                        Button(
                            onClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Join my DarePack crew! Use code: ${group?.inviteCode}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, null, Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Share Link")
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            
            Text("Find on DarePack", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Invite friends who already have an account.", color = TextSecondary, fontSize = 14.sp)
            
            Spacer(Modifier.height(16.dp))
            
            OutlinedTextField(
                value = query,
                onValueChange = { 
                    query = it
                    vm.searchUsers(it)
                },
                placeholder = { Text("Search by email...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple,
                    unfocusedBorderColor = LightSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
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
                    Text("No users found", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
fun UserInviteRow(user: UserModel, isMember: Boolean, onInvite: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightCard, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(Purple.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(user.name.firstOrNull()?.toString() ?: "?", color = Purple, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(user.name, color = TextPrimary, fontWeight = FontWeight.Medium)
            Text(user.email, color = TextSecondary, fontSize = 12.sp)
        }
        
        if (isMember) {
            Text("Member", color = Teal, fontSize = 13.sp)
        } else {
            TextButton(onClick = onInvite) {
                Text("Add", color = Purple)
            }
        }
    }
}
