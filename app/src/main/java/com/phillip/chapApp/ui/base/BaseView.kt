package com.phillip.chapApp.ui.base

interface BaseView {
    fun showProgressDialog()
    fun hideProgressDialog()
    fun showSuccessDialog(title: String? = null, msg: String, onItemClick: () -> Unit)
    fun showErrorDialog(msg: String)
    fun handleError(e: Throwable)
}