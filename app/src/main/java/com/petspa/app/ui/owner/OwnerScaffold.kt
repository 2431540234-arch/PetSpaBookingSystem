package com.petspa.app.ui.owner

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.petspa.app.ui.shared.PetSpaColors

import kotlinx.coroutines.launch

data class OwnerNavItem(val route: String, val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerScaffold(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val bottomItems = listOf(
        OwnerNavItem("owner_dashboard", "Tổng quan", Icons.Default.Dashboard),
        OwnerNavItem("owner_customers", "Khách hàng", Icons.Default.People),
        OwnerNavItem("owner_staff", "Nhân viên", Icons.Default.Badge),
        OwnerNavItem("owner_services", "Dịch vụ", Icons.Default.ContentCut),
        OwnerNavItem("owner_bookings", "Lịch hẹn", Icons.Default.CalendarMonth)
    )

    val drawerItems = listOf(
        OwnerNavItem("owner_requests", "Yêu cầu NV", Icons.Default.QuestionAnswer),
        OwnerNavItem("owner_reports", "Báo cáo", Icons.Default.BarChart),
        OwnerNavItem("owner_settings", "Cài đặt", Icons.Default.Settings)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(16.dp))
                Text("QUẢN TRỊ PET SPA", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium, color = PetSpaColors.PetPinkDeep)
                HorizontalDivider()
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            onNavigate(item.route)
                            scope.launch { drawerState.close() }
                        },
                        icon = { Icon(item.icon, contentDescription = null) },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                Spacer(Modifier.weight(1f))
                NavigationDrawerItem(
                    label = { Text("Đăng xuất") },
                    selected = false,
                    onClick = onLogout,
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Pet Spa Admin") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PetSpaColors.PetPinkSurface
                    )
                )
            },
            bottomBar = {
                NavigationBar(containerColor = PetSpaColors.Background) {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = { onNavigate(item.route) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PetSpaColors.PetPinkDeep,
                                selectedTextColor = PetSpaColors.PetPinkDeep,
                                indicatorColor = PetSpaColors.PetPinkSurface
                            )
                        )
                    }
                }
            },
            content = content
        )
    }
}
