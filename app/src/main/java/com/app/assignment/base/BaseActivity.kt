package com.app.assignment.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.app.assignment.R
import com.app.assignment.customview.CustomProgressBar
import com.app.assignment.helpers.Utils

open class BaseActivity : AppCompatActivity(){
    lateinit var context: Context
    lateinit var viewContext: Context

    lateinit var pDialog: CustomProgressBar
    var folderName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = applicationContext
        //FirebaseApp.initializeApp(context)
        //prefs = SharedPrefManager(context)
        folderName = getString(R.string.app_name)
        viewContext = this
        initProgressDialog()
    }

    private var doubleBackToExitPressedOnce = false
    fun askForExit() {
        if (doubleBackToExitPressedOnce) {
            setResult(RESULT_CANCELED)
            finishAffinity()
            return
        }
        doubleBackToExitPressedOnce = true
//        showToast("Press again to exit $folderName.") // JACK
        Utils.showToast(context , getString(R.string.press_again_to_exit) + "$folderName.")
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    fun isConnectingToInternet(): Boolean {
        val connectivity = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivity != null) {
            val info = connectivity.allNetworkInfo
            if (info != null) for (anInfo in info) if (anInfo.state == NetworkInfo.State.CONNECTED) {
                return true
            }
        }
        return false
    }


    private fun initProgressDialog() {
        pDialog = CustomProgressBar(this)
        pDialog.setCancelable(false)
    }

    fun showProgressDialog() {
        if (!pDialog.isShowing) pDialog.show()
    }

    fun dismissProgressDialog() {
        if (pDialog.isShowing) pDialog.dismiss()
    }

    private fun getRootView(): View? {
        val contentViewGroup = findViewById<ViewGroup>(android.R.id.content)
        var rootView: View? = null
        if (contentViewGroup != null) rootView = contentViewGroup.getChildAt(0)
        if (rootView == null) rootView = window.decorView.rootView
        return rootView
    }

    fun hideKeyBoard() {
        try {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            if (imm != null) {
                val view = currentFocus
                if (view != null) imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
        } catch (ignored: Exception) {
        }
    }

    override fun onDestroy() {
        dismissProgressDialog()
//        AppController.instance.cancelPendingRequests(this.javaClass.simpleName)
//        context = null
//        viewContext = null
//        prefs = null
//        pDialog = null
        super.onDestroy()
        callGC()
    }

    fun callGC() {
        System.gc()
        Runtime.getRuntime().gc()
    }

    open fun circleReveal(
//        viewID: Int,
        layout: LinearLayout,
        posFromRight: Int,
        containsOverflow: Boolean,
        isShow: Boolean) {
//        val myView = findViewById<View>(viewID)
        var width = layout.width
        if (posFromRight > 0) width -= posFromRight *
                48
        - 48 / 2
        if (containsOverflow) width -= 36
        val cx = width
        val cy = layout.height / 2
        val anim: Animator
        anim =
            if (isShow) ViewAnimationUtils.createCircularReveal(
                layout,
                cx,
                cy,
                0f,
                width.toFloat()
            ) else ViewAnimationUtils.createCircularReveal(layout, cx, cy, width.toFloat(), 0f)
        anim.setDuration(220L)

        // make the view invisible when the animation is done
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (!isShow) {
                    super.onAnimationEnd(animation)
                    layout.visibility = View.INVISIBLE
                }
            }
        })

        // make the view visible and start the animation
        if (isShow) layout.visibility = View.VISIBLE

        // start the animation
        anim.start()
    }


}