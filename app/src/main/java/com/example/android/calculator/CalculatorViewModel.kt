package com.example.android.calculator

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.absoluteValue
import kotlin.text.StringBuilder

/**
 * @author : yinshengpan
 * Email : shengpan.yin@gmail.com
 * Created date 2022/8/7 10:26
 * Describe : CalculatorViewModel
 */
class CalculatorViewModel : ViewModel() {
    companion object {
        private const val MAX_NUMBER_LENGTH = 10
        private const val PI = 3.14159265
    }

    private val _displayContent = MutableLiveData<String>()
    val displayContent: LiveData<String> = _displayContent

    private val mFirstNumberBuilder = StringBuilder()
    private val mSecondNumberBuilder = StringBuilder()

    private var mFirstNegativeNumber = false
    private var mSecondNegativeNumber = false

    private var mCalculateResult = false

    private var mOperator = ""

    fun getKeyData(): Array<Array<String>> {
        return arrayOf(
            arrayOf("AC", "±", "π", "÷"),
            arrayOf("7", "8", "9", "×"),
            arrayOf("4", "5", "6", "−"),
            arrayOf("1", "2", "3", "+"),
            arrayOf("0", ".", "=", "⬅︎"),
        )
    }

    fun processKey(value: String) {
        when (value) {
            "AC" -> {
                reset()
            }
            "±" -> {
                if (mCalculateResult) {
                    reset()
                }
                if (mOperator.isEmpty()) {
                    mFirstNegativeNumber = !mFirstNegativeNumber
                    postValue(mFirstNumberBuilder.toString(), mFirstNegativeNumber)
                } else {
                    mSecondNegativeNumber = !mSecondNegativeNumber
                    postValue(mSecondNumberBuilder.toString(), mSecondNegativeNumber)
                }
            }
            "π" -> {
                if (mCalculateResult) {
                    reset()
                }
                if (mOperator.isEmpty()) {
                    mFirstNumberBuilder.delete(0, mFirstNumberBuilder.length)
                    mFirstNumberBuilder.append(PI)
                    postValue(mFirstNumberBuilder.toString(), mFirstNegativeNumber)
                } else {
                    mSecondNumberBuilder.delete(0, mSecondNumberBuilder.length)
                    mSecondNumberBuilder.append(PI)
                    postValue(mSecondNumberBuilder.toString(), mSecondNegativeNumber)
                }
            }
            "⬅︎" -> {
                if (mCalculateResult) {
                    reset()
                }
                if (mOperator.isEmpty()) {
                    if (mFirstNumberBuilder.isNotEmpty()) {
                        val length = mFirstNumberBuilder.length
                        mFirstNumberBuilder.delete(length - 1, length)
                        postValue(mFirstNumberBuilder.toString(), mFirstNegativeNumber)
                    }
                } else {
                    if (mSecondNumberBuilder.isNotEmpty()) {
                        val length = mSecondNumberBuilder.length
                        mSecondNumberBuilder.delete(length - 1, length)
                        postValue(mSecondNumberBuilder.toString(), mSecondNegativeNumber)
                    }
                }
            }
            "." -> {
                if (mCalculateResult) {
                    reset()
                }
                if (mOperator.isEmpty()) {
                    when {
                        mFirstNumberBuilder.isEmpty() -> {
                            mFirstNumberBuilder.append("0.")
                        }
                        mFirstNumberBuilder.contains(".") -> {

                        }
                        else -> {
                            mFirstNumberBuilder.append(".")
                        }
                    }
                    postValue(mFirstNumberBuilder.toString(), mFirstNegativeNumber)
                } else {
                    when {
                        mSecondNumberBuilder.isEmpty() -> {
                            mSecondNumberBuilder.append("0.")
                        }
                        mSecondNumberBuilder.contains(".") -> {

                        }
                        else -> {
                            mSecondNumberBuilder.append(".")
                        }
                    }
                    postValue(mSecondNumberBuilder.toString(), mSecondNegativeNumber)
                }
            }
            "=" -> {
                if (mOperator.isEmpty() || mSecondNumberBuilder.isEmpty()) {
                    postValue(mFirstNumberBuilder.toString(), mFirstNegativeNumber)
                } else {
                    val isFirstDecimal = mFirstNumberBuilder.contains(".")
                    val isSecondDecimal = mSecondNumberBuilder.contains(".")
                    val result: Double = calculate()
                    _displayContent.postValue(
                        if (isFirstDecimal || isSecondDecimal) result.toString()
                        else result.toLong().toString()
                    )
                }
            }
            "+", "-", "×", "÷" -> {
                if (mFirstNumberBuilder.isEmpty()) {
                    return
                }
                mCalculateResult = false
                if (mOperator.isNotEmpty()) {
                    if (mSecondNumberBuilder.isNotEmpty()) {
                        val isFirstDecimal = mFirstNumberBuilder.contains(".")
                        val isSecondDecimal = mSecondNumberBuilder.contains(".")
                        val result = calculate()
                        _displayContent.postValue(
                            if (isFirstDecimal || isSecondDecimal) result.toString()
                            else result.toLong().toString()
                        )
                    }
                }
                mOperator = value
            }
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" -> {
                if (mOperator.isEmpty()) {
                    if (mFirstNumberBuilder.isEmpty() && value == "0") {
                        return
                    }
                    if (mCalculateResult) {
                        reset()
                    }
                    if (mFirstNumberBuilder.length > MAX_NUMBER_LENGTH) {
                        return
                    }
                    mFirstNumberBuilder.append(value)
                    postValue(mFirstNumberBuilder.toString(), mFirstNegativeNumber)
                } else {
                    if (mSecondNumberBuilder.isEmpty() && value == "0") {
                        return
                    }
                    if (mSecondNumberBuilder.length > MAX_NUMBER_LENGTH) {
                        return
                    }
                    mSecondNumberBuilder.append(value)
                    postValue(mSecondNumberBuilder.toString(), mSecondNegativeNumber)
                }
            }
        }
    }

    private fun calculate(): Double {
        val firstN =
            (if (mFirstNegativeNumber) -1 else 1) * mFirstNumberBuilder.toString().toDouble()
        val secondN =
            (if (mSecondNegativeNumber) -1 else 1) * mSecondNumberBuilder.toString().toDouble()
        val result = when (mOperator) {
            "+" -> firstN + secondN
            "−" -> firstN - secondN
            "×" -> firstN * secondN
            "÷" -> firstN / secondN
            else -> 0.0
        }
        val isFirstDecimal = mFirstNumberBuilder.contains(".")
        val isSecondDecimal = mSecondNumberBuilder.contains(".")
        mFirstNegativeNumber = result < 0
        mFirstNumberBuilder.delete(0, mFirstNumberBuilder.length)
        mFirstNumberBuilder.append(
            if (isSecondDecimal || isFirstDecimal) result.absoluteValue.toString()
            else result.absoluteValue.toLong().toString()
        )
        mOperator = ""
        mSecondNegativeNumber = false
        mCalculateResult = true
        mSecondNumberBuilder.delete(0, mSecondNumberBuilder.length)
        return result
    }

    private fun postValue(value: String, negativeNumber: Boolean) {
        var displayValue = if (negativeNumber) "-$value" else value
        if (value.isEmpty()) {
            displayValue = ""
        }
        _displayContent.postValue(displayValue)
    }

    private fun reset() {
        mOperator = ""
        mSecondNumberBuilder.delete(0, mSecondNumberBuilder.length)
        mFirstNumberBuilder.delete(0, mFirstNumberBuilder.length)
        mFirstNegativeNumber = false
        mSecondNegativeNumber = false
        mCalculateResult = false
        _displayContent.postValue("")
    }
}