package com.fynix.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fynix.android.ui.input.InputMode
import com.fynix.android.ui.input.LocalInputManager

@Composable
fun FocusableCard(
    focused: Boolean,
    onClick: () -> Unit,
    logoUrl: String = "",
    name: String = "",
    logoHeight: Int = 120,
    content: (@Composable () -> Unit)? = null
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (content != null) {
                content()
            } else if (logoUrl.isNotEmpty()) {
                // Logo: height-constrained, centered — never stretches the card
                AsyncImage(
                    model = logoUrl,
                    contentDescription = name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(logoHeight.dp)
                        .fillMaxWidth()
                )
            } else {
                Text(
                    text = name,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(vertical = 4.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        if (content == null && logoUrl.isNotEmpty()) {
            Text(
                text = name,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, bottom = 6.dp),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}
