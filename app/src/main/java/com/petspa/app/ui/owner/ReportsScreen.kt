package com.petspa.app.ui.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.petspa.app.model.StaffMember
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.OwnerViewModel

@Composable
fun OwnerReportsScreen(vm: OwnerViewModel) {
    LaunchedEffect(Unit) { vm.loadReports() }
    val staffState by vm.staff.collectAsState()

    var period by remember { mutableStateOf("month") }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(PetSpaColors.OwnerBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("Báo cáo & Thống kê", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PetSpaColors.Foreground)
                Text("Tổng quan doanh thu và hiệu suất", color = PetSpaColors.MutedForeground, fontSize = 14.sp)
            }
        }

        // Summary cards
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ReportSummaryCard("Tiền mặt", "15.0M", Color(0xFF27AE60), Modifier.weight(1f))
                ReportSummaryCard("MoMo", "8.5M", Color(0xFF9B59B6), Modifier.weight(1f))
                ReportSummaryCard("VNPay", "12.0M", Color(0xFF3498DB), Modifier.weight(1f))
            }
        }

        // Chart Card
        item {
            AppCard {
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Doanh thu theo thời gian", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("day", "week", "month", "year").forEach { p ->
                                val isSelected = period == p
                                Surface(
                                    onClick = { period = p },
                                    color = if (isSelected) PetSpaColors.PetPink else PetSpaColors.PetPinkSurface,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        p.replaceFirstChar { it.uppercase() },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = if (isSelected) Color.White else PetSpaColors.MutedForeground,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Box(Modifier.fillMaxWidth().height(200.dp).background(PetSpaColors.PetPinkSurface, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Text("Line Chart Placeholder", color = PetSpaColors.PetPinkDeep)
                    }
                }
            }
        }

        // Services & Payment Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                TopServicesCard()
                PaymentBreakdownCard()
            }
        }

        // Staff Performance Table
        item {
            StaffPerformanceTable(staffState)
        }
        
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
fun ReportSummaryCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    AppCard(modifier) {
        Column {
            Surface(modifier = Modifier.size(36.dp), shape = RoundedCornerShape(10.dp), color = color.copy(0.12f)) {
                Icon(Icons.Default.CreditCard, null, modifier = Modifier.padding(8.dp), tint = color)
            }
            Spacer(Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
        }
    }
}

@Composable
fun TopServicesCard() {
    AppCard {
        Column {
            Text("Dịch vụ phổ biến", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            listOf(
                Triple("Tắm + Cắt tỉa", "145", "46.4M"),
                Triple("Tắm & Sấy", "132", "19.8M"),
                Triple("Cắt tỉa lông", "98", "19.6M"),
                Triple("Spa thư giãn", "67", "30.1M")
            ).forEach { (name, count, revenue) ->
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                        Text("$count lượt đặt", fontSize = 10.sp, color = PetSpaColors.MutedForeground)
                    }
                    Text(revenue, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = PetSpaColors.PetPinkDeep)
                }
                HorizontalDivider(color = Color(0xFFF5F5F5))
            }
        }
    }
}

@Composable
fun PaymentBreakdownCard() {
    AppCard {
        Column {
            Text("Trạng thái thanh toán", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                Text("Pie Chart", color = PetSpaColors.MutedForeground)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PaymentStatusBox("Fully Paid", "35.5M", "87 bookings", Color(0xFF27AE60), Modifier.weight(1f))
                PaymentStatusBox("Partially Paid", "12.0M", "24 bookings", Color(0xFFF39C12), Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun PaymentStatusBox(label: String, value: String, sub: String, color: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = PetSpaColors.PetPinkSurface, shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontSize = 10.sp, color = PetSpaColors.MutedForeground)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(sub, fontSize = 9.sp, color = PetSpaColors.MutedForeground)
        }
    }
}

@Composable
fun StaffPerformanceTable(staffState: UiState<List<StaffMember>>) {
    AppCard {
        Column {
            Text("Hiệu suất Nhân viên", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            UiStateContent(staffState, {}) { list ->
                Column {
                    list.take(5).forEach { s ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(32.dp).background(PetSpaColors.PetPinkSurface, CircleShape), contentAlignment = Alignment.Center) {
                                Text(s.name.take(1), color = PetSpaColors.PetPinkDeep, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(s.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                Text("Hoàn thành: ${s.completedBookings}", fontSize = 10.sp, color = PetSpaColors.MutedForeground)
                            }
                            Text("★ 4.8", color = Color(0xFFF9CA24), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        if (list.indexOf(s) < 4) HorizontalDivider(color = Color(0xFFF5F5F5))
                    }
                }
            }
        }
    }
}
