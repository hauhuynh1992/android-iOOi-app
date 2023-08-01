package com.phillip.chapApp.ui.search

import android.content.Context
import com.phillip.chapApp.utils.extentions.SocialDb
import io.reactivex.disposables.Disposable

class SearchPresenter(view: SearchContract.View, private val context: Context) :
    SearchContract.Presenter {
    private var mView: SearchContract.View = view
    private var mSubscription: Disposable? = null

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

}