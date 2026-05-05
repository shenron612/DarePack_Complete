package com.example.darepack_complete.darepack

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.darepack_complete.models.Group
import com.example.darepack_complete.models.UserModel as User
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.viewmodel.GroupsViewModel
import com.example.darepack_complete.ui.components.*

@Composable
fun GroupsScreen(
    onCreateGroup: () -> Unit,
    onInviteFriends: (String) -> Unit,
    onNavHome: () -> Unit,
    onNavBucket: () -> Unit,
    onNavProfile: () -> Unit,
    vm: GroupsViewModel = viewModel()
) {
    val groups  by vm.groups.collectAsState()
    val allGroups by vm.allGroups.collectAsState()
    val members by vm.members.collectAsState()
    var selectedGroup by remember { mutableStateOf<Group?>(null) }
    var joinDialog    by remember { mutableStateOf(false) }

    val publicGroups = allGroups.filter { group -> !groups.any { it.groupId == group.groupId } }

    LaunchedEffect(selectedGroup) {
        selectedGroup?.let { vm.loadMembers(it.members) }
    }

    Scaffold(
        containerColor = CyberDark,
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick        = onCreateGroup,
                containerColor = Color.Transparent,
                contentColor   = Color.White,
                shape          = RoundedCornerShape(20.dp),
                modifier       = Modifier.background(Brush.linearGradient(CyberGradient), RoundedCornerShape(20.dp))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create group", modifier = Modifier.size(32.dp))
            }
        },
        bottomBar = {
            DarePackBottomNav(
                current   = NavTab.GROUPS,
                onHome    = onNavHome,
                onGroups  = {},
                onBucket  = onNavBucket,
                onProfile = onNavProfile
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            CyberBackground()
            
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text("Groups", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = CyberText)
                            TextButton(
                                onClick = { joinDialog = true },
                                colors = ButtonDefaults.textButtonColors(contentColor = CyberBlue)
                            ) {
                                Text("JOIN CODE", fontWeight = FontWeight.Bold, letterSpacing = 1.sp, fontSize = 12.sp)
                            }
                        }
                        Text("Connect with your pack", fontSize = 14.sp, color = CyberBlue.copy(alpha = 0.7f))
                    }
                }

                if (groups.isNotEmpty()) {
                    item {
                        Text("My Groups", color = CyberText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    items(groups) { group ->
                        GroupCard(
                            group      = group,
                            isSelected = selectedGroup?.groupId == group.groupId,
                            onClick    = {
                                selectedGroup =
                                    if (selectedGroup?.groupId == group.groupId) null else group
                            }
                        )
                    }
                }

                if (publicGroups.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text("Discover Packs", color = CyberText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    items(publicGroups) { group ->
                        PublicGroupCard(group = group, onJoin = { vm.joinGroup(group.groupId) })
                    }
                }

                if (groups.isEmpty() && publicGroups.isEmpty()) {
                    item {
                        CyberCard(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                            ) {
                                Icon(Icons.Default.Person, null, tint = CyberBlue.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                                Spacer(Modifier.height(8.dp))
                                Text("No groups found. Create one!", color = CyberText.copy(alpha = 0.5f), fontSize = 14.sp)
                            }
                        }
                    }
                }

                selectedGroup?.let { group ->
                    item {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Leaderboard — ${group.name}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberText
                            )
                            IconButton(onClick = { onInviteFriends(group.groupId) }) {
                                Icon(Icons.Default.Person, null, tint = CyberBlue)
                            }
                        }
                    }
                    val sortedMembers = members.sortedByDescending { it.totalCompleted }
                    items(sortedMembers) { user ->
                        LeaderboardRow(user = user, rank = sortedMembers.indexOf(user) + 1)
                    }
                }
            }
        }
    }

    if (joinDialog) {
        JoinGroupDialog(
            onDismiss = { joinDialog = false },
            onJoin    = { id -> vm.joinGroup(id); joinDialog = false }
        )
    }
}

@Composable
fun PublicGroupCard(group: Group, onJoin: () -> Unit) {
    CyberCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(40.dp).background(CyberBlue.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (group.name.isNotEmpty()) group.name.first().uppercaseChar().toString() else "?",
                    color = CyberBlue, fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(group.name, color = CyberText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("${group.members.size} members", color = CyberText.copy(alpha = 0.6f), fontSize = 12.sp)
            }
            Button(
                onClick = onJoin,
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue.copy(alpha = 0.1f), contentColor = CyberBlue),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("JOIN", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun GroupCard(group: Group, isSelected: Boolean, onClick: () -> Unit) {
    CyberCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(48.dp).background(Brush.linearGradient(listOf(CyberBlue.copy(alpha = 0.1f), CyberBlue.copy(alpha = 0.2f))), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (group.name.isNotEmpty()) group.name.first().uppercaseChar().toString() else "?",
                    color = CyberBlue, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(group.name, color = CyberText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${group.members.size} members", color = CyberText.copy(alpha = 0.6f), fontSize = 13.sp)
            }
            if (isSelected) {
                Icon(Icons.Default.Check, null, tint = CyberBlue)
            }
        }
    }
}

@Composable
fun LeaderboardRow(user: User, rank: Int) {
    val rankColor = when (rank) {
        1    -> Yellow
        2    -> Color(0xFF94A3B8) // Silver
        3    -> Color(0xFFD97706) // Bronze
        else -> CyberBlue.copy(alpha = 0.1f)
    }
    
    CyberCard(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(28.dp).background(rankColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("$rank", color = if (rank <= 3) Color.White else CyberText.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(Modifier.width(12.dp))
            Text(user.name, color = CyberText, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
            LuminousBadge(text = "${user.totalCompleted} DARES", color = CyberBlue)
        }
    }
}

@Composable
fun JoinGroupDialog(onDismiss: () -> Unit, onJoin: (String) -> Unit) {
    var groupId by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = CyberSurface,
        title = { Text("Join a group", color = CyberText, fontWeight = FontWeight.Bold) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Enter the 6-digit invite code or Group ID to join your friends.", color = CyberBlue.copy(alpha = 0.7f), fontSize = 13.sp)
                OutlinedTextField(
                    value         = groupId,
                    onValueChange = { groupId = it },
                    label         = { Text("Invite Code") },
                    placeholder   = { Text("e.g. 123456") },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = CyberBlue,
                        unfocusedBorderColor = CyberBlue.copy(alpha = 0.3f),
                        focusedLabelColor    = CyberBlue,
                        unfocusedLabelColor  = CyberBlue.copy(alpha = 0.6f),
                        focusedTextColor     = CyberText,
                        unfocusedTextColor   = CyberText,
                        cursorColor          = CyberBlue
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (groupId.isNotBlank()) onJoin(groupId) },
                colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Join")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = CyberText.copy(alpha = 0.5f)) }
        }
    )
}
