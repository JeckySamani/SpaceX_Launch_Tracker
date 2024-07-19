package com.app.assignment.helpers

import android.app.Activity
import android.content.*
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.app.assignment.BuildConfig

object Utils {
    //return true;
    val isDebug: Boolean
        get() =//return true;
            BuildConfig.DEBUG

    fun showToast(context: Context?, msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }


    fun openKeyboard(context: Context, view: View) {
        view.requestFocus()
        view.isFocusableInTouchMode = true
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    fun closeKeyboard(context: Context, view: View) {
        val imm = (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        (context as Activity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

}