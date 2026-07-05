package com.petspa.app.ui.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petspa.app.model.Booking
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.StaffViewModel

@Composable
fun StaffBookingsScreen(vm: StaffViewModel, onDetail: (String) -> Unit) {
    LaunchedEffect(Unit) { vm.loadBookings() }
    val state by vm.bookings.collectAsState()
    
    Column(Modifier.fillMaxSize()) {
        PinkGradientHeader("Danh sách đặt lịch", "Quản lý công việc của bạn")
        UiStateContent(state, { vm.loadBookings() }) { list ->
            LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(list) { booking ->
                    BookingItemCard(booking, onClick = { onDetail(booking.id) })
                }
            }
        }
    }
}

@Composable
fun StaffBookingDetailScreen(vm: StaffViewModel, bookingId: String, onBack: () -> Unit) {
    val state by vm.bookings.collectAsState()
    val booking = (state as? com.petspa.app.model.UiState.Success)?.data?.find { it.id == bookingId }
    
    var currentStatus by remember(booking) { mutableStateOf(booking?.status ?: "pending") }
    
    val statusSteps = listOf(
        Triple("pending", "Chờ xác nhận", Color(0xFFF59E0B)),
        Triple("checked-in", "Checked In", Color(0xFF3B82F6)),
        Triple("bathing", "Bathing", Color(0xFF8B5CF6)),
        Triple("grooming", "Grooming", Color(0xFF6366F1)),
        Triple("drying", "Drying", Color(0xFF06B6D4)),
        Triple("completed", "Completed", Color(0xFF10B981))
    )

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Chi tiết lịch hẹn", onBack)
        if (booking != null) {
            Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                // Pet Info Card
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🐩", fontSize = 48.sp) // Mock pet image
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(booking.petName, style = MaterialTheme.typography.titleLarge)
                            Text(booking.customerName, color = PetSpaColors.MutedForeground)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DetailItem("Loài", "Chó", Modifier.weight(1f))
                        DetailItem("Giống", "Poodle", Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DetailItem("Tuổi", "3 tuổi", Modifier.weight(1f))
                        DetailItem("Cân nặng", "4.5 kg", Modifier.weight(1f))
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Service Info
                AppCard {
                    Text("THÔNG TIN DỊCH VỤ", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
                    Spacer(Modifier.height(12.dp))
                    IconText(Icons.Default.CalendarToday, booking.date)
                    Spacer(Modifier.height(8.dp))
                    IconText(Icons.Default.Schedule, "${booking.time} - ${booking.serviceDuration} phút")
                    Spacer(Modifier.height(8.dp))
                    Text(booking.serviceName, color = PetSpaColors.PetPinkDeep, fontWeight = FontWeight.Bold)
                    if (booking.customerRequests.isNotBlank()) {
                        Spacer(Modifier.height(12.dp))
                        Text("Yêu cầu khách hàng:", style = MaterialTheme.typography.labelSmall)
                        Text(booking.customerRequests, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Status Update
                Text("CẬP NHẬT TRẠNG THÁI", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
                Spacer(Modifier.height(8.dp))
                statusSteps.forEach { (valStr, label, color) ->
                    val isActive = currentStatus == valStr
                    val isPast = statusSteps.indexOfFirst { it.first == currentStatus } > statusSteps.indexOfFirst { it.first == valStr }
                    
                    Surface(
                        onClick = { currentStatus = valStr },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isActive) color else if (isPast) PetSpaColors.PetPinkSurface else PetSpaColors.InputBackground,
                        border = if (isActive) null else androidx.compose.foundation.BorderStroke(1.dp, PetSpaColors.PetPinkBorder.copy(alpha = 0.5f))
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (isActive || isPast) Icon(Icons.Default.CheckCircle, null, tint = if (isActive) Color.White else color)
                            else Box(Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(label, color = if (isActive) Color.White else if (isPast) color else PetSpaColors.Foreground, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Photos Section
                Text("HÌNH ẢNH DỊCH VỤ", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
                Spacer(Modifier.height(8.dp))
                PhotoUploadSection("Ảnh trước dịch vụ")
                Spacer(Modifier.height(12.dp))
                PhotoUploadSection("Ảnh sau dịch vụ")

                Spacer(Modifier.height(16.dp))

                // Professional Notes
                Text("GHI CHÚ CHUYÊN MÔN", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
                Spacer(Modifier.height(8.dp))
                AppCard {
                    var customNote by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = customNote,
                        onValueChange = { customNote = it },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        placeholder = { Text("Thêm ghi chú...") },
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    PrimaryButton("Thêm ghi chú", onClick = { /* Logic */ })
                }
                
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun IconText(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = PetSpaColors.MutedForeground)
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun PhotoUploadSection(label: String) {
    AppCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.titleSmall)
            IconButton(onClick = { /* Upload */ }) {
                Icon(Icons.Default.CameraAlt, null, tint = PetSpaColors.PetPinkDeep)
            }
        }
        Box(Modifier.fillMaxWidth().height(100.dp).background(PetSpaColors.InputBackground, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            Text("Chưa có ảnh", color = PetSpaColors.MutedForeground, fontSize = 12.sp)
        }
    }
}
