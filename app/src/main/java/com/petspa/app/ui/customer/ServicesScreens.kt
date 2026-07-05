package com.petspa.app.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petspa.app.model.Service
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.CustomerViewModel

@Composable
fun ServicesScreen(
    vm: CustomerViewModel,
    onBack: () -> Unit,
    onDetail: (String) -> Unit,
    onBook: (String) -> Unit
) {
    LaunchedEffect(Unit) { vm.loadServices() }
    val state by vm.services.collectAsState()
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Tất cả") }

    val categories = listOf("Tất cả", "Tắm", "Cắt tỉa lông", "Chăm sóc da", "Spa cao cấp")

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB))
    ) {
        BackHeader("Dịch Vụ", onBack)

        // Search Bar
        Box(Modifier.padding(16.dp)) {
            InputField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Tìm kiếm dịch vụ...",
                leadingIcon = Icons.Default.Search
            )
        }

        // Category Filter
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    onClick = { selectedCategory = cat },
                    color = if (isSelected) PetSpaColors.PetPink else PetSpaColors.PetPinkSurface,
                    shape = RoundedCornerShape(50.dp),
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, PetSpaColors.PetPinkBorder)
                ) {
                    Text(
                        cat,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else PetSpaColors.Foreground,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        UiStateContent(state, { vm.loadServices() }) { list ->
            val filtered = list.filter {
                (selectedCategory == "Tất cả" || it.category == selectedCategory) &&
                        it.name.contains(query, ignoreCase = true)
            }

            if (filtered.isEmpty()) {
                EmptyView(emoji = "🔍", title = "Không tìm thấy dịch vụ", subtitle = "Vui lòng thử tìm kiếm với từ khóa khác")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filtered) { s ->
                        ServiceCard(s, onClick = { onDetail(s.id) })
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun ServiceCard(s: Service, onClick: () -> Unit) {
    AppCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.Top) {
            Text(s.emoji, fontSize = 48.sp)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.weight(1f)) {
                        Text(s.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Surface(color = PetSpaColors.PetPinkSurface, shape = RoundedCornerShape(50.dp)) {
                            Text(s.category, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), color = PetSpaColors.PetPinkDeep, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(formatVnd(s.price), color = PetSpaColors.PetPinkDeep, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                Text(s.description, style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(8.dp))
                Text("⏱ ${s.duration} phút", style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground.copy(0.6f))
            }
        }
    }
}

@Composable
fun ServiceDetailScreen(vm: CustomerViewModel, serviceId: String, onBack: () -> Unit, onBook: (String) -> Unit) {
    LaunchedEffect(Unit) { if (vm.services.value !is UiState.Success) vm.loadServices() }
    val state by vm.services.collectAsState()
    val svc = (state as? UiState.Success)?.data?.find { it.id == serviceId }

    Column(Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {
        BackHeader("Chi Tiết Dịch Vụ", onBack)
        if (svc != null) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                // Hero Section
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(PetSpaColors.PetPinkSurface, Color(0xFFFFF5F7))))
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(svc.emoji, fontSize = 80.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(svc.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Surface(color = PetSpaColors.PetPink, shape = RoundedCornerShape(50.dp)) {
                            Text(svc.category, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                Column(Modifier.padding(16.dp)) {
                    // Info Row
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AppCard(Modifier.weight(1f)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text("Giá dịch vụ", fontSize = 11.sp, color = PetSpaColors.MutedForeground)
                                Text(formatVnd(svc.price), style = MaterialTheme.typography.titleMedium, color = PetSpaColors.PetPinkDeep, fontWeight = FontWeight.Bold)
                            }
                        }
                        AppCard(Modifier.weight(1f)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text("Thời lượng", fontSize = 11.sp, color = PetSpaColors.MutedForeground)
                                Text("⏱ ${svc.duration} phút", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Description
                    AppCard {
                        Text("Mô Tả Dịch Vụ", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(svc.description, style = MaterialTheme.typography.bodyMedium, color = PetSpaColors.MutedForeground)
                    }

                    Spacer(Modifier.height(16.dp))

                    // Included section
                    AppCard {
                        Text("Bao Gồm", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(12.dp))
                        val included = when(svc.category) {
                            "Tắm" -> listOf("Tắm với sữa tắm chuyên dụng", "Sấy lông khô hoàn toàn", "Chải lông mềm mượt", "Kiểm tra sức khỏe cơ bản")
                            "Cắt tỉa lông" -> listOf("Cắt lông theo yêu cầu", "Tỉa lông vùng mặt và chân", "Cắt móng", "Làm sạch tai")
                            "Chăm sóc da" -> listOf("Kiểm tra tình trạng da", "Điều trị bằng sản phẩm chuyên dụng", "Massage vùng da", "Tư vấn chăm sóc tại nhà")
                            else -> listOf("Tắm thơm với sản phẩm cao cấp", "Massage toàn thân", "Chăm sóc da mặt", "Cắt tỉa và tạo kiểu", "Ảnh kỷ niệm sau dịch vụ")
                        }
                        included.forEach { item ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                Icon(Icons.Default.Check, null, tint = PetSpaColors.Success, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(item, style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground)
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    PrimaryButton("📅 Đặt Lịch Ngay", onClick = { onBook(svc.id) })
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}
