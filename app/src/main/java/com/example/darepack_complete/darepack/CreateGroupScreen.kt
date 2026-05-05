package com.example.darepack_complete.darepack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.viewmodel.CreateGroupState
import com.example.darepack_complete.viewmodel.CreateGroupViewModel
import com.example.darepack_complete.ui.components.*


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
        containerColor = CyberDark,
        topBar = {
            TopAppBar(
                title = {
                    Text("Create a group", color = CyberText, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CyberText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CyberDark)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            CyberBackground()
            
            Column(
                modifier            = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))

                Text(
                    "Name your crew",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = CyberText
                )
                Text(
                    "Give your group a name your friends will recognise.",
                    fontSize = 14.sp,
                    color    = CyberBlue.copy(alpha = 0.7f),
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
                        focusedBorderColor   = CyberBlue,
                        unfocusedBorderColor = CyberBlue.copy(alpha = 0.3f),
                        focusedLabelColor    = CyberBlue,
                        unfocusedLabelColor  = CyberBlue.copy(alpha = 0.6f),
                        focusedTextColor     = CyberText,
                        unfocusedTextColor   = CyberText,
                        cursorColor          = CyberBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(16.dp))

                // Info box
                CyberCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "After creating the group, share the Group ID with friends so they can join from the Groups screen.",
                        fontSize = 13.sp,
                        color    = CyberText.copy(alpha = 0.8f)
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

                if (state is CreateGroupState.Loading) {
                    CircularProgressIndicator(color = CyberBlue)
                } else {
                    GradientButton(
                        text = "Create group",
                        onClick = { vm.createGroup(name) },
                        modifier = Modifier.fillMaxWidth(),
                        gradient = CyberGradient
                    )
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}
