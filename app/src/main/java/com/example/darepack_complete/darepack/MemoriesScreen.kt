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
import androidx.compose.ui.graphics.Brush
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
import com.example.darepack_complete.ui.components.LuminousBadge
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
        containerColor = LightBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MEMORIES", color = PurpleDark, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = LightBg)
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
                        Box(
                            Modifier.size(100.dp).background(Purple.copy(alpha = 0.1f), RoundedCornerShape(30.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏆", fontSize = 48.sp)
                        }
                        Spacer(Modifier.height(24.dp))
                        Text(
                            "Your archive is empty",
                            color      = TextPrimary,
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "Complete a dare to immortalize it!",
                            color    = TextSecondary,
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
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        LuminousBadge(text = "${memories.size} ARCHIVED DARES", color = Purple)
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
        colors = CardDefaults.cardColors(containerColor = LightCard),
        shape  = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(LightSurface, Color.Transparent)))
    ) {
        Column {
            // Proof photo with gradient overlay
            Box {
                if (memory.proof?.photoUrl?.isNotBlank() == true) {
                    AsyncImage(
                        model              = memory.proof.photoUrl,
                        contentDescription = "Memory photo",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    )
                } else {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .background(
                                Brush.linearGradient(PurpleGradient),
                                RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎯", fontSize = 48.sp)
                    }
                }
                
                // Completed badge on top of image
                Box(Modifier.align(Alignment.TopEnd).padding(12.dp)) {
                    LuminousBadge(text = "COMPLETED", color = Teal)
                }
            }

            // Info
            Column(Modifier.padding(20.dp)) {
                Text(
                    memory.dare.title,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color      = TextPrimary
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(6.dp).background(Purple, RoundedCornerShape(2.dp)))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Dared by ${memory.dare.daredByName}",
                            color    = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        sdf.format(memory.dare.deadline.toDate()),
                        color    = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Caption
                memory.proof?.caption?.takeIf { it.isNotBlank() }?.let { cap ->
                    Spacer(Modifier.height(16.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(LightBg, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            "\"$cap\"",
                            color      = TextPrimary,
                            fontSize   = 14.sp,
                            fontStyle  = FontStyle.Italic,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Reactions
                memory.proof?.reactions?.takeIf { it.isNotEmpty() }?.let { reactions ->
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for ((emoji, list) in reactions.values.groupBy { it }) {
                            Box(
                                Modifier
                                    .background(Purple.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text("$emoji ${list.size}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PurpleDark)
                            }
                        }
                    }
                }
            }
        }
    }
}
