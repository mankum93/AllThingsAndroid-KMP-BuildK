package com.allthingsandroid.kmp.plugin

import com.allthingsandroid.kmp.plugin.util.INDENT_4_SPACES
import com.allthingsandroid.kmp.plugin.util.INDENT_8_SPACES
import com.allthingsandroid.kmp.plugin.util.StatementSpec
import com.allthingsandroid.kmp.plugin.util.StatementType
import com.allthingsandroid.kmp.plugin.util.ValueQuote
import com.allthingsandroid.kmp.plugin.util.generateMapStatement
import com.allthingsandroid.kmp.plugin.util.generateStatement
import com.allthingsandroid.kmp.plugin.util.generateStatements
import com.allthingsandroid.kmp.plugin.util.toStatementSpecs

internal const val generatedBuildConfigClassName = "BuildConfig"
internal const val varName_Code_DistributablePackageName = "distributablePackageName"
internal const val varName_Code_DistributablePackageVersion = "distributablePackageVersion"
internal const val varName_Code_applicationId = "applicationId"
internal const val varName_Code_environment = "environment"
internal const val varName_Code_applicationVersion = "applicationVersion"

internal const val methodName_Code_applicationVersionComprehensive = "applicationVersionComprehensive"
internal const val varName_Code_suffixAdditionalForVersion = "suffixAdditionalForVersion"

internal const val varName_ENVIRONMENT_SHORT_FORM = "ENVIRONMENT_SHORT_FORM"

internal const val varName_environmentProduction = "environmentProduction"
internal const val varName_environmentDevelopment = "environmentDevelopment"
internal const val varName_environmentUAT = "environmentUAT"
internal const val varName_environmentStaging = "environmentStaging"
internal const val varName_environmentQA = "environmentQA"
internal const val varName_environmentIntegration = "environmentIntegration"
internal const val varName_environmentSandbox = "environmentSandbox"
internal const val varName_environmentPreProduction = "environmentPreProduction"

internal val environmentVarNamesAndValues = mapOf(
    varName_environmentProduction to environmentProduction,
    varName_environmentDevelopment to environmentDevelopment,
    varName_environmentUAT to environmentUAT,
    varName_environmentStaging to environmentStaging,
    varName_environmentQA to environmentQA,
    varName_environmentIntegration to environmentIntegration,
    varName_environmentSandbox to environmentSandbox,
    varName_environmentPreProduction to environmentPreProduction
)

internal val environmentValuesAndVarNames =
    environmentVarNamesAndValues.entries.associate { (key, value) -> value to key }

internal const val varName_Code_environmentsProperties = "environmentsProperties"

// Fields

internal const val generatedFieldsClassName = "Fields"

@Suppress("UNCHECKED_CAST")
internal fun generateBuildConfigCode(
    distributablePackageName: String,
    distributablePackageVersion: String?,
    applicationId: String,
    environment: String,
    applicationVersion: String,
    mainClass: String,
    customFields: Map<String, Any?>
): String{

    val generatedClassFields: List<StatementSpec> = listOf(
        StatementSpec(varName_Code_DistributablePackageName, distributablePackageName, StatementType.STMT_VAL, INDENT_4_SPACES),
        StatementSpec(varName_Code_DistributablePackageVersion, distributablePackageVersion, StatementType.STMT_VAL, INDENT_4_SPACES),
        StatementSpec(varName_Code_applicationId, applicationId, StatementType.STMT_VAL, INDENT_4_SPACES),
        StatementSpec(
            varName_Code_environment,
            if (environments.contains(environment)) environmentValuesAndVarNames[environment]!! else environment,
            StatementType.STMT_VAL,
            INDENT_4_SPACES,
            quoteValue = if (environments.contains(environment)) ValueQuote.NO else ValueQuote.YES
        ),
        StatementSpec(varName_Code_applicationVersion, applicationVersion, StatementType.STMT_VAL, INDENT_4_SPACES)
    ).filter { it.variableValue != null }

    return """
// Generated BuildConfig file for JVM(Desktop) platform
package ${mainClass.substringBeforeLast(".")}

${generateStatement(StatementSpec(
        variableName = varName_ENVIRONMENT_SHORT_FORM,
        variableValue = ENVIRONMENT_SHORT_FORM,
        statementType = StatementType.STMT_CONST_VAL
    ))}

${generateStatements(environmentVarNamesAndValues.toStatementSpecs(statementType = StatementType.STMT_CONST_VAL))}

${
    generateMapStatement(
        varName_Code_environmentsProperties, mapOf(
            varName_environmentProduction to mapOf(varName_ENVIRONMENT_SHORT_FORM to environmentsProperties[environmentProduction]!![ENVIRONMENT_SHORT_FORM]!!),
            varName_environmentDevelopment to mapOf(varName_ENVIRONMENT_SHORT_FORM to environmentsProperties[environmentDevelopment]!![ENVIRONMENT_SHORT_FORM]!!),
            varName_environmentUAT to mapOf(varName_ENVIRONMENT_SHORT_FORM to environmentsProperties[environmentUAT]!![ENVIRONMENT_SHORT_FORM]!!),
            varName_environmentStaging to mapOf(varName_ENVIRONMENT_SHORT_FORM to environmentsProperties[environmentStaging]!![ENVIRONMENT_SHORT_FORM]!!),
            varName_environmentQA to mapOf(varName_ENVIRONMENT_SHORT_FORM to environmentsProperties[environmentQA]!![ENVIRONMENT_SHORT_FORM]!!),
            varName_environmentIntegration to mapOf(varName_ENVIRONMENT_SHORT_FORM to environmentsProperties[environmentIntegration]!![ENVIRONMENT_SHORT_FORM]!!),
            varName_environmentSandbox to mapOf(varName_ENVIRONMENT_SHORT_FORM to environmentsProperties[environmentSandbox]!![ENVIRONMENT_SHORT_FORM]!!),
            varName_environmentPreProduction to mapOf(varName_ENVIRONMENT_SHORT_FORM to environmentsProperties[environmentPreProduction]!![ENVIRONMENT_SHORT_FORM]!!)
        )
    )
}

object $generatedBuildConfigClassName {
    ${generateStatements(generatedClassFields)}
    
    object $generatedFieldsClassName {
        ${
        if (customFields.isNotEmpty()) {
            generateStatements(customFields.toStatementSpecs(StatementType.STMT_VAL, INDENT_8_SPACES))
        } else {
            ""
        }
        }
    }
}

""".trimIndent()

}