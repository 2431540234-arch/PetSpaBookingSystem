package com.petspa.app.ui.staff

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.petspa.app.model.Booking
import com.petspa.app.ui.shared.AppCard
import com.petspa.app.ui.shared.PetSpaColors
import com.petspa.app.ui.shared.StatusBadge

@Composable
fun BookingItemCard(booking: Booking, onClick: () -> Unit) {
    AppCard(onClick = onClick) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("${booking.time} • ${booking.petName}", style = MaterialTheme.typography.titleSmall)
                Text(booking.serviceName, color = PetSpaColors.MutedForeground)
            }
            StatusBadge(booking.status, when(booking.status) {
                "confirmed" -> PetSpaColors.PetPinkDeep
                "completed" -> PetSpaColors.Success
                else -> PetSpaColors.Warning
            })
        }
    }
}
