package com.example.darepack_complete.darepack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.darepack_complete.models.Group
import com.example.darepack_complete.models.UserModel as User
import com.example.darepack_complete.darepack.DarePackBottomNav
import com.example.darepack_complete.darepack.NavTab
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.viewmodel.GroupsViewModel

@Composable
fun GroupsScreen(
    onCreateGroup: () -> Unit,
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
        containerColor = DarkBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick        = onCreateGroup,
                containerColor = Purple,
                contentColor   = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create group")
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Groups", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    TextButton(onClick = { joinDialog = true }) {
                        Text("Join group", color = Purple, fontSize = 13.sp)
                    }
                }
            }

            if (groups.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxWidth()
                            .background(DarkCard, RoundedCornerShape(12.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No groups yet. Create one!", color = Color.Gray, fontSize = 14.sp)
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
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Leaderboard — ${group.name}",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color      = Color.White
                    )
                }
                items(members) { user ->
                    LeaderboardRow(user = user, rank = members.indexOf(user) + 1)
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
            containerColor = if (isSelected) Purple.copy(alpha = 0.2f) else DarkCard
        ),
        shape   = RoundedCornerShape(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(44.dp).background(Purple.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (group.name.isNotEmpty()) group.name.first().uppercaseChar().toString() else "?",
                    color = Purple, fontWeight = FontWeight.Bold, fontSize = 18.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(group.name, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                Text("${group.members.size} members", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun LeaderboardRow(user: User, rank: Int) {
    val rankColor = when (rank) {
        1    -> Color(0xFFEF9F27)
        2    -> Color(0xFFB4B2A9)
        3    -> Color(0xFFBA7517)
        else -> Color.Gray
    }
    Row(
        Modifier.fillMaxWidth()
            .background(DarkCard, RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("#$rank", color = rankColor, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.width(32.dp))
        Text(user.name, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text("${user.totalCompleted} dares", color = Purple, fontSize = 13.sp)
    }
    Spacer(Modifier.height(6.dp))
}

@Composable
fun JoinGroupDialog(onDismiss: () -> Unit, onJoin: (String) -> Unit) {
    var groupId by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = DarkSurface,
        title = { Text("Join a group", color = Color.White) },
        text  = {
            OutlinedTextField(
                value         = groupId,
                onValueChange = { groupId = it },
                label         = { Text("Group ID") },
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Purple,
                    unfocusedBorderColor = Color(0xFF2A2A3A),
                    focusedLabelColor    = Purple,
                    unfocusedLabelColor  = Color.Gray,
                    focusedTextColor     = Color.White,
                    unfocusedTextColor   = Color.White
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { if (groupId.isNotBlank()) onJoin(groupId) }) {
                Text("Join", color = Purple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GroupsScreenPreview() {
    DarePackTheme {
        GroupsScreen(
            onCreateGroup = {},
            onNavHome     = {},
            onNavBucket   = {},
            onNavProfile  = {}
        )
    }
}
