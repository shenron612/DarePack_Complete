package com.example.darepack_complete.darepack

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
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
import com.example.darepack_complete.models.BucketItem
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.darepack.DarePackBottomNav
import com.example.darepack_complete.darepack.NavTab
import com.example.darepack_complete.viewmodel.BucketListViewModel
import com.example.darepack_complete.ui.components.LuminousBadge

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
    val suggestions     by vm.suggestions.collectAsState()
    val selectedGroupId by vm.selectedGroupId.collectAsState()
    var showAddDialog   by remember { mutableStateOf(false) }

    LaunchedEffect(groups) {
        if (groups.isNotEmpty() && selectedGroupId.isBlank()) {
            vm.selectGroup(groups.first().groupId)
        }
    }

    Scaffold(
        containerColor = LightBg,
        floatingActionButton = {
            if (selectedGroupId.isNotBlank()) {
                LargeFloatingActionButton(
                    onClick        = { showAddDialog = true },
                    containerColor = Color.Transparent,
                    contentColor   = Color.White,
                    shape          = RoundedCornerShape(20.dp),
                    modifier       = Modifier.background(Brush.linearGradient(PurpleGradient), RoundedCornerShape(20.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add item", modifier = Modifier.size(32.dp))
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)) {
                    Text("Bucket List", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = PurpleDark)
                    Text("Collective challenges for your pack", fontSize = 14.sp, color = TextSecondary)
                }
            }

            // Suggestions section
            item {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Icon(Icons.Default.Info, null, tint = Yellow, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Top Suggestions", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(suggestions) { suggestion ->
                        SuggestionCard(
                            item = suggestion,
                            onClick = { if (selectedGroupId.isNotBlank()) vm.addItem(suggestion.title, suggestion.category) }
                        )
                    }
                }
            }

            item {
                Text("Select Group", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(groups) { group ->
                        FilterChip(
                            selected = selectedGroupId == group.groupId,
                            onClick  = { vm.selectGroup(group.groupId) },
                            label    = { Text(group.name) },
                            shape    = RoundedCornerShape(12.dp),
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Purple,
                                selectedLabelColor     = Color.White,
                                containerColor         = LightCard,
                                labelColor             = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selectedGroupId == group.groupId, borderColor = LightSurface, selectedBorderColor = Purple)
                        )
                    }
                }
            }

            if (items.isEmpty()) {
                item {
                    Box(
                        Modifier.fillMaxWidth()
                            .background(LightCard, RoundedCornerShape(16.dp))
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No items yet. Tap + to add one!", color = TextSecondary, fontSize = 14.sp)
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
fun SuggestionCard(item: BucketItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = LightCard),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(LightSurface, Color.Transparent)))
    ) {
        Column(Modifier.padding(16.dp)) {
            Box(Modifier.background(Purple.copy(alpha = 0.1f), CircleShape).padding(horizontal = 10.dp, vertical = 4.dp)) {
                Text(item.category, color = Purple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            Text(item.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 2, minLines = 2)
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .background(Brush.linearGradient(PurpleGradient), RoundedCornerShape(12.dp)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("ADD", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BucketItemCard(item: BucketItem, onClick: () -> Unit) {
    val catColor = when (item.category) {
        "Adventure" -> Purple
        "Food"      -> Yellow
        "Travel"    -> Teal
        "Social"    -> Pink
        "Fitness"   -> Color(0xFF10B981) // Vibrant Green
        else        -> Color.Gray
    }
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors   = CardDefaults.cardColors(containerColor = LightCard),
        shape    = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(Color.Transparent, LightSurface)))
    ) {
        Row(
            Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(item.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(6.dp))
                LuminousBadge(text = item.category, color = catColor)
            }
            Box(
                Modifier.size(36.dp).background(Purple.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ArrowForward, null, tint = Purple, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun AddItemDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var title    by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(CATEGORIES.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = LightCard,
        title = { Text("Add Challenge", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value         = title,
                    onValueChange = { title = it },
                    label         = { Text("What's the challenge?") },
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
                Text("Category", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CATEGORIES) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick  = { category = cat },
                            label    = { Text(cat, fontSize = 12.sp) },
                            shape    = RoundedCornerShape(12.dp),
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Purple,
                                selectedLabelColor     = Color.White,
                                containerColor         = LightSurface,
                                labelColor             = TextSecondary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (title.isNotBlank()) onAdd(title, category) },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        }
    )
}
