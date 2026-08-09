package com.fynix.android

import android.app.Application
import com.fynix.android.ui.input.InputManager

class FynixApp : Application() {
    val inputManager = InputManager()
}
