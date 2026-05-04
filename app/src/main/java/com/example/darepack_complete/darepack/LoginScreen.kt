package com.example.darepack_complete.darepack

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.darepack_complete.ui.components.GradientButton

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

    Box(modifier = Modifier.fillMaxSize().background(LightBg)) {
        // Decorative background elements
        Box(
            modifier = Modifier
                .offset(x = (-50).dp, y = (-50).dp)
                .size(200.dp)
                .background(Brush.radialGradient(listOf(Purple.copy(alpha = 0.15f), Color.Transparent)), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 50.dp, y = 50.dp)
                .size(250.dp)
                .background(Brush.radialGradient(listOf(Teal.copy(alpha = 0.15f), Color.Transparent)), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isSignUp) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Purple.copy(alpha = 0.1f))
                        .clickable {
                            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = "Profile", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ArrowDropDown, null, tint = Purple, modifier = Modifier.size(24.dp))
                            Text("PHOTO", color = Purple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }

            Text(
                if (isSignUp) "Create Account" else "Welcome Back",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                if (isSignUp) "Join the dare community" else "Login to continue daring",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(40.dp))

            if (isSignUp) {
                LoginTextField(value = name, onValueChange = { name = it }, label = "Full Name", placeholder = "John Doe")
                Spacer(Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = gender, onValueChange = {}, readOnly = true, label = { Text("Gender") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, "Dropdown", Modifier.clickable { showGenderMenu = true }, tint = Purple) },
                        colors = loginTextFieldColors(),
                        shape = RoundedCornerShape(16.dp)
                    )
                    DropdownMenu(expanded = showGenderMenu, onDismissRequest = { showGenderMenu = false }) {
                        listOf("Male", "Female", "Other").forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = { gender = option; showGenderMenu = false })
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            LoginTextField(value = email, onValueChange = { email = it }, label = "Email", placeholder = "example@mail.com", keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            Spacer(Modifier.height(16.dp))
            LoginTextField(value = password, onValueChange = { password = it }, label = "Password", placeholder = "••••••••", isPassword = true)

            if (isSignUp) {
                Spacer(Modifier.height(16.dp))
                LoginTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "Confirm Password", placeholder = "••••••••", isPassword = true)
            }

            Spacer(Modifier.height(32.dp))

            if (state is LoginState.Error) {
                Text((state as LoginState.Error).message, color = Pink, fontSize = 12.sp, modifier = Modifier.padding(bottom = 16.dp))
            }

            if (state is LoginState.Loading) {
                CircularProgressIndicator(color = Purple)
            } else {
                GradientButton(
                    text = if (isSignUp) "Sign Up" else "Sign In",
                    onClick = {
                        if (isSignUp) {
                            if (password == confirmPassword) vm.signUp(name, gender, email, password, imageUri)
                        } else {
                            vm.login(email, password)
                        }
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            TextButton(onClick = { isSignUp = !isSignUp }) {
                Text(
                    if (isSignUp) "Already have an account? Sign In" else "New here? Create Account",
                    color = PurpleDark,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LoginTextField(value: String, onValueChange: (String) -> Unit, label: String, placeholder: String, isPassword: Boolean = false, keyboardOptions: KeyboardOptions = KeyboardOptions.Default) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label) },
        placeholder = { Text(placeholder, color = TextSecondary.copy(alpha = 0.5f)) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = keyboardOptions, colors = loginTextFieldColors(), shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun loginTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Purple, unfocusedBorderColor = LightSurface, focusedLabelColor = Purple,
    unfocusedLabelColor = TextSecondary, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, cursorColor = Purple
)
