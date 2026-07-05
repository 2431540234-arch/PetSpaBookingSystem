package com.petspa.app.navigation

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.petspa.app.ui.owner.*
import com.petspa.app.viewmodel.AppViewModelFactory
import com.petspa.app.viewmodel.OwnerViewModel

@Composable
fun OwnerNavGraph(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext as Application
    val vm: OwnerViewModel = viewModel(factory = AppViewModelFactory(context))
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "owner_dashboard"

    OwnerScaffold(
        currentRoute = currentRoute,
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo("owner_dashboard") { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
        onLogout = onLogout
    ) { padding ->
        NavHost(navController, "owner_dashboard", Modifier.padding(padding)) {
            composable("owner_dashboard") { OwnerDashboardScreen(vm) }
            composable("owner_customers") { OwnerCustomersScreen(vm) }
            composable("owner_staff") { OwnerStaffScreen(vm) }
            composable("owner_services") { OwnerServicesScreen(vm) }
            composable("owner_bookings") { OwnerBookingsScreen(vm) }
            composable("owner_requests") { OwnerRequestsScreen(vm) }
            composable("owner_reports") { OwnerReportsScreen(vm) }
            composable("owner_settings") { OwnerSettingsScreen() }
        }
    }
}
