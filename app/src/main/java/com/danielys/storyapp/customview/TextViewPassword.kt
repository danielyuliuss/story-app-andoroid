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


class TextViewPassword : AppCompatEditText {
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
        onAboveChar()
        hint = context.getString(R.string.hint_password)
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length < 8) onBelowChar() else onAboveChar()
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    private fun onBelowChar() {
        backgroundTintMode = PorterDuff.Mode.ADD
        backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.red))
        error = context.getString(R.string.wrong_password)
    }

    private fun onAboveChar() {
        backgroundTintMode = PorterDuff.Mode.SRC_IN
        backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black))
    }

}