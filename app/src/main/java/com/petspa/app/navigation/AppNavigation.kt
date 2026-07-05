package com.petspa.app.navigation

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.petspa.app.model.UserRole
import com.petspa.app.ui.auth.LoginScreen
import com.petspa.app.ui.shared.PetSpaColors
import com.petspa.app.viewmodel.AppViewModelFactory
import com.petspa.app.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

sealed class NavScreen(val route: String) {
    data object Splash : NavScreen("splash")
    data object Login : NavScreen("login")
    data object CustomerRoot : NavScreen("customer_root")
    data object StaffRoot : NavScreen("staff_root")
    data object OwnerRoot : NavScreen("owner_root")
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel(
        factory = AppViewModelFactory(LocalContext.current.applicationContext as Application)
    ),
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = NavScreen.Splash.route
    ) {
        composable(route = NavScreen.Splash.route) {
            SplashScreen(
                authViewModel = authViewModel,
                navController = navController,
                onNavigate = { dest: String ->
                    navController.navigate(dest) {
                        popUpTo(NavScreen.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = NavScreen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    val session = authViewModel.session.value
                    val dest = when (session?.role) {
                        UserRole.CUSTOMER -> NavScreen.CustomerRoot.route
                        UserRole.STAFF -> NavScreen.StaffRoot.route
                        UserRole.OWNER -> NavScreen.OwnerRoot.route
                        else -> NavScreen.CustomerRoot.route
                    }
                    navController.navigate(dest) {
                        popUpTo(NavScreen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = NavScreen.CustomerRoot.route) {
            CustomerNavGraph(
                authViewModel = authViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(NavScreen.Login.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = NavScreen.StaffRoot.route) {
            StaffNavGraph(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(NavScreen.Login.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = NavScreen.OwnerRoot.route) {
            OwnerNavGraph(
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(NavScreen.Login.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SplashScreen(authViewModel: AuthViewModel, navController: NavHostController, onNavigate: (String) -> Unit) {
    val checking by authViewModel.checking.collectAsState()
    val session by authViewModel.session.collectAsState()

    LaunchedEffect(checking, session) {
        if (!checking) {
            // Cho hiển thị màn hình Splash 1.5 giây
            delay(1500)
            
            // KIỂM TRA: Chỉ điều hướng nếu hiện tại vẫn đang ở màn hình Splash.
            // Nếu NavController đã khôi phục về một màn hình khác (như trang đặt lịch), 
            // chúng ta không gọi onNavigate để tránh đè màn hình Home lên.
            if (navController.currentBackStackEntry?.destination?.route == NavScreen.Splash.route) {
                val dest = when (session?.role) {
                    UserRole.CUSTOMER -> NavScreen.CustomerRoot.route
                    UserRole.STAFF -> NavScreen.StaffRoot.route
                    UserRole.OWNER -> NavScreen.OwnerRoot.route
                    null -> NavScreen.Login.route
                }
                onNavigate(dest)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(PetSpaColors.PetPink, PetSpaColors.PetPinkDeep)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🐾 Pet Spa",
            style = MaterialTheme.typography.displayMedium,
            color = Color.White
        )
    }
}
