package com.allthingsandroid.kmp.plugin

import org.gradle.api.DefaultTask

internal abstract class BuildConfigTaskBase : DefaultTask() {
    var isTaskInputValid = true
}