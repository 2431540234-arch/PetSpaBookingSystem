package com.petspa.app.ui.staff

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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.util.ImageUtils
import com.petspa.app.viewmodel.StaffViewModel
import java.io.File

@Composable
fun StaffProfileScreen(vm: StaffViewModel, onPersonalInfo: () -> Unit, onChangePassword: () -> Unit, onNotifSettings: () -> Unit, onLogout: () -> Unit) {
    LaunchedEffect(Unit) { vm.loadProfile() }
    val state by vm.profile.collectAsState()
    
    Column(Modifier.fillMaxSize()) {
        PinkGradientHeader("Hồ sơ nhân viên", "Quản lý thông tin của bạn")
        UiStateContent(state, { vm.loadProfile() }) { profile ->
            Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(PetSpaColors.PetPinkSurface),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!profile.avatar.isNullOrEmpty()) {
                                AsyncImage(
                                    model = profile.avatar,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text("👤", fontSize = 32.sp)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(profile.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(profile.position, color = PetSpaColors.PetPinkDeep, fontWeight = FontWeight.Medium)
                            Text(profile.email, color = PetSpaColors.MutedForeground, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                StaffMenuButton("Thông tin cá nhân", onPersonalInfo)
                StaffMenuButton("Đổi mật khẩu", onChangePassword)
                StaffMenuButton("Cài đặt thông báo", onNotifSettings)
                
                Spacer(Modifier.height(24.dp))
                
                Text(
                    "TÙY CHỌN",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
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
                                Icon(
                                    Icons.Outlined.DarkMode,
                                    null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    "Chế độ tối",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Giao diện ban đêm",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        Switch(
                            checked = false,
                            onCheckedChange = {},
                            colors = SwitchDefaults.colors(checkedTrackColor = PetSpaColors.PetPinkDeep)
                        )
                    }
                }
                
                Spacer(Modifier.height(40.dp))
                PrimaryButton("Đăng xuất", onClick = onLogout, containerColor = PetSpaColors.Destructive)
            }
        }
    }
}

@Composable
fun StaffPersonalInfoScreen(vm: StaffViewModel, onBack: () -> Unit, onEdit: () -> Unit) {
    val state by vm.profile.collectAsState()
    val profile = (state as? UiState.Success)?.data
    Column(Modifier.fillMaxSize()) {
        AppTopBar("Thông tin cá nhân", onBack)
        if (profile != null) {
            Column(Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                InfoRow("Họ tên", profile.name)
                InfoRow("Email", profile.email)
                InfoRow("Số điện thoại", profile.phone)
                InfoRow("Giới tính", profile.gender)
                InfoRow("Ngày sinh", profile.birthDate)
                InfoRow("Địa chỉ", profile.address)
                InfoRow("Kinh nghiệm", profile.expertise.joinToString(", "))
                InfoRow("Ngày gia nhập", profile.joinDate)
                Spacer(Modifier.height(24.dp))
                PrimaryButton("Chỉnh sửa", onClick = onEdit)
            }
        }
    }
}

@Composable
fun StaffEditPersonalInfoScreen(vm: StaffViewModel, onBack: () -> Unit, onSaved: () -> Unit) {
    val context = LocalContext.current
    val profileState by vm.profile.collectAsState()
    val profile = (profileState as? UiState.Success)?.data

    var name by remember { mutableStateOf(profile?.name ?: "") }
    var phone by remember { mutableStateOf(profile?.phone ?: "") }
    var address by remember { mutableStateOf(profile?.address ?: "") }
    
    var imageUri by remember { mutableStateOf<Uri?>(profile?.avatar?.let { if (it.startsWith("http")) Uri.parse(it) else null }) }
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
        val file = File(context.cacheDir, "staff_avatar_temp.jpg")
        val uri = FileProvider.getUriForFile(context, "com.petspa.app.fileprovider", file)
        tempCameraUri = uri
        cameraLauncher.launch(uri)
    }

    LaunchedEffect(imageUri) {
        val currentUri = imageUri
        if (currentUri != null && currentUri.toString() != profile?.avatar && !currentUri.toString().startsWith("http")) {
            val file = ImageUtils.uriToFile(context, currentUri)
            if (file != null) {
                val compressed = ImageUtils.compressAndResizeImage(context, file, 512, 512)
                val part = ImageUtils.toMultipartBody(compressed)
                vm.uploadImage(part, "avatar")
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Chỉnh sửa thông tin", onBack)
        
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

        Column(Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
            // Avatar Picker
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
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
            InputField(phone, { phone = it }, label = "Số điện thoại", placeholder = "0912345678", required = true)
            Spacer(Modifier.height(12.dp))
            InputField(address, { address = it }, label = "Địa chỉ", placeholder = "Địa chỉ của bạn...")
            
            Spacer(Modifier.height(24.dp))
            PrimaryButton("Lưu thay đổi", onClick = {
                val finalAvatarUrl = (uploadState as? UiState.Success)?.data ?: profile?.avatar
                vm.updateProfile(name, phone, address, finalAvatarUrl)
                vm.clearImageUploadState()
                onSaved()
            }, enabled = !isUploading)
        }
    }
}

@Composable
fun StaffChangePasswordScreen(onBack: () -> Unit, onSaved: () -> Unit) {
    var oldPw by remember { mutableStateOf("") }
    var newPw by remember { mutableStateOf("") }
    var confirmPw by remember { mutableStateOf("") }
    
    Column(Modifier.fillMaxSize()) {
        AppTopBar("Đổi mật khẩu", onBack)
        Column(Modifier.padding(24.dp)) {
            OutlinedTextField(oldPw, { oldPw = it }, label = { Text("Mật khẩu cũ") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(newPw, { newPw = it }, label = { Text("Mật khẩu mới") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(confirmPw, { confirmPw = it }, label = { Text("Xác nhận mật khẩu") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
            Spacer(Modifier.height(24.dp))
            PrimaryButton("Cập nhật mật khẩu", onClick = onSaved, enabled = newPw == confirmPw && newPw.length >= 6)
        }
    }
}

@Composable
fun StaffNotifSettingsScreen(onBack: () -> Unit) {
    var bookingNew by remember { mutableStateOf(true) }
    var shiftChange by remember { mutableStateOf(true) }
    
    Column(Modifier.fillMaxSize()) {
        AppTopBar("Cài đặt thông báo", onBack)
        Column(Modifier.padding(24.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Thông báo lịch hẹn mới")
                Switch(bookingNew, { bookingNew = it })
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Thông báo thay đổi ca làm")
                Switch(shiftChange, { shiftChange = it })
            }
            Spacer(Modifier.height(24.dp))
            PrimaryButton("Lưu cài đặt", onClick = onBack)
        }
    }
}

@Composable
private fun StaffMenuButton(text: String, onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(10.dp)) {
        Text(text, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
        Text(value, style = MaterialTheme.typography.bodyLarge)
        HorizontalDivider(Modifier.padding(top = 8.dp), color = PetSpaColors.PetPinkSurface)
    }
}
