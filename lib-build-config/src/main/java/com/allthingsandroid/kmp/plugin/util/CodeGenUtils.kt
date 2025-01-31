package com.allthingsandroid.kmp.plugin.util



internal const val INDENT_4_SPACES = "    "
internal const val INDENT_8_SPACES = "        "

internal enum class StatementType(val statementSyntaxPrefix: String){
    STMT_PRIVATE_CONST_VAL("private const val"),
    STMT_CONST_VAL("const val"),
    STMT_VAL("val"),
    STMT_VAR("var"),
    STMT_LATEINIT_VAR("lateinit var")
}

internal enum class ValueQuote{
    YES,
    NO,
    AUTO
}

internal data class StatementSpec(
    val variableName: String,
    val variableValue: Any?,
    val statementType: StatementType,
    val indent: String = "",     // No spaces default
    val quoteValue: ValueQuote = ValueQuote.AUTO
)

internal fun Map<String, Any?>.toStatementSpecs(
    statementType: StatementType,
    indent: String = "",
    quoteValue: ValueQuote = ValueQuote.AUTO
): List<StatementSpec> {
    return buildList<StatementSpec> {
        this@toStatementSpecs.forEach {
            add(
                StatementSpec(
                    variableName = it.key,
                    variableValue = it.value,
                    statementType = statementType,
                    indent = indent,
                    quoteValue = quoteValue
                )
            )
        }
    }
}

/*
Output will be,
fromVarName to toVarName
 */
internal fun generateMappingForAMap(fromVarName: String, toVarName: String) = "$fromVarName to $toVarName"

/*
Output will be something like,

myMap to mapOf(
    key1 to "value1",
    key2 to mapOf(
        subKey1 to "subValue1",
        subKey2 to mapOf(
            deepKey1 to "deepValue1"
        )
    ),
    key3 to "value3"
)

 */
@Suppress("UNCHECKED_CAST")
internal fun generateMapStatement(varNameAssignment: String, map: Map<String, Any>): String {
    fun mapToString(map: Map<String, Any>, indent: String = "    "): String {
        return map.entries.joinToString(",\n$indent") { (key, value) ->
            val valueString = when (value) {
                is Map<*, *> -> "mapOf(\n$indent    " + mapToString(value as Map<String, Any>, "$indent    ") + "\n$indent)"
                is String -> "\"$value\""
                else -> value.toString()
            }
            generateMappingForAMap(key, valueString)
        }
    }

    return "val $varNameAssignment = mapOf(\n    ${mapToString(map, "    ")}\n)".trimMargin()
}

internal fun generateStatement(spec: StatementSpec): String {
    val value = when(spec.quoteValue){
        ValueQuote.YES -> {
            "\"${spec.variableValue}\""
        }
        ValueQuote.NO -> {
            spec.variableValue.toString()
        }
        ValueQuote.AUTO -> {
            if(spec.variableValue == null){
                "null"
            }
            else{
                when (spec.variableValue) {
                    is Number -> when (spec.variableValue) {
                        is Byte -> "${(spec.variableValue)}"
                        is Short -> "${(spec.variableValue)}"
                        is Int -> "${(spec.variableValue)}"
                        is Long -> "${(spec.variableValue)}L"
                        is Float -> "${spec.variableValue}F"
                        is Double -> "${spec.variableValue}"
                        else -> "\"${spec.variableValue}\""
                    }
                    is Boolean -> "${spec.variableValue}"
                    is Char -> "'${spec.variableValue}'"
                    // The reason behind replace:
                    // Since the variable value will be used a programming language statement construct,
                    // the value should have quotes represented as \" in the printed output.
                    else -> "\"${spec.variableValue.toString().replace("\"", "\\\"")}\"" // Handles other types
                }
            }
        }
    }
    return "${spec.indent}${spec.statementType.statementSyntaxPrefix} ${spec.variableName} = $value"
}

internal fun generateStatements(
    statementSpecs: List<StatementSpec>
): String {
    return buildString {
        statementSpecs.forEach { spec ->
            appendLine(generateStatement(spec))
        }
    }.trim()
}
