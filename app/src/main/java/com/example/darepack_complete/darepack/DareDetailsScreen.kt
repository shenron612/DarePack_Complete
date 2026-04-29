package com.example.darepack_complete.darepack

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.viewmodel.DareDetailState
import com.example.darepack_complete.viewmodel.DareDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DareDetailScreen(
    dareId: String,
    onBack: () -> Unit,
    vm: DareDetailViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    val dare  by vm.dare.collectAsState()
    val proof by vm.proof.collectAsState()

    var caption      by remember { mutableStateOf("") }
    var selectedUri  by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { selectedUri = it } }

    LaunchedEffect(dareId) { vm.load(dareId) }

    val sdf     = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val isMyDare = dare?.daredTo == com.google.firebase.auth.FirebaseAuth
        .getInstance().currentUser?.uid

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Dare detail", color = Color.White, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        }
    ) { padding ->
        when (state) {
            is DareDetailState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Purple)
                }
            }

            is DareDetailState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text((state as DareDetailState.Error).message, color = Color.Gray)
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Status badge
                    val statusColor = when (dare?.status) {
                        "completed" -> Teal
                        "expired"   -> Color(0xFFE24B4A)
                        else        -> Purple
                    }
                    Box(
                        Modifier
                            .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(dare?.status ?: "", color = statusColor, fontSize = 12.sp)
                    }

                    // Title
                    Text(
                        dare?.title ?: "",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )

                    // Meta info
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkCard),
                        shape  = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetaRow(label = "From",     value = dare?.daredByName ?: "")
                            MetaRow(label = "To",       value = dare?.daredToName ?: "")
                            if (dare?.status == "pending") {
                                MetaRow(
                                    label = "Deadline",
                                    value = dare?.deadline?.toDate()?.let { sdf.format(it) } ?: ""
                                )
                            }
                        }
                    }

                    // Proof section (if completed)
                    proof?.let { p ->
                        Text("Proof", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                        AsyncImage(
                            model              = p.photoUrl,
                            contentDescription = "Proof photo",
                            contentScale       = ContentScale.Crop,
                            modifier           = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        if (p.caption.isNotBlank()) {
                            Text(p.caption, color = Color.Gray, fontSize = 14.sp)
                        }
                        // Reactions
                        if (p.reactions.isNotEmpty()) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                p.reactions.values.groupBy { it }
                                    .forEach { (emoji, list) ->
                                        Box(
                                            Modifier
                                                .background(DarkCard, RoundedCornerShape(20.dp))
                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("$emoji ${list.size}", fontSize = 13.sp, color = Color.White)
                                        }
                                    }
                            }
                        }
                        // Add reaction
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("🔥", "😂", "👏", "😮", "❤️").forEach { emoji ->
                                TextButton(
                                    onClick = { vm.addReaction(p.proofId, emoji) },
                                    contentPadding = PaddingValues(4.dp)
                                ) {
                                    Text(emoji, fontSize = 20.sp)
                                }
                            }
                        }
                    }

                    // Complete dare section (only for the recipient, only if pending)
                    if (isMyDare && dare?.status == "pending") {
                        HorizontalDivider(color = Color(0xFF2A2A3A))
                        Text(
                            "Complete this dare",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color      = Color.White
                        )

                        // Photo picker
                        if (selectedUri != null) {
                            AsyncImage(
                                model              = selectedUri,
                                contentDescription = "Selected photo",
                                contentScale       = ContentScale.Crop,
                                modifier           = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }

                        OutlinedButton(
                            onClick  = { imagePicker.launch("image/*") },
                            modifier = Modifier.fillMaxWidth(),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.outlinedButtonColors(contentColor = Purple)
                        ) {
                            Text(if (selectedUri == null) "Pick proof photo" else "Change photo")
                        }

                        OutlinedTextField(
                            value         = caption,
                            onValueChange = { caption = it },
                            label         = { Text("Add a caption (optional)") },
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

                        if (state is DareDetailState.Error) {
                            Text(
                                (state as DareDetailState.Error).message,
                                color    = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp
                            )
                        }

                        Button(
                            onClick  = {
                                selectedUri?.let {
                                    vm.completeDare(dareId, it, caption)
                                }
                            },
                            enabled  = selectedUri != null && state !is DareDetailState.Uploading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            colors   = ButtonDefaults.buttonColors(containerColor = Teal),
                            shape    = RoundedCornerShape(12.dp)
                        ) {
                            if (state is DareDetailState.Uploading) {
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(20.dp),
                                    color       = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Mark as completed ✓", fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun MetaRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 13.sp)
        Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}
