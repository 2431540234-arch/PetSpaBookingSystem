package com.petspa.app.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petspa.app.model.Service
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.OwnerViewModel

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun OwnerServicesScreen(vm: OwnerViewModel) {
    LaunchedEffect(Unit) { vm.loadServices() }
    val state by vm.services.collectAsState()

    // modal: "add" | "edit" | "delete"
    var modalType    by remember { mutableStateOf<String?>(null) }
    var editingService by remember { mutableStateOf<Service?>(null) }
    var selectedService by remember { mutableStateOf<Service?>(null) }

    // toast (tương đương toast trong Services.tsx)
    var toast by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

    // form state
    var formName        by remember { mutableStateOf("") }
    var formPrice       by remember { mutableStateOf("") }
    var formDuration    by remember { mutableStateOf("") }
    var formDescription by remember { mutableStateOf("") }
    var formStatus      by remember { mutableStateOf("active") }
    var formError       by remember { mutableStateOf("") }
    var loading         by remember { mutableStateOf(false) }

    fun openAdd() {
        formName = ""; formPrice = ""; formDuration = ""; formDescription = ""; formStatus = "active"; formError = ""
        editingService = null
        modalType = "add"
    }

    fun openEdit(s: Service) {
        formName = s.name; formPrice = s.price.toString(); formDuration = s.duration.toString()
        formDescription = s.description; formStatus = s.status; formError = ""
        editingService = s
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
                    "Quản lý Dịch vụ",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PetSpaColors.Foreground
                )
                if (state is UiState.Success) {
                    val list = (state as UiState.Success<List<Service>>).data
                    Text(
                        "${list.count { it.status == "active" }} dịch vụ đang hoạt động",
                        style = MaterialTheme.typography.bodySmall,
                        color = PetSpaColors.MutedForeground
                    )
                }
            }
            // "Thêm dịch vụ" button
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
                    Text("Thêm dịch vụ", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Grid (tương đương grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3)
        UiStateContent(state, { vm.loadServices() }) { list ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(list) { service ->
                    OwnerServiceCard(
                        service = service,
                        onEdit   = { openEdit(service) },
                        onDelete = { selectedService = service; modalType = "delete" }
                    )
                }
            }
        }
    }

    // ── Modals ────────────────────────────────────────────────────────────────

    if (modalType == "add" || modalType == "edit") {
        val isAdd = modalType == "add"
        AppModal(
            title = if (isAdd) "Thêm dịch vụ" else "Chỉnh sửa dịch vụ",
            onDismiss = { modalType = null }
        ) {
            ServiceFormContent(
                name        = formName,        onName        = { formName = it },
                price       = formPrice,       onPrice       = { formPrice = it },
                duration    = formDuration,    onDuration    = { formDuration = it },
                description = formDescription, onDescription = { formDescription = it },
                status      = formStatus,      onStatus      = { formStatus = it },
                formError   = formError,
                loading     = loading,
                onDismiss   = { modalType = null },
                onSave      = {
                    if (formName.isBlank() || formPrice.isBlank() || formDuration.isBlank()) {
                        formError = "Vui lòng điền đầy đủ thông tin."
                        return@ServiceFormContent
                    }
                    // TODO: vm.addService / vm.updateService
                    modalType = null
                }
            )
        }
    }

    if (modalType == "delete" && selectedService != null) {
        ConfirmActionDialog(
            title       = "Xóa dịch vụ",
            message     = "Xóa dịch vụ ${selectedService!!.name}?",
            confirmText = "Xóa",
            confirmColor = Color(0xFFE53E3E),
            onDismiss   = { modalType = null },
            onConfirm   = {
                // TODO: vm.deleteService(selectedService!!.id)
                modalType = null
            }
        )
    }
}

// ── Service form ──────────────────────────────────────────────────────────────

@Composable
fun ServiceFormContent(
    name: String,        onName: (String) -> Unit,
    price: String,       onPrice: (String) -> Unit,
    duration: String,    onDuration: (String) -> Unit,
    description: String, onDescription: (String) -> Unit,
    status: String,      onStatus: (String) -> Unit,
    formError: String,
    loading: Boolean,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        InputField(name, onName, label = "Tên dịch vụ *", placeholder = "Ví dụ: Tắm & Sấy")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) {
                InputField(price,    onPrice,    label = "Giá (VNĐ) *",         placeholder = "320000")
            }
            Box(Modifier.weight(1f)) {
                InputField(duration, onDuration, label = "Thời lượng (phút) *",  placeholder = "60")
            }
        }
        InputField(description, onDescription, label = "Mô tả", placeholder = "Mô tả dịch vụ...")

        // Status radio (tương đương select trong Services.tsx)
        Column {
            Text("Trạng thái", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = status == "active",   onClick = { onStatus("active") })
                Text("Hoạt động",  style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.width(16.dp))
                RadioButton(selected = status == "inactive", onClick = { onStatus("inactive") })
                Text("Tạm ngừng", style = MaterialTheme.typography.bodySmall)
            }
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

// ── Service card ──────────────────────────────────────────────────────────────

@Composable
fun OwnerServiceCard(service: Service, onEdit: () -> Unit, onDelete: () -> Unit) {
    AppCard {
        Column {
            // Image placeholder với gradient overlay (tương đương <img> + gradient div trong Services.tsx)
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PetSpaColors.PetPinkSurface)
            ) {
                // Gradient overlay
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.4f)))
                        )
                )
                // Status badge (top-right)
                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopEnd),
                    color = if (service.status == "active") Color(0xFFD5F5E3) else Color(0xFFFDECEA),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        if (service.status == "active") "Hoạt động" else "Tạm ngừng",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = if (service.status == "active") Color(0xFF27AE60) else Color(0xFFE53E3E),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                // Service ID (bottom-left)
                Text(
                    service.id,
                    modifier = Modifier.padding(12.dp).align(Alignment.BottomStart),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(service.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PetSpaColors.Foreground)
            Text(
                service.description.ifEmpty { "Không có mô tả" },
                style = MaterialTheme.typography.bodySmall,
                color = PetSpaColors.MutedForeground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(12.dp))

            // Price + Duration row
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AttachMoney, null, modifier = Modifier.size(16.dp), tint = PetSpaColors.PetPinkDeep)
                    Text(
                        formatVnd(service.price.toLong()),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PetSpaColors.PetPinkDeep
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AccessTime, null, modifier = Modifier.size(14.dp), tint = PetSpaColors.MutedForeground)
                    Spacer(Modifier.width(4.dp))
                    Text("${service.duration} phút", style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Action buttons (tương đương Sửa / Xóa trong Services.tsx)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StaffActionBtn(Icons.Outlined.Edit,   "Sửa", Color(0xFFF9CA24), Color(0xFFFEF9E7), Modifier.weight(1f)) { onEdit() }
                StaffActionBtn(Icons.Outlined.Delete, "Xóa", Color(0xFFE53E3E), Color(0xFFFDECEA), Modifier.weight(1f)) { onDelete() }
            }
        }
    }
}