package com.petspa.app.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.petspa.app.viewmodel.OwnerViewModel

// Tương đương CHART_COLORS trong Dashboard.tsx
private val CHART_COLORS = listOf(
    Color(0xFFFF8FA3),
    Color(0xFF4ECDC4),
    Color(0xFF45B7D1),
    Color(0xFFF9CA24),
    Color(0xFF6C5CE7)
)

private val bookingStatusMap = mapOf(
    "pending"     to Triple("Chờ xác nhận",    Color(0xFFF39C12), Color(0xFFFFF3CD)),
    "confirmed"   to Triple("Đã xác nhận",     Color(0xFF3498DB), Color(0xFFD6EAF8)),
    "in_progress" to Triple("Đang thực hiện",  Color(0xFF9B59B6), Color(0xFFE8DAEF)),
    "completed"   to Triple("Hoàn thành",      Color(0xFF27AE60), Color(0xFFD5F5E3)),
    "cancelled"   to Triple("Đã hủy",          Color(0xFFE53E3E), Color(0xFFFDECEA)),
)

@Composable
fun OwnerDashboardScreen(vm: OwnerViewModel) {
    LaunchedEffect(Unit) { vm.loadDashboard() }
    val bookingsState by vm.bookings.collectAsState()
    var chartPeriod by remember { mutableStateOf("day") }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(PetSpaColors.OwnerBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    "Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = PetSpaColors.Foreground
                )
                Text(
                    "Chào mừng trở lại, Nguyễn Văn Admin",
                    color = PetSpaColors.MutedForeground,
                    fontSize = 14.sp
                )
            }
        }

        // Stats grid – 8 thẻ, tương đương stats[] trong Dashboard.tsx
        item {
            DashboardStatsGrid(bookingsState)
        }

        // Charts row – Revenue chart + Top services pie
        item {
            DashboardChartsRow(chartPeriod) { chartPeriod = it }
        }

        // Bottom row – Recent bookings + Staff performance bar
        item {
            DashboardBottomRow(bookingsState)
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

// ── Stats grid ────────────────────────────────────────────────────────────────

@Composable
fun DashboardStatsGrid(bookingsState: com.petspa.app.model.UiState<List<Booking>>) {
    val bookings = (bookingsState as? com.petspa.app.model.UiState.Success)?.data ?: emptyList()

    data class StatItem(
        val label: String,
        val value: String,
        val icon: ImageVector,
        val color: Color,
        val bg: Color,
        val change: String
    )

    val stats = listOf(
        StatItem("Tổng khách hàng",  "1,247",                         Icons.Default.People,        Color(0xFFFF8FA3), Color(0xFFFFE4E8), "+12%"),
        StatItem("Tổng nhân viên",   "5",                             Icons.Default.Badge,         Color(0xFF4ECDC4), Color(0xFFE0F8F6), "+2"),
        StatItem("Tổng thú cưng",    "24",                            Icons.Default.Pets,          Color(0xFF45B7D1), Color(0xFFDBF0F9), "+8"),
        StatItem("Tổng booking",     bookings.size.toString(),        Icons.Default.CalendarToday, Color(0xFFF9CA24), Color(0xFFFEF9E7), "+34%"),
        StatItem("Booking hôm nay",  "5",                             Icons.Default.Schedule,      Color(0xFF6C5CE7), Color(0xFFEDE7FF), "hôm nay"),
        StatItem("Booking chờ",      bookings.count { it.status == "pending" }.toString(),
            Icons.Default.BarChart,      Color(0xFFFD79A8), Color(0xFFFFE4F0), "cần duyệt"),
        StatItem("Doanh thu hôm nay","4.1M",                         Icons.Default.AttachMoney,   Color(0xFF00B894), Color(0xFFD8F8F0), "+15%"),
        StatItem("Doanh thu tháng",  "35.5M",                        Icons.Default.TrendingUp,    Color(0xFFE17055), Color(0xFFFDE8E3), "+8%"),
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        stats.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { s ->
                    OwnerStatCard(
                        label  = s.label,
                        value  = s.value,
                        icon   = s.icon,
                        color  = s.color,
                        bg     = s.bg,
                        change = s.change,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun OwnerStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    bg: Color,
    change: String,
    modifier: Modifier = Modifier
) {
    AppCard(modifier) {
        Column {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = bg
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, null, modifier = Modifier.size(18.dp), tint = color)
                    }
                }
                Surface(color = bg, shape = RoundedCornerShape(50.dp)) {
                    Text(
                        change,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = color,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PetSpaColors.Foreground)
            Text(label, style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
        }
    }
}

// ── Charts row ────────────────────────────────────────────────────────────────

@Composable
fun DashboardChartsRow(chartPeriod: String, onPeriodChange: (String) -> Unit) {
    // Trên mobile Compose xếp dọc (tương đương lg:grid-cols-3 trên web)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Revenue chart (chiếm 2/3 trên web → full width trên mobile)
        AppCard(Modifier.fillMaxWidth()) {
            Column {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Doanh thu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("day" to "Theo ngày", "month" to "Theo tháng").forEach { (key, label) ->
                            val selected = chartPeriod == key
                            Surface(
                                onClick = { onPeriodChange(key) },
                                color = if (selected) PetSpaColors.PetPink else PetSpaColors.PetPinkSurface,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    label,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = if (selected) Color.White else PetSpaColors.MutedForeground,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(PetSpaColors.PetPinkSurface, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Area Chart – Doanh thu (${if (chartPeriod == "day") "theo ngày" else "theo tháng"})", color = PetSpaColors.PetPinkDeep)
                }
            }
        }

        // Top services pie
        AppCard(Modifier.fillMaxWidth()) {
            Column {
                Text("Dịch vụ phổ biến", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Pie Chart – Top services", color = PetSpaColors.MutedForeground)
                }
                Spacer(Modifier.height(8.dp))
                // Legend 3 mục đầu (tương đương topServices.slice(0,3))
                val topServices = listOf("Tắm + Cắt tỉa lông" to 145, "Tắm & Sấy" to 132, "Cắt tỉa lông" to 98)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    topServices.forEachIndexed { i, (name, count) ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(10.dp)
                                        .background(CHART_COLORS[i], RoundedCornerShape(50.dp))
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(name, fontSize = 11.sp, color = PetSpaColors.MutedForeground)
                            }
                            Text(count.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PetSpaColors.Foreground)
                        }
                    }
                }
            }
        }
    }
}

// ── Bottom row ────────────────────────────────────────────────────────────────

@Composable
fun DashboardBottomRow(bookingsState: com.petspa.app.model.UiState<List<Booking>>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Recent bookings
        AppCard(Modifier.fillMaxWidth()) {
            Column {
                Text(
                    "Booking gần nhất",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PetSpaColors.Foreground
                )
                Spacer(Modifier.height(16.dp))
                UiStateContent(bookingsState, {}) { list ->
                    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                        list.take(4).forEachIndexed { index, b ->
                            val s = bookingStatusMap[b.status]
                                ?: Triple("Không xác định", PetSpaColors.MutedForeground, PetSpaColors.PetPinkSurface)
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "${b.id} — ${b.customerName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = PetSpaColors.Foreground
                                    )
                                    Text(
                                        "${b.petName} · ${b.serviceName}",
                                        fontSize = 12.sp,
                                        color = PetSpaColors.MutedForeground
                                    )
                                }
                                Surface(color = s.third, shape = RoundedCornerShape(50.dp)) {
                                    Text(
                                        s.first,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = s.second,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            if (index < 3) HorizontalDivider(color = Color(0xFFF5F5F5))
                        }
                    }
                }
            }
        }

        // Staff performance bar chart
        AppCard(Modifier.fillMaxWidth()) {
            Column {
                Text(
                    "Hiệu suất nhân viên",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PetSpaColors.Foreground
                )
                Spacer(Modifier.height(16.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(PetSpaColors.PetPinkSurface, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Bar Chart – Hiệu suất nhân viên", color = PetSpaColors.PetPinkDeep)
                }
            }
        }
    }
}