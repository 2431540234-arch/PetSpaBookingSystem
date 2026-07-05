package com.petspa.app.ui.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = PetSpaColors.PetPinkDeep)
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = PetSpaColors.Destructive)
            if (onRetry != null) {
                Spacer(Modifier.height(16.dp))
                PrimaryButton("Thử lại", onClick = onRetry, modifier = Modifier.widthIn(max = 200.dp))
            }
        }
    }
}

@Composable
fun <T> UiStateContent(state: com.petspa.app.model.UiState<T>, onRetry: () -> Unit, content: @Composable (T) -> Unit) {
    when (state) {
        is com.petspa.app.model.UiState.Loading -> LoadingIndicator()
        is com.petspa.app.model.UiState.Error -> ErrorScreen(state.message, onRetry)
        is com.petspa.app.model.UiState.Success -> content(state.data)
    }
}
