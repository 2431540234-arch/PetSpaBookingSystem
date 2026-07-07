package com.petspa.app.ui.customer

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.util.ImageUtils
import com.petspa.app.viewmodel.AuthViewModel
import com.petspa.app.viewmodel.CustomerViewModel
import java.io.File

@Composable
fun ProfileScreen(
    vm: CustomerViewModel,
    authVm: AuthViewModel,
    onEdit: () -> Unit,
    onChangePassword: () -> Unit,
    onNotifSettings: () -> Unit,
    onPaymentHistory: () -> Unit,
    onLogout: () -> Unit
) {
    LaunchedEffect(Unit) {
        vm.loadUser()
        vm.loadBookings()
    }
    val userState by vm.user.collectAsState()
    val bookingsState by vm.bookings.collectAsState()
    val context = LocalContext.current

    val bookings = (bookingsState as? UiState.Success)?.data ?: emptyList()
    val stats = listOf(
        "Lịch hẹn" to bookings.size.toString(),
        "Hoàn thành" to bookings.count { it.status == "completed" }.toString(),
        "Đã hủy" to bookings.count { it.status == "cancelled" }.toString()
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB))
    ) {
        // Gradient Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(Brush.linearGradient(listOf(PetSpaColors.PetPink, PetSpaColors.PetPinkDeep)))
                .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
        ) {
            Column {
                UiStateContent(userState, { vm.loadUser() }) { user ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White.copy(0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!user.avatarUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = user.avatarUrl,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(user.name.take(1).uppercase(), color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(user.name, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            Text(user.email, color = Color.White.copy(0.7f), style = MaterialTheme.typography.bodySmall)
                            Text(user.phone, color = Color.White.copy(0.7f), style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = onEdit, modifier = Modifier.background(Color.White.copy(0.2f), CircleShape).size(36.dp)) {
                            Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Stats Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(0.15f))
                        .padding(vertical = 12.dp)
                ) {
                    stats.forEachIndexed { index, stat ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(stat.second, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(stat.first, color = Color.White.copy(0.7f), fontSize = 11.sp)
                        }
                        if (index < stats.size - 1) {
                            Box(Modifier.width(1.dp).height(24.dp).background(Color.White.copy(0.2f)).align(Alignment.CenterVertically))
                        }
                    }
                }
            }
        }

        Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            // Account Section
            Text("TÀI KHOẢN", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
            ShadcnCard {
                MenuItemNew(Icons.Outlined.Person, "Thông tin cá nhân", "Xem và chỉnh sửa thông tin", onEdit)
                HorizontalDivider(color = Color(0xFFF1F5F9))
                MenuItemNew(Icons.Outlined.Payments, "Lịch sử thanh toán", "Xem các giao dịch đã thực hiện", onPaymentHistory)
                HorizontalDivider(color = Color(0xFFF1F5F9))
                MenuItemNew(Icons.Outlined.Lock, "Đổi mật khẩu", "Cập nhật mật khẩu bảo mật", onChangePassword)
                HorizontalDivider(color = Color(0xFFF1F5F9))
                MenuItemNew(Icons.Outlined.Notifications, "Cài đặt thông báo", "Quản lý thông báo", onNotifSettings)
            }

            Spacer(Modifier.height(24.dp))

            // Settings Section
            Text("TÙY CHỌN", style = MaterialTheme.typography.labelSmall, color = Color(0xFF64748B), fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
            ShadcnCard {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.DarkMode, null, tint = Color(0xFF64748B), modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Chế độ tối", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("Giao diện ban đêm", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                        }
                    }
                    Switch(checked = false, onCheckedChange = {}, colors = SwitchDefaults.colors(checkedTrackColor = PetSpaColors.PetPinkDeep))
                }
            }

            Spacer(Modifier.height(16.dp))

            // Info Section
            Text("THÔNG TIN", style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground.copy(0.6f), fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
            AppCard {
                InfoRow("🐾 Pet Spa Booking", "v1.0.0")
                InfoRow("📍 Địa chỉ", "123 Nguyễn Huệ, Q.1, TP.HCM", onClick = {
                    val address = "123 Nguyễn Huệ, Q.1, TP.HCM"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(address)}"))
                    context.startActivity(intent)
                })
                InfoRow("📞 Hotline", "1900 0000", onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:19000000"))
                    context.startActivity(intent)
                })
            }

            Spacer(Modifier.height(24.dp))

            // Logout Button
            OutlinedButton(
                onClick = { authVm.logout(); onLogout() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFEEEE)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252))
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Đăng xuất", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MenuItemNew(icon: ImageVector, label: String, description: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PetSpaColors.PetPinkSurface),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = PetSpaColors.PetPinkDeep, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text(description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color(0xFF94A3B8), modifier = Modifier.size(20.dp))
    }
}

@Composable
fun ProfileEditScreen(vm: CustomerViewModel, onBack: () -> Unit, onSaved: () -> Unit) {
    val context = LocalContext.current
    val userState by vm.user.collectAsState()
    val user = (userState as? UiState.Success)?.data

    var name by remember { mutableStateOf(user?.name ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var phone by remember { mutableStateOf(user?.phone ?: "") }
    var gender by remember { mutableStateOf(user?.gender ?: "Nam") }
    var dob by remember { mutableStateOf(user?.dob ?: "") }
    var address by remember { mutableStateOf(user?.address ?: "") }

    var imageUri by remember { mutableStateOf<Uri?>(user?.avatarUrl?.let { Uri.parse(it) }) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showPhotoOptions by remember { mutableStateOf(false) }

    val uploadState by vm.imageUploadState.collectAsState()
    val isUploading = uploadState is UiState.Loading

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) imageUri = tempCameraUri
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) imageUri = uri
    }

    fun startCamera() {
        val file = File(context.cacheDir, "avatar_temp.jpg")
        val uri = FileProvider.getUriForFile(context, "com.petspa.app.fileprovider", file)
        tempCameraUri = uri
        cameraLauncher.launch(uri)
    }

    LaunchedEffect(imageUri) {
        val currentUri = imageUri
        if (currentUri != null && currentUri.toString() != user?.avatarUrl && !currentUri.toString().startsWith("http")) {
            val file = ImageUtils.uriToFile(context, currentUri)
            if (file != null) {
                val compressed = ImageUtils.compressAndResizeImage(context, file, 512, 512)
                val part = ImageUtils.toMultipartBody(compressed)
                vm.uploadImage(part, "avatar")
            }
        }
    }

    Column(Modifier.fillMaxSize().background(Color.White)) {
        BackHeader("Chỉnh Sửa Hồ Sơ", onBack)
        
        if (showPhotoOptions) {
            AppModal(title = "Ảnh đại diện", onDismiss = { showPhotoOptions = false }) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(onClick = { showPhotoOptions = false; startCamera() }, shape = RoundedCornerShape(16.dp), color = Color(0xFFF8FAFC), modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CameraAlt, null, tint = PetSpaColors.PetPinkDeep)
                            Spacer(Modifier.width(12.dp)); Text("Chụp ảnh", fontWeight = FontWeight.Bold)
                        }
                    }
                    Surface(onClick = { showPhotoOptions = false; galleryLauncher.launch("image/*") }, shape = RoundedCornerShape(16.dp), color = Color(0xFFF8FAFC), modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PhotoLibrary, null, tint = Color(0xFF3B82F6))
                            Spacer(Modifier.width(12.dp)); Text("Chọn từ thư viện", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            // Avatar Picker
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(PetSpaColors.PetPinkSurface)
                        .clickable(enabled = !isUploading) { showPhotoOptions = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Text(name.take(1).uppercase(), color = PetSpaColors.PetPinkDeep, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                    }
                    if (isUploading) {
                        Box(Modifier.fillMaxSize().background(Color.Black.copy(0.3f)), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(32.dp))
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text("Thay ảnh đại diện", color = PetSpaColors.PetPinkDeep, style = MaterialTheme.typography.labelMedium, modifier = Modifier.clickable { showPhotoOptions = true })
            }

            InputField(name, { name = it }, label = "Họ tên", placeholder = "Nguyễn Văn An", required = true)
            Spacer(Modifier.height(12.dp))
            InputField(email, { email = it }, label = "Email", placeholder = "email@gmail.com", required = true)
            Spacer(Modifier.height(12.dp))
            InputField(phone, { phone = it }, label = "Số điện thoại", placeholder = "0912345678", required = true)

            Spacer(Modifier.height(16.dp))
            Text("Giới tính", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Nam", "Nữ", "Khác").forEach { g ->
                    val isSelected = gender == g
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) PetSpaColors.PetPinkSurface else Color.White)
                            .border(1.dp, if (isSelected) PetSpaColors.PetPink else Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                            .clickable { gender = g },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(g, color = if (isSelected) PetSpaColors.PetPinkDeep else PetSpaColors.MutedForeground, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            InputField(dob, { dob = it }, label = "Ngày sinh", placeholder = "YYYY-MM-DD")
            Spacer(Modifier.height(12.dp))
            InputField(address, { address = it }, label = "Địa chỉ", placeholder = "Địa chỉ của bạn...")

            Spacer(Modifier.height(32.dp))
            PrimaryButton("Lưu Thay Đổi", onClick = {
                val finalAvatarUrl = (uploadState as? UiState.Success)?.data ?: user?.avatarUrl
                vm.updateUser(name, phone, gender, dob, address, finalAvatarUrl)
                vm.clearImageUploadState()
                onSaved()
            }, enabled = !isUploading)
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun ChangePasswordScreen(onBack: () -> Unit, onSaved: () -> Unit) {
    var oldPw by remember { mutableStateOf("") }
    var newPw by remember { mutableStateOf("") }
    var confirmPw by remember { mutableStateOf("") }
    var showOld by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(Color.White)) {
        BackHeader("Đổi Mật Khẩu", onBack)
        Column(
            Modifier
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Mật khẩu mới phải có ít nhất 6 ký tự để đảm bảo bảo mật cho tài khoản của bạn.",
                color = PetSpaColors.MutedForeground,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(24.dp))

            Text("Mật khẩu hiện tại", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = oldPw,
                onValueChange = { oldPw = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (showOld) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showOld = !showOld }) {
                        Icon(if (showOld) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                    }
                }
            )

            Spacer(Modifier.height(16.dp))
            Text("Mật khẩu mới", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = newPw,
                onValueChange = { newPw = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showNew = !showNew }) {
                        Icon(if (showNew) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                    }
                }
            )

            Spacer(Modifier.height(16.dp))
            Text("Xác nhận mật khẩu mới", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = confirmPw,
                onValueChange = { confirmPw = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation()
            )

            Spacer(Modifier.height(32.dp))
            PrimaryButton(
                "Cập Nhật Mật Khẩu",
                onClick = onSaved,
                enabled = newPw == confirmPw && newPw.length >= 6 && oldPw.isNotEmpty()
            )
        }
    }
}
