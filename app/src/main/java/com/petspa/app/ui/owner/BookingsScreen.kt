package com.petspa.app.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petspa.app.model.Booking
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.OwnerViewModel

// Tương đương bookingStatusMap trong Bookings.tsx
private val bookingStatusMap = mapOf(
    "pending"     to Triple("Chờ xác nhận",    Color(0xFFF39C12), Color(0xFFFFF3CD)),
    "confirmed"   to Triple("Đã xác nhận",     Color(0xFF3498DB), Color(0xFFD6EAF8)),
    "in_progress" to Triple("Đang thực hiện",  Color(0xFF9B59B6), Color(0xFFE8DAEF)),
    "completed"   to Triple("Hoàn thành",      Color(0xFF27AE60), Color(0xFFD5F5E3)),
    "cancelled"   to Triple("Đã hủy",          Color(0xFFE53E3E), Color(0xFFFDECEA)),
)

// Tương đương paymentStatusMap trong Bookings.tsx
private val paymentStatusMap = mapOf(
    "unpaid"         to Triple("Chưa thanh toán", Color(0xFFE53E3E), Color(0xFFFDECEA)),
    "partially_paid" to Triple("Đặt cọc",         Color(0xFFF39C12), Color(0xFFFFF3CD)),
    "fully_paid"     to Triple("Đã thanh toán",   Color(0xFF27AE60), Color(0xFFD5F5E3)),
)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun OwnerBookingsScreen(vm: OwnerViewModel) {
    LaunchedEffect(Unit) { vm.loadBookings() }
    val state by vm.bookings.collectAsState()
    var search by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("all") }

    // modal: "detail" | "edit" | "cancel"
    var modalType by remember { mutableStateOf<String?>(null) }
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }

    // toast
    var toast by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

    // edit form
    var editDate  by remember { mutableStateOf("") }
    var editTime  by remember { mutableStateOf("") }

    // cancel
    var cancelReason by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(PetSpaColors.OwnerBackground)
            .padding(16.dp)
    ) {
        // Header
        Column {
            Text(
                "Quản lý Booking",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PetSpaColors.Foreground
            )
            if (state is UiState.Success) {
                Text(
                    "${(state as UiState.Success<List<Booking>>).data.size} booking tổng cộng",
                    color = PetSpaColors.MutedForeground,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Summary cards (tương đương grid grid-cols-3 sm:grid-cols-5)
        if (state is UiState.Success) {
            val list = (state as UiState.Success<List<Booking>>).data
            BookingStatusSummary(list, statusFilter) { statusFilter = it }
        }

        Spacer(Modifier.height(16.dp))

        // Filters – search + status select
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.weight(1f)) {
                InputField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = "Tìm theo ID, khách hàng, thú cưng...",
                    leadingIcon = Icons.Default.Search
                )
            }
            Surface(
                modifier = Modifier.size(52.dp),
                shape = RoundedCornerShape(16.dp),
                color = PetSpaColors.PetPinkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, PetSpaColors.PetPinkBorder)
            ) {
                IconButton(onClick = { /* filter dialog */ }) {
                    Icon(Icons.Default.FilterList, null, tint = PetSpaColors.PetPinkDeep)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Table / List
        UiStateContent(state, { vm.loadBookings() }) { list ->
            val filtered = list.filter { b ->
                val matchSearch = b.id.contains(search) ||
                        b.customerName.lowercase().contains(search.lowercase()) ||
                        b.petName.lowercase().contains(search.lowercase())
                val matchStatus = statusFilter == "all" || b.status == statusFilter
                matchSearch && matchStatus
            }

            if (filtered.isEmpty()) {
                EmptyView(emoji = "📅", title = "Không có booking", subtitle = "Không tìm thấy booking phù hợp")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filtered, key = { it.id }) { b ->
                        BookingRowItem(
                            b = b,
                            onDetail = { selectedBooking = b; modalType = "detail" },
                            onEdit   = {
                                editDate = b.date; editTime = b.time
                                selectedBooking = b; modalType = "edit"
                            },
                            onCancel = {
                                cancelReason = ""
                                selectedBooking = b; modalType = "cancel"
                            }
                        )
                    }
                }
            }
        }
    }

    // ── Modals ────────────────────────────────────────────────────────────────

    // Detail modal
    if (modalType == "detail" && selectedBooking != null) {
        val b = selectedBooking!!
        val bs = bookingStatusMap[b.status] ?: Triple("Không xác định", PetSpaColors.MutedForeground, PetSpaColors.PetPinkSurface)
        val ps = paymentStatusMap[b.paymentStatus ?: "unpaid"] ?: Triple("Không xác định", PetSpaColors.MutedForeground, PetSpaColors.PetPinkSurface)

        AppModal(title = "Chi tiết Booking", onDismiss = { modalType = null }) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(
                    "Booking ID"      to b.id,
                    "Khách hàng"      to b.customerName,
                    "Thú cưng"        to b.petName,
                    "Dịch vụ"         to b.serviceName,
                    "Nhân viên"       to (b.staffName ?: "Chưa phân công"),
                    "Ngày & Giờ"      to "${b.date} · ${b.time}",
                    "Giá"             to formatVnd(b.totalAmount),
                    "Phương thức TT"  to (b.paymentMethod ?: "Chưa xác định"),
                ).forEach { (label, value) ->
                    InfoRowSmall(label, value)
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Trạng thái booking", style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground)
                    Surface(color = bs.third, shape = RoundedCornerShape(50.dp)) {
                        Text(bs.first, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = bs.second, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Thanh toán", style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground)
                    Surface(color = ps.third, shape = RoundedCornerShape(50.dp)) {
                        Text(ps.first, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = ps.second, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                if (!b.notes.isNullOrBlank()) {
                    Surface(color = PetSpaColors.PetPinkSurface, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("Ghi chú: ${b.notes}", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground)
                    }
                }
            }
        }
    }

    // Edit modal
    if (modalType == "edit" && selectedBooking != null) {
        AppModal(title = "Chỉnh sửa Booking", onDismiss = { modalType = null }) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                InputField(editDate, { editDate = it }, label = "Ngày", placeholder = "yyyy-mm-dd", leadingIcon = Icons.Default.CalendarToday)
                InputField(editTime, { editTime = it }, label = "Giờ",  placeholder = "HH:mm",      leadingIcon = Icons.Default.Schedule)
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SecondaryButton("Hủy", onClick = { modalType = null }, modifier = Modifier.weight(1f))
                    PrimaryButton(if (loading) "Đang lưu..." else "Lưu", onClick = {
                        // TODO: vm.updateBooking
                        modalType = null
                    }, modifier = Modifier.weight(1f))
                }
            }
        }
    }

    // Cancel modal
    if (modalType == "cancel" && selectedBooking != null) {
        AppModal(title = "Hủy Booking", onDismiss = { modalType = null }) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(
                    Modifier
                        .size(56.dp)
                        .background(Color(0xFFFDECEA), RoundedCornerShape(50.dp))
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Cancel, null, tint = Color(0xFFE53E3E), modifier = Modifier.size(28.dp))
                }
                Text(
                    "Hủy booking ${selectedBooking!!.id}?",
                    color = PetSpaColors.Foreground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Column {
                    Text("Lý do hủy *", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = cancelReason,
                        onValueChange = { cancelReason = it },
                        placeholder = { Text("Nhập lý do hủy booking...", color = PetSpaColors.MutedForeground) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PetSpaColors.PetPink,
                            unfocusedBorderColor = PetSpaColors.PetPinkBorder
                        )
                    )
                }
                Text(
                    "Thông báo hủy sẽ được gửi đến khách hàng.",
                    fontSize = 11.sp,
                    color = PetSpaColors.MutedForeground
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SecondaryButton("Đóng", onClick = { modalType = null }, modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            if (cancelReason.isBlank()) return@Button
                            // TODO: vm.cancelBooking(selectedBooking!!.id, cancelReason)
                            modalType = null
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53E3E))
                    ) {
                        Text(if (loading) "Đang hủy..." else "Xác nhận hủy", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ── Summary cards (tương đương grid booking status trong Bookings.tsx) ────────

@Composable
fun BookingStatusSummary(
    bookings: List<Booking>,
    currentFilter: String,
    onFilterChange: (String) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        bookingStatusMap.entries.toList().let { entries ->
            items(entries) { (key, triple) ->
                val count = bookings.count { it.status == key }
                val selected = currentFilter == key
                Surface(
                    onClick = { onFilterChange(if (selected) "all" else key) },
                    modifier = Modifier.width(108.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = if (selected) triple.third else Color.White,
                    border = androidx.compose.foundation.BorderStroke(
                        2.dp,
                        if (selected) triple.second.copy(alpha = 0.5f) else Color(0xFFF5F5F5)
                    )
                ) {
                    Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(count.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = triple.second)
                        Text(triple.first, fontSize = 10.sp, color = PetSpaColors.MutedForeground)
                    }
                }
            }
        }
    }
}

// ── Booking row card ──────────────────────────────────────────────────────────

@Composable
fun BookingRowItem(
    b: Booking,
    onDetail: () -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit
) {
    val bs = bookingStatusMap[b.status] ?: Triple("Không xác định", PetSpaColors.MutedForeground, PetSpaColors.PetPinkSurface)
    val ps = paymentStatusMap[b.paymentStatus ?: "unpaid"] ?: Triple("Không xác định", PetSpaColors.MutedForeground, PetSpaColors.PetPinkSurface)
    val canModify = b.status != "cancelled" && b.status != "completed"

    AppCard {
        // Top info
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(b.id, style = MaterialTheme.typography.labelMedium, color = PetSpaColors.PetPinkDeep, fontWeight = FontWeight.Bold)
                Text(b.customerName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text("${b.petName} · ${b.serviceName}", style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, modifier = Modifier.size(14.dp), tint = PetSpaColors.MutedForeground)
                    Spacer(Modifier.width(4.dp))
                    Text("${b.date} · ${b.time}", fontSize = 11.sp, color = PetSpaColors.MutedForeground)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                // Booking status badge
                Surface(color = bs.third, shape = RoundedCornerShape(50.dp)) {
                    Text(bs.first, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = bs.second, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(4.dp))
                // Payment status badge
                Surface(color = ps.third, shape = RoundedCornerShape(50.dp)) {
                    Text(ps.first, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = ps.second, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                Text(formatVnd(b.totalAmount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PetSpaColors.Foreground)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Action buttons (tương đương icon buttons trong table row của Bookings.tsx)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StaffActionBtn(Icons.Default.Visibility, "Xem",  Color(0xFF45B7D1), Color(0xFFDBF0F9), Modifier.weight(1f)) { onDetail() }
            if (canModify) {
                StaffActionBtn(Icons.Default.Edit,   "Sửa",  Color(0xFFF9CA24), Color(0xFFFEF9E7), Modifier.weight(1f)) { onEdit() }
                StaffActionBtn(Icons.Default.Cancel, "Hủy",  Color(0xFFE53E3E), Color(0xFFFDECEA), Modifier.weight(1f)) { onCancel() }
            }
        }
    }
}