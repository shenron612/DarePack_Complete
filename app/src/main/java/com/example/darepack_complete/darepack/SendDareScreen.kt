package com.example.darepack_complete.darepack

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.darepack_complete.models.UserModel as User
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.viewmodel.SendDareState
import com.example.darepack_complete.viewmodel.SendDareViewModel
import com.example.darepack_complete.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendDareScreen(
    itemId: String,
    groupId: String,
    onDareSent: () -> Unit,
    vm: SendDareViewModel = viewModel()
) {
    val state   by vm.state.collectAsState()
    val item    by vm.item.collectAsState()
    val members by vm.members.collectAsState()

    var selectedUser     by remember { mutableStateOf<User?>(null) }
    var deadlineMillis   by remember { mutableStateOf(
        System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000  // default 7 days
    )}
    var showDatePicker   by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.load(itemId, groupId) }
    LaunchedEffect(state) { if (state is SendDareState.Success) onDareSent() }

    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Scaffold(
        containerColor = CyberDark,
        topBar = {
            TopAppBar(
                title = { Text("Send a dare", color = CyberText, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onDareSent) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CyberText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDark)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            CyberBackground()
            
            LazyColumn(
                modifier            = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding      = PaddingValues(vertical = 16.dp)
            ) {
                // Dare title card
                item {
                    CyberCard(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Text("Dare", fontSize = 11.sp, color = CyberBlue)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                item?.title ?: "Loading...",
                                fontSize   = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color      = CyberText
                            )
                            Text(
                                item?.category ?: "",
                                fontSize = 12.sp,
                                color    = CyberBlue.copy(alpha = 0.7f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Pick a friend
                item {
                    Text(
                        "Who do you dare?",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color      = CyberText
                    )
                }

                if (members.isEmpty()) {
                    item {
                        Text("No other members in this group yet.", color = CyberBlue.copy(alpha = 0.5f), fontSize = 13.sp)
                    }
                } else {
                    items(members) { user ->
                        MemberPickerRow(
                            user       = user,
                            isSelected = selectedUser?.userId == user.userId,
                            onClick    = { selectedUser = user }
                        )
                    }
                }

                // Deadline picker
                item {
                    Text(
                        "Set a deadline",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color      = CyberText
                    )
                    Spacer(Modifier.height(8.dp))
                    CyberCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDatePicker = true }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(sdf.format(Date(deadlineMillis)), color = CyberText, fontSize = 15.sp)
                            Text("Change →", color = CyberBlue, fontSize = 13.sp)
                        }
                    }
                }

                // Quick deadline buttons
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("3 days" to 3, "1 week" to 7, "2 weeks" to 14).forEach { (label, days) ->
                            FilterChip(
                                selected = false,
                                onClick  = {
                                    deadlineMillis = System.currentTimeMillis() +
                                            days.toLong() * 24 * 60 * 60 * 1000
                                },
                                label  = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = CyberSurface,
                                    labelColor     = CyberText.copy(alpha = 0.7f),
                                    selectedContainerColor = CyberBlue,
                                    selectedLabelColor = Color.White
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = CyberBlue.copy(alpha = 0.3f),
                                    enabled = true,
                                    selected = false
                                )
                            )
                        }
                    }
                }

                // Error
                if (state is SendDareState.Error) {
                    item {
                        Text(
                            (state as SendDareState.Error).message,
                            color    = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp
                        )
                    }
                }

                // Send button
                item {
                    GradientButton(
                        text = if (selectedUser != null) "Dare ${selectedUser!!.name}!" else "Select a friend first",
                        onClick = { selectedUser?.let { vm.sendDare(it, deadlineMillis) } },
                        modifier = Modifier.fillMaxWidth(),
                        gradient = CyberGradient
                    )
                }
            }
        }
    }
}

@Composable
fun MemberPickerRow(user: User, isSelected: Boolean, onClick: () -> Unit) {
    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier         = Modifier
                    .size(40.dp)
                    .background(CyberBlue.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    user.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color      = CyberBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(user.name, color = CyberText, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                Text("${user.totalCompleted} dares completed", color = CyberText.copy(alpha = 0.6f), fontSize = 12.sp)
            }
            if (isSelected) {
                Box(
                    modifier         = Modifier
                        .size(22.dp)
                        .background(CyberBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✓", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}
