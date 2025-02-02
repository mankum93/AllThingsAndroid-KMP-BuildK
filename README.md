# KMP BuildConfig  Plugin

This Gradle plugin generates a `BuildConfig` file for your KMP (Kotlin Multiplatform) Desktop (JVM) projects, inspired by the Android Gradle Plugin's (AGP) approach to *BuildConfig generation*.

As a developer with limited time, I was able to create this plugin in **2 days**. Despite the rush, I've made it functional and easy to use for your projects. Feel free to reach out to me for any issues, questions, or feedback.

## Features
- Generates a `BuildConfig` with key properties such as `applicationId`, `applicationVersion`, `environment`, and allows you to define **custom fields**.
- Supports **unicode characters** in both variable names and values.
- Easily configure the `BuildConfig` properties directly in your `build.gradle` using a dedicated extension.

## Limitations
- This plugin is **only compatible with the KMP Desktop (JVM)** environment.
- It **does not support Android, iOS, or any other mobile/native platforms**.

## How To

### Step 1: Apply the Plugin

In your `build.gradle.kts` file, apply the plugin as follows:

```kotlin
plugins {
    id("com.allthingsandroid.buildconfig-kmp-desktop").version("0.1.0-dev")
}
```

### Step 2: Define the 'build config' block

In your `build.gradle.kts` file, define the block:

```kotlin
buildKConfig {
    ...
}
```

Note: Don't forget to add `gradlePluginPortal()` in plugin repositories. Typically, this would be done something like this,

```kotlin
// In you settings.gradle.kts
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
```

## Sample Usage (also mentioned in the composeApp in this repo)

```kotlin
buildKConfig {

    // Built-in fields

    applicationId = "com.allthingsandroid.kmp.buildk.app"
    environment = environmentDevelopment
    applicationVersion = "0.1.0"

    val applicationVersionComprehensive = applicationId.let {
        val suffixAdditionalForVersion = "_" + when(environment) {
            environmentDevelopment, environmentQA, environmentSandbox ->
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:mm:ss"))
            else -> ""
        }
        // Formula for 'app version comprehensive' calculation,
        /*
            application-version_environment-short-form_suffix-for-version
        */
        "${applicationVersion}_${environmentsProperties[environment]!![ENVIRONMENT_SHORT_FORM]!!}${suffixAdditionalForVersion}"
    }

    // Additional fields

    fields {
        field("applicationVersionComprehensive", applicationVersionComprehensive)

        field("toStringExample", Object())

        field("nullExample", null)

        field("numberExample", 123)
        field("longExample", 123L)
        field("doubleExample", 123.45)
        field("floatExample", 123.45f)
        field("booleanExample", true)
        field("charExample", 'A')
        field("charSequenceExample", "SampleCharSequence" as CharSequence)
        field("stringExample", "SampleString")

        field("emptyString", "")

        field("unicodeString", "hello\uD83D\uDE00")

        field("stringWithDoubleQuote", "\"quote\"")

        field("customCharSequence", object : CharSequence {
            override val length: Int = 10
            override fun get(index: Int): Char = if (index == 0) 'W' else 'X'
            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = this
            override fun toString(): String = "CustomCharSequence"
        })

        field("complexNumber", object : Number() {
            override fun toByte() = 1.toByte()
            override fun toShort() = 1.toShort()
            override fun toDouble() = 1.0
            override fun toFloat() = 1.0f
            override fun toInt() = 1
            override fun toLong() = 1L
            override fun toString() = "1 + 2i"
        })


        field("fractionExample", 1.5)

        field("negativeValue", -42)

        field("negativeLongValue", -123L)

        field("booleanTrue", true)

        field("booleanFalse", false)

        field("stringFalse", "false")

        field("varWithUnicodeName变量", 100)

        field("complexString", "Char\uD83D\uDE00Boolean:true Number:123")

    }
}
```

This would generate a BuildConfig as follows:

```kotlin
// Generated BuildConfig file for JVM(Desktop) platform
package com.allthingsandroid.kmp.buildk

const val ENVIRONMENT_SHORT_FORM = "envShortForm"

const val environmentProduction = "production"
const val environmentDevelopment = "development"
const val environmentUAT = "UAT"
const val environmentStaging = "staging"
const val environmentQA = "QA"
const val environmentIntegration = "integration"
const val environmentSandbox = "sandbox"
const val environmentPreProduction = "pre-production"

val environmentsProperties = mapOf(
    environmentProduction to mapOf(
        ENVIRONMENT_SHORT_FORM to "prod"
    ),
    environmentDevelopment to mapOf(
        ENVIRONMENT_SHORT_FORM to "dev"
    ),
    environmentUAT to mapOf(
        ENVIRONMENT_SHORT_FORM to "uat"
    ),
    environmentStaging to mapOf(
        ENVIRONMENT_SHORT_FORM to "staging"
    ),
    environmentQA to mapOf(
        ENVIRONMENT_SHORT_FORM to "qa"
    ),
    environmentIntegration to mapOf(
        ENVIRONMENT_SHORT_FORM to "integration"
    ),
    environmentSandbox to mapOf(
        ENVIRONMENT_SHORT_FORM to "sandbox"
    ),
    environmentPreProduction to mapOf(
        ENVIRONMENT_SHORT_FORM to "pre-prod"
    )
)

object BuildConfig {
    val distributablePackageName = "com.allthingsandroid.kmp.buildk"
    val distributablePackageVersion = "1.0.0"
    val applicationId = "com.allthingsandroid.kmp.buildk.app"
    val environment = environmentDevelopment
    val applicationVersion = "0.1.0"
    
    object Fields {
        val applicationVersionComprehensive = "0.1.0_dev_02-02-2025-20:55:24"
        val toStringExample = "java.lang.Object@6da07253"
        val nullExample = null
        val numberExample = 123
        val longExample = 123L
        val doubleExample = 123.45
        val floatExample = 123.45F
        val booleanExample = true
        val charExample = 'A'
        val charSequenceExample = "SampleCharSequence"
        val stringExample = "SampleString"
        val emptyString = ""
        val unicodeString = "hello😀"
        val stringWithDoubleQuote = "\"quote\""
        val customCharSequence = "CustomCharSequence"
        val complexNumber = "1 + 2i"
        val fractionExample = 1.5
        val negativeValue = -42
        val negativeLongValue = -123L
        val booleanTrue = true
        val booleanFalse = false
        val stringFalse = "false"
        val varWithUnicodeName变量 = 100
        val complexString = "Char😀Boolean:true Number:123"
    }
}
```

That's all.

Now you can reference this BuildConfig object in your KMP desktop(JVM) source set.
