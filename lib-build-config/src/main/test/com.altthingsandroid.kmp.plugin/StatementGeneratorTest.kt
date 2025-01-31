package com.altthingsandroid.kmp.plugin

import com.allthingsandroid.kmp.plugin.util.StatementSpec
import com.allthingsandroid.kmp.plugin.util.StatementType
import com.allthingsandroid.kmp.plugin.util.ValueQuote
import com.allthingsandroid.kmp.plugin.util.generateStatement
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * Unit tests for [generateStatement] function.
 * This test suite validates various cases for generating Kotlin variable/val statements
 * based on given [StatementSpec].
 */
class StatementGeneratorTest {

    @Test
    fun `should generate correct statement based on spec`() = runTest {
        forAll(
            // ✅ Basic test cases
            row(
                StatementSpec(
                    variableName = "x",
                    variableValue = "hello",
                    statementType = StatementType.STMT_VAR,
                    indent = "    ",  // 4 spaces indentation
                    quoteValue = ValueQuote.YES
                ),
               "    var x = \"hello\""
            ),
            row(
                StatementSpec(
                    variableName = "y",
                    variableValue = 42,
                    statementType = StatementType.STMT_VAL,
                    indent = "",
                    quoteValue = ValueQuote.NO
                ),
               "val y = 42"
            ),

            // ✅ Null value handling
            row(
                StatementSpec(
                    variableName = "z",
                    variableValue = null,
                    statementType = StatementType.STMT_VAR,
                    indent = "",
                    quoteValue = ValueQuote.AUTO
                ),
               "var z = null"
            ),

            // ✅ Floating-point numbers
            row(
                StatementSpec(
                    variableName = "a",
                    variableValue = 3.14,
                    statementType = StatementType.STMT_VAL,
                    indent = "",
                    quoteValue = ValueQuote.AUTO
                ),
               "val a = 3.14"
            ),

            // ✅ Boolean values
            row(
                StatementSpec(
                    variableName = "b",
                    variableValue = true,
                    statementType = StatementType.STMT_VAR,
                    indent = "  ",  // 2 spaces indentation
                    quoteValue = ValueQuote.AUTO
                ),
               "  var b = true"
            ),

            // ✅ Character values
            row(
                StatementSpec(
                    variableName = "c",
                    variableValue = 'c',
                    statementType = StatementType.STMT_VAL,
                    indent = "",
                    quoteValue = ValueQuote.AUTO
                ),
               "val c = 'c'"
            ),

            // ✅ String with quotes inside
            row(
                StatementSpec(
                    variableName = "quotedString",
                    variableValue = "This is a \"quote\" inside",
                    statementType = StatementType.STMT_VAR,
                    indent = "",
                    quoteValue = ValueQuote.YES
                ),
               "var quotedString = \"This is a \"quote\" inside\""
            ),

            // ✅ Custom CharSequence implementation
            row(
                StatementSpec(
                    variableName = "weirdSeq",
                    variableValue = WeirdCharSequence("test"),
                    statementType = StatementType.STMT_VAR,
                    indent = "",
                    quoteValue = ValueQuote.YES
                ),
               "var weirdSeq = \"weird-test\""
            ),

            // ✅ Custom Number implementation (complex number)
            row(
                StatementSpec(
                    variableName = "complex",
                    variableValue = ComplexNumber(2.5, -3.1),
                    statementType = StatementType.STMT_VAL,
                    indent = "",
                    quoteValue = ValueQuote.AUTO
                ),
               "val complex = \"2.5 + -3.1i\""
            ),

            // ✅ Handling negative decimals
            row(
                StatementSpec(
                    variableName = "negativeDecimal",
                    variableValue = -0.75,
                    statementType = StatementType.STMT_VAR,
                    indent = "",
                    quoteValue = ValueQuote.AUTO
                ),
               "var negativeDecimal = -0.75"
            ),

            // ✅ Handling fractions
            row(
                StatementSpec(
                    variableName = "fraction",
                    variableValue = 1.0 / 3.0,
                    statementType = StatementType.STMT_VAL,
                    indent = "",
                    quoteValue = ValueQuote.AUTO
                ),
               "val fraction = 0.3333333333333333"
            ),

            // ✅ Boolean as string
            row(
                StatementSpec(
                    variableName = "stringFalse",
                    variableValue = "false",
                    statementType = StatementType.STMT_VAR,
                    indent = "",
                    quoteValue = ValueQuote.YES
                ),
               "var stringFalse = \"false\""
            ),

            // ✅ Unicode variable names
            row(
                StatementSpec(
                    variableName = "变量",  // Chinese for "variable"
                    variableValue = 10,
                    statementType = StatementType.STMT_VAL,
                    indent = "",
                    quoteValue = ValueQuote.NO
                ),
               "val 变量 = 10"
            ),

            // ✅ Mixed-type string (contains char, boolean, Unicode, and a number)
            row(
                StatementSpec(
                    variableName = "mixedString",
                    variableValue = "a 'c' true 世界 42",
                    statementType = StatementType.STMT_VAR,
                    indent = "",
                    quoteValue = ValueQuote.YES
                ),
               "var mixedString = \"a 'c' true 世界 42\""
            )
        ) { spec, expected ->
            generateStatement(spec) shouldBe expected
        }
    }

    // 🔥 Custom CharSequence implementation for testing
    class WeirdCharSequence(private val text: String) : CharSequence {
        override val length: Int get() = text.length
        override fun get(index: Int): Char = text[index]
        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = text.subSequence(startIndex, endIndex)
        override fun toString(): String = "weird-$text"  // Returns prefixed output
    }

    // 🔥 Custom Number implementation (Complex Number)
    class ComplexNumber(val real: Double, val imaginary: Double) : Number() {
        override fun toDouble() = real
        override fun toFloat() = real.toFloat()
        override fun toLong() = real.toLong()
        override fun toInt() = real.toInt()
        override fun toShort() = real.toInt().toShort()
        override fun toByte() = real.toInt().toByte()
        override fun toString() = "$real + ${imaginary}i"  // Returns complex number representation
    }
}
