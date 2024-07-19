package com.app.assignment.base

abstract class AppBaseActivity  : BaseActivity(){

    abstract fun init()
    abstract fun setListsAndAdapters()
    abstract fun setListeners()
    abstract fun initObservers()

}