package com.petspa.app.ui.staff

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.petspa.app.model.NotificationItem
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.StaffViewModel

@Composable
fun StaffNotificationsScreen(vm: StaffViewModel) {
    LaunchedEffect(Unit) { vm.loadNotifications() }
    val state by vm.notifications.collectAsState()
    
    Column(Modifier.fillMaxSize()) {
        PinkGradientHeader("Thông báo", "Cập nhật mới nhất")
        UiStateContent(state, { vm.loadNotifications() }) { list ->
            LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(list) { item ->
                    NotifItemCard(item)
                }
            }
        }
    }
}

@Composable
private fun NotifItemCard(n: NotificationItem) {
    AppCard {
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(n.title, style = MaterialTheme.typography.titleSmall)
                Text(n.time, style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground)
            }
            Spacer(Modifier.height(4.dp))
            Text(n.message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
