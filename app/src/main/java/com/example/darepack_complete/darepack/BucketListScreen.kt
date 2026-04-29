package com.example.darepack_complete.darepack

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.darepack_complete.models.BucketItem
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.darepack.DarePackBottomNav
import com.example.darepack_complete.darepack.NavTab
import com.example.darepack_complete.viewmodel.BucketListViewModel

val CATEGORIES = listOf("Adventure", "Food", "Travel", "Social", "Creative", "Fitness", "Other")

@Composable
fun BucketListScreen(
    onSendDare: (String, String) -> Unit,
    onNavHome: () -> Unit,
    onNavGroups: () -> Unit,
    onNavProfile: () -> Unit,
    vm: BucketListViewModel = viewModel()
) {
    val groups          by vm.groups.collectAsState()
    val items           by vm.items.collectAsState()
    val selectedGroupId by vm.selectedGroupId.collectAsState()
    var showAddDialog   by remember { mutableStateOf(false) }

    LaunchedEffect(groups) {
        if (groups.isNotEmpty() && selectedGroupId.isBlank()) {
            vm.selectGroup(groups.first().groupId)
        }
    }

    Scaffold(
        containerColor = DarkBg,
        floatingActionButton = {
            if (selectedGroupId.isNotBlank()) {
                FloatingActionButton(
                    onClick        = { showAddDialog = true },
                    containerColor = Purple,
                    contentColor   = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add item")
                }
            }
        },
        bottomBar = {
            DarePackBottomNav(
                current   = NavTab.BUCKET,
                onHome    = onNavHome,
                onGroups  = onNavGroups,
                onBucket  = {},
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
                Text("Bucket List", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(top = 8.dp))
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(groups) { group ->
                        FilterChip(
                            selected = selectedGroupId == group.groupId,
                            onClick  = { vm.selectGroup(group.groupId) },
                            label    = { Text(group.name) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Purple,
                                selectedLabelColor     = Color.White,
                                containerColor         = DarkCard,
                                labelColor             = Color.Gray
                            )
                        )
                    }
                }
            }

            if (items.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxWidth()
                            .background(DarkCard, RoundedCornerShape(12.dp))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No items yet. Tap + to add one!", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                items(items) { item ->
                    BucketItemCard(item = item, onClick = { onSendDare(item.itemId, selectedGroupId) })
                }
            }
        }
    }

    if (showAddDialog) {
        AddItemDialog(
            onDismiss = { showAddDialog = false },
            onAdd     = { title, cat -> vm.addItem(title, cat); showAddDialog = false }
        )
    }
}

@Composable
fun BucketItemCard(item: BucketItem, onClick: () -> Unit) {
    val catColor = when (item.category) {
        "Adventure" -> Purple
        "Food"      -> Color(0xFFEF9F27)
        "Travel"    -> Teal
        "Social"    -> Pink
        "Fitness"   -> Color(0xFF639922)
        else        -> Color.Gray
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors   = CardDefaults.cardColors(containerColor = DarkCard),
        shape    = RoundedCornerShape(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(item.title, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier.background(catColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(item.category, color = catColor, fontSize = 11.sp)
                }
            }
            Text("Dare →", color = Purple, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun AddItemDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var title    by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(CATEGORIES.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = DarkSurface,
        title = { Text("Add bucket list item", color = Color.White) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value         = title,
                    onValueChange = { title = it },
                    label         = { Text("What's the challenge?") },
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
                Text("Category", color = Color.Gray, fontSize = 13.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CATEGORIES) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick  = { category = cat },
                            label    = { Text(cat, fontSize = 12.sp) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Purple,
                                selectedLabelColor     = Color.White,
                                containerColor         = DarkCard,
                                labelColor             = Color.Gray
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { if (title.isNotBlank()) onAdd(title, category) }, enabled = title.isNotBlank()) {
                Text("Add", color = Purple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun BucketListScreenPreview() {
    DarePackTheme {
        Column(Modifier.padding(16.dp)) {
            BucketItemCard(
                item = BucketItem(title = "Skydiving", category = "Adventure"),
                onClick = {}
            )
        }
    }
}

