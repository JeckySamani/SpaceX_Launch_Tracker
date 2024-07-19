package com.app.assignment.base

import android.view.View

abstract class AppBaseFragment : BaseFragment() {
    abstract fun init(view: View)
    abstract fun setListsAndAdapters()
    abstract fun setListeners()
    abstract fun initObservers()
}