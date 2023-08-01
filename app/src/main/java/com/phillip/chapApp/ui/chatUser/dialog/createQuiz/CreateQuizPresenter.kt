package com.phillip.chapApp.ui.chatUser.dialog.createQuiz

import android.content.Context
import io.reactivex.disposables.Disposable

class CreateQuizPresenter(view: CreateQuizContract.View, context: Context) :
    CreateQuizContract.Presenter {
    private var mView: CreateQuizContract.View = view
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