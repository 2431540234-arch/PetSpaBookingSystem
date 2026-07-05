package com.petspa.app.ui.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun BottomNavBar(items: List<BottomNavItem>, currentRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar(containerColor = PetSpaColors.Background) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PetSpaColors.PetPinkDeep,
                    selectedTextColor = PetSpaColors.PetPinkDeep,
                    indicatorColor = PetSpaColors.PetPinkSurface,
                )
            )
        }
    }
}

val customerBottomNav = listOf(
    BottomNavItem("customer/home", "Trang chủ", Icons.Default.Home),
    BottomNavItem("customer/appointments", "Lịch hẹn", Icons.Default.CalendarMonth),
    BottomNavItem("customer/pets", "Thú cưng", Icons.Default.Pets),
    BottomNavItem("customer/notifications", "Thông báo", Icons.Default.Notifications),
    BottomNavItem("customer/profile", "Cá nhân", Icons.Default.Person),
)

val staffBottomNav = listOf(
    BottomNavItem("staff/dashboard", "Dashboard", Icons.Default.Dashboard),
    BottomNavItem("staff/bookings", "Bookings", Icons.AutoMirrored.Filled.EventNote),
    BottomNavItem("staff/shifts", "Ca làm", Icons.Default.Schedule),
    BottomNavItem("staff/notifications", "Thông báo", Icons.Default.Notifications),
    BottomNavItem("staff/profile", "Cá nhân", Icons.Default.Person),
)
