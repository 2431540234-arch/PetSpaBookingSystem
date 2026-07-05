package com.petspa.app.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petspa.app.model.Booking
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.CustomerViewModel

@Composable
fun AppointmentsScreen(vm: CustomerViewModel, onDetail: (String) -> Unit, onBook: () -> Unit) {
    LaunchedEffect(Unit) { vm.loadBookings() }
    val state by vm.bookings.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("all") }

    val filters = listOf(
        "all" to "Tất cả",
        "pending" to "Sắp tới",
        "completed" to "Xong",
        "cancelled" to "Hủy"
    )

    Column(Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        PinkGradientHeader(
            title = "Lịch hẹn",
            subtitle = "Theo dõi các cuộc hẹn của bạn",
            trailingContent = {
                IconButton(
                    onClick = onBook,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        )
        
        // Search and Filter
        Column(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ShadcnInput(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = "Tìm theo tên thú cưng hoặc dịch vụ...",
                leadingIcon = Icons.Default.Search
            )
            
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { (value, label) ->
                    val selected = selectedFilter == value
                    Surface(
                        modifier = Modifier
                            .clickable { selectedFilter = value }
                            .weight(1f),
                        color = if (selected) PetSpaColors.PetPinkDeep else Color.White,
                        shape = RoundedCornerShape(8.dp),
                        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Text(
                            label,
                            modifier = Modifier.padding(vertical = 8.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) Color.White else Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        UiStateContent(state, { vm.loadBookings() }) { list ->
            val filteredList = list.filter { b ->
                val matchesSearch = b.petName.contains(searchQuery, ignoreCase = true) || 
                                   b.serviceName.contains(searchQuery, ignoreCase = true)
                val matchesFilter = if (selectedFilter == "all") true else b.status == selectedFilter
                matchesSearch && matchesFilter
            }

            if (filteredList.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📅", fontSize = 48.sp)
                        Spacer(Modifier.height(16.dp))
                        Text("Không tìm thấy lịch hẹn nào", color = Color(0xFF64748B))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                    // Add Appointment Card (Dashed style) - Now at the TOP
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Transparent)
                            .border(
                                width = 2.dp,
                                color = PetSpaColors.PetPinkBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { onBook() }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = PetSpaColors.PetPinkDeep,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Đặt lịch hẹn mới",
                                color = PetSpaColors.PetPinkDeep,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                items(filteredList) { b ->
                    BookingCardNew(b) { onDetail(b.id) }
                }
            }
            }
        }
    }
}

@Composable
private fun BookingCardNew(b: Booking, onClick: () -> Unit) {
    ShadcnCard(modifier = Modifier.clickable { onClick() }) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PetSpaColors.PetPinkSurface),
                contentAlignment = Alignment.Center
            ) {
                Text("🐾", fontSize = 24.sp)
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(b.petName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    ShadcnBadge(
                        text = when(b.status) {
                            "pending" -> "Chờ xác nhận"
                            "confirmed" -> "Đã xác nhận"
                            "completed" -> "Hoàn thành"
                            "cancelled" -> "Đã hủy"
                            else -> b.status
                        },
                        backgroundColor = when(b.status) {
                            "completed" -> Color(0xFFDCFCE7)
                            "cancelled" -> Color(0xFFFEE2E2)
                            "pending" -> Color(0xFFFEF3C7)
                            else -> PetSpaColors.PetPinkSurface
                        },
                        contentColor = when(b.status) {
                            "completed" -> Color(0xFF16A34A)
                            "cancelled" -> Color(0xFFDC2626)
                            "pending" -> Color(0xFFD97706)
                            else -> PetSpaColors.PetPinkDeep
                        }
                    )
                }
                Text(b.serviceName, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                
                Spacer(Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(14.dp), tint = Color(0xFF94A3B8))
                    Spacer(Modifier.width(4.dp))
                    Text(b.date, fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.Default.AccessTime, null, modifier = Modifier.size(14.dp), tint = Color(0xFF94A3B8))
                    Spacer(Modifier.width(4.dp))
                    Text(b.time, fontSize = 12.sp, color = Color(0xFF64748B))
                }
            }
        }
    }
}

@Composable
fun AppointmentDetailScreen(vm: CustomerViewModel, bookingId: String, onBack: () -> Unit, onReschedule: () -> Unit, onCancel: () -> Unit) {
    LaunchedEffect(Unit) { if (vm.bookings.value !is UiState.Success) vm.loadBookings() }
    val state by vm.bookings.collectAsState()
    val booking = (state as? UiState.Success)?.data?.find { it.id == bookingId }
    var showCancelDialog by remember { mutableStateOf(false) }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Xác nhận hủy") },
            text = { Text("Bạn có chắc chắn muốn hủy lịch hẹn này không?") },
            confirmButton = {
                TextButton(onClick = {
                    vm.cancelBooking(bookingId)
                    showCancelDialog = false
                    onCancel()
                }) {
                    Text("Hủy lịch", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Quay lại")
                }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        AppTopBar("Chi tiết lịch hẹn", onBack)
        if (booking != null) {
            Column(Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text("#${booking.id}", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
                Text("${booking.petName} - ${booking.serviceName}", style = MaterialTheme.typography.titleLarge)
                ProfileDetail("Ngày", booking.date); ProfileDetail("Giờ", booking.time)
                ProfileDetail("Nhân viên", booking.staffName); ProfileDetail("Tổng tiền", formatVnd(booking.totalAmount))
                ProfileDetail("Thanh toán", booking.paymentStatus)
                StatusBadge(booking.status, statusColor(booking.status))
                if (booking.notes.isNotBlank()) { Spacer(Modifier.height(8.dp)); Text(booking.notes) }
                Spacer(Modifier.height(16.dp))
                if (booking.status in listOf("pending","confirmed")) {
                    PrimaryButton("Đổi lịch", onReschedule)
                    Spacer(Modifier.height(8.dp))
                    SecondaryButton("Hủy lịch", onClick = { showCancelDialog = true })
                }
            }
        }
    }
}

@Composable
fun RescheduleScreen(vm: CustomerViewModel, bookingId: String, onBack: () -> Unit, onDone: () -> Unit) {
    var date by remember { mutableStateOf("2027-08-16") }
    var time by remember { mutableStateOf("10:00") }
    val slots = vm.getTimeSlots()
    Column(Modifier.fillMaxSize()) {
        AppTopBar("Đổi lịch hẹn", onBack)
        Column(Modifier.padding(24.dp)) {
            OutlinedTextField(date, { date = it }, label = { Text("Ngày mới") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp))
            Spacer(Modifier.height(12.dp))
            Text("Khung giờ mới", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 240.dp)) {
                items(slots) { slot ->
                    val booked = vm.getBookedSlots(date).contains(slot)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !booked) { time = slot },
                        color = if (time == slot) PetSpaColors.PetPinkSurface else if (booked) Color(0xFFF1F5F9) else Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (time == slot) PetSpaColors.PetPink else Color(0xFFE2E8F0))
                    ) {
                        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(slot, fontWeight = if (time == slot) FontWeight.Bold else FontWeight.Normal)
                            if (booked) Text("Đã có lịch", color = Color.Red, fontSize = 11.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            PrimaryButton("Xác nhận đổi lịch", onClick = {
                vm.rescheduleBooking(bookingId, date, time)
                onDone()
            })
        }
    }
}


@Composable private fun ProfileDetail(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = PetSpaColors.MutedForeground); Text(value)
    }
}

private fun statusColor(status: String) = when (status) {
    "completed" -> PetSpaColors.Success
    "cancelled" -> PetSpaColors.Destructive
    "confirmed" -> PetSpaColors.PetPinkDeep
    else -> PetSpaColors.Warning
}
