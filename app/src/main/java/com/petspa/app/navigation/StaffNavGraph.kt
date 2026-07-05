package com.petspa.app.navigation

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.petspa.app.ui.shared.BottomNavBar
import com.petspa.app.ui.shared.BottomNavItem
import com.petspa.app.ui.staff.*
import com.petspa.app.viewmodel.AppViewModelFactory
import com.petspa.app.viewmodel.StaffViewModel

@Composable
fun StaffNavGraph(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext as Application
    val vm: StaffViewModel = viewModel(factory = AppViewModelFactory(context))
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "staff_dashboard"

    Scaffold(
        bottomBar = {
            val showBottomBar = currentRoute in listOf("staff_dashboard", "staff_bookings", "staff_shifts", "staff_notifications", "staff_profile")
            if (showBottomBar) {
                BottomNavBar(
                    items = listOf(
                        BottomNavItem("staff_dashboard", "Dashboard", Icons.Default.Dashboard),
                        BottomNavItem("staff_bookings", "Bookings", Icons.AutoMirrored.Filled.EventNote),
                        BottomNavItem("staff_shifts", "Ca làm", Icons.Default.Schedule),
                        BottomNavItem("staff_notifications", "Thông báo", Icons.Default.Notifications),
                        BottomNavItem("staff_profile", "Cá nhân", Icons.Default.Person)
                    ),
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(navController, "staff_dashboard", Modifier.padding(padding)) {
            val navigateTab: (String) -> Unit = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            composable("staff_dashboard") {
                StaffDashboardScreen(
                    vm,
                    onBookingDetail = { id -> navController.navigate("staff_booking_detail/$id") },
                    onNavigateBookings = { navigateTab("staff_bookings") },
                    onNavigateShifts = { navigateTab("staff_shifts") },
                    onNavigateNotifications = { navigateTab("staff_notifications") },
                    onNavigateProfile = { navigateTab("staff_profile") }
                )
            }
            composable("staff_bookings") {
                StaffBookingsScreen(vm, onDetail = { id -> navController.navigate("staff_booking_detail/$id") })
            }
            composable("staff_booking_detail/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) { bse ->
                StaffBookingDetailScreen(vm, bse.arguments?.getString("id") ?: "", onBack = { navController.popBackStack() })
            }
            composable("staff_shifts") {
                StaffShiftsScreen(vm)
            }
            composable("staff_notifications") {
                StaffNotificationsScreen(vm)
            }
            composable("staff_profile") {
                StaffProfileScreen(
                    vm,
                    onPersonalInfo = { navController.navigate("staff_personal_info") },
                    onChangePassword = { navController.navigate("staff_change_password") },
                    onNotifSettings = { navController.navigate("staff_notif_settings") },
                    onLogout = onLogout
                )
            }
            composable("staff_personal_info") {
                StaffPersonalInfoScreen(vm, onBack = { navController.popBackStack() }, onEdit = { navController.navigate("staff_edit_personal_info") })
            }
            composable("staff_edit_personal_info") {
                StaffEditPersonalInfoScreen(vm, onBack = { navController.popBackStack() }, onSaved = { navController.popBackStack() })
            }
            composable("staff_change_password") {
                StaffChangePasswordScreen(onBack = { navController.popBackStack() }, onSaved = { navController.popBackStack() })
            }
            composable("staff_notif_settings") {
                StaffNotifSettingsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
