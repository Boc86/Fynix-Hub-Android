package com.fynix.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import com.fynix.android.ui.input.InputMode
import com.fynix.android.ui.input.LocalInputManager

@Composable
fun FocusableCard(
    focused: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val inputManager = LocalInputManager.current
    val isDpad = inputManager.mode.value == InputMode.DPAD

    Card(
        modifier = Modifier
            .focusable(enabled = isDpad)
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.Enter, Key.DirectionCenter -> {
                            onClick()
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }
            .clickable(enabled = !isDpad) {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (focused) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        content()
    }
}
