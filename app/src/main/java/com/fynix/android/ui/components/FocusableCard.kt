package com.fynix.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.keyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import com.fynix.android.ui.input.InputMode
import com.fynix.android.ui.input.LocalInputManager

@Composable
fun FocusableCard(
    focused: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val inputManager = LocalInputManager.current

    Card(
        modifier = Modifier
            .focusable(enable = inputManager.mode.value == InputMode.DPAD)
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    when (event.key) {
                        Key.Enter, Key.DpadCenter -> {
                            onClick()
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }
            .then(if (focused) Modifier.padding(2.dp) else Modifier)
            .clickable(enabled = inputManager.mode.value == InputMode.TOUCH) {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (focused) {
                androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
            } else {
                androidx.compose.material3.MaterialTheme.colorScheme.surface
            }
        )
    ) {
        content()
    }
}
