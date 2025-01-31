package com.allthingsandroid.kmp.plugin.util

import com.allthingsandroid.kmp.plugin.DEBUG_TASK
import org.gradle.api.Task

fun Task.runTaskNowIfRequired() {
    if (!state.upToDate) {
        if (DEBUG_TASK) {
            println("${this.toString()}: Task is out-of-date. Executing the actions...")
        }
        // Manually execute each action of the task
        actions.forEach { action ->
            action.execute(this)
        }
    } else {
        if (DEBUG_TASK) {
            println("${this.toString()}: Task is up-to-date. No need to execute.")
        }
    }
}