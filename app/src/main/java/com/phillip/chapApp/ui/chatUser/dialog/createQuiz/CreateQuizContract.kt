package com.phillip.chapApp.ui.chatUser.dialog.createQuiz

class CreateQuizContract {
    interface View {
//        fun getSuccessBrandShopModel(model: BrandShopModel)

    }

    interface Presenter {
        fun loadPresenter(brandshopuid: String, userid: String)
        fun disposeAPI()
    }
}