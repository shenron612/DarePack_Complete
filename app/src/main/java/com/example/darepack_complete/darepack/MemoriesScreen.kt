package com.example.darepack_complete.darepack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.viewmodel.MemoriesViewModel
import com.example.darepack_complete.models.MemoryItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoriesScreen(
    onBack: () -> Unit,
    vm: MemoriesViewModel = viewModel()
) {
    val memories by vm.memories.collectAsState()
    val loading  by vm.loading.collectAsState()

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Memories", color = Color.White, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        }
    ) { padding ->
        when {
            loading -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Purple)
                }
            }

            memories.isEmpty() -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🏆", fontSize = 48.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No memories yet.",
                            color      = Color.White,
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "Complete a dare to add your first memory!",
                            color    = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier            = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding      = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            "${memories.size} memories",
                            color    = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                    items(memories) { memory ->
                        MemoryCard(memory = memory)
                    }
                }
            }
        }
    }
}

@Composable
fun MemoryCard(memory: MemoryItem) {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape  = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Proof photo
            if (memory.proof?.photoUrl?.isNotBlank() == true) {
                AsyncImage(
                    model              = memory.proof.photoUrl,
                    contentDescription = "Memory photo",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
            } else {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            Purple.copy(alpha = 0.15f),
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎯", fontSize = 36.sp)
                }
            }

            // Info
            Column(Modifier.padding(16.dp)) {
                Text(
                    memory.dare.title,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Dared by ${memory.dare.daredByName}",
                        color    = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        sdf.format(memory.dare.deadline.toDate()),
                        color    = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                // Caption
                memory.proof?.caption?.takeIf { it.isNotBlank() }?.let { cap ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "\"$cap\"",
                        color      = Color.White,
                        fontSize   = 14.sp,
                        fontStyle  = FontStyle.Italic
                    )
                }

                // Reactions
                memory.proof?.reactions?.takeIf { it.isNotEmpty() }?.let { reactions ->
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for ((emoji, list) in reactions.values.groupBy { it }) {
                            Box(
                                Modifier
                                    .background(DarkSurface, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("$emoji ${list.size}", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }

                // Completed badge
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier
                        .background(Teal.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("✓ Completed", color = Teal, fontSize = 11.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MemoriesEmptyPreview() {
    DarePackTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(DarkBg),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🏆", fontSize = 48.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    "No memories yet.",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
