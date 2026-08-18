package com.fynix.android.ui.input

import android.content.res.Configuration
import android.view.InputDevice
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext

enum class InputMode {
    TOUCH,    // Phone / tablet touch
    DPAD      // TV remote D-pad
}

/**
 * Detects whether the current device is primarily D-pad (TV / Android TV box)
 * driven or touch driven.
 *
 * [context] is optional: a no-arg constructor falls back to TOUCH and lets
 * the activity re-detect later via [detectMode] with a Context.
 */
class InputManager(private val context: android.content.Context? = null) {
    val mode = mutableStateOf(context?.let { detectMode(it) } ?: InputMode.TOUCH)

    fun setMode(mode: InputMode) {
        this.mode.value = mode
    }

    companion object {
        fun detectMode(context: android.content.Context): InputMode {
            // Android TV / set-top box → D-pad driven by default
            if ((context.resources.configuration.uiMode and Configuration.UI_MODE_TYPE_MASK)
                == Configuration.UI_MODE_TYPE_TELEVISION
            ) {
                return InputMode.DPAD
            }

            // If a hardware D-pad is plugged in, use D-pad mode
            for (id in InputDevice.getDeviceIds()) {
                val dev = InputDevice.getDevice(id)
                if (dev != null && (dev.sources and InputDevice.SOURCE_DPAD) == InputDevice.SOURCE_DPAD) {
                    return InputMode.DPAD
                }
            }

            return InputMode.TOUCH
        }
    }
}

val LocalInputManager = compositionLocalOf { InputManager() }
