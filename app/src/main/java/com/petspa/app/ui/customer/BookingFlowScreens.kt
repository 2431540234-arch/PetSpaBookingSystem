package com.petspa.app.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.petspa.app.model.BookingDraft
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.AppCard
import com.petspa.app.ui.shared.BackHeader
import com.petspa.app.ui.shared.EmptyView
import com.petspa.app.ui.shared.InputField
import com.petspa.app.ui.shared.InfoRow
import com.petspa.app.ui.shared.PetSpaColors
import com.petspa.app.ui.shared.PrimaryButton
import com.petspa.app.ui.shared.SecondaryButton
import com.petspa.app.ui.shared.UiStateContent
import com.petspa.app.ui.shared.formatVnd
import com.petspa.app.viewmodel.CustomerViewModel

@Composable
fun StepIndicator(current: Int, total: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { i ->
            val stepNum = i + 1
            val isCompleted = stepNum < current

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (stepNum <= current) PetSpaColors.PetPink else Color(0xFFEEEEEE)),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                } else {
                    Text(
                        stepNum.toString(),
                        color = if (stepNum <= current) Color.White else PetSpaColors.MutedForeground.copy(0.4f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (i < total - 1) {
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .height(2.dp)
                        .background(if (stepNum < current) PetSpaColors.PetPink else Color(0xFFEEEEEE))
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Text(
            "Bước $current/$total",
            fontSize = 11.sp,
            color = PetSpaColors.MutedForeground,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
    HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)
}

@Composable
private fun BookStepScaffold(
    step: Int,
    total: Int,
    title: String,
    subtitle: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    nextEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {
        BackHeader("Đặt Lịch", onBack)
        StepIndicator(step, total)
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground)
            Spacer(Modifier.height(20.dp))
            content()
            Spacer(Modifier.height(32.dp))
            PrimaryButton(
                text = if (step == 8) "Xác nhận đặt lịch" else "Tiếp Theo",
                onClick = onNext,
                enabled = nextEnabled
            )
            Spacer(Modifier.height(40.dp))
        }
    }
}

// Step 1: Select Pet
@Composable
fun BookStep1Screen(vm: CustomerViewModel, onBack: () -> Unit, onNext: () -> Unit) {
    LaunchedEffect(Unit) { vm.loadPets() }
    val petsState by vm.pets.collectAsState()
    val draft by vm.bookingDraft.collectAsState()

    BookStepScaffold(
        step = 1, total = 8,
        title = "Chọn Thú Cưng",
        subtitle = "Thú cưng nào cần được chăm sóc?",
        onBack = onBack,
        onNext = onNext,
        nextEnabled = draft.petId.isNotEmpty()
    ) {
        UiStateContent(petsState, { vm.loadPets() }) { list ->
            if (list.isEmpty()) {
                EmptyView(emoji = "🐾", title = "Chưa có thú cưng nào", subtitle = "Vui lòng thêm thú cưng trước.")
            } else {
                list.forEach { pet ->
                    val isSelected = draft.petId == pet.id
                    AppCard(
                        modifier = Modifier.padding(bottom = 12.dp),
                        onClick = { vm.updateDraft(draft.copy(petId = pet.id)) }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(pet.emoji, fontSize = 40.sp)
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(pet.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                Text("${pet.species} · ${pet.breed}", style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground)
                            }
                            if (isSelected) {
                                Box(Modifier.size(24.dp).background(PetSpaColors.PetPink, CircleShape), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Step 2: Select Service
@Composable
fun BookStep2Screen(vm: CustomerViewModel, onBack: () -> Unit, onNext: () -> Unit) {
    LaunchedEffect(Unit) { vm.loadServices() }
    val servicesState by vm.services.collectAsState()
    val draft by vm.bookingDraft.collectAsState()
    var selectedCategory by rememberSaveable { mutableStateOf("Tất cả") }
    val categories = listOf("Tất cả", "Tắm", "Cắt tỉa lông", "Chăm sóc da", "Spa cao cấp")

    Column(Modifier.fillMaxSize().background(Color(0xFFFBFBFB))) {
        BackHeader("Đặt Lịch", onBack)
        StepIndicator(2, 8)
        
        // Category Filter
        LazyRow(contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    onClick = { selectedCategory = cat },
                    color = if (isSelected) PetSpaColors.PetPink else PetSpaColors.PetPinkSurface,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(cat, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = if (isSelected) Color.White else PetSpaColors.Foreground, fontSize = 11.sp)
                }
            }
        }

        Column(Modifier.weight(1f).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
            Text("Chọn Dịch Vụ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))

            UiStateContent(servicesState, { vm.loadServices() }) { list ->
                val filtered = list.filter { selectedCategory == "Tất cả" || it.category == selectedCategory }
                filtered.forEach { svc ->
                    val isSelected = draft.serviceId == svc.id
                    AppCard(
                        modifier = Modifier.padding(bottom = 12.dp),
                        onClick = { vm.updateDraft(draft.copy(serviceId = svc.id)) }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(svc.emoji, fontSize = 32.sp)
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(svc.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                Text("${svc.category} · ${svc.duration} phút", style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground)
                                Text(formatVnd(svc.price), color = PetSpaColors.PetPinkDeep, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            if (isSelected) {
                                Box(Modifier.size(24.dp).background(PetSpaColors.PetPink, CircleShape), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            PrimaryButton("Tiếp Theo", onNext, enabled = draft.serviceId.isNotEmpty())
            Spacer(Modifier.height(40.dp))
        }
    }
}

// Step 3: Select Date
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookStep3Screen(vm: CustomerViewModel, onBack: () -> Unit, onNext: () -> Unit) {
    val draft by vm.bookingDraft.collectAsState()
    var date by rememberSaveable { mutableStateOf(draft.date) }

    BookStepScaffold(
        step = 3, total = 8,
        title = "Chọn Ngày",
        subtitle = "Chọn ngày bạn muốn đặt lịch",
        onBack = onBack,
        onNext = {
            vm.updateDraft(draft.copy(date = date))
            onNext()
        },
        nextEnabled = date.isNotEmpty()
    ) {
        InputField(
            value = date,
            onValueChange = { newDate -> date = newDate },
            label = "Ngày hẹn",
            placeholder = "YYYY-MM-DD (Ví dụ: 2027-08-15)",
            required = true
        )
        
        Spacer(Modifier.height(24.dp))
        Text("Chọn nhanh:", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(12.dp))
        
        val quickDates = listOf("2027-08-15", "2027-08-16", "2027-08-17", "2027-08-22")
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickDates.forEach { d ->
                val isSelected = date == d
                Surface(
                    onClick = { date = d },
                    color = if (isSelected) PetSpaColors.PetPink else Color.White,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) PetSpaColors.PetPink else PetSpaColors.PetPinkBorder)
                ) {
                    Text(d, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = if (isSelected) Color.White else PetSpaColors.Foreground, fontSize = 13.sp)
                }
            }
        }
    }
}

// Step 4: Select Time
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookStep4Screen(vm: CustomerViewModel, onBack: () -> Unit, onNext: () -> Unit) {
    val draft by vm.bookingDraft.collectAsState()
    val slots = vm.getTimeSlots()
    val booked = vm.getBookedSlots(draft.date)
    var time by rememberSaveable { mutableStateOf(draft.time) }

    BookStepScaffold(
        step = 4, total = 8,
        title = "Chọn Giờ",
        subtitle = "Ngày ${draft.date}",
        onBack = onBack,
        onNext = {
            vm.updateDraft(draft.copy(time = time))
            onNext()
        },
        nextEnabled = time.isNotEmpty()
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            slots.forEach { slot ->
                val isBooked = booked.contains(slot)
                val isSelected = time == slot
                Surface(
                    onClick = { if (!isBooked) time = slot },
                    color = when {
                        isBooked -> Color(0xFFEEEEEE)
                        isSelected -> PetSpaColors.PetPink
                        else -> Color.White
                    },
                    enabled = !isBooked,
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected || isBooked) null else androidx.compose.foundation.BorderStroke(1.dp, PetSpaColors.PetPinkBorder)
                ) {
                    Box(Modifier.width(100.dp).padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(slot, color = if (isSelected) Color.White else if (isBooked) PetSpaColors.MutedForeground.copy(0.4f) else PetSpaColors.Foreground, fontWeight = FontWeight.Bold)
                            if (isBooked) Text("Đầy", fontSize = 9.sp, color = PetSpaColors.MutedForeground.copy(0.4f))
                        }
                    }
                }
            }
        }
    }
}

// Step 5: Select Staff
@Composable
fun BookStep5Screen(vm: CustomerViewModel, onBack: () -> Unit, onNext: () -> Unit) {
    val draft by vm.bookingDraft.collectAsState()
    val staff = vm.getStaff()
    var selectedStaffId by rememberSaveable { mutableStateOf(draft.staffId) }

    BookStepScaffold(
        step = 5, total = 8,
        title = "Chọn Nhân Viên",
        subtitle = "Ai sẽ chăm sóc thú cưng của bạn?",
        onBack = onBack,
        onNext = {
            vm.updateDraft(draft.copy(staffId = selectedStaffId))
            onNext()
        },
        nextEnabled = selectedStaffId.isNotEmpty()
    ) {
        staff.forEach { st ->
            val isSelected = selectedStaffId == st.id
            AppCard(
                modifier = Modifier.padding(bottom = 12.dp),
                onClick = { selectedStaffId = st.id }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(st.emoji, fontSize = 40.sp)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(st.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text(st.specialty, style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground)
                        Text("● Đang rảnh", color = Color(0xFF22C55E), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    if (isSelected) {
                        Box(Modifier.size(24.dp).background(PetSpaColors.PetPink, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
        
        // Option: Any
        AppCard(onClick = { selectedStaffId = "any" }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎲", fontSize = 40.sp)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text("Bất kỳ nhân viên", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text("Hệ thống tự phân công", style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground)
                }
                if (selectedStaffId == "any") {
                    Box(Modifier.size(24.dp).background(PetSpaColors.PetPink, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// Step 6: Notes
@Composable
fun BookStep6Screen(vm: CustomerViewModel, onBack: () -> Unit, onNext: () -> Unit) {
    val draft by vm.bookingDraft.collectAsState()
    var notes by rememberSaveable { mutableStateOf(draft.notes) }

    BookStepScaffold(
        step = 6, total = 8,
        title = "Ghi Chú",
        subtitle = "Thêm yêu cầu đặc biệt (không bắt buộc)",
        onBack = onBack,
        onNext = {
            vm.updateDraft(draft.copy(notes = notes))
            onNext()
        }
    ) {
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            modifier = Modifier.fillMaxWidth().height(160.dp),
            placeholder = { Text("Ví dụ: Cắt kiểu Poodle truyền thống, thú cưng hay cắn, cần chú ý dị ứng da...", fontSize = 14.sp) },
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PetSpaColors.PetPink,
                unfocusedBorderColor = Color(0xFFEEEEEE)
            )
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PetSpaColors.PetPinkSurface, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text("💡 Ghi chú giúp nhân viên chuẩn bị tốt hơn cho thú cưng của bạn", fontSize = 12.sp, color = PetSpaColors.MutedForeground)
        }
    }
}

// Step 7: Confirm
@Composable
fun BookStep7Screen(vm: CustomerViewModel, onBack: () -> Unit, onNext: () -> Unit) {
    val draft by vm.bookingDraft.collectAsState()
    val pet = vm.localPets.find { it.id == draft.petId }
    val servicesState by vm.services.collectAsState()
    val service = (servicesState as? UiState.Success)?.data?.find { it.id == draft.serviceId }
    val staff = vm.getStaff().find { it.id == draft.staffId }

    BookStepScaffold(
        step = 7, total = 8,
        title = "Xác Nhận",
        subtitle = "Kiểm tra lại thông tin đặt lịch",
        onBack = onBack,
        onNext = onNext
    ) {
        AppCard {
            InfoRow("🐾 Thú cưng", "${pet?.emoji} ${pet?.name}")
            InfoRow("✂️ Dịch vụ", "${service?.emoji} ${service?.name}")
            InfoRow("📅 Ngày", draft.date)
            InfoRow("⏰ Giờ", draft.time)
            InfoRow("👤 Nhân viên", staff?.name ?: "Bất kỳ")
            if (draft.notes.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text("📝 Ghi chú", fontSize = 12.sp, color = PetSpaColors.MutedForeground)
                Text(draft.notes, style = MaterialTheme.typography.bodySmall)
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(PetSpaColors.PetPinkSurface, Color(0xFFFFF5F7))), RoundedCornerShape(16.dp))
                .border(1.dp, PetSpaColors.PetPinkBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tổng thanh toán", fontWeight = FontWeight.Bold)
                Text(formatVnd(service?.price ?: 0), color = PetSpaColors.PetPinkDeep, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Step 8: Payment
@Composable
fun BookStep8Screen(vm: CustomerViewModel, onBack: () -> Unit, onConfirm: () -> Unit) {
    val draft by vm.bookingDraft.collectAsState()
    val servicesState by vm.services.collectAsState()
    val service = (servicesState as? UiState.Success)?.data?.find { it.id == draft.serviceId }
    val confirmState by vm.confirmBookingState.collectAsState()

    var payType by rememberSaveable { mutableStateOf("deposit") } // "deposit" | "full"
    var method by rememberSaveable { mutableStateOf("cash") } // "cash" | "momo" | "vnpay"
    var momoScreen by rememberSaveable { mutableStateOf(false) }
    var vnpayScreen by rememberSaveable { mutableStateOf(false) }

    val total = service?.price ?: 0L
    val payAmount = if (payType == "deposit") total / 2 else total
    val isLoading = confirmState is UiState.Loading

    fun methodLabel() = when (method) {
        "momo" -> "MoMo"
        "vnpay" -> "VNPay"
        else -> "Tiền mặt"
    }

    fun finalize() {
        vm.confirmBooking(payType, methodLabel())
    }

    LaunchedEffect(confirmState) {
        if (confirmState is UiState.Success) {
            momoScreen = false
            vnpayScreen = false
            vm.clearConfirmBookingState()
            onConfirm()
        }
    }

    // Màn hình giả lập thanh toán MoMo
    if (momoScreen) {
        PaymentSimScreen(
            title = "Thanh toán MoMo",
            icon = "💜",
            iconBg = Color(0xFFD82D8B).copy(alpha = 0.13f),
            headline = "Đang mở ứng dụng MoMo...",
            amount = payAmount,
            isLoading = isLoading,
            errorMessage = (confirmState as? UiState.Error)?.message,
            confirmLabel = "✅ Xác Nhận Đã Thanh Toán",
            onConfirm = { finalize() },
            onCancel = { momoScreen = false; vm.clearConfirmBookingState() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFD82D8B).copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFFD82D8B), RoundedCornerShape(16.dp))
                    .padding(16.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("MoMo: 0912 345 678", color = Color(0xFFD82D8B), fontWeight = FontWeight.Bold)
                    Text("Pet Spa Booking System", fontSize = 12.sp, color = PetSpaColors.MutedForeground)
                    Text("Nội dung: ${draft.petId.uppercase()}${draft.serviceId.uppercase()}", fontSize = 12.sp, color = PetSpaColors.MutedForeground)
                }
            }
        }
        return
    }

    // Màn hình giả lập thanh toán VNPay (QR code)
    if (vnpayScreen) {
        PaymentSimScreen(
            title = "Thanh toán VNPay",
            icon = "🏦",
            iconBg = Color(0xFF0066B3).copy(alpha = 0.13f),
            headline = "Quét mã bằng ứng dụng ngân hàng",
            amount = payAmount,
            isLoading = isLoading,
            errorMessage = (confirmState as? UiState.Error)?.message,
            confirmLabel = "✅ Xác Nhận Đã Quét Mã",
            onConfirm = { finalize() },
            onCancel = { vnpayScreen = false; vm.clearConfirmBookingState() }
        ) {
            // Fake QR code
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0066B3))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    repeat(5) { row ->
                        Row {
                            repeat(5) { col ->
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(1.dp)
                                        .background(if ((row + col) % 2 == 0) Color.White else Color(0xFF0066B3))
                                )
                            }
                        }
                    }
                }
            }
        }
        return
    }

    BookStepScaffold(
        step = 8, total = 8,
        title = "Thanh Toán",
        subtitle = "Chọn phương thức thanh toán",
        onBack = onBack,
        onNext = {
            when (method) {
                "momo" -> momoScreen = true
                "vnpay" -> vnpayScreen = true
                else -> finalize()
            }
        },
        nextEnabled = !isLoading
    ) {
        // Tổng kết hóa đơn
        AppCard {
            InfoRow("Tổng hóa đơn", formatVnd(total))
            InfoRow("Đã thanh toán", formatVnd(0))
            InfoRow("Còn lại", formatVnd(total))
        }

        Spacer(Modifier.height(24.dp))
        Text("Lựa chọn thanh toán", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        // Loại thanh toán: Đặt cọc 50% hoặc Thanh toán 100%
        val payOptions = listOf(
            Triple("deposit", "🔖" to "Đặt cọc 50%", "Thanh toán ${formatVnd(total / 2)} ngay"),
            Triple("full", "💯" to "Thanh toán 100%", "Thanh toán đầy đủ ${formatVnd(total)}")
        )
        payOptions.forEach { (value, iconLabel, desc) ->
            val (emoji, label) = iconLabel
            val isSelected = payType == value
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) PetSpaColors.PetPinkSurface else Color.White)
                    .border(2.dp, if (isSelected) PetSpaColors.PetPink else Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                    .clickable { payType = value }
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, fontSize = 24.sp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(label, fontWeight = FontWeight.Bold)
                        Text(desc, fontSize = 12.sp, color = PetSpaColors.MutedForeground)
                    }
                    if (isSelected) {
                        Box(Modifier.size(24.dp).background(PetSpaColors.PetPink, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Text("Phương thức thanh toán", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        val methods = listOf("cash" to ("💵" to "Tiền mặt"), "momo" to ("💜" to "MoMo"), "vnpay" to ("🏦" to "VNPay"))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            methods.forEach { (value, iconLabel) ->
                val (emoji, label) = iconLabel
                val isSelected = method == value
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) PetSpaColors.PetPinkSurface else Color.White)
                        .border(1.dp, if (isSelected) PetSpaColors.PetPink else Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
                        .clickable { method = value }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(emoji, fontSize = 24.sp)
                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (confirmState is UiState.Error) {
            Spacer(Modifier.height(12.dp))
            Text((confirmState as UiState.Error).message, color = PetSpaColors.Destructive, fontSize = 12.sp)
        }

        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(PetSpaColors.PetPink, PetSpaColors.PetPinkDeep)), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Cần thanh toán", color = Color.White)
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(formatVnd(payAmount), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Khung màn hình giả lập thanh toán MoMo/VNPay, tương ứng với 2 màn hình
 * "momoScreen"/"vnpayScreen" trong BookStep8 của file BookingFlow.tsx.
 */
@Composable
private fun PaymentSimScreen(
    title: String,
    icon: String,
    iconBg: Color,
    headline: String,
    amount: Long,
    isLoading: Boolean,
    errorMessage: String?,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(Modifier.fillMaxSize().background(Color.White)) {
        BackHeader(title, onCancel)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 48.sp)
            }
            Spacer(Modifier.height(20.dp))
            Text(headline, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text("Số tiền cần thanh toán", fontSize = 13.sp, color = PetSpaColors.MutedForeground)
            Text(formatVnd(amount), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PetSpaColors.PetPinkDeep)
            Spacer(Modifier.height(20.dp))
            content()
            if (errorMessage != null) {
                Spacer(Modifier.height(12.dp))
                Text(errorMessage, color = PetSpaColors.Destructive, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
            Spacer(Modifier.height(24.dp))
            PrimaryButton(confirmLabel, onClick = onConfirm, enabled = !isLoading)
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onCancel, enabled = !isLoading) {
                Text("Hủy", color = PetSpaColors.MutedForeground)
            }
        }
    }
}

@Composable
fun BookSuccessScreen(vm: CustomerViewModel, onHome: () -> Unit, onViewAppts: () -> Unit) {
    val confirmState by vm.confirmBookingState.collectAsState()
    val booking = (confirmState as? UiState.Success)?.data

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(Brush.linearGradient(listOf(PetSpaColors.PetPink, PetSpaColors.PetPinkDeep)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🎉", fontSize = 48.sp)
        }
        Spacer(Modifier.height(24.dp))
        Text("Đặt Lịch Thành Công!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Chúng tôi sẽ liên hệ xác nhận sớm nhất", color = PetSpaColors.MutedForeground, fontSize = 14.sp)

        if (booking != null) {
            Spacer(Modifier.height(20.dp))
            AppCard(Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Mã đặt lịch", fontSize = 11.sp, color = PetSpaColors.MutedForeground)
                    Text("#${booking.id}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PetSpaColors.PetPinkDeep)
                }
                Spacer(Modifier.height(12.dp))
                InfoRow("📅 Ngày giờ", "${booking.date} · ${booking.time}")
                InfoRow("💳 Trạng thái", if (booking.paymentStatus == "fully-paid") "Đã thanh toán đủ ✅" else "Đặt cọc 50% ✅")
            }
        }

        Spacer(Modifier.height(40.dp))
        PrimaryButton("Trang Chủ 🏠", onClick = onHome)
        Spacer(Modifier.height(12.dp))
        SecondaryButton("Xem Lịch Hẹn", onClick = onViewAppts)
        Spacer(Modifier.height(20.dp))
    }
}
