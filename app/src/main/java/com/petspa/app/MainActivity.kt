package com.petspa.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.petspa.app.navigation.AppNavigation
import com.petspa.app.ui.shared.PetSpaTheme

// MainActivity là single-activity host cho toàn bộ Compose UI
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetSpaTheme {
                AppNavigation()
            }
        }
    }
}
