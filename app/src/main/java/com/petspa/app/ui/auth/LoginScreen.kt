package com.petspa.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.AuthViewModel

// Màn hình đăng nhập thống nhất cho Customer/Staff/Owner
@Composable
fun LoginScreen(authViewModel: AuthViewModel, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginState by authViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            authViewModel.clearLoginState()
            onLoginSuccess()
        }
    }

    Column(Modifier.fillMaxSize().background(PetSpaColors.Background)) {
        Box(
            Modifier.fillMaxWidth().height(220.dp).background(
                Brush.verticalGradient(listOf(PetSpaColors.PetPink, PetSpaColors.PetPinkDeep))
            ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🐾", fontSize = 48.sp)
                Text("Pet Spa", style = MaterialTheme.typography.headlineMedium, color = PetSpaColors.Background)
                Text("Đăng nhập hệ thống", color = PetSpaColors.Background.copy(0.9f))
            }
        }
        Column(Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
            Text("Email", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                placeholder = { Text("email@example.com") },
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))
            Text("Mật khẩu", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
            if (loginState is UiState.Error) {
                Text((loginState as UiState.Error).message, color = PetSpaColors.Destructive, modifier = Modifier.padding(top = 8.dp))
            }
            Spacer(Modifier.height(24.dp))
            if (loginState is UiState.Loading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PetSpaColors.PetPinkDeep)
                }
            } else {
                PrimaryButton("Đăng nhập", onClick = { authViewModel.login(email, password) })
            }
            Spacer(Modifier.height(16.dp))
            AppCard {
                Text("Tài khoản demo:", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
                Text("Khách: vanvan@gmail.com / Password1", style = MaterialTheme.typography.bodySmall)
                Text("Nhân viên: tuan.tran@petspa.com / password123", style = MaterialTheme.typography.bodySmall)
                Text("Chủ spa: admin@petspa.com / admin123", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
