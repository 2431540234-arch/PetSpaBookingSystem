package com.petspa.app.ui.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petspa.app.model.Booking
import com.petspa.app.model.Shift
import com.petspa.app.model.TechnicianProfile
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.StaffViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun StaffDashboardScreen(
    vm: StaffViewModel,
    onBookingDetail: (String) -> Unit,
    onNavigateBookings: () -> Unit,
    onNavigateShifts: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateProfile: () -> Unit
) {
    LaunchedEffect(Unit) { vm.loadDashboard(); vm.loadProfile() }
    val bookingsState by vm.bookings.collectAsState()
    val shiftsState by vm.shifts.collectAsState()
    val profileState by vm.profile.collectAsState()

    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("vi")))

    Column(Modifier.fillMaxSize()) {
        PinkGradientHeader("Dashboard", today)
        
        LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Technician Info Card
            item {
                UiStateContent(profileState, { vm.loadProfile() }) { profile ->
                    TechnicianCard(profile)
                }
            }

            // Stats Grid
            item {
                if (bookingsState is com.petspa.app.model.UiState.Success) {
                    val bookings = (bookingsState as com.petspa.app.model.UiState.Success).data
                    StaffStatsGrid(bookings)
                }
            }
            
            // Today's Schedule
            item {
                Text("Lịch hôm nay", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                UiStateContent(shiftsState, { vm.loadShifts() }) { shifts ->
                    shifts.firstOrNull()?.let { shift ->
                        TodayScheduleCard(shift)
                    } ?: Text("Không có ca làm việc hôm nay", color = PetSpaColors.MutedForeground)
                }
            }

            // Quick Actions
            item {
                Text("Thao tác nhanh", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                QuickActionsGrid(
                    onNavigateBookings = onNavigateBookings,
                    onNavigateShifts = onNavigateShifts,
                    onNavigateNotifications = onNavigateNotifications,
                    onNavigateProfile = onNavigateProfile
                )
            }
            
            // Upcoming Bookings
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Booking sắp tới", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = onNavigateBookings) {
                        Text("Xem tất cả", color = PetSpaColors.PetPinkDeep)
                    }
                }
                UiStateContent(bookingsState, { vm.loadBookings() }) { bookings ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        bookings.take(3).forEach { booking ->
                            BookingItemCard(booking, onClick = { onBookingDetail(booking.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TechnicianCard(profile: TechnicianProfile) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(PetSpaColors.PetPink, PetSpaColors.PetPinkDeep)),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(profile.avatar, fontSize = 32.sp)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(profile.name, style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Text(profile.position, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(0.8f))
                    Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        profile.expertise.take(3).forEach { exp ->
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    exp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StaffStatsGrid(bookings: List<Booking>) {
    val total = bookings.size
    val pending = bookings.count { it.status == "pending" }
    val inProgress = bookings.count { it.status in listOf("checked_in", "bathing", "grooming", "drying") }
    val completed = bookings.count { it.status == "completed" }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StaffStatItem("Hôm nay", total.toString(), Icons.Default.CalendarToday, PetSpaColors.PetPinkDeep, Modifier.weight(1f))
            StaffStatItem("Sắp tới", pending.toString(), Icons.Default.Schedule, Color(0xFFF59E0B), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StaffStatItem("Đang làm", inProgress.toString(), Icons.Default.PlayArrow, Color(0xFF3B82F6), Modifier.weight(1f))
            StaffStatItem("Hoàn thành", completed.toString(), Icons.Default.CheckCircle, Color(0xFF10B981), Modifier.weight(1f))
        }
    }
}

@Composable
fun StaffStatItem(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.1f)
            ) {
                Icon(icon, null, modifier = Modifier.padding(8.dp), tint = color)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(label, style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
            }
        }
    }
}

@Composable
fun TodayScheduleCard(shift: Shift) {
    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth().background(PetSpaColors.PetPinkSurface, RoundedCornerShape(8.dp)).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, null, tint = PetSpaColors.PetPinkDeep, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Ca ${if (shift.type == "morning") "sáng" else if (shift.type == "afternoon") "chiều" else "tối"}", style = MaterialTheme.typography.titleSmall)
                    Text("${shift.startTime} - ${shift.endTime}", color = PetSpaColors.MutedForeground)
                }
            }
            StatusBadge("Đã duyệt", PetSpaColors.Success)
        }
    }
}

@Composable
fun QuickActionsGrid(
    onNavigateBookings: () -> Unit,
    onNavigateShifts: () -> Unit,
    onNavigateNotifications: () -> Unit,
    onNavigateProfile: () -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        QuickActionItem("Bookings", Icons.Default.EventNote, onClick = onNavigateBookings, modifier = Modifier.weight(1f))
        QuickActionItem("Ca làm", Icons.Default.Schedule, onClick = onNavigateShifts, modifier = Modifier.weight(1f))
        QuickActionItem("Thông báo", Icons.Default.Notifications, onClick = onNavigateNotifications, modifier = Modifier.weight(1f))
        QuickActionItem("Hồ sơ", Icons.Default.Person, onClick = onNavigateProfile, modifier = Modifier.weight(1f))
    }
}

@Composable
fun QuickActionItem(label: String, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier, onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()) {
            Icon(icon, null, tint = PetSpaColors.PetPinkDeep)
            Spacer(Modifier.height(4.dp))
            Text(label, fontSize = 10.sp, maxLines = 1)
        }
    }
}
