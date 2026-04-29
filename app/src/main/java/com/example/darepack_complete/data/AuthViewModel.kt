package com.example.darepack_complete.data


import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.darepack_complete.models.UserModel
import com.example.darepack_complete.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

    class AuthViewModel : ViewModel() {

        private val auth: FirebaseAuth = FirebaseAuth.getInstance()

        fun isUserLoggedIn(): Boolean {
            return auth.currentUser != null
        }

        fun signup(
            username: String,
            email: String,
            phone: String,
            password: String,
            confirmpassword: String,
            navController: NavController,
            context: Context
        ) {
            if (username.isBlank() || email.isBlank() || phone.isBlank() ||
                password.isBlank() || confirmpassword.isBlank()
            ) {
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_LONG).show()
                return
            }
            if (password != confirmpassword) {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG).show()
                return
            }
            if (password.length < 6) {
                Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_LONG)
                    .show()
                return
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: ""
                        val user = UserModel(
                            username = username,
                            email = email,
                            phone = phone,
                            userId = userId
                        )
                        saveUserToDatabase(user, navController, context)
                    } else {
                        Toast.makeText(
                            context,
                            task.exception?.message ?: "Registration failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        private fun saveUserToDatabase(
            user: UserModel,
            navController: NavController,
            context: Context
        ) {
            val dbRef = FirebaseDatabase.getInstance().getReference("Users/${user.userId}")
            dbRef.setValue(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Registration Successful", Toast.LENGTH_LONG).show()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                } else {
                    Toast.makeText(
                        context,
                        task.exception?.message ?: "Failed to save user",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        fun login(
            email: String,
            password: String,
            navController: NavController,
            context: Context
        ) {
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Email and Password are required", Toast.LENGTH_LONG).show()
                return
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
                        navController.navigate(Routes.HOME) {
                            popUpTo(0)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            task.exception?.message ?: "Login failed",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        fun logout(navController: NavController, context: Context) {
            auth.signOut()
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
            navController.navigate(Routes.LOGIN) {
                popUpTo(0)
            }
        }
    }

