package com.petspa.app.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petspa.app.model.Customer
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.OwnerViewModel

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun OwnerCustomersScreen(vm: OwnerViewModel) {
    LaunchedEffect(Unit) { vm.loadCustomers() }
    val state by vm.customers.collectAsState()
    var search by remember { mutableStateOf("") }
    // toast state (tương đương toast trong Customers.tsx)
    var toastMessage by remember { mutableStateOf<Pair<String, Boolean>?>(null) } // msg, isSuccess

    // Modal state (tương đương modal trong Customers.tsx: "add" | "edit" | "delete" | "lock" | "detail")
    var modalType by remember { mutableStateOf<String?>(null) }
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }

    // form state – dùng chung cho Add / Edit
    var formName    by remember { mutableStateOf("") }
    var formEmail   by remember { mutableStateOf("") }
    var formPhone   by remember { mutableStateOf("") }
    var formGender  by remember { mutableStateOf("Nam") }
    var formDob     by remember { mutableStateOf("") }
    var formAddress by remember { mutableStateOf("") }
    var formPassword by remember { mutableStateOf("") }
    var formError   by remember { mutableStateOf("") }
    var loading     by remember { mutableStateOf(false) }

    fun openAdd() {
        formName = ""; formEmail = ""; formPhone = ""
        formGender = "Nam"; formDob = ""; formAddress = ""; formPassword = ""
        formError = ""
        modalType = "add"
    }

    fun openEdit(c: Customer) {
        formName = c.name; formEmail = c.email; formPhone = c.phone
        formGender = c.gender.ifEmpty { "Nam" }
        formDob = c.dob; formAddress = c.address; formPassword = ""
        formError = ""
        selectedCustomer = c
        modalType = "edit"
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
                    "Quản lý Khách hàng",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PetSpaColors.Foreground
                )
                if (state is UiState.Success) {
                    Text(
                        "${(state as UiState.Success<List<Customer>>).data.size} khách hàng",
                        color = PetSpaColors.MutedForeground,
                        fontSize = 14.sp
                    )
                }
            }
            // "Thêm khách hàng" button
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
                Row(
                    Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Thêm khách hàng", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Search
        InputField(
            value = search,
            onValueChange = { search = it },
            placeholder = "Tìm theo tên, email, SĐT...",
            leadingIcon = Icons.Default.Search
        )

        Spacer(Modifier.height(16.dp))

        // List
        UiStateContent(state, { vm.loadCustomers() }) { list ->
            val filtered = list.filter {
                it.name.lowercase().contains(search.lowercase()) ||
                        it.email.lowercase().contains(search.lowercase()) ||
                        it.phone.contains(search)
            }

            if (filtered.isEmpty()) {
                EmptyView(emoji = "👤", title = "Không tìm thấy khách hàng", subtitle = "Thử thay đổi từ khóa tìm kiếm")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(filtered) { customer ->
                        CustomerRowItem(
                            c = customer,
                            onView   = { selectedCustomer = customer; modalType = "detail" },
                            onEdit   = { openEdit(customer) },
                            onLock   = { selectedCustomer = customer; modalType = "lock" },
                            onDelete = { selectedCustomer = customer; modalType = "delete" }
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
            title = if (isAdd) "Thêm khách hàng" else "Chỉnh sửa khách hàng",
            onDismiss = { modalType = null }
        ) {
            CustomerFormContent(
                name        = formName,     onName     = { formName = it },
                email       = formEmail,    onEmail    = { formEmail = it },
                phone       = formPhone,    onPhone    = { formPhone = it },
                gender      = formGender,   onGender   = { formGender = it },
                dob         = formDob,      onDob      = { formDob = it },
                address     = formAddress,  onAddress  = { formAddress = it },
                password    = if (isAdd) formPassword else null,
                onPassword  = { formPassword = it },
                formError   = formError,
                loading     = loading,
                onDismiss   = { modalType = null },
                onSave      = {
                    if (formName.isBlank() || formEmail.isBlank() || formPhone.isBlank()) {
                        formError = "Vui lòng điền đầy đủ thông tin."
                    } else {
                        // TODO: call vm.addCustomer / vm.updateCustomer
                        modalType = null
                    }
                }
            )
        }
    }

    if (modalType == "detail" && selectedCustomer != null) {
        CustomerDetailsDialog(customer = selectedCustomer!!, onDismiss = { modalType = null })
    }

    if (modalType == "lock" && selectedCustomer != null) {
        val c = selectedCustomer!!
        val isLocked = c.status == "locked"
        ConfirmActionDialog(
            title       = if (isLocked) "Mở khóa tài khoản" else "Khóa tài khoản",
            message     = "Bạn có chắc chắn muốn ${if (isLocked) "mở khóa" else "khóa"} tài khoản của ${c.name}?",
            confirmText = if (isLocked) "Mở khóa" else "Khóa",
            confirmColor = if (isLocked) PetSpaColors.Success else Color(0xFF9B59B6),
            onDismiss   = { modalType = null },
            onConfirm   = { /* TODO: vm.toggleLockCustomer(c.id) */ modalType = null }
        )
    }

    if (modalType == "delete" && selectedCustomer != null) {
        ConfirmActionDialog(
            title       = "Xóa khách hàng",
            message     = "Bạn có chắc chắn muốn xóa tài khoản của ${selectedCustomer!!.name}?",
            confirmText = "Xóa",
            confirmColor = Color(0xFFE53E3E),
            onDismiss   = { modalType = null },
            onConfirm   = { /* TODO: vm.deleteCustomer(selectedCustomer!!.id) */ modalType = null }
        )
    }
}

// ── Customer form (dùng chung Add + Edit, tương đương InputField block trong Customers.tsx) ──

@Composable
fun CustomerFormContent(
    name: String,     onName: (String) -> Unit,
    email: String,    onEmail: (String) -> Unit,
    phone: String,    onPhone: (String) -> Unit,
    gender: String,   onGender: (String) -> Unit,
    dob: String,      onDob: (String) -> Unit,
    address: String,  onAddress: (String) -> Unit,
    password: String? = null, onPassword: (String) -> Unit = {},
    formError: String,
    loading: Boolean,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var expandedGender by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InputField(name,  onName,    label = "Họ tên *",         placeholder = "Nhập họ tên...")
        InputField(email, onEmail,   label = "Email *",          placeholder = "example@mail.com")
        InputField(phone, onPhone,   label = "Số điện thoại *",  placeholder = "090xxxxxxx")

        if (password != null) {
            InputField(password, onPassword, label = "Mật khẩu", placeholder = "••••••••")
        }

        // Gender dropdown
        Column {
            Text("Giới tính", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
            Spacer(Modifier.height(6.dp))
            Box(Modifier.fillMaxWidth()) {
                OutlinedCard(
                    onClick = { expandedGender = true },
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
                        Text(gender)
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                }
                DropdownMenu(expanded = expandedGender, onDismissRequest = { expandedGender = false }) {
                    listOf("Nam", "Nữ", "Khác").forEach { g ->
                        DropdownMenuItem(text = { Text(g) }, onClick = { onGender(g); expandedGender = false })
                    }
                }
            }
        }

        InputField(dob,     onDob,     label = "Ngày sinh",  placeholder = "yyyy-mm-dd", leadingIcon = Icons.Default.CalendarMonth)
        InputField(address, onAddress, label = "Địa chỉ",    placeholder = "Nhập địa chỉ...")

        if (formError.isNotEmpty()) {
            Text(formError, color = Color(0xFFE53E3E), fontSize = 12.sp)
        }

        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecondaryButton("Hủy",  onClick = onDismiss, modifier = Modifier.weight(1f))
            PrimaryButton(if (loading) "Đang lưu..." else "Lưu", onClick = onSave, modifier = Modifier.weight(1f))
        }
    }
}

// ── Customer detail dialog ────────────────────────────────────────────────────

@Composable
fun CustomerDetailsDialog(customer: Customer, onDismiss: () -> Unit) {
    AppModal(title = "Chi tiết khách hàng", onDismiss = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Avatar + name
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = PetSpaColors.PetPink
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            customer.name.take(1),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(customer.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(customer.id, style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
                    Spacer(Modifier.height(4.dp))
                    // Status badge
                    val isActive = customer.status == "active"
                    Surface(
                        color = if (isActive) Color(0xFFD5F5E3) else Color(0xFFFDECEA),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            if (isActive) "Hoạt động" else "Đã khóa",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = if (isActive) Color(0xFF27AE60) else Color(0xFFE53E3E),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Info rows
            listOf(
                "Email"    to customer.email,
                "Điện thoại" to customer.phone,
                "Địa chỉ"  to customer.address.ifEmpty { "Chưa cập nhật" },
                "Ngày sinh" to customer.dob.ifEmpty { "Chưa cập nhật" },
                "Ngày tạo" to customer.createdAt.ifEmpty { "2024-01-15" },
            ).forEach { (label, value) ->
                InfoRowSmall(label, value)
            }

            // Stats row (4 ô)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatBox(customer.totalBookings.toString(),     "Tổng booking",  Modifier.weight(1f))
                StatBox(customer.completedBookings.toString(), "Hoàn thành",    Modifier.weight(1f))
                StatBox(customer.cancelledBookings.toString(), "Đã hủy",        Modifier.weight(1f))
                StatBox(formatVndShort(customer.totalSpent),   "Chi tiêu",      Modifier.weight(1f))
            }
        }
    }
}

// ── Customer row card ─────────────────────────────────────────────────────────

@Composable
fun CustomerRowItem(
    c: Customer,
    onView: () -> Unit,
    onEdit: () -> Unit,
    onLock: () -> Unit,
    onDelete: () -> Unit
) {
    AppCard {
        // Top row – avatar + name + status
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Brush.linearGradient(listOf(PetSpaColors.PetPink, PetSpaColors.PetPinkDeep)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(c.name.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(c.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PetSpaColors.Foreground)
                Text(c.email, style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
                Text(c.phone, style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
            }

            val isActive = c.status == "active"
            Surface(
                color = if (isActive) Color(0xFFD5F5E3) else Color(0xFFFDECEA),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    if (isActive) "Hoạt động" else "Đã khóa",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    color = if (isActive) Color(0xFF27AE60) else Color(0xFFE53E3E),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Stats row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("Booking", style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
                Text(c.totalBookings.toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            }
            Column {
                Text("Hoàn thành", style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
                Text(c.completedBookings.toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = PetSpaColors.Success)
            }
            Column {
                Text("Chi tiêu", style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
                Text(formatVnd(c.totalSpent), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = PetSpaColors.PetPinkDeep)
            }
        }

        HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))

        // Action buttons
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StaffActionBtn(Icons.Default.Visibility, "Xem",  Color(0xFF45B7D1), Color(0xFFDBF0F9), Modifier.weight(1f)) { onView() }
            StaffActionBtn(Icons.Default.Edit,       "Sửa",  Color(0xFFF9CA24), Color(0xFFFEF9E7), Modifier.weight(1f)) { onEdit() }
            StaffActionBtn(Icons.Default.Lock,       "Khóa", Color(0xFF9B59B6), Color(0xFFE8DAEF), Modifier.weight(1f)) { onLock() }
            StaffActionBtn(Icons.Default.Delete,     "Xóa",  Color(0xFFE53E3E), Color(0xFFFDECEA), Modifier.weight(1f)) { onDelete() }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@Composable
fun StatBox(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = PetSpaColors.PetPinkSurface,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PetSpaColors.PetPinkDeep)
            Text(label, fontSize = 9.sp, color = PetSpaColors.MutedForeground, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

fun formatVndShort(amount: Long): String =
    if (amount >= 1_000_000) "${amount / 1_000_000}M" else if (amount >= 1_000) "${amount / 1_000}K" else "${amount}đ"