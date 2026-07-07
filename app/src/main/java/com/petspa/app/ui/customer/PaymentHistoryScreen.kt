package com.petspa.app.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.CustomerViewModel

@Composable
fun PaymentHistoryScreen(vm: CustomerViewModel, onBack: () -> Unit) {
    LaunchedEffect(Unit) { vm.loadPaymentHistory() }
    val state by vm.paymentHistory.collectAsState()

    Column(Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        AppTopBar("Lịch sử thanh toán", onBack)
        
        UiStateContent(state, { vm.loadPaymentHistory() }) { list ->
            if (list.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có lịch sử giao dịch", color = Color(0xFF64748B))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(list) { tx ->
                        ShadcnCard {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Giao dịch #${tx.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(tx.paymentGateway, color = Color(0xFF64748B), fontSize = 12.sp)
                                    Text(tx.createdAt ?: "", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(formatVnd(tx.amount.toLong()), fontWeight = FontWeight.Bold, color = PetSpaColors.PetPinkDeep)
                                    ShadcnBadge(
                                        text = tx.paymentStatus,
                                        backgroundColor = when(tx.paymentStatus) {
                                            "SUCCESS", "FULLY_PAID" -> Color(0xFFDCFCE7)
                                            "FAILED", "EXPIRED" -> Color(0xFFFEE2E2)
                                            else -> Color(0xFFFEF3C7)
                                        },
                                        contentColor = when(tx.paymentStatus) {
                                            "SUCCESS", "FULLY_PAID" -> Color(0xFF16A34A)
                                            "FAILED", "EXPIRED" -> Color(0xFFDC2626)
                                            else -> Color(0xFFD97706)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
