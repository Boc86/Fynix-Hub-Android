package com.fynix.android.ui.input

import androidx.compose.runtime.compositionLocalOf

enum class InputMode {
    TOUCH,    // Phone/tablet touch
    DPAD      // TV remote D-pad
}

class InputManager {
    private val _mode = androidx.compose.runtime.mutableStateOf(InputMode.TOUCH)
    val mode: androidx.compose.runtime.State<InputMode> = _mode

    fun detectMode(): InputMode {
        // TODO: Implement based on device capabilities
        return InputMode.TOUCH
    }

    fun setMode(mode: InputMode) {
        _mode.value = mode
    }
}

val LocalInputManager = compositionLocalOf { InputManager() }
