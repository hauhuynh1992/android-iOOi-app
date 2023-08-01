package com.phillip.chapApp.service

import com.phillip.chapApp.api.APIManager
import com.phillip.chapApp.model.ResultApi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ChatServicePresenter(view: ChatServiceContract.View) :
    ChatServiceContract.Presenter {
    private var mView: ChatServiceContract.View = view
    private var mSubscription: Disposable? = null

    override fun updateSocketId(socketId: String) {
        mSubscription =
            APIManager.getInstance().getApi()
                .updateSocket(socketId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        mView.onUpdateSocketIdSuccess()
                    }
                }, {
                    // do nothing
                }, {
                    // do nothing
                })
    }

    override fun getListGroup() {
        mSubscription =
            APIManager.getInstance().getApi()
                .getChannelGroup("")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe({
                    if (it.result == ResultApi.OK.value) {
                        if (it.groups != null) {
                            mView.onGetListGroupSuccess(it.groups.map { it.id } as ArrayList<Int>)
                        } else {
                            mView.onGetListGroupSuccess(arrayListOf())
                        }
                    }
                }, {
                    // do nothing
                }, {
                    // do nothing
                })
    }

    override fun disposeAPI() {
        if (mSubscription != null && !mSubscription!!.isDisposed) {
            mSubscription!!.dispose()
        }
    }
}