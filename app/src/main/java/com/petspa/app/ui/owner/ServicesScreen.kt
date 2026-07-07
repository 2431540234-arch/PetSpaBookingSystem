package com.petspa.app.ui.owner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.petspa.app.model.Service
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.util.ImageUtils
import com.petspa.app.viewmodel.OwnerViewModel
import java.io.File

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun OwnerServicesScreen(vm: OwnerViewModel) {
    LaunchedEffect(Unit) { vm.loadServices() }
    val state by vm.services.collectAsState()

    // modal: "add" | "edit" | "delete"
    var modalType    by remember { mutableStateOf<String?>(null) }
    var editingService by remember { mutableStateOf<Service?>(null) }
    var selectedService by remember { mutableStateOf<Service?>(null) }

    // form state
    var formName        by remember { mutableStateOf("") }
    var formPrice       by remember { mutableStateOf("") }
    var formDuration    by remember { mutableStateOf("") }
    var formDescription by remember { mutableStateOf("") }
    var formStatus      by remember { mutableStateOf("active") }
    var formImageUrl    by remember { mutableStateOf("") }
    var formError       by remember { mutableStateOf("") }
    var loading         by remember { mutableStateOf(false) }

    fun openAdd() {
        formName = ""; formPrice = ""; formDuration = ""; formDescription = ""; formStatus = "active"; formImageUrl = ""; formError = ""
        editingService = null
        modalType = "add"
    }

    fun openEdit(s: Service) {
        formName = s.name; formPrice = s.price.toString(); formDuration = s.duration.toString()
        formDescription = s.description; formStatus = s.status; formImageUrl = s.imageUrl; formError = ""
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
            onDismiss = { modalType = null; vm.clearImageUploadState() }
        ) {
            ServiceFormContent(
                vm          = vm,
                name        = formName,        onName        = { formName = it },
                price       = formPrice,       onPrice       = { formPrice = it },
                duration    = formDuration,    onDuration    = { formDuration = it },
                description = formDescription, onDescription = { formDescription = it },
                status      = formStatus,      onStatus      = { formStatus = it },
                imageUrl    = formImageUrl,    onImageUrl    = { formImageUrl = it },
                formError   = formError,
                loading     = loading,
                onDismiss   = { modalType = null; vm.clearImageUploadState() },
                onSave      = {
                    if (formName.isBlank() || formPrice.isBlank() || formDuration.isBlank()) {
                        formError = "Vui lòng điền đầy đủ thông tin."
                        return@ServiceFormContent
                    }
                    val s = Service(
                        id = editingService?.id ?: "s${System.currentTimeMillis()}",
                        name = formName,
                        price = formPrice.toLongOrNull() ?: 0L,
                        duration = formDuration.toIntOrNull() ?: 0,
                        description = formDescription,
                        status = formStatus,
                        imageUrl = formImageUrl
                    )
                    if (isAdd) vm.addService(s) else vm.updateService(s)
                    modalType = null
                    vm.clearImageUploadState()
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
                vm.deleteService(selectedService!!.id)
                modalType = null
            }
        )
    }
}

// ── Service form ──────────────────────────────────────────────────────────────

@Composable
fun ServiceFormContent(
    vm: OwnerViewModel,
    name: String,        onName: (String) -> Unit,
    price: String,       onPrice: (String) -> Unit,
    duration: String,    onDuration: (String) -> Unit,
    description: String, onDescription: (String) -> Unit,
    status: String,      onStatus: (String) -> Unit,
    imageUrl: String,    onImageUrl: (String) -> Unit,
    formError: String,
    loading: Boolean,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    var tempUri by remember { mutableStateOf<Uri?>(if (imageUrl.isNotEmpty() && imageUrl.startsWith("http")) Uri.parse(imageUrl) else null) }
    val uploadState by vm.imageUploadState.collectAsState()
    val isUploading = uploadState is UiState.Loading

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            tempUri = uri
            val file = ImageUtils.uriToFile(context, uri)
            if (file != null) {
                val compressed = ImageUtils.compressAndResizeImage(context, file)
                vm.uploadImage(ImageUtils.toMultipartBody(compressed), "service")
            }
        }
    }

    LaunchedEffect(uploadState) {
        val state = uploadState
        if (state is UiState.Success) {
            onImageUrl(state.data)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Image Picker
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PetSpaColors.PetPinkSurface)
                .clickable(enabled = !isUploading) { galleryLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (tempUri != null) {
                AsyncImage(model = tempUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddPhotoAlternate, null, tint = PetSpaColors.PetPinkDeep, modifier = Modifier.size(48.dp))
                    Text("Thêm ảnh dịch vụ", color = PetSpaColors.PetPinkDeep, fontWeight = FontWeight.Bold)
                }
            }
            if (isUploading) {
                Box(Modifier.fillMaxSize().background(Color.Black.copy(0.3f)), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        }

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
            PrimaryButton(if (loading || isUploading) "Đang lưu..." else "Lưu", onClick = onSave, modifier = Modifier.weight(1f), enabled = !isUploading)
        }
    }
}

// ── Service card ──────────────────────────────────────────────────────────────

@Composable
fun OwnerServiceCard(service: Service, onEdit: () -> Unit, onDelete: () -> Unit) {
    AppCard {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PetSpaColors.PetPinkSurface)
            ) {
                if (!service.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = service.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(service.emoji, fontSize = 48.sp)
                    }
                }
                
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
