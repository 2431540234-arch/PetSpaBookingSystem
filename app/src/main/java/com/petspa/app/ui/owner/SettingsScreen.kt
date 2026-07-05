package com.petspa.app.ui.owner

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.petspa.app.ui.shared.*

@Composable
fun OwnerSettingsScreen() {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Cài đặt hệ thống", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        
        AppCard {
            Column {
                Text("Thông tin Spa", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField("Pet Spa Center", {}, label = { Text("Tên Spa") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField("123 Pet Street, Dog City", {}, label = { Text("Địa chỉ") }, modifier = Modifier.fillMaxWidth())
            }
        }
        
        Spacer(Modifier.height(16.dp))
        AppCard {
            Column {
                Text("Cấu hình lịch hẹn", style = MaterialTheme.typography.titleMedium)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tự động xác nhận")
                    Switch(true, {})
                }
            }
        }
        
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Lưu cài đặt", onClick = { /* Save */ })
    }
}
