package com.phillip.chapApp.ui.changePhone

class ChangePhoneContract {
    interface View {
//        fun getSuccessBrandShopModel(model: BrandShopModel)

    }

    interface Presenter {
        fun loadPresenter(brandshopuid: String, userid: String)
        fun disposeAPI()
    }
}