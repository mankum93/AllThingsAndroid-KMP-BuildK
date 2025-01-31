package com.allthingsandroid.kmp.plugin

import com.allthingsandroid.kmp.plugin.util.composeExtension
import com.allthingsandroid.kmp.plugin.util.desktopExtension
import com.allthingsandroid.kmp.plugin.util.getJVMMainSourceSet
import com.allthingsandroid.kmp.plugin.util.jvmTargetName
import com.allthingsandroid.kmp.plugin.util.jvmTarget
import com.allthingsandroid.kmp.plugin.util.kmpExtension
import com.allthingsandroid.kmp.plugin.util.runTaskNowIfRequired
import com.allthingsandroid.kmp.plugin.util.validateRequiredFields
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal const val DEBUG_TASK = true

const val environmentProduction = "production"
const val environmentDevelopment = "development"
const val environmentUAT = "UAT"
const val environmentStaging = "staging"
const val environmentQA = "QA"
const val environmentIntegration = "integration"
const val environmentSandbox = "sandbox"
const val environmentPreProduction = "pre-production"

val environments = setOf(
    environmentProduction,
    environmentDevelopment,
    environmentUAT,
    environmentStaging,
    environmentQA,
    environmentIntegration,
    environmentSandbox,
    environmentPreProduction
)

const val ENVIRONMENT_SHORT_FORM = "envShortForm"

val environmentsProperties = mapOf(
    environmentProduction to mapOf(ENVIRONMENT_SHORT_FORM to "prod"),
    environmentDevelopment to mapOf(ENVIRONMENT_SHORT_FORM to "dev"),
    environmentUAT to mapOf(ENVIRONMENT_SHORT_FORM to "uat"),
    environmentStaging to mapOf(ENVIRONMENT_SHORT_FORM to "staging"),
    environmentQA to mapOf(ENVIRONMENT_SHORT_FORM to "qa"),
    environmentIntegration to mapOf(ENVIRONMENT_SHORT_FORM to "integration"),
    environmentSandbox to mapOf(ENVIRONMENT_SHORT_FORM to "sandbox"),
    environmentPreProduction to mapOf(ENVIRONMENT_SHORT_FORM to "pre-prod")
)

open class BuildConfigExtension {
    /**
     * Unique identifier for the App
     */
    var applicationId: String? = null

    /**
     * The environment that the App is built built for
     */
    var environment: String? = null

    /**
     * The Application version
     */
    var applicationVersion: String? = null

    internal val customFields = FieldsExtension()

    /**
     * To configure the fields. Configure as,
     * buildKConfig {
     *  fields{
     *      field(name, value)
     *  }
     * }
     *
     * Allowed field values: All Primitives, String
     */
    fun fields(configure: FieldsExtension.() -> Unit) {
        customFields.configure()
    }

    /**
     * A default computation of the 'Comprehensive App version', for convenience
     *
     * Note: User is encouraged to employ their custom implementation as per the requirements.
     */
    fun applicationVersionComprehensive() = applicationId.let {
        val suffixAdditionalForVersion = "_" + when(environment){
            environmentDevelopment, environmentQA, environmentSandbox -> LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:mm:ss"))
            else -> {
                ""  // Default suffix.
            }
        }
        // Formula for 'app version comprehensive' calculation,
        /*
             application-version_environment-short-form_suffix-for-version
        */
        "${applicationVersion}_${environmentsProperties[environment]!![ENVIRONMENT_SHORT_FORM]!!}${suffixAdditionalForVersion}"
    }
}

// FieldsExtension to hold custom field name-value (s)
open class FieldsExtension {

    private val _fields: MutableMap<String, Any?> = mutableMapOf<String, Any?>()
    val fields: Map<String, Any?> = _fields

    // Overloaded method for Number types (Int, Double, etc.)
    fun field(fieldName: String, fieldValue: Any?) {
        _fields[fieldName] = fieldValue
    }
}

class BuildKConfigPlugin : Plugin<Project> {

    lateinit var buildConfigExtension: BuildConfigExtension

    override fun apply(project: Project) {

        // Register the custom extension block
        buildConfigExtension = project.extensions.create("buildKConfig", BuildConfigExtension::class.java)

        project.afterEvaluate {

            val kmpExtension = it.kmpExtension()

            if(kmpExtension == null){
                println("BuildKConfigPlugin: KMP extension not found. Plugin couldn't be applied!")
                return@afterEvaluate
            }

            val jvmTarget = kmpExtension.jvmTarget()
            if(jvmTarget == null){
                println("BuildKConfigPlugin: jvm target not defined. Plugin couldn't be applied!")
            }

            kmpExtension.configureBuildConfigTask(project)
        }
    }

    private fun KotlinMultiplatformExtension.configureBuildConfigTask(
        project: Project,

        ) {
        // The target name could be 'desktop' or something.
        val jvmTargetName = jvmTargetName()

        val jvmTargetNameCapitalized = jvmTargetName?.capitalized()

        val buildConfigDirJVMTarget = project.layout.buildDirectory.dir("generated/source/buildConfig/${jvmTargetName}").get().asFile

        getJVMMainSourceSet()!!.kotlin.srcDir(buildConfigDirJVMTarget)

        val taskNameGenerateBuildConfigFile = "generateBuildConfig${jvmTargetNameCapitalized}"

        val tasks = project.tasks

        val task = tasks.register<BuildConfigTaskBase>(taskNameGenerateBuildConfigFile, BuildConfigTaskBase::class.java) {
            val buildConfigFile = File(buildConfigDirJVMTarget, "BuildConfig.kt")

            // Access the mainClass and packageVersion from the compose.desktop block
            val composeExtension = project.composeExtension()
            val composeDesktopExtension = composeExtension?.desktopExtension()

            if(composeExtension == null){
                println("BuildKConfigPlugin: The plugin cannot be applied. No compose extension - the 'compose{}' block defined for your KMP project!")
                it.isTaskInputValid = false
                return@register
            }

            if(composeDesktopExtension == null){
                println("BuildKConfigPlugin: The plugin cannot be applied. No desktop extension - the 'compose{ desktop{ .. }}' block defined for your KMP project!")
                it.isTaskInputValid = false
                return@register
            }

            val mainClass = composeDesktopExtension.application.mainClass
            val distributablePackageName = composeDesktopExtension.application.nativeDistributions.packageName // Extract package name from mainClass
            val distributablePackageVersion = composeDesktopExtension.application.nativeDistributions.packageVersion

            if(mainClass == null){
                println("BuildKConfigPlugin: The plugin cannot be applied. Have you defined the 'mainClass' for the desktop config?")
                it.isTaskInputValid = false
                return@register
            }
            if(distributablePackageName == null){
                println("BuildKConfigPlugin: The plugin cannot be applied. Have you defined the 'packageName' for the desktop config?")
                it.isTaskInputValid = false
                return@register
            }

            // Define task inputs and outputs

            // Defined by user; get from extension
            val applicationId = buildConfigExtension.applicationId
            val environment = buildConfigExtension.environment
            val applicationVersion = buildConfigExtension.applicationVersion

            it.isTaskInputValid = validateRequiredFields(mapOf(
                "applicationId" to applicationId,
                "environment" to environment,
                "applicationVersion" to applicationVersion,
            ))
            if(!it.isTaskInputValid){
                return@register
            }

            // Task inputs are valid at this point
            applicationId!!
            environment!!
            applicationVersion!!

            val taskOutputs = it.outputs

            it.taskInputProperties(propertiesAtRuntime = mapOf(
                // If the App's main class' package changes..
                "jvmMainClass" to mainClass,
                "applicationId" to applicationId,
                "environment" to environment,
                "applicationVersion" to applicationVersion
            ))

            taskOutputs.dir(buildConfigDirJVMTarget)
            taskOutputs.file(buildConfigFile)

            it.doLast {
                // Create the directory if it doesn't exist
                buildConfigDirJVMTarget.mkdirs()

                buildConfigFile.writeText(
                    generateBuildConfigCode(
                        distributablePackageName = distributablePackageName,
                        distributablePackageVersion = distributablePackageVersion,
                        applicationId = applicationId,
                        environment = environment,
                        applicationVersion = applicationVersion,
                        mainClass = mainClass,
                        customFields = buildConfigExtension.customFields.fields
                    )
                )
            }
        }.get()

        if(task.isTaskInputValid){
            task.runTaskNowIfRequired()
        }
    }

    private fun Task.taskInputProperties(propertiesAtRuntime: Map<String, String>) {
        val properties = mapOf(

            // Variable names(for generated code)
            "varName_Code_applicationId" to varName_Code_applicationId,
            "varName_Code_environment" to varName_Code_environment,
            "varName_Code_applicationVersion" to varName_Code_applicationVersion,

            "varName_Code_DistributablePackageName" to varName_Code_DistributablePackageName,
            "varName_Code_DistributablePackageVersion" to varName_Code_DistributablePackageVersion,

            "methodName_Code_applicationVersionComprehensive" to methodName_Code_applicationVersionComprehensive,
            "varName_Code_suffixAdditionalForVersion" to varName_Code_suffixAdditionalForVersion,

            "varName_environmentProduction" to varName_environmentProduction,
            "varName_environmentDevelopment" to varName_environmentDevelopment,
            "varName_environmentUAT" to varName_environmentUAT,
            "varName_environmentStaging" to varName_environmentStaging,
            "varName_environmentQA" to varName_environmentQA,
            "varName_environmentIntegration" to varName_environmentIntegration,
            "varName_environmentSandbox" to varName_environmentSandbox,
            "varName_environmentPreProduction" to varName_environmentPreProduction,

            "varName_Code_environmentsProperties" to varName_Code_environmentsProperties,
            "varName_ENVIRONMENT_SHORT_FORM" to varName_ENVIRONMENT_SHORT_FORM,

            "generatedClassName" to generatedBuildConfigClassName,

        )

        // Apply properties to the task
        (properties + propertiesAtRuntime).forEach { (name, value) ->
            inputs.property(name, value)
        }
    }

}