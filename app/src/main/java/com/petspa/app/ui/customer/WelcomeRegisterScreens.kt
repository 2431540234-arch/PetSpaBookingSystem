package com.petspa.app.ui.customer

import android.Manifest
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.AuthViewModel

@Composable
fun WelcomeScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(PetSpaColors.PetPinkSurface, Color.White)))
    ) {
        // Hero
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🐾", fontSize = 80.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🐩", fontSize = 32.sp)
                Text("🛁", fontSize = 32.sp)
                Text("✂️", fontSize = 32.sp)
                Text("🌸", fontSize = 32.sp)
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "Chào mừng đến với\nPet Spa",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = PetSpaColors.PetPinkDeep,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )
            Text("Booking System", color = PetSpaColors.MutedForeground, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            Text(
                "Chăm sóc và làm đẹp cho thú cưng của bạn một cách chuyên nghiệp & yêu thương",
                color = PetSpaColors.MutedForeground.copy(0.7f),
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 16.dp).padding(horizontal = 40.dp),
                lineHeight = 22.sp
            )
        }

        // Role Card
        Column(Modifier.padding(24.dp).padding(bottom = 32.dp)) {
            AppCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👤", fontSize = 32.sp)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Tôi là Khách Hàng", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        Text("Đặt lịch và theo dõi dịch vụ cho thú cưng", fontSize = 12.sp, color = PetSpaColors.MutedForeground)
                    }
                }
                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PrimaryButton("Đăng Nhập", onLogin, modifier = Modifier.weight(1f))
                    SecondaryButton("Đăng Ký", onRegister, modifier = Modifier.weight(1f))
                }
            }
            
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("🐶", fontSize = 14.sp)
                Text("  Yêu thương · Chuyên nghiệp · Tận tâm  ", fontSize = 12.sp, color = PetSpaColors.MutedForeground.copy(0.5f))
                Text("🐱", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun LoginScreen(vm: AuthViewModel, onBack: () -> Unit, onRegister: () -> Unit, onSuccess: () -> Unit) {
    var email by remember { mutableStateOf("vanvan@gmail.com") }
    var password by remember { mutableStateOf("Password1") }
    var showPassword by remember { mutableStateOf(false) }
    
    val loginState by vm.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is UiState.Success) {
            onSuccess()
            vm.clearLoginState()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(PetSpaColors.PetPinkSurface.copy(0.4f), Color.White, Color(0xFFF1F5F9).copy(0.4f)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShadcnCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Logo Section
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(PetSpaColors.PetPinkDeep),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Pets, null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                        Text("Pet Spa Booking", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Khách Hàng - Đăng nhập", color = Color(0xFF64748B), fontSize = 14.sp)
                    }

                    // Form
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        ShadcnInput(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email hoặc Số điện thoại",
                            placeholder = "vanvan@gmail.com",
                            leadingIcon = Icons.Default.Mail
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Mật khẩu",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF0F172A)
                            )
                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("••••••••", color = Color(0xFF64748B)) },
                                shape = RoundedCornerShape(8.dp),
                                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                leadingIcon = { Icon(Icons.Default.Lock, null, modifier = Modifier.size(18.dp), tint = Color(0xFF64748B)) },
                                trailingIcon = {
                                    IconButton(onClick = { showPassword = !showPassword }) {
                                        Icon(
                                            if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            null,
                                            modifier = Modifier.size(18.dp),
                                            tint = Color(0xFF64748B)
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PetSpaColors.PetPinkDeep,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color(0xFFF8FAFC)
                                ),
                                singleLine = true
                            )
                        }

                        if (loginState is UiState.Error) {
                            Text(
                                (loginState as UiState.Error).message,
                                color = PetSpaColors.Destructive,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        ShadcnButton(
                            text = if (loginState is UiState.Loading) "Đang đăng nhập..." else "Đăng nhập",
                            onClick = { vm.login(email, password) },
                            modifier = Modifier.fillMaxWidth(),
                            isLoading = loginState is UiState.Loading
                        )
                    }

                    // Demo Accounts
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC))
                            .padding(12.dp)
                    ) {
                        Text(
                            "Demo: vanvan@gmail.com / Password1",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Chưa có tài khoản? ", fontSize = 14.sp, color = Color(0xFF64748B))
                Text(
                    "Đăng ký ngay",
                    fontSize = 14.sp,
                    color = PetSpaColors.PetPinkDeep,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onRegister() }
                )
            }
        }
    }
}

@Composable
fun RegisterScreen(onBack: () -> Unit, onLogin: () -> Unit, onSuccess: () -> Unit) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Contact Picker Launcher
    val contactLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickContact()) { uri ->
        if (uri != null) {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val id = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val hasPhone = it.getInt(it.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    if (hasPhone > 0) {
                        val phones = context.contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                            null,
                            null
                        )
                        phones?.use { pCursor ->
                            if (pCursor.moveToFirst()) {
                                phone = pCursor.getString(pCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            }
                        }
                    }
                }
            }
        }
    }

    val contactPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) contactLauncher.launch(null)
    }

    Column(Modifier.fillMaxSize().background(Color.White)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(PetSpaColors.PetPink, PetSpaColors.PetPinkDeep)))
                .padding(top = 48.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
        ) {
            Column {
                Text("← Quay lại", color = Color.White.copy(0.8f), fontSize = 14.sp, modifier = Modifier.clickable { onBack() })
                Spacer(Modifier.height(16.dp))
                Text("Đăng Ký 🐾", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Tạo tài khoản để đặt lịch cho thú cưng", color = Color.White.copy(0.7f), fontSize = 13.sp)
            }
        }

        Column(Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
            InputField(name, { name = it }, label = "Họ tên", placeholder = "Nguyễn Văn A", leadingIcon = Icons.Default.Person, required = true)
            Spacer(Modifier.height(12.dp))
            InputField(email, { email = it }, label = "Email", placeholder = "email@gmail.com", leadingIcon = Icons.Default.Email, required = true)
            Spacer(Modifier.height(12.dp))
            InputField(
                value = phone,
                onValueChange = { phone = it },
                label = "Số điện thoại",
                placeholder = "0912345678",
                leadingIcon = Icons.Default.Phone,
                required = true,
                trailingIcon = {
                    IconButton(onClick = {
                        val permission = Manifest.permission.READ_CONTACTS
                        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                            contactLauncher.launch(null)
                        } else {
                            contactPermissionLauncher.launch(permission)
                        }
                    }) {
                        Icon(Icons.Default.ContactPage, null, tint = PetSpaColors.PetPinkDeep)
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
            InputField(password, { password = it }, label = "Mật khẩu", placeholder = "••••••••", leadingIcon = Icons.Default.Lock, visualTransformation = PasswordVisualTransformation(), required = true)
            Spacer(Modifier.height(12.dp))
            InputField(confirmPassword, { confirmPassword = it }, label = "Xác nhận mật khẩu", placeholder = "••••••••", leadingIcon = Icons.Default.Lock, visualTransformation = PasswordVisualTransformation(), required = true)

            Spacer(Modifier.height(32.dp))
            PrimaryButton("Tạo Tài Khoản", onClick = onSuccess)

            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Đã có tài khoản? ", fontSize = 14.sp, color = PetSpaColors.MutedForeground)
                Text("Đăng nhập", fontSize = 14.sp, color = PetSpaColors.PetPinkDeep, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onLogin() })
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}
