package com.phillip.chapApp.ui.changePhone

import android.content.Context
import com.phillip.chapApp.R
import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.helper.PreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ChangePhonePresenter(view: ChangePhoneContract.View, context: Context) :
    ChangePhoneContract.Presenter {
    private var mView: ChangePhoneContract.View = view
    private var mSubscription: Disposable? = null

    override fun loadPresenter(brandShopId: String, userid: String) {
//        mSubscription =
//            APIManager.getInstance().getApi().getListPopup(popUprequest)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.newThread())
//                .subscribe(this::onRetrieveListPopUpSuccess, this::onRetrieveListPopUpFailed)
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }

}