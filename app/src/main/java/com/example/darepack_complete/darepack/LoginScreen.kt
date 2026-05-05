package com.example.darepack_complete.darepack

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.darepack_complete.ui.theme.*
import com.example.darepack_complete.viewmodel.LoginState
import com.example.darepack_complete.viewmodel.LoginViewModel
import com.example.darepack_complete.ui.components.*

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val state by vm.loginState.collectAsState()

    var isSignUp by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showGenderMenu by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> imageUri = uri }

    LaunchedEffect(state) {
        if (state is LoginState.Success) onLoginSuccess()
    }

    Box(modifier = Modifier.fillMaxSize().background(CyberDark).systemBarsPadding()) {
        CyberBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Top Icon (Airplane in circle style)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .border(2.dp, CyberBlue.copy(alpha = 0.5f), CircleShape)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(CyberBlue.copy(alpha = 0.1f))
                        .clickable {
                            if (isSignUp) launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null && isSignUp) {
                        AsyncImage(model = imageUri, contentDescription = "Profile", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else {
                        Icon(
                            imageVector = if (isSignUp) Icons.Default.AddCircle else Icons.Default.Star,
                            contentDescription = "Logo",
                            tint = CyberBlue,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                if (isSignUp) "Create Account" else "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = CyberText
            )
            Text(
                if (isSignUp) "Join the dare community" else "Login to continue daring",
                fontSize = 14.sp,
                color = CyberBlue.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(40.dp))

            // Card Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CyberSurface.copy(alpha = 0.4f))
                    .border(1.dp, CyberBlue.copy(alpha = 0.2f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                if (isSignUp) {
                    LoginTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Username",
                        placeholder = "AlphaPilot",
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = CyberBlue) }
                    )
                    Spacer(Modifier.height(16.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = gender, onValueChange = {}, readOnly = true, 
                            label = { Text("Gender", color = CyberBlue.copy(alpha = 0.6f)) },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Face, null, tint = CyberBlue) },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Dropdown", Modifier.clickable { showGenderMenu = true }, tint = CyberBlue) },
                            colors = loginTextFieldColors(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        DropdownMenu(
                            expanded = showGenderMenu, 
                            onDismissRequest = { showGenderMenu = false },
                            modifier = Modifier.background(CyberSurface)
                        ) {
                            listOf("Male", "Female", "Other").forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, color = CyberText) }, 
                                    onClick = { gender = option; showGenderMenu = false }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                LoginTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email",
                    placeholder = "A.P@skyline.org",
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = CyberBlue) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(Modifier.height(16.dp))
                
                LoginTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "••••••••",
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = CyberBlue) },
                    isPassword = true
                )

                if (isSignUp) {
                    Spacer(Modifier.height(16.dp))
                    LoginTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirm Password",
                        placeholder = "••••••••",
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = CyberBlue) },
                        isPassword = true
                    )
                }

                Spacer(Modifier.height(32.dp))

                if (state is LoginState.Error) {
                    Text((state as LoginState.Error).message, color = Pink, fontSize = 12.sp, modifier = Modifier.padding(bottom = 16.dp))
                }

                if (state is LoginState.Loading) {
                    CircularProgressIndicator(color = CyberBlue, modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    GradientButton(
                        text = if (isSignUp) "Register" else "Sign In",
                        gradient = CyberGradient,
                        onClick = {
                            if (isSignUp) {
                                vm.signUp(name, gender, email, password, confirmPassword, imageUri)
                            } else {
                                vm.login(email, password)
                            }
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (isSignUp) "Already have an account? " else "New here? ",
                        color = CyberText.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                    Text(
                        if (isSignUp) "Sign In" else "Create Account",
                        color = CyberBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { isSignUp = !isSignUp }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginTextField(
    value: String, 
    onValueChange: (String) -> Unit, 
    label: String, 
    placeholder: String, 
    isPassword: Boolean = false, 
    leadingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, 
        label = { Text(label, color = CyberBlue.copy(alpha = 0.6f)) },
        placeholder = { Text(placeholder, color = CyberText.copy(alpha = 0.3f)) },
        leadingIcon = leadingIcon,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = keyboardOptions, 
        colors = loginTextFieldColors(), 
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun loginTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CyberBlue, 
    unfocusedBorderColor = CyberBlue.copy(alpha = 0.3f), 
    focusedLabelColor = CyberBlue,
    unfocusedLabelColor = CyberBlue.copy(alpha = 0.6f), 
    focusedTextColor = CyberText, 
    unfocusedTextColor = CyberText, 
    cursorColor = CyberBlue,
    focusedContainerColor = CyberSurface.copy(alpha = 0.2f),
    unfocusedContainerColor = Color.Transparent
)
