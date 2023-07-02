package com.danielys.storyapp.customview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.danielys.storyapp.R

class TextViewEmail : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        onNotError()
        hint = context.getString(R.string.hint_email)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (isValidEmail(s.toString())) onNotError() else onError()
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+\$".toRegex()
        return pattern.matches(email)
    }

    private fun onError() {
        backgroundTintMode = PorterDuff.Mode.ADD
        backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
        error = context.getString(R.string.wrong_email)
    }

    private fun onNotError() {
        backgroundTintMode = PorterDuff.Mode.SRC_IN
        backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black))
    }

}