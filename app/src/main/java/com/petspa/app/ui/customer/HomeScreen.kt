package com.petspa.app.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.petspa.app.model.Booking
import com.petspa.app.model.Pet
import com.petspa.app.model.Service
import com.petspa.app.model.UiState
import com.petspa.app.ui.shared.*
import com.petspa.app.viewmodel.CustomerViewModel

@Composable
fun HomeScreen(
    vm: CustomerViewModel,
    onNavigateServices: () -> Unit,
    onNavigateBook: () -> Unit,
    onNavigatePets: () -> Unit,
    onNavigateAppts: () -> Unit,
    onNavigateApptDetail: (String) -> Unit,
    onNavigateNotifs: () -> Unit,
    onNavigatePetDetail: (String) -> Unit,
    onNavigateServiceDetail: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        vm.loadUser()
        vm.loadPets()
        vm.loadBookings()
        vm.loadNotifications()
        vm.loadServices()
    }

    val userState by vm.user.collectAsState()
    val petsState by vm.pets.collectAsState()
    val bookingsState by vm.bookings.collectAsState()
    val notifsState by vm.notifications.collectAsState()
    val servicesState by vm.services.collectAsState()

    val unreadNotifs = (notifsState as? UiState.Success)?.data?.count { !it.read } ?: 0
    val petsCount = (petsState as? UiState.Success)?.data?.size ?: 0
    val upcomingCount = (bookingsState as? UiState.Success)?.data?.count { it.status in listOf("pending", "confirmed") } ?: 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC)),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Combined Header with Gradient
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(PetSpaColors.PetPink.copy(alpha = 0.8f), Color(0xFFF8FAFC)),
                            startY = 0f,
                            endY = 1200f
                        )
                    )
            ) {
                Column {
                    HomeHeaderNew(
                        userState = userState,
                        onNotifClick = onNavigateNotifs,
                        unreadCount = unreadNotifs
                    )
                    StatsGrid(
                        petsCount = petsCount,
                        upcomingCount = upcomingCount,
                        unreadNotifs = unreadNotifs
                    )
                    QuickActionsSection(
                        onBookClick = onNavigateBook,
                        onPetsClick = onNavigatePets,
                        onServicesClick = onNavigateServices,
                        onApptsClick = onNavigateAppts
                    )
                }
            }
        }

        // My Pets Section
        item {
            SectionHeader("Thú Cưng Của Tôi", onNavigatePets)
            PetsHorizontalList(petsState, onNavigatePetDetail, onNavigatePets)
        }

        // Upcoming Appointments
        item {
            SectionHeader("Lịch Hẹn Sắp Tới", onNavigateAppts)
            UpcomingBookingsList(bookingsState, onNavigateApptDetail, onNavigateBook)
        }

        // Featured Services
        item {
            SectionHeader("Dịch Vụ Nổi Bật", onNavigateServices)
            FeaturedServicesGrid(servicesState, onNavigateServiceDetail)
        }

        item { Spacer(Modifier.height(20.dp)) }
    }
}

@Composable
fun HomeHeaderNew(
    userState: UiState<com.petspa.app.model.User>,
    onNotifClick: () -> Unit,
    unreadCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 48.dp, bottom = 16.dp, start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            val userName = (userState as? UiState.Success)?.data?.name ?: "Khách hàng"
            Text("Chào mừng trở lại,", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
            Text(userName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Box {
            IconButton(
                onClick = onNotifClick,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .size(44.dp)
            ) {
                Icon(Icons.Default.Notifications, null, tint = Color.White)
            }
            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 2.dp, y = (-2).dp)
                        .size(18.dp)
                        .background(Color.White, CircleShape)
                        .border(2.dp, PetSpaColors.PetPink, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(unreadCount.toString(), color = PetSpaColors.PetPinkDeep, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StatsGrid(petsCount: Int, upcomingCount: Int, unreadNotifs: Int) {
    val stats = listOf(
        Triple("Thú cưng", petsCount.toString(), Icons.Default.Pets),
        Triple("Lịch hẹn", upcomingCount.toString(), Icons.Default.CalendarMonth),
        Triple("Thông báo", unreadNotifs.toString(), Icons.Default.Notifications)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        stats.forEach { stat ->
            StatCard(stat, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(stat: Triple<String, String, ImageVector>, modifier: Modifier = Modifier) {
    ShadcnCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(PetSpaColors.PetPinkSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(stat.third, null, tint = PetSpaColors.PetPinkDeep, modifier = Modifier.size(20.dp))
            }
            Column {
                Text(stat.second, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(stat.first, fontSize = 11.sp, color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    onBookClick: () -> Unit,
    onPetsClick: () -> Unit,
    onServicesClick: () -> Unit,
    onApptsClick: () -> Unit
) {
    ShadcnCard(modifier = Modifier.padding(20.dp)) {
        Text("Quick Actions", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            QuickActionBtn(Icons.Default.AddCircle, "Đặt lịch", onBookClick)
            QuickActionBtn(Icons.Default.Pets, "Thú cưng", onPetsClick)
            QuickActionBtn(Icons.Default.ContentCut, "Dịch vụ", onServicesClick)
            QuickActionBtn(Icons.Default.Assignment, "Lịch hẹn", onApptsClick)
        }
    }
}

@Composable
fun QuickActionBtn(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .width(64.dp)
    ) {
        Icon(icon, null, tint = PetSpaColors.PetPinkDeep, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(8.dp))
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFF64748B))
    }
}

@Composable
fun SectionHeader(title: String, onViewAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(Modifier.clickable { onViewAll() }, verticalAlignment = Alignment.CenterVertically) {
            Text("Xem tất cả", color = PetSpaColors.PetPinkDeep, style = MaterialTheme.typography.labelMedium)
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = PetSpaColors.PetPinkDeep, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun PetsHorizontalList(state: UiState<List<Pet>>, onPetClick: (String) -> Unit, onAddClick: () -> Unit) {
    UiStateContent(state, {}) { list ->
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(list) { pet ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(84.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(20.dp))
                        .clickable { onPetClick(pet.id) }
                        .padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(PetSpaColors.PetPinkSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!pet.imageUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = pet.imageUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(pet.emoji, fontSize = 24.sp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(pet.name, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(pet.breed, fontSize = 10.sp, color = PetSpaColors.MutedForeground, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .width(84.dp)
                        .height(100.dp) // Approximate height to match pet cards
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Transparent)
                        .border(2.dp, PetSpaColors.PetPinkBorder, RoundedCornerShape(20.dp))
                        .clickable { onAddClick() }
                        .padding(12.dp)
                ) {
                    Text("+", fontSize = 28.sp, color = PetSpaColors.PetPinkDeep)
                    Text("Thêm", fontSize = 11.sp, color = PetSpaColors.PetPinkDeep)
                }
            }
        }
    }
}

@Composable
fun UpcomingBookingsList(state: UiState<List<Booking>>, onClick: (String) -> Unit, onBookNow: () -> Unit) {
    UiStateContent(state, {}) { list ->
        val upcoming = list.filter { it.status in listOf("pending", "confirmed") }.take(2)
        if (upcoming.isEmpty()) {
            AppCard(Modifier.padding(horizontal = 20.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("📅", fontSize = 32.sp)
                    Text("Chưa có lịch hẹn sắp tới", color = PetSpaColors.MutedForeground, style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = onBookNow) {
                        Text("Đặt lịch ngay →", color = PetSpaColors.PetPinkDeep)
                    }
                }
            }
        } else {
            Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Add Appointment "Card" style - Now at the TOP
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Transparent)
                        .border(
                            width = 2.dp,
                            color = PetSpaColors.PetPinkBorder,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onBookNow() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AddCircle, null, tint = PetSpaColors.PetPinkDeep, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Đặt thêm lịch hẹn mới", color = PetSpaColors.PetPinkDeep, fontWeight = FontWeight.Bold)
                    }
                }

                upcoming.forEach { b ->
                    AppCard(onClick = { onClick(b.id) }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🐾", fontSize = 24.sp)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(b.petName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                Text(b.serviceName, style = MaterialTheme.typography.bodySmall, color = PetSpaColors.MutedForeground)
                            }
                            StatusBadge(b.status)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📅 ${b.date}", fontSize = 11.sp, color = PetSpaColors.MutedForeground)
                            Spacer(Modifier.width(16.dp))
                            Text("⏰ ${b.time}", fontSize = 11.sp, color = PetSpaColors.MutedForeground)
                            Spacer(Modifier.weight(1f))
                            Text(formatVnd(b.totalAmount), color = PetSpaColors.PetPinkDeep, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedServicesGrid(state: UiState<List<Service>>, onServiceClick: (String) -> Unit) {
    UiStateContent(state, {}) { list ->
        val featured = list.take(4)
        Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            featured.chunked(2).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    rowItems.forEach { svc ->
                        AppCard(
                            modifier = Modifier.weight(1f),
                            onClick = { onServiceClick(svc.id) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PetSpaColors.PetPinkSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!svc.imageUrl.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = svc.imageUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(svc.emoji, fontSize = 20.sp)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(svc.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${svc.duration} phút", fontSize = 10.sp, color = PetSpaColors.MutedForeground)
                            Text(formatVnd(svc.price), color = PetSpaColors.PetPinkDeep, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                    if (rowItems.size < 2) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}