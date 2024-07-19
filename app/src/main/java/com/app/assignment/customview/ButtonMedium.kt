package com.app.assignment.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.res.ResourcesCompat
import com.app.assignment.R

class ButtonMedium : AppCompatButton {
    constructor(context: Context) : super(context) {
        setFontStyle(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setFontStyle(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setFontStyle(context)
    }

    private fun setFontStyle(context: Context?) {
        val typeface = ResourcesCompat.getFont(context!!, R.font.poppins_medium)
        setTypeface(typeface)
    }
}