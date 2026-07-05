package com.petspa.app.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShadcnCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun ShadcnBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = PetSpaColors.PetPinkSurface,
    contentColor: Color = PetSpaColors.PetPinkDeep
) {
    Surface(
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ShadcnButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    variant: ButtonVariant = ButtonVariant.Default
) {
    val containerColor = when (variant) {
        ButtonVariant.Default -> PetSpaColors.PetPinkDeep
        ButtonVariant.Secondary -> Color(0xFFF1F5F9)
        ButtonVariant.Outline -> Color.Transparent
        ButtonVariant.Ghost -> Color.Transparent
        ButtonVariant.Destructive -> PetSpaColors.Destructive
    }
    
    val contentColor = when (variant) {
        ButtonVariant.Default -> Color.White
        ButtonVariant.Secondary -> Color(0xFF0F172A)
        ButtonVariant.Outline -> Color(0xFF0F172A)
        ButtonVariant.Ghost -> Color(0xFF0F172A)
        ButtonVariant.Destructive -> Color.White
    }

    val borderModifier = if (variant == ButtonVariant.Outline) {
        Modifier.border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
    } else Modifier

    Button(
        onClick = onClick,
        modifier = modifier.then(borderModifier),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = contentColor,
                strokeWidth = 2.dp
            )
        } else {
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        }
    }
}

enum class ButtonVariant {
    Default, Secondary, Outline, Ghost, Destructive
}

@Composable
fun ShadcnInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    leadingIcon: ImageVector? = null
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (label != null) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0F172A)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color(0xFF64748B)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PetSpaColors.PetPinkDeep,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color(0xFFF8FAFC)
            ),
            leadingIcon = if (leadingIcon != null) {
                { Icon(leadingIcon, null, modifier = Modifier.size(18.dp)) }
            } else null,
            singleLine = true
        )
    }
}
