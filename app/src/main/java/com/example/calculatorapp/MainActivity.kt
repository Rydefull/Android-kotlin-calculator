package com.example.calculatorapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var resultText: TextView
    private val defVal = "0"
    private var currentNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultText = findViewById(R.id.resultText)

        val appendButtons = listOf<Button>(
            findViewById(R.id.Zero),
            findViewById(R.id.One),
            findViewById(R.id.Two),
            findViewById(R.id.Three),
            findViewById(R.id.Four),
            findViewById(R.id.Five),
            findViewById(R.id.Six),
            findViewById(R.id.Seven),
            findViewById(R.id.Eight),
            findViewById(R.id.Nine),
            findViewById(R.id.Point),
            findViewById(R.id.PlB),
            findViewById(R.id.MinB),
            findViewById(R.id.MulB),
            findViewById(R.id.DIVButton),
            findViewById(R.id.OBButton),
            findViewById(R.id.CBButton)

        )

        appendButtons.forEach { button ->
            button.setOnClickListener {
                appendSymbol(button.text.toString())
            }
        }

        findViewById<Button>(R.id.ACButton).setOnClickListener {
            ac()
        }
        findViewById<Button>(R.id.DELButton).setOnClickListener {
            delete()
        }
        findViewById<Button>(R.id.PerButton).setOnClickListener {
            percent()
            appendSymbol("%")
        }
        findViewById<Button>(R.id.EqB).setOnClickListener {
            if (currentNumber.last() in "+-*/")
                currentNumber = currentNumber.dropLast(1)
            appendSymbol("=")
            eq()
        }
    }

    private fun calculate(calculationStr: String): Double {
        val sums = mutableListOf<Double>()
        var num = ""
        var braceNum = ""
        var multiplying = Pair(false, '_')
        val sSigns = listOf('+', '-', '=')
        val mSigns = listOf('*', '/', '=')
        var braces = 0

        calculationStr.forEach {
            if (braces > 0) {
                if (it == '(') braces++
                else if (it == ')') braces--
                if (braces == 0) {
                    num += calculate("$braceNum=").toString()
                    braceNum = ""
                } else {
                    braceNum += it
                }
                return@forEach
            }
            if (it == '(') {
                braces++
                return@forEach
            }
            if (it == '%') {
                if (multiplying.first) num = "${num.toDouble() / 100.0}"
                else num = "${sums.sum() * num.toDouble() / 100}"
                return@forEach
            }
            if (it in sSigns + mSigns) {
                if (multiplying.first) {
                    when (multiplying.second) {
                        '*' -> sums[sums.lastIndex] *= num.toDouble()
                        '/' -> sums[sums.lastIndex] /= num.toDouble()
                    }
                    multiplying = Pair(it in mSigns, it)
                    num = if (it in sSigns) "$it" else ""
                } else if (it in sSigns) {
                    if (num.isNotEmpty()) {
                        sums += num.toDouble()
                        num = "$it"
                    } else num = "$it"
                } else if (it in mSigns) {
                    multiplying = Pair(true, it)
                    sums += num.toDouble()
                    num = ""
                }
            } else num += it
        }
        println(sums)
        return sums.sum()
    }

    private fun String.canAppend(): Boolean {
        val last = currentNumber.lastOrNull() ?: 'n'
        val preLast = currentNumber.takeLast(2).firstOrNull() ?: 'n'

        if (this in ('0'..'9').joinToString("")) {
            if (
                last in "%"
                ||
                (preLast in "+-*/" && last == '0')
            ) return false
            println(last)
        }
        if (this in "+-*/") {
            if (last in "+-*/") {
                currentNumber = currentNumber.dropLast(1)
            }
        }
        if (this in "*/") {
            return currentNumber.isNotEmpty()
        }
        if (this in "%.") {
            if (
                last !in '0'..'9'
            ) return false
        }
        if (this == "0") {
            if (
                currentNumber.isEmpty()
            ) return false
        }

        return true
    }

    private fun appendSymbol(symbol: String) {
        if (symbol.canAppend()) {
            currentNumber += symbol
            resultText.text = currentNumber
        }
    }

    private fun ac() {
        println("Нажата кнопка: ac")
        currentNumber = ""
        resultText.text = defVal
    }

    private fun delete() {
        println("Нажата кнопка: del")
        currentNumber = if (currentNumber == "Infinity") ""
        else currentNumber.dropLast(1)
        resultText.text = currentNumber.ifEmpty { defVal }
    }

    private fun percent() {
        println("Нажата кнопка: %")
    }

    private fun eq() {
        println("Нажата кнопка: eq")
        val result = calculate(currentNumber)
        currentNumber = if (result.toInt().toDouble() == result)
            "${result.toInt()}"
        else "$result"
        if (currentNumber == "0") currentNumber = ""
        resultText.text = currentNumber.ifEmpty { defVal }
    }
}
