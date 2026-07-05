package com.petspa.app.ui.customer

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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petspa.app.model.NotifSettings
import com.petspa.app.model.NotificationItem
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.CustomerViewModel

@Composable
fun NotificationsScreen(vm: CustomerViewModel, onApptDetail: (String) -> Unit) {
    LaunchedEffect(Unit) { vm.loadNotifications() }
    val state by vm.notifications.collectAsState()

    val unreadCount = (state as? UiState.Success)?.data?.count { !it.read } ?: 0

    Column(Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {
        BackHeader(
            title = "Thông Báo",
            onBack = null,
            rightAction = {
                if (unreadCount > 0) {
                    TextButton(onClick = { vm.markAllNotificationsRead() }) {
                        Text("Đọc hết", color = PetSpaColors.PetPinkDeep, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        )

        if (unreadCount > 0) {
            Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                Surface(
                    color = PetSpaColors.PetPinkSurface,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        "$unreadCount thông báo chưa đọc",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = PetSpaColors.PetPinkDeep,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        UiStateContent(state, { vm.loadNotifications() }) { list ->
            if (list.isEmpty()) {
                EmptyView(emoji = "🔔", title = "Chưa có thông báo nào")
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(list) { n ->
                        NotifItem(n, onClick = {
                            if (n.relatedId.isNotEmpty()) onApptDetail(n.relatedId)
                        })
                        HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
fun NotifItem(n: NotificationItem, onClick: () -> Unit) {
    val unread = !n.read
    val bg = if (unread) PetSpaColors.PetPinkSurface.copy(0.5f) else Color.White
    val borderColor = if (unread) PetSpaColors.PetPink else Color(0xFFF1F5F9)
    
    val typeIcon = when(n.type) {
        "booking" -> Icons.Default.CalendarMonth
        "payment" -> Icons.Default.CreditCard
        "service" -> Icons.Default.ContentCut
        else -> Icons.Default.Notifications
    }
    
    val iconBg = when(n.type) {
        "booking" -> Color(0xFFE0F2FE)
        "payment" -> Color(0xFFDCFCE7)
        "service" -> Color(0xFFF3E8FF)
        else -> Color(0xFFF1F5F9)
    }
    
    val iconTint = when(n.type) {
        "booking" -> Color(0xFF0284C7)
        "payment" -> Color(0xFF16A34A)
        "service" -> Color(0xFF9333EA)
        else -> Color(0xFF64748B)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(typeIcon, null, modifier = Modifier.size(20.dp), tint = iconTint)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        n.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.weight(1f)
                    )
                    if (unread) {
                        Box(Modifier.padding(top = 6.dp).size(8.dp).background(PetSpaColors.PetPinkDeep, CircleShape))
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    n.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    n.time,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun NotifSettingsScreen(vm: CustomerViewModel, onBack: () -> Unit) {
    LaunchedEffect(Unit) { vm.loadNotifSettings() }
    val state by vm.notifSettings.collectAsState()

    Column(Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {
        BackHeader("Cài Đặt Thông Báo", onBack)
        UiStateContent(state, { vm.loadNotifSettings() }) { settings ->
            var s by remember(settings) { mutableStateOf(settings) }
            Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {

                SettingsSection("Đặt lịch") {
                    SettingToggle("Booking mới", s.bookingNew) { s = s.copy(bookingNew = it) }
                    SettingToggle("Booking xác nhận", s.bookingConfirmed) { s = s.copy(bookingConfirmed = it) }
                    SettingToggle("Booking đổi lịch", s.bookingRescheduled) { s = s.copy(bookingRescheduled = it) }
                    SettingToggle("Booking hủy", s.bookingCancelled) { s = s.copy(bookingCancelled = it) }
                }

                SettingsSection("Thanh Toán") {
                    SettingToggle("Thanh toán thành công", s.paymentSuccess) { s = s.copy(paymentSuccess = it) }
                    SettingToggle("Thanh toán thất bại", s.paymentFailed) { s = s.copy(paymentFailed = it) }
                }

                SettingsSection("Dịch Vụ") {
                    SettingToggle("Đang thực hiện", s.serviceInProgress) { s = s.copy(serviceInProgress = it) }
                    SettingToggle("Hoàn thành", s.serviceCompleted) { s = s.copy(serviceCompleted = it) }
                }

                SettingsSection("Kênh Nhận") {
                    SettingToggle("Push Notification", s.channelPush) { s = s.copy(channelPush = it) }
                    SettingToggle("Email", s.channelEmail) { s = s.copy(channelEmail = it) }
                }

                Spacer(Modifier.height(32.dp))
                PrimaryButton("✅ Lưu Cài Đặt", onClick = { vm.updateNotifSettings(s); onBack() })
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(
        title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = PetSpaColors.MutedForeground.copy(0.6f),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, top = 16.dp)
    )
    AppCard {
        content()
    }
}

@Composable
fun SettingToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = PetSpaColors.PetPink,
                checkedThumbColor = Color.White
            )
        )
    }
}
