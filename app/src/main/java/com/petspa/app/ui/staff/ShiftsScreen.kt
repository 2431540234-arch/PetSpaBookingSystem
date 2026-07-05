package com.petspa.app.ui.staff

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.StaffViewModel

@Composable
fun StaffShiftsScreen(vm: StaffViewModel) {
    LaunchedEffect(Unit) { vm.loadShifts() }
    val state by vm.shifts.collectAsState()

    var showRegisterDialog by remember { mutableStateOf(false) }
    var showLeaveDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        PinkGradientHeader("Lịch làm việc", "Xem ca làm của bạn")

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { showRegisterDialog = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = PetSpaColors.PetPinkDeep),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Đăng ký ca làm", fontSize = 13.sp)
            }
            Button(
                onClick = { showLeaveDialog = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8FA3).copy(alpha = 0.8f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Xin nghỉ", fontSize = 13.sp)
            }
        }

        UiStateContent(state, { vm.loadShifts() }) { shifts ->
            Column(Modifier.padding(horizontal = 16.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Danh sách ca làm", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(shifts) { shift ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(shift.date, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        Text("${shift.startTime} - ${shift.endTime}", style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground)
                                        Text("Loại: ${shift.type}", style = MaterialTheme.typography.labelSmall, color = PetSpaColors.MutedForeground.copy(0.7f))
                                    }
                                    StatusBadge(shift.status)
                                }
                                if (shifts.indexOf(shift) < shifts.size - 1) {
                                    HorizontalDivider(color = Color(0xFFF5F5F5))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRegisterDialog) {
        RegisterShiftDialog(onDismiss = { showRegisterDialog = false })
    }

    if (showLeaveDialog) {
        LeaveRequestDialog(onDismiss = { showLeaveDialog = false })
    }
}

@Composable
fun RegisterShiftDialog(onDismiss: () -> Unit) {
    var date by remember { mutableStateOf("") }
    var selectedShift by remember { mutableStateOf("morning") }

    AppModal(title = "Đăng ký ca làm", onDismiss = onDismiss) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            InputField(
                value = date,
                onValueChange = { date = it },
                label = "Ngày",
                placeholder = "mm/dd/yyyy",
                leadingIcon = Icons.Default.CalendarMonth
            )

            Text("Loại ca", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)

            val shiftTypes = listOf(
                Triple("morning", "Ca sáng", "08:00 - 12:00"),
                Triple("afternoon", "Ca chiều", "13:00 - 17:00"),
                Triple("evening", "Ca tối", "18:00 - 22:00")
            )

            shiftTypes.forEach { (id, label, time) ->
                val isSelected = selectedShift == id
                Surface(
                    onClick = { selectedShift = id },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) PetSpaColors.PetPinkSurface else Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PetSpaColors.PetPink else Color(0xFFEEEEEE))
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(label, fontWeight = FontWeight.Bold, color = if (isSelected) PetSpaColors.PetPinkDeep else Color.Unspecified)
                        Text(time, style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground)
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SecondaryButton("Hủy", onClick = onDismiss, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(12.dp))
                PrimaryButton("Đăng ký", onClick = onDismiss, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun LeaveRequestDialog(onDismiss: () -> Unit) {
    var date by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }

    AppModal(title = "Xin nghỉ", onDismiss = onDismiss) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            InputField(
                value = date,
                onValueChange = { date = it },
                label = "Ngày nghỉ",
                placeholder = "mm/dd/yyyy",
                leadingIcon = Icons.Default.DateRange
            )

            Column {
                Text("Lý do", style = MaterialTheme.typography.labelMedium, color = PetSpaColors.MutedForeground)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    placeholder = { Text("Nhập lý do xin nghỉ...", color = PetSpaColors.MutedForeground.copy(0.5f)) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PetSpaColors.PetPink,
                        unfocusedBorderColor = PetSpaColors.PetPinkBorder
                    )
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SecondaryButton("Hủy", onClick = onDismiss, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(12.dp))
                PrimaryButton("Gửi yêu cầu", onClick = onDismiss, modifier = Modifier.weight(1f), containerColor = Color(0xFFFF8FA3))
            }
        }
    }
}
