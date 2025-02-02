import com.allthingsandroid.kmp.plugin.ENVIRONMENT_SHORT_FORM
import com.allthingsandroid.kmp.plugin.environmentDevelopment
import com.allthingsandroid.kmp.plugin.environmentQA
import com.allthingsandroid.kmp.plugin.environmentSandbox
import com.allthingsandroid.kmp.plugin.environmentsProperties
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("com.allthingsandroid.buildconfig-kmp-desktop").version("0.1.0-dev")
}

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

kotlin {
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}


compose.desktop {
    application {
        mainClass = "com.allthingsandroid.kmp.buildk.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.allthingsandroid.kmp.buildk"
            packageVersion = "1.0.0"
        }
    }
}
