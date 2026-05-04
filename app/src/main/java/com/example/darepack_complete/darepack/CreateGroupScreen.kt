package com.example.darepack_complete.darepack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.viewmodel.CreateGroupState
import com.example.darepack_complete.viewmodel.CreateGroupViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onBack: () -> Unit,
    onGroupCreated: (String) -> Unit,
    vm: CreateGroupViewModel = viewModel()
) {
    val state by vm.state.collectAsState()
    var name  by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        if (state is CreateGroupState.Success) {
            val groupId = (state as CreateGroupState.Success).groupId
            onGroupCreated(groupId)
            vm.resetState()
        }
    }

    Scaffold(
        containerColor = LightBg,
        topBar = {
            TopAppBar(
                title = {
                    Text("Create a group", color = TextPrimary, fontWeight = FontWeight.Medium)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBg)
            )
        }
    ) { padding ->
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                "Name your crew",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = TextPrimary
            )
            Text(
                "Give your group a name your friends will recognise.",
                fontSize = 14.sp,
                color    = TextSecondary,
                modifier = Modifier.padding(top = 8.dp, bottom = 28.dp)
            )

            OutlinedTextField(
                value         = name,
                onValueChange = { name = it },
                label         = { Text("Group name") },
                placeholder   = { Text("e.g. Squad Goals") },
                modifier      = Modifier.fillMaxWidth(),
                singleLine    = true,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Purple,
                    unfocusedBorderColor = LightSurface,
                    focusedLabelColor    = Purple,
                    unfocusedLabelColor  = TextSecondary,
                    focusedTextColor     = TextPrimary,
                    unfocusedTextColor   = TextPrimary,
                    cursorColor          = Purple
                )
            )

            Spacer(Modifier.height(16.dp))

            // Info box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Purple.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text(
                    "After creating the group, share the Group ID with friends so they can join from the Groups screen.",
                    fontSize = 13.sp,
                    color    = Purple
                )
            }

            Spacer(Modifier.weight(1f))

            if (state is CreateGroupState.Error) {
                Text(
                    (state as CreateGroupState.Error).message,
                    color    = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Button(
                onClick  = { vm.createGroup(name) },
                enabled  = name.isNotBlank() && state !is CreateGroupState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Purple),
                shape    = RoundedCornerShape(12.dp)
            ) {
                if (state is CreateGroupState.Loading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        color       = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Create group", fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateGroupScreenPreview() {
    DarePackTheme {
        Box(Modifier.background(LightBg).fillMaxSize()) {
            CreateGroupScreen(onBack = {}, onGroupCreated = {})
        }
    }
}
