package com.allthingsandroid.kmp.plugin.util
import org.gradle.api.Project
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.desktop.DesktopExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

internal fun KotlinMultiplatformExtension.jvmTargetName(): String? {
    return lazy {
        jvmTarget()?.targetName
    }.value
}

internal fun KotlinMultiplatformExtension.getJVMMainSourceSet(): KotlinSourceSet? {
    val jvmTarget = targets.withType<KotlinJvmTarget>(KotlinJvmTarget::class.java).firstOrNull()
    val sourceSet = jvmTarget?.compilations?.getByName("main")?.allKotlinSourceSets?.firstOrNull()
    return sourceSet
}

/**
 * Extension function to retrieve the KotlinMultiplatformExtension from a Gradle project.
 */
internal fun Project.kmpExtension(): KotlinMultiplatformExtension? =
    extensions.findByType(KotlinMultiplatformExtension::class.java)

/**
 * Extension function to retrieve the ComposeExtension from a Gradle project.
 */
internal fun Project.composeExtension(): ComposeExtension? =
    extensions.findByType(ComposeExtension::class.java)

/**
 * Extension function to retrieve the DesktopExtension from a ComposeExtension.
 */
internal fun ComposeExtension.desktopExtension(): DesktopExtension? =
    extensions.findByType(DesktopExtension::class.java)

/**
 * Extension function to retrieve the Kotlin JVM target from a KotlinMultiplatformExtension.
 */
internal fun KotlinMultiplatformExtension.jvmTarget(): KotlinJvmTarget? {
    return targets.filterIsInstance<KotlinJvmTarget>().firstOrNull()
}