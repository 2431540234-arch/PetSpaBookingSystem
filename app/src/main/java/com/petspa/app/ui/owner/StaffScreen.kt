package com.petspa.app.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petspa.app.model.StaffMember
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.OwnerViewModel

private val roleColors = mapOf(
    "Groomer"      to Pair(Color(0xFFFFE4E8), Color(0xFFFF8FA3)),
    "Vet Assistant" to Pair(Color(0xFFDBF0F9), Color(0xFF45B7D1)),
    "Receptionist" to Pair(Color(0xFFD5F5E3), Color(0xFF27AE60)),
)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun OwnerStaffScreen(vm: OwnerViewModel) {
    LaunchedEffect(Unit) { vm.loadStaff() }
    val state by vm.staff.collectAsState()
    var search by remember { mutableStateOf("") }

    // modal: "add" | "edit" | "delete" | "reset"
    var modalType      by remember { mutableStateOf<String?>(null) }
    var selectedStaff  by remember { mutableStateOf<StaffMember?>(null) }

    // toast
    var toast by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

    // form
    var formName     by remember { mutableStateOf("") }
    var formEmail    by remember { mutableStateOf("") }
    var formPhone    by remember { mutableStateOf("") }
    var formRole     by remember { mutableStateOf("Groomer") }
    var formPassword by remember { mutableStateOf("") }
    var formError    by remember { mutableStateOf("") }
    var loading      by remember { mutableStateOf(false) }

    fun openAdd() {
        formName = ""; formEmail = ""; formPhone = ""; formRole = "Groomer"; formPassword = ""; formError = ""
        selectedStaff = null; modalType = "add"
    }

    fun openEdit(m: StaffMember) {
        formName = m.name; formEmail = m.email; formPhone = m.phone; formRole = m.role ?: "Groomer"; formPassword = ""; formError = ""
        selectedStaff = m; modalType = "edit"
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(PetSpaColors.OwnerBackground)
            .padding(16.dp)
    ) {
        // Header
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Quản lý Nhân viên",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PetSpaColors.Foreground
                )
                if (state is UiState.Success) {
                    Text(
                        "${(state as UiState.Success<List<StaffMember>>).data.size} nhân viên",
                        color = PetSpaColors.MutedForeground,
                        fontSize = 14.sp
                    )
                }
            }
            Button(
                onClick = { openAdd() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(44.dp)
                    .background(
                        Brush.linearGradient(listOf(PetSpaColors.PetPink, PetSpaColors.PetPinkDeep)),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Thêm nhân viên", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Search
        Box(Modifier.fillMaxWidth()) {
            InputField(
                value = search,
                onValueChange = { search = it },
                placeholder = "Tìm theo tên, email, SĐT...",
                leadingIcon = Icons.Default.Search
            )
        }

        Spacer(Modifier.height(16.dp))

        // Staff list
        UiStateContent(state, { vm.loadStaff() }) { list ->
            val filtered = list.filter {
                it.name.contains(search, true) ||
                        it.email.contains(search, true) ||
                        it.phone.contains(search)
            }

            if (filtered.isEmpty()) {
                EmptyView(emoji = "👤", title = "Không tìm thấy nhân viên", subtitle = "Thử thay đổi từ khóa tìm kiếm")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(filtered) { member ->
                        OwnerStaffCard(
                            m = member,
                            onEdit   = { openEdit(member) },
                            onLock   = {
                                // toggleLock ngay, không cần dialog (tương đương toggleLock trong Staff.tsx)
                                vm.updateStaff(member.copy(status = if (member.status == "active") "locked" else "active"))
                            },
                            onReset  = { selectedStaff = member; modalType = "reset" },
                            onDelete = { selectedStaff = member; modalType = "delete" }
                        )
                    }
                }
            }
        }
    }

    // ── Modals ────────────────────────────────────────────────────────────────

    if (modalType == "add" || modalType == "edit") {
        val isAdd = modalType == "add"
        AppModal(
            title = if (isAdd) "Thêm nhân viên" else "Chỉnh sửa nhân viên",
            onDismiss = { modalType = null }
        ) {
            StaffFormContent(
                name      = formName,     onName     = { formName = it },
                email     = formEmail,    onEmail    = { formEmail = it },
                phone     = formPhone,    onPhone    = { formPhone = it },
                role      = formRole,     onRole     = { formRole = it },
                password  = if (isAdd) formPassword else null,
                onPassword = { formPassword = it },
                formError = formError,
                loading   = loading,
                onDismiss = { modalType = null },
                onSave    = {
                    if (formName.isBlank() || formEmail.isBlank() || formPhone.isBlank()) {
                        formError = "Vui lòng điền đầy đủ thông tin."; return@StaffFormContent
                    }
                    if (isAdd) {
                        // TODO: vm.addStaff(...)
                    } else {
                        // TODO: vm.updateStaff(...)
                    }
                    modalType = null
                }
            )
        }
    }

    if (modalType == "delete" && selectedStaff != null) {
        AppModal(title = "Xóa nhân viên", onDismiss = { modalType = null }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    Modifier.size(64.dp).background(Color(0xFFFDECEA), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Delete, null, tint = Color(0xFFE53E3E), modifier = Modifier.size(28.dp))
                }
                Text(
                    buildAnnotatedString {
                        append("Xóa nhân viên ")
                        withStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)) { append(selectedStaff!!.name) }
                        append("?")
                    },
                    color = PetSpaColors.Foreground
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SecondaryButton("Hủy", onClick = { modalType = null }, modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            vm.deleteStaff(selectedStaff!!.id)
                            modalType = null
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53E3E))
                    ) {
                        Text(if (loading) "Đang xóa..." else "Xóa", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (modalType == "reset" && selectedStaff != null) {
        ResetPasswordDialog(staffName = selectedStaff!!.name, staffEmail = selectedStaff!!.email, onDismiss = { modalType = null })
    }
}

// ── Staff form ────────────────────────────────────────────────────────────────

@Composable
fun StaffFormContent(
    name: String,     onName: (String) -> Unit,
    email: String,    onEmail: (String) -> Unit,
    phone: String,    onPhone: (String) -> Unit,
    role: String,     onRole: (String) -> Unit,
    password: String? = null, onPassword: (String) -> Unit = {},
    formError: String,
    loading: Boolean,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var expandedRole by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        InputField(name,  onName,  label = "Họ tên *",          placeholder = "Nhập họ tên...")
        InputField(email, onEmail, label = "Email *",            placeholder = "nv@petspa.com")
        InputField(phone, onPhone, label = "Số điện thoại *",   placeholder = "090xxxxxxx")

        // Role dropdown
        Column {
            Text("Chức vụ", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
            Spacer(Modifier.height(6.dp))
            Box(Modifier.fillMaxWidth()) {
                OutlinedCard(
                    onClick = { expandedRole = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PetSpaColors.PetPinkBorder)
                ) {
                    Row(
                        Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(role)
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                }
                DropdownMenu(expanded = expandedRole, onDismissRequest = { expandedRole = false }) {
                    listOf("Groomer", "Vet Assistant", "Receptionist").forEach { r ->
                        DropdownMenuItem(text = { Text(r) }, onClick = { onRole(r); expandedRole = false })
                    }
                }
            }
        }

        if (password != null) {
            InputField(password, onPassword, label = "Mật khẩu", placeholder = "••••••••")
        }

        if (formError.isNotEmpty()) {
            Text(formError, color = Color(0xFFE53E3E), fontSize = 12.sp)
        }

        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SecondaryButton("Hủy", onClick = onDismiss, modifier = Modifier.weight(1f))
            PrimaryButton(if (loading) "Đang lưu..." else "Lưu", onClick = onSave, modifier = Modifier.weight(1f))
        }
    }
}

// ── Reset password dialog ─────────────────────────────────────────────────────

@Composable
fun ResetPasswordDialog(staffName: String, staffEmail: String, onDismiss: () -> Unit) {
    var loading by remember { mutableStateOf(false) }

    AppModal(title = "Reset mật khẩu", onDismiss = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(64.dp).background(Color(0xFFFFE4E8), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Refresh, null, tint = PetSpaColors.PetPinkDeep, modifier = Modifier.size(32.dp))
            }

            Text(
                text = buildAnnotatedString {
                    append("Reset mật khẩu cho ")
                    withStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)) { append(staffName) }
                    append("?")
                },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = PetSpaColors.Foreground
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryButton("Hủy", onClick = onDismiss, modifier = Modifier.weight(1f))
                PrimaryButton(
                    text = if (loading) "Đang gửi..." else "Xác nhận",
                    onClick = { /* TODO: gửi email reset */ onDismiss() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ── Staff card ────────────────────────────────────────────────────────────────

@Composable
fun OwnerStaffCard(
    m: StaffMember,
    onEdit: () -> Unit,
    onLock: () -> Unit,
    onReset: () -> Unit,
    onDelete: () -> Unit
) {
    val rc = roleColors[m.role] ?: Pair(Color(0xFFFFF0F3), PetSpaColors.PetPinkDeep)

    AppCard {
        // Top row: avatar + name + status
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF4ECDC4), Color(0xFF45B7D1))),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(m.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(m.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PetSpaColors.Foreground)
                Text(m.email, style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
            }

            val isActive = m.status == "active"
            Surface(
                color = if (isActive) Color(0xFFD5F5E3) else Color(0xFFFDECEA),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    if (isActive) "Hoạt động" else "Đã khóa",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    color = if (isActive) PetSpaColors.Success else PetSpaColors.Destructive,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Role + bookings + phone
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = rc.first, shape = RoundedCornerShape(50.dp)) {
                    Text(
                        m.role ?: "Staff",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = rc.second,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    m.completedBookings.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = PetSpaColors.PetPinkDeep
                )
                Spacer(Modifier.width(4.dp))
                Text("bookings", style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
            }
            Text(m.phone, style = MaterialTheme.typography.bodySmall, color = PetSpaColors.Foreground)
        }

        HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))

        // Action buttons (tương đương icon buttons trong Staff.tsx table row)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StaffActionBtn(Icons.Default.Edit,    "Sửa",  Color(0xFFF9CA24), Color(0xFFFEF9E7), Modifier.weight(1f)) { onEdit() }
            StaffActionBtn(Icons.Default.Lock,    "Khóa", Color(0xFF9B59B6), Color(0xFFE8DAEF), Modifier.weight(1f)) { onLock() }
            StaffActionBtn(Icons.Default.Refresh, "Reset", PetSpaColors.PetPinkDeep, PetSpaColors.PetPinkSurface, Modifier.weight(1f)) { onReset() }
            StaffActionBtn(Icons.Default.Delete,  "Xóa",  Color(0xFFE53E3E), Color(0xFFFDECEA), Modifier.weight(1f)) { onDelete() }
        }
    }
}

// ── Shared button ─────────────────────────────────────────────────────────────

@Composable
fun StaffActionBtn(
    icon: ImageVector,
    label: String,
    color: Color,
    bg: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(8.dp),
        color = bg
    ) {
        Row(
            Modifier.padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(14.dp), tint = color)
            Spacer(Modifier.width(4.dp))
            Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}