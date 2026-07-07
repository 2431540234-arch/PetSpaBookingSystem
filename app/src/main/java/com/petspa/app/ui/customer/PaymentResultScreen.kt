package com.petspa.app.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.browser.customtabs.CustomTabsIntent
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.CustomerViewModel
import kotlinx.coroutines.delay

@Composable
fun PaymentResultDeepLinkScreen(
    vm: CustomerViewModel,
    bookingId: String,
    onHome: () -> Unit,
    onViewAppts: () -> Unit
) {
    val context = LocalContext.current
    val paymentStatusState by vm.paymentStatus.collectAsState()
    val paymentState by vm.paymentState.collectAsState()
    
    var pollingSeconds by remember { mutableIntStateOf(0) }
    val maxPollingSeconds = 30
    
    // Auto-verify on entry and polling
    LaunchedEffect(bookingId, pollingSeconds) {
        if (bookingId.isNotEmpty()) {
            val status = paymentStatusState
            val shouldPoll = status == null || 
                             status is UiState.Loading || 
                             (status is UiState.Success && status.data.paymentStatus == "PROCESSING") ||
                             (status is UiState.Success && status.data.paymentStatus == "PENDING")

            if (shouldPoll && pollingSeconds < maxPollingSeconds) {
                vm.checkPaymentStatus(bookingId.toLong())
                delay(3000L)
                pollingSeconds += 3
            }
        }
    }

    // Handle Retry Payment Link
    LaunchedEffect(paymentState) {
        val state = paymentState
        if (state is UiState.Success) {
            // Open new payment URL
            val intent = CustomTabsIntent.Builder().build()
            try {
                intent.launchUrl(context, android.net.Uri.parse(state.data.payUrl))
            } catch (e: Exception) {
                val browserIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(state.data.payUrl))
                context.startActivity(browserIntent)
            }
            vm.clearPaymentState() // Reset so it can be clicked again
            pollingSeconds = 0 // Reset polling for new transaction
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val status = paymentStatusState
        
        // Icon based on status
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    brush = when {
                        status is UiState.Success && (status.data.paymentStatus == "SUCCESS" || status.data.paymentStatus == "FULLY_PAID") -> 
                            Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669)))
                        status is UiState.Success && (status.data.paymentStatus == "FAILED" || status.data.paymentStatus == "EXPIRED") -> 
                            Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFFDC2626)))
                        status is UiState.Success && status.data.paymentStatus == "CANCELLED" ->
                            Brush.linearGradient(listOf(Color(0xFF94A3B8), Color(0xFF64748B)))
                        else -> Brush.linearGradient(listOf(PetSpaColors.PetPink, PetSpaColors.PetPinkDeep))
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            val icon = when {
                status is UiState.Success && (status.data.paymentStatus == "SUCCESS" || status.data.paymentStatus == "FULLY_PAID") -> "✅"
                status is UiState.Success && status.data.paymentStatus == "FAILED" -> "❌"
                status is UiState.Success && status.data.paymentStatus == "CANCELLED" -> "🚫"
                status is UiState.Success && status.data.paymentStatus == "EXPIRED" -> "⏰"
                status is UiState.Success && status.data.paymentStatus == "PROCESSING" -> "🔄"
                status is UiState.Loading -> "⏳"
                else -> "✨"
            }
            Text(icon, fontSize = 48.sp)
        }
        
        Spacer(Modifier.height(24.dp))

        val titleText = when {
            status is UiState.Success -> {
                when (status.data.paymentStatus) {
                    "SUCCESS", "FULLY_PAID" -> "Thanh Toán Thành Công!"
                    "FAILED" -> "Thanh Toán Thất Bại"
                    "CANCELLED" -> "Đã Hủy Thanh Toán"
                    "PENDING" -> "Đang Chờ Thanh Toán..."
                    "PROCESSING" -> "Đang Xử Lý..."
                    "EXPIRED" -> "Giao Dịch Hết Hạn"
                    else -> "Trạng Thái: ${status.data.paymentStatus}"
                }
            }
            status is UiState.Error -> "Lỗi Xác Minh"
            else -> "Đang Kiểm Tra Kết Quả..."
        }

        Text(titleText, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        
        val subtitleText = when {
            status is UiState.Success && (status.data.paymentStatus == "SUCCESS" || status.data.paymentStatus == "FULLY_PAID") -> "Đơn hàng của bạn đã được xác nhận."
            status is UiState.Success && status.data.paymentStatus == "FAILED" -> "Có lỗi xảy ra trong quá trình thanh toán."
            status is UiState.Success && status.data.paymentStatus == "CANCELLED" -> "Bạn đã hủy yêu cầu thanh toán."
            status is UiState.Success && status.data.paymentStatus == "EXPIRED" -> "Giao dịch đã quá thời gian 15 phút."
            status is UiState.Success && status.data.paymentStatus == "PROCESSING" -> "Hệ thống đang xác nhận với ngân hàng."
            status is UiState.Error -> status.message
            else -> "Vui lòng chờ trong giây lát"
        }
        Text(subtitleText, color = PetSpaColors.MutedForeground, fontSize = 14.sp, textAlign = TextAlign.Center)

        if (status is UiState.Success && status.data.paymentStatus == "PROCESSING" && pollingSeconds >= maxPollingSeconds) {
            Spacer(Modifier.height(8.dp))
            Text("Thanh toán đang được xác nhận lâu hơn dự kiến. Bạn có thể quay lại sau hoặc thử làm mới.", 
                color = Color(0xFFF59E0B), fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))
        }

        Spacer(Modifier.height(32.dp))

        // Info Card
        if (bookingId.isNotEmpty()) {
            AppCard(Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Mã đặt lịch", fontSize = 11.sp, color = PetSpaColors.MutedForeground)
                    Text("#$bookingId", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PetSpaColors.PetPinkDeep)
                }
                
                if (status is UiState.Success) {
                    val data = status.data
                    Spacer(Modifier.height(12.dp))
                    InfoRow("💰 Số tiền", formatVnd(data.amount.toLong()))
                    InfoRow("💳 Cổng", data.paymentGateway)
                    if (data.transactionId != null) {
                        InfoRow("🆔 Giao dịch", data.transactionId)
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        val currentStatus = (status as? UiState.Success)?.data?.paymentStatus ?: ""
        val canRetry = currentStatus == "FAILED" || currentStatus == "CANCELLED" || currentStatus == "EXPIRED"
        val isProcessing = currentStatus == "PROCESSING" || currentStatus == "PENDING" || status is UiState.Loading

        if (canRetry) {
            PrimaryButton("Thanh Toán Lại 🔄", onClick = {
                vm.createPayment(bookingId.toLong(), (status as UiState.Success).data.paymentGateway)
            })
            Spacer(Modifier.height(12.dp))
        } else if (isProcessing) {
            if (pollingSeconds >= maxPollingSeconds) {
                PrimaryButton("Làm mới trạng thái 🔃", onClick = {
                    pollingSeconds = 0
                    vm.checkPaymentStatus(bookingId.toLong())
                })
                Spacer(Modifier.height(12.dp))
            } else {
                CircularProgressIndicator(color = PetSpaColors.PetPink)
                Spacer(Modifier.height(12.dp))
                Text("Đang tự động kiểm tra... (${maxPollingSeconds - pollingSeconds}s)", fontSize = 12.sp, color = PetSpaColors.MutedForeground)
                Spacer(Modifier.height(12.dp))
            }
        }

        PrimaryButton("Về Trang Chủ 🏠", onClick = onHome)
        Spacer(Modifier.height(12.dp))
        SecondaryButton("Xem Lịch Hẹn", onClick = onViewAppts)
        Spacer(Modifier.height(20.dp))
    }
}
