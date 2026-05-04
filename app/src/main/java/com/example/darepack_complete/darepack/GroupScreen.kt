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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.darepack_complete.models.Group
import com.example.darepack_complete.models.UserModel as User
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.viewmodel.GroupsViewModel
import com.example.darepack_complete.ui.components.LuminousBadge

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
    val members by vm.members.collectAsState()
    var selectedGroup by remember { mutableStateOf<Group?>(null) }
    var joinDialog    by remember { mutableStateOf(false) }

    LaunchedEffect(selectedGroup) {
        selectedGroup?.let { vm.loadMembers(it.members) }
    }

    Scaffold(
        containerColor = LightBg,
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick        = onCreateGroup,
                containerColor = Color.Transparent,
                contentColor   = Color.White,
                shape          = RoundedCornerShape(20.dp),
                modifier       = Modifier.background(Brush.linearGradient(PurpleGradient), RoundedCornerShape(20.dp))
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
        LazyColumn(
            modifier            = Modifier.fillMaxSize().padding(padding),
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
                        Text("Groups", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = PurpleDark)
                        TextButton(
                            onClick = { joinDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = Purple)
                        ) {
                            Text("JOIN GROUP", fontWeight = FontWeight.Bold, letterSpacing = 1.sp, fontSize = 12.sp)
                        }
                    }
                    Text("Connect with your pack", fontSize = 14.sp, color = TextSecondary)
                }
            }

            if (groups.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxWidth()
                            .background(LightCard, RoundedCornerShape(16.dp))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Person, null, tint = LightSurface, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("No groups yet. Create one!", color = TextSecondary, fontSize = 14.sp)
                        }
                    }
                }
            } else {
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
                            color = TextPrimary
                        )
                        IconButton(onClick = { onInviteFriends(group.groupId) }) {
                            Icon(Icons.Default.Person, null, tint = Purple)
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

    if (joinDialog) {
        JoinGroupDialog(
            onDismiss = { joinDialog = false },
            onJoin    = { id -> vm.joinGroup(id); joinDialog = false }
        )
    }
}

@Composable
fun GroupCard(group: Group, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors  = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.White else LightCard
        ),
        shape   = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 0.dp),
        border = if (isSelected) BorderStroke(2.dp, Brush.linearGradient(PurpleGradient)) else null
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(48.dp).background(Brush.linearGradient(listOf(Purple.copy(alpha = 0.1f), Purple.copy(alpha = 0.2f))), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (group.name.isNotEmpty()) group.name.first().uppercaseChar().toString() else "?",
                    color = Purple, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(group.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${group.members.size} members", color = TextSecondary, fontSize = 13.sp)
            }
            if (isSelected) {
                Icon(Icons.Default.Check, null, tint = Purple)
            }
        }
    }
}

@Composable
fun LeaderboardRow(user: User, rank: Int) {
    val rankColor = when (rank) {
        1    -> Yellow
        2    -> Color(0xFF94A3B8) // Bright Silver
        3    -> Color(0xFFD97706) // Vibrant Bronze
        else -> LightSurface
    }
    Row(
        Modifier.fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(LightCard, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(28.dp).background(rankColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$rank", color = if (rank <= 3) Color.White else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(Modifier.width(12.dp))
        Text(user.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        LuminousBadge(text = "${user.totalCompleted} DARES", color = Purple)
    }
}

@Composable
fun JoinGroupDialog(onDismiss: () -> Unit, onJoin: (String) -> Unit) {
    var groupId by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = LightCard,
        title = { Text("Join a group", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text  = {
            OutlinedTextField(
                value         = groupId,
                onValueChange = { groupId = it },
                label         = { Text("Group ID") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(16.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Purple,
                    unfocusedBorderColor = LightSurface,
                    focusedLabelColor    = Purple,
                    unfocusedLabelColor  = TextSecondary,
                    focusedTextColor     = TextPrimary,
                    unfocusedTextColor   = TextPrimary
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { if (groupId.isNotBlank()) onJoin(groupId) },
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Join")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        }
    )
}
