package com.petspa.app.navigation

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.petspa.app.model.UiState
import com.petspa.app.ui.customer.*
import com.petspa.app.ui.shared.BottomNavBar
import com.petspa.app.ui.shared.BottomNavItem
import com.petspa.app.viewmodel.AppViewModelFactory
import com.petspa.app.viewmodel.AuthViewModel
import com.petspa.app.viewmodel.CustomerViewModel

@Composable
fun CustomerNavGraph(authViewModel: AuthViewModel, onLogout: () -> Unit) {
    val navController = rememberNavController()
    
    val context = LocalContext.current.applicationContext as Application
    val customerViewModel: CustomerViewModel = viewModel(
        factory = AppViewModelFactory(context)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "customer_home"

    // Lưu lại route cuối cùng nếu đang ở luồng đặt lịch
    LaunchedEffect(currentRoute) {
        if (currentRoute.startsWith("customer_booking_step")) {
            customerViewModel.saveLastRoute(currentRoute)
        } else if (currentRoute == "customer_home" || currentRoute == "booking_success") {
            customerViewModel.saveLastRoute(null)
        }
    }

    // Khôi phục route khi khởi tạo
    val savedRoute by customerViewModel.lastRoute.collectAsState()
    LaunchedEffect(savedRoute) {
        if (savedRoute != null && currentRoute == "customer_home") {
            navController.navigate(savedRoute!!) {
                popUpTo("customer_home") { inclusive = false }
            }
        }
    }

    // Scaffold bọc ngoài NavHost
    Scaffold(
        bottomBar = {
            // Chỉ hiện thanh Bottom Nav trên các màn hình chính để tối ưu diện tích chi tiết/flow
            val showBottomBar = currentRoute in listOf(
                "customer_home",
                "customer_appointments",
                "customer_pets",
                "customer_notifications",
                "customer_profile"
            )
            if (showBottomBar) {
                BottomNavBar(
                    items = listOf(
                        BottomNavItem("customer_home", "Trang chủ", Icons.Default.Home),
                        BottomNavItem("customer_appointments", "Lịch hẹn", Icons.Default.CalendarMonth),
                        BottomNavItem("customer_pets", "Thú cưng", Icons.Default.Pets),
                        BottomNavItem("customer_notifications", "Thông báo", Icons.Default.Notifications),
                        BottomNavItem("customer_profile", "Cá nhân", Icons.Default.Person)
                    ),
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "customer_home",
            modifier = Modifier.padding(paddingValues)
        ) {
            val navigateTab: (String) -> Unit = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            // Màn hình trang chủ và chặn phím Back của hệ thống
            composable("customer_home") {
                BackHandler(enabled = true) {
                    // Chặn phím back để không vô tình thoát app khỏi màn hình chính
                }
                HomeScreen(
                    vm = customerViewModel,
                    onNavigateServices = { navController.navigate("customer_services") },
                    onNavigateBook = { navController.navigate("customer_booking_step1") },
                    onNavigatePets = { navigateTab("customer_pets") },
                    onNavigateAppts = { navigateTab("customer_appointments") },
                    onNavigateNotifs = { navigateTab("customer_notifications") },
                    onNavigatePetDetail = { petId -> navController.navigate("customer_pet_profile/$petId") },
                    onNavigateApptDetail = { apptId -> navController.navigate("customer_appointment_detail/$apptId") },
                    onNavigateServiceDetail = { serviceId -> navController.navigate("customer_service_detail/$serviceId") }
                )
            }

            // Đăng ký lịch hẹn
            composable("customer_appointments") {
                AppointmentsScreen(
                    vm = customerViewModel,
                    onDetail = { apptId -> navController.navigate("customer_appointment_detail/$apptId") },
                    onBook = { navController.navigate("customer_booking_step1") }
                )
            }

            composable(
                route = "customer_appointment_detail/{appointmentId}",
                arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
                AppointmentDetailScreen(
                    vm = customerViewModel,
                    bookingId = appointmentId,
                    onBack = { navController.popBackStack() },
                    onReschedule = { navController.navigate("customer_reschedule/$appointmentId") },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                route = "customer_reschedule/{appointmentId}",
                arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val appointmentId = backStackEntry.arguments?.getString("appointmentId") ?: ""
                RescheduleScreen(
                    vm = customerViewModel,
                    bookingId = appointmentId,
                    onBack = { navController.popBackStack() },
                    onDone = { navController.popBackStack() }
                )
            }

            // Đăng ký thú cưng
            composable("customer_pets") {
                PetsScreen(
                    vm = customerViewModel,
                    onAdd = { navController.navigate("customer_pet_add") },
                    onEdit = { petId -> navController.navigate("customer_pet_edit/$petId") },
                    onProfile = { petId -> navController.navigate("customer_pet_profile/$petId") }
                )
            }

            composable("customer_pet_add") {
                PetFormScreen(
                    vm = customerViewModel,
                    petId = null,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = "customer_pet_edit/{petId}",
                arguments = listOf(navArgument("petId") { type = NavType.StringType })
            ) { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId") ?: ""
                PetFormScreen(
                    vm = customerViewModel,
                    petId = petId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(
                route = "customer_pet_profile/{petId}",
                arguments = listOf(navArgument("petId") { type = NavType.StringType })
            ) { backStackEntry ->
                val petId = backStackEntry.arguments?.getString("petId") ?: ""
                PetProfileScreen(
                    vm = customerViewModel,
                    petId = petId,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate("customer_pet_edit/$petId") },
                    onBook = { pid ->
                        customerViewModel.updateDraft(customerViewModel.bookingDraft.value.copy(petId = pid))
                        navController.navigate("customer_booking_step1")
                    }
                )
            }

            // Đăng ký dịch vụ
            composable("customer_services") {
                ServicesScreen(
                    vm = customerViewModel,
                    onBack = { navController.popBackStack() },
                    onDetail = { serviceId -> navController.navigate("customer_service_detail/$serviceId") },
                    onBook = { serviceId ->
                        customerViewModel.updateDraft(customerViewModel.bookingDraft.value.copy(serviceId = serviceId))
                        navController.navigate("customer_booking_step1")
                    }
                )
            }

            composable(
                route = "customer_service_detail/{serviceId}",
                arguments = listOf(navArgument("serviceId") { type = NavType.StringType })
            ) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
                ServiceDetailScreen(
                    vm = customerViewModel,
                    serviceId = serviceId,
                    onBack = { navController.popBackStack() },
                    onBook = {
                        customerViewModel.updateDraft(customerViewModel.bookingDraft.value.copy(serviceId = serviceId))
                        navController.navigate("customer_booking_step1")
                    }
                )
            }

            // Luồng đặt lịch 8 bước
            composable("customer_booking_step1") {
                BookStep1Screen(
                    vm = customerViewModel,
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate("customer_booking_step2") }
                )
            }

            composable("customer_booking_step2") {
                BookStep2Screen(
                    vm = customerViewModel,
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate("customer_booking_step3") }
                )
            }

            composable("customer_booking_step3") {
                BookStep3Screen(
                    vm = customerViewModel,
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate("customer_booking_step4") }
                )
            }

            composable("customer_booking_step4") {
                BookStep4Screen(
                    vm = customerViewModel,
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate("customer_booking_step5") }
                )
            }

            composable("customer_booking_step5") {
                BookStep5Screen(
                    vm = customerViewModel,
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate("customer_booking_step6") }
                )
            }

            composable("customer_booking_step6") {
                BookStep6Screen(
                    vm = customerViewModel,
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate("customer_booking_step7") }
                )
            }

            composable("customer_booking_step7") {
                BookStep7Screen(
                    vm = customerViewModel,
                    onBack = { navController.popBackStack() },
                    onNext = { navController.navigate("customer_booking_step8") }
                )
            }

            composable("customer_booking_step8") {
                BookStep8Screen(
                    vm = customerViewModel,
                    onBack = { navController.popBackStack() },
                    onConfirm = {
                        navController.navigate("booking_success") {
                            popUpTo("customer_booking_step1") { inclusive = true }
                        }
                    }
                )
            }

            composable("booking_success") {
                BookSuccessScreen(
                    vm = customerViewModel,
                    onHome = {
                        navController.navigate("customer_home") {
                            popUpTo("customer_home") { inclusive = true }
                        }
                    },
                    onViewAppts = {
                        navController.navigate("customer_appointments") {
                            popUpTo("customer_home")
                        }
                    }
                )
            }

            // Đăng ký thông báo
            composable("customer_notifications") {
                NotificationsScreen(
                    vm = customerViewModel,
                    onApptDetail = { apptId -> navController.navigate("customer_appointment_detail/$apptId") }
                )
            }

            composable("customer_notification_settings") {
                NotifSettingsScreen(
                    vm = customerViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // Đăng ký cá nhân & hồ sơ
            composable("customer_profile") {
                ProfileScreen(
                    vm = customerViewModel,
                    authVm = authViewModel,
                    onEdit = { navController.navigate("customer_profile_edit") },
                    onChangePassword = { navController.navigate("customer_change_password") },
                    onNotifSettings = { navController.navigate("customer_notification_settings") },
                    onLogout = onLogout
                )
            }

            composable("customer_profile_edit") {
                ProfileEditScreen(
                    vm = customerViewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable("customer_change_password") {
                ChangePasswordScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            // Màn hình chào mừng và đăng ký khách hàng mới (nếu cần)
            composable("customer_welcome") {
                WelcomeScreen(
                    onLogin = onLogout,
                    onRegister = { navController.navigate("customer_register") }
                )
            }

            composable("customer_register") {
                RegisterScreen(
                    onBack = { navController.popBackStack() },
                    onLogin = { navController.navigate("customer_welcome") },
                    onSuccess = {
                        navController.navigate("customer_home") {
                            popUpTo("customer_welcome") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
