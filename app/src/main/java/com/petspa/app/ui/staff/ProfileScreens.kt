package com.petspa.app.ui.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.StaffViewModel

@Composable
fun StaffProfileScreen(vm: StaffViewModel, onPersonalInfo: () -> Unit, onChangePassword: () -> Unit, onNotifSettings: () -> Unit, onLogout: () -> Unit) {
    LaunchedEffect(Unit) { vm.loadProfile() }
    val state by vm.profile.collectAsState()
    
    Column(Modifier.fillMaxSize()) {
        PinkGradientHeader("Hồ sơ nhân viên", "Quản lý thông tin của bạn")
        UiStateContent(state, { vm.loadProfile() }) { profile ->
            Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                AppCard {
                    Row {
                        Text(profile.avatar, style = MaterialTheme.typography.displaySmall)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(profile.name, style = MaterialTheme.typography.titleLarge)
                            Text(profile.position, color = PetSpaColors.PetPinkDeep)
                            Text(profile.email, color = PetSpaColors.MutedForeground)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                StaffMenuButton("Thông tin cá nhân", onPersonalInfo)
                StaffMenuButton("Đổi mật khẩu", onChangePassword)
                StaffMenuButton("Cài đặt thông báo", onNotifSettings)
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
            }
                Spacer(Modifier.height(24.dp))
                PrimaryButton("Đăng xuất", onClick = onLogout, containerColor = PetSpaColors.Destructive)
            }
        }
    }

@Composable
fun StaffPersonalInfoScreen(vm: StaffViewModel, onBack: () -> Unit, onEdit: () -> Unit) {
    val profile = (vm.profile.value as? com.petspa.app.model.UiState.Success)?.data
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
    val profile = (vm.profile.value as? com.petspa.app.model.UiState.Success)?.data
    var name by remember { mutableStateOf(profile?.name ?: "") }
    var phone by remember { mutableStateOf(profile?.phone ?: "") }
    var address by remember { mutableStateOf(profile?.address ?: "") }
    
    Column(Modifier.fillMaxSize()) {
        AppTopBar("Chỉnh sửa thông tin", onBack)
        Column(Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
            OutlinedTextField(name, { name = it }, label = { Text("Họ tên") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(phone, { phone = it }, label = { Text("Số điện thoại") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(address, { address = it }, label = { Text("Địa chỉ") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
            Spacer(Modifier.height(24.dp))
            PrimaryButton("Lưu thay đổi", onClick = onSaved)
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
