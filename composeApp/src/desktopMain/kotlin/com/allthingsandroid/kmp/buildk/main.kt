package com.allthingsandroid.kmp.buildk

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "AllThingsAndroid-KMP-BuildK",
    ) {
        App()
    }
}