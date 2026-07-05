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
import com.petspa.app.model.StaffRequest
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.OwnerViewModel

// Tương đương typeMap trong StaffRequests.tsx
private data class TypeInfo(val label: String, val color: Color, val bg: Color, val emoji: String)
private val typeMap = mapOf(
    "register_shift" to TypeInfo("Đăng ký ca làm", Color(0xFF3498DB), Color(0xFFD6EAF8), "📅"),
    "busy"           to TypeInfo("Báo bận",         Color(0xFFF39C12), Color(0xFFFFF3CD), "🚫"),
    "day_off"        to TypeInfo("Xin nghỉ phép",   Color(0xFF9B59B6), Color(0xFFE8DAEF), "🏖️"),
)

// Tương đương statusMap trong StaffRequests.tsx
private data class StatusInfo(val label: String, val color: Color, val bg: Color)
private val statusInfoMap = mapOf(
    "pending"  to StatusInfo("Chờ duyệt", Color(0xFFF39C12), Color(0xFFFFF3CD)),
    "approved" to StatusInfo("Đã duyệt",  Color(0xFF27AE60), Color(0xFFD5F5E3)),
    "rejected" to StatusInfo("Từ chối",   Color(0xFFE53E3E), Color(0xFFFDECEA)),
)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun OwnerRequestsScreen(vm: OwnerViewModel) {
    LaunchedEffect(Unit) { vm.loadRequests() }
    val state by vm.requests.collectAsState()
    var filter by remember { mutableStateOf("all") }

    // Detail modal
    var selectedRequest  by remember { mutableStateOf<StaffRequest?>(null) }
    var showDetail       by remember { mutableStateOf(false) }
    var rejectMode       by remember { mutableStateOf(false) }
    var rejectReason     by remember { mutableStateOf("") }
    var loading          by remember { mutableStateOf(false) }

    // toast
    var toast by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .background(PetSpaColors.OwnerBackground)
            .padding(16.dp)
    ) {
        // Header
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Yêu cầu Nhân viên",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = PetSpaColors.Foreground
                )
                if (state is UiState.Success) {
                    val pending = (state as UiState.Success<List<StaffRequest>>).data.count { it.status == "pending" }
                    if (pending > 0) {
                        Spacer(Modifier.width(8.dp))
                        Surface(color = PetSpaColors.PetPinkDeep, shape = RoundedCornerShape(50.dp)) {
                            Text(
                                "$pending chờ duyệt",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            if (state is UiState.Success) {
                Text(
                    "${(state as UiState.Success<List<StaffRequest>>).data.size} yêu cầu tổng cộng",
                    color = PetSpaColors.MutedForeground,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Summary filter row (tương đương grid 3 ô trong StaffRequests.tsx)
        if (state is UiState.Success) {
            val list = (state as UiState.Success<List<StaffRequest>>).data
            RequestSummaryRow(list, filter) { filter = it }
        }

        Spacer(Modifier.height(16.dp))

        // Request cards
        UiStateContent(state, { vm.loadRequests() }) { list ->
            val filtered = if (filter == "all") list else list.filter { it.status == filter }

            if (filtered.isEmpty()) {
                EmptyView(emoji = "📋", title = "Không có yêu cầu", subtitle = "Các yêu cầu từ nhân viên sẽ hiện ở đây")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(filtered, key = { it.id }) { req ->
                        StaffRequestCard(req) {
                            selectedRequest = req
                            rejectMode = false
                            rejectReason = ""
                            showDetail = true
                        }
                    }
                }
            }
        }
    }

    // ── Detail modal (tương đương modal trong StaffRequests.tsx) ──────────────

    if (showDetail && selectedRequest != null) {
        val req = selectedRequest!!
        val t = typeMap[req.type] ?: TypeInfo(req.type, PetSpaColors.MutedForeground, PetSpaColors.PetPinkSurface, "❓")
        val s = statusInfoMap[req.status] ?: StatusInfo(req.status, PetSpaColors.MutedForeground, PetSpaColors.PetPinkSurface)

        AppModal(title = "Chi tiết yêu cầu", onDismiss = { showDetail = false; rejectMode = false }) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Staff avatar + name + id
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = t.bg
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(t.emoji, fontSize = 24.sp)
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(req.staffName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(req.id, style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
                    }
                }

                // Info rows
                listOf(
                    "Loại yêu cầu" to t.label,
                    "Ngày"         to req.date,
                    "Ngày tạo"     to req.createdAt.ifEmpty { "2025-01-15" },
                ).forEach { (label, value) -> InfoRowSmall(label, value) }

                // Lý do
                Column {
                    Text("Lý do", style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
                    Spacer(Modifier.height(6.dp))
                    Surface(
                        color = PetSpaColors.PetPinkSurface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            req.reason.ifEmpty { "Không có lý do cụ thể" },
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Status badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Trạng thái: ", style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
                    Surface(color = s.bg, shape = RoundedCornerShape(50.dp)) {
                        Text(
                            s.label,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = s.color,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Action buttons (chỉ hiện khi pending)
                if (req.status == "pending" && !rejectMode) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { rejectMode = true },
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFDECEA),
                                contentColor   = Color(0xFFE53E3E)
                            )
                        ) { Text("Từ chối", fontWeight = FontWeight.Bold) }

                        Button(
                            onClick = {
                                vm.approveRequest(req.id)
                                showDetail = false
                            },
                            modifier = Modifier.weight(1.5f).height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ECC71))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(if (loading) "Đang xử lý..." else "✓ Phê duyệt", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Reject reason input (tương đương rejectMode trong StaffRequests.tsx)
                if (rejectMode) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column {
                            Text("Lý do từ chối *", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
                            Spacer(Modifier.height(6.dp))
                            OutlinedTextField(
                                value = rejectReason,
                                onValueChange = { rejectReason = it },
                                placeholder = { Text("Nhập lý do từ chối...", color = PetSpaColors.MutedForeground) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor   = PetSpaColors.PetPink,
                                    unfocusedBorderColor = PetSpaColors.PetPinkBorder
                                )
                            )
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SecondaryButton("Hủy", onClick = { rejectMode = false }, modifier = Modifier.weight(1f))
                            Button(
                                onClick = {
                                    if (rejectReason.isBlank()) return@Button
                                    vm.rejectRequest(req.id)
                                    showDetail = false; rejectMode = false
                                },
                                modifier = Modifier.weight(1f).height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53E3E))
                            ) {
                                Text(if (loading) "Đang gửi..." else "Xác nhận từ chối", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Summary row ───────────────────────────────────────────────────────────────

@Composable
fun RequestSummaryRow(list: List<StaffRequest>, current: String, onFilter: (String) -> Unit) {
    // Tương đương grid 3 ô trong StaffRequests.tsx
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(statusInfoMap.entries.toList()) { (key, info) ->
            val count = list.count { it.status == key }
            val selected = current == key
            Surface(
                onClick = { onFilter(if (selected) "all" else key) },
                modifier = Modifier.width(108.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (selected) info.bg else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    if (selected) info.color.copy(alpha = 0.5f) else Color(0xFFF5F5F5)
                )
            ) {
                Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(count.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = info.color)
                    Text(info.label, fontSize = 10.sp, color = PetSpaColors.MutedForeground)
                }
            }
        }
    }
}

// ── Request card ──────────────────────────────────────────────────────────────

@Composable
fun StaffRequestCard(r: StaffRequest, onView: () -> Unit) {
    val t = typeMap[r.type] ?: TypeInfo(r.type, PetSpaColors.MutedForeground, PetSpaColors.PetPinkSurface, "❓")
    val s = statusInfoMap[r.status] ?: StatusInfo(r.status, PetSpaColors.MutedForeground, PetSpaColors.PetPinkSurface)

    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Emoji icon (tương đương div emoji trong StaffRequests.tsx)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(t.bg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(t.emoji, fontSize = 24.sp)
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(r.staffName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(6.dp))
                    Surface(color = t.bg, shape = RoundedCornerShape(50.dp)) {
                        Text(t.label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), color = t.color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(6.dp))
                    Surface(color = s.bg, shape = RoundedCornerShape(50.dp)) {
                        Text(s.label, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), color = s.color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text("Ngày: ${r.date} · Tạo: ${r.createdAt}", fontSize = 10.sp, color = PetSpaColors.MutedForeground)
                Text(
                    "Lý do: ${r.reason}",
                    fontSize = 11.sp,
                    color = PetSpaColors.MutedForeground,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = onView) {
                Icon(Icons.Default.Visibility, null, tint = PetSpaColors.MutedForeground, modifier = Modifier.size(18.dp))
            }
        }
    }
}