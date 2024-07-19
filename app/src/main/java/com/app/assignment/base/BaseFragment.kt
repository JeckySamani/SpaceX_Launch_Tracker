package com.app.assignment.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.assignment.customview.CustomProgressBar

open class BaseFragment : Fragment() {
    lateinit var context1: Context
    lateinit var viewContext: Context

    lateinit var pDialog: CustomProgressBar
    //    lateinit var callBackForRetry: CallBackForRetry
//    private lateinit var snackbar: Snackbar

    override fun onCreate(arg0: Bundle?) {
        super.onCreate(arg0)
        context1 = requireActivity().applicationContext
        viewContext = requireActivity()
        //prefs = ApplicationPrefs(context1)
        checkPermission()
        initProgressDialog()
    }

    @SuppressLint("NewApi")
    private fun checkPermission() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }


    private fun getRootView(): View {
        var rootView: View? = null
        if (activity != null) {
            val contentViewGroup =
                requireActivity().findViewById<ViewGroup>(androidx.appcompat.R.id.content)
            if (contentViewGroup != null) rootView = contentViewGroup.getChildAt(0)
            if (rootView == null) rootView = requireActivity().window.decorView.rootView
            return rootView!!
        }
        return rootView!!
    }

    private fun initProgressDialog() {
        pDialog = CustomProgressBar(viewContext)
        pDialog.setCancelable(true)
    }

    fun showProgressDialog() {
        if (!pDialog.isShowing) pDialog.show()
    }

    fun dismissProgressDialog() {
        if (pDialog.isShowing) pDialog.dismiss()
    }

    override fun onDestroy() {
        dismissProgressDialog()
        //        AppController.getInstance().cancelPendingRequests(this.getClass().getSimpleName());
        super.onDestroy()
        callGC()
    }

    private fun callGC() {
        System.gc()
        Runtime.getRuntime().gc()
    }

}