package com.petspa.app.ui.customer

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.petspa.app.model.Pet
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.CustomerViewModel
import android.graphics.Bitmap

@Composable
fun PetsScreen(vm: CustomerViewModel, onAdd: () -> Unit, onEdit: (String) -> Unit, onProfile: (String) -> Unit) {
    LaunchedEffect(Unit) { vm.loadPets() }
    val state by vm.pets.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB))
    ) {
        BackHeader(
            title = "Thú Cưng Của Tôi"
        )

        UiStateContent(state, { vm.loadPets() }) { list ->
            if (list.isEmpty()) {
                EmptyView(
                    emoji = "🐾",
                    title = "Chưa có thú cưng nào",
                    subtitle = "Thêm thú cưng để bắt đầu đặt lịch dịch vụ",
                    action = {
                        PrimaryButton("+ Thêm Thú Cưng", onClick = onAdd, modifier = Modifier.width(200.dp))
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(list) { pet ->
                        PetCard(
                            pet = pet,
                            onEdit = { onEdit(pet.id) },
                            onDelete = { vm.deletePet(pet.id) },
                            onView = { onProfile(pet.id) }
                        )
                    }
                    item {
                        SecondaryButton("+ Thêm Thú Cưng", onClick = onAdd)
                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PetCard(pet: Pet, onEdit: () -> Unit, onDelete: () -> Unit, onView: () -> Unit) {
    AppCard(onClick = onView) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (pet.imageUrl != null) {
                AsyncImage(
                    model = pet.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp).clip(CircleShape).border(1.dp, PetSpaColors.PetPinkBorder, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(pet.emoji, fontSize = 48.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(pet.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("${pet.species} · ${pet.breed}", style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground)
                Text("${pet.age} tuổi · ${pet.weight} kg · ${pet.gender}", fontSize = 11.sp, color = PetSpaColors.MutedForeground.copy(0.7f))
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFFF5F5F5))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onView,
                modifier = Modifier.weight(1f).height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PetSpaColors.PetPinkSurface, contentColor = PetSpaColors.PetPinkDeep),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Hồ sơ", style = MaterialTheme.typography.labelMedium)
            }
            TextButton(onClick = onEdit, modifier = Modifier.weight(1f).height(36.dp)) {
                Icon(Icons.Default.Edit, null, modifier = Modifier.size(14.dp), tint = PetSpaColors.PetPinkDeep)
                Spacer(Modifier.width(4.dp))
                Text("Sửa", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.PetPinkDeep)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = Color(0xFFFF8A8A), modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun PetProfileScreen(vm: CustomerViewModel, petId: String, onBack: () -> Unit, onEdit: () -> Unit, onBook: (String) -> Unit) {
    LaunchedEffect(Unit) { if (vm.pets.value !is UiState.Success) vm.loadPets() }
    val petsState by vm.pets.collectAsState()
    val pet = (petsState as? UiState.Success)?.data?.find { it.id == petId }

    Column(Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {
        BackHeader("Hồ Sơ Thú Cưng", onBack, rightAction = {
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = PetSpaColors.PetPinkDeep) }
        })

        if (pet != null) {
            Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                // Hero Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.linearGradient(listOf(PetSpaColors.PetPinkSurface, Color(0xFFFFF5F7))))
                        .border(1.dp, PetSpaColors.PetPinkBorder, RoundedCornerShape(24.dp))
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (pet.imageUrl != null) {
                            AsyncImage(
                                model = pet.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.size(120.dp).clip(CircleShape).border(2.dp, Color.White, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(pet.emoji, fontSize = 72.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(pet.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("${pet.species} · ${pet.breed}", color = PetSpaColors.MutedForeground, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Basic Info
                AppCard {
                    Text("Thông Tin Cơ Bản", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    InfoRow("Giới tính", pet.gender)
                    InfoRow("Tuổi", "${pet.age} tuổi")
                    InfoRow("Cân nặng", "${pet.weight} kg")
                    InfoRow("Dị ứng", pet.allergies.ifEmpty { "Không có" })
                    InfoRow("Tiền sử bệnh", pet.medicalHistory.ifEmpty { "Chưa có" })
                    InfoRow("Ghi chú", pet.notes.ifEmpty { "Không có" })
                }

                Spacer(Modifier.height(24.dp))

                PrimaryButton("📅 Đặt Lịch Cho ${pet.name}", onClick = { onBook(pet.id) })
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun PetFormScreen(vm: CustomerViewModel, petId: String? = null, onBack: () -> Unit, onSaved: () -> Unit) {
    val context = LocalContext.current
    val existingPet = if (petId != null) vm.localPets.find { it.id == petId } else null
    val title = if (existingPet == null) "Thêm Thú Cưng" else "Sửa: ${existingPet.name}"

    var name by remember { mutableStateOf(existingPet?.name ?: "") }
    var species by remember { mutableStateOf(existingPet?.species ?: "Chó") }
    var breed by remember { mutableStateOf(existingPet?.breed ?: "") }
    var gender by remember { mutableStateOf(existingPet?.gender ?: "Đực") }
    var age by remember { mutableStateOf(existingPet?.age?.toString() ?: "") }
    var weight by remember { mutableStateOf(existingPet?.weight?.toString() ?: "") }
    var allergies by remember { mutableStateOf(existingPet?.allergies ?: "") }
    var medicalHistory by remember { mutableStateOf(existingPet?.medicalHistory ?: "") }
    var notes by remember { mutableStateOf(existingPet?.notes ?: "") }
    
    var imageUri by remember { mutableStateOf<Any?>(existingPet?.imageUrl) }
    var isImageConfirmed by remember { mutableStateOf(existingPet?.imageUrl != null) }
    var showPhotoOptions by remember { mutableStateOf(false) }

    val speciesEmojis = mapOf("Chó" to "🐕", "Mèo" to "🐈", "Thỏ" to "🐇", "Chim" to "🐦", "Cá" to "🐠", "Khác" to "🐾")

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            imageUri = bitmap
            isImageConfirmed = false // Đợi xác nhận
        }
    }

    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            isImageConfirmed = false // Đợi xác nhận
        }
    }

    // Permission Launchers
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) cameraLauncher.launch(null)
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) galleryLauncher.launch("image/*")
    }

    Column(Modifier.fillMaxSize().background(Color.White)) {
        BackHeader(title, onBack)
        
        if (showPhotoOptions) {
            AppModal(
                title = "Chọn ảnh thú cưng",
                onDismiss = { showPhotoOptions = false }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(
                        onClick = {
                            showPhotoOptions = false
                            val permission = Manifest.permission.CAMERA
                            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                cameraLauncher.launch(null)
                            } else {
                                cameraPermissionLauncher.launch(permission)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CameraAlt, null, tint = PetSpaColors.PetPinkDeep)
                            Spacer(Modifier.width(12.dp))
                            Text("Chụp ảnh mới", fontWeight = FontWeight.Bold)
                        }
                    }

                    Surface(
                        onClick = {
                            showPhotoOptions = false
                            val permission = if (android.os.Build.VERSION.SDK_INT >= 33)
                                Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE

                            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                                galleryLauncher.launch("image/*")
                            } else {
                                galleryPermissionLauncher.launch(permission)
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PhotoLibrary, null, tint = Color(0xFF3B82F6))
                            Spacer(Modifier.width(12.dp))
                            Text("Chọn từ Album", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            // Image Picker Section
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(PetSpaColors.PetPinkSurface)
                        .border(
                            width = 2.dp, 
                            color = if (isImageConfirmed) PetSpaColors.Success else PetSpaColors.PetPinkBorder, 
                            shape = CircleShape
                        )
                        .clickable { showPhotoOptions = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CameraAlt, null, tint = PetSpaColors.PetPinkDeep, modifier = Modifier.size(32.dp))
                            Text("Thêm ảnh", fontSize = 12.sp, color = PetSpaColors.PetPinkDeep, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (imageUri != null && !isImageConfirmed) {
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { isImageConfirmed = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PetSpaColors.Success),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Xác nhận ảnh này", fontSize = 13.sp)
                    }
                    TextButton(onClick = { imageUri = null }) {
                        Text("Hủy bỏ", color = PetSpaColors.Destructive, fontSize = 12.sp)
                    }
                } else if (isImageConfirmed) {
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = PetSpaColors.Success, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Ảnh đã xác nhận", color = PetSpaColors.Success, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Text("Thay đổi", color = PetSpaColors.PetPinkDeep, fontSize = 11.sp, modifier = Modifier.clickable { showPhotoOptions = true })
                    }
                }
            }

            // Species Selection
            Text("Loài", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                speciesEmojis.keys.forEach { s ->
                    val isSelected = species == s
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PetSpaColors.PetPinkSurface else Color.White)
                            .border(1.dp, if (isSelected) PetSpaColors.PetPink else Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                            .clickable { species = s }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(speciesEmojis[s] ?: "", fontSize = 20.sp)
                            Text(s, fontSize = 10.sp, color = if (isSelected) PetSpaColors.PetPinkDeep else PetSpaColors.MutedForeground)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            InputField(name, { name = it }, label = "Tên thú cưng", placeholder = "Lucky, Mimi...", required = true)
            Spacer(Modifier.height(12.dp))
            InputField(breed, { breed = it }, label = "Giống", placeholder = "Poodle, Ba Tư...", required = true)
            Spacer(Modifier.height(12.dp))

            Text("Giới tính", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Đực", "Cái").forEach { g ->
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
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(Modifier.weight(1f)) { InputField(age, { age = it }, label = "Tuổi (năm)", placeholder = "3", required = true) }
                Box(Modifier.weight(1f)) { InputField(weight, { weight = it }, label = "Cân nặng (kg)", placeholder = "4.5", required = true) }
            }

            Spacer(Modifier.height(12.dp))
            InputField(allergies, { allergies = it }, label = "Dị ứng", placeholder = "Không có, hoặc ghi rõ...")
            Spacer(Modifier.height(12.dp))
            InputField(medicalHistory, { medicalHistory = it }, label = "Tiền sử bệnh", placeholder = "Đã tiêm phòng, bệnh cũ...")
            Spacer(Modifier.height(12.dp))
            InputField(notes, { notes = it }, label = "Ghi chú đặc biệt", placeholder = "Tính cách, sở thích...")

            Spacer(Modifier.height(32.dp))
            PrimaryButton("Lưu Thú Cưng", onClick = {
                val p = Pet(
                    id = existingPet?.id ?: "p${System.currentTimeMillis()}",
                    name = name,
                    species = species,
                    breed = breed,
                    gender = gender,
                    age = age.toIntOrNull() ?: 0,
                    weight = weight.toDoubleOrNull() ?: 0.0,
                    allergies = allergies,
                    medicalHistory = medicalHistory,
                    notes = notes,
                    emoji = speciesEmojis[species] ?: "🐾",
                    imageUrl = if (isImageConfirmed) imageUri?.toString() else null
                )
                if (existingPet == null) vm.addPet(p) else vm.updatePet(p)
                onSaved()
            })
            Spacer(Modifier.height(40.dp))
        }
    }
}
