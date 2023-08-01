package com.phillip.chapApp.api

import com.phillip.chapApp.helper.Config

class APIManager {
    private val HOST_URL = Config.URL_SERVER

    private val mApi: ItemAPI
    private val generator: APIGenerator = APIGenerator(HOST_URL)

    init {
        mApi = generator.createService(ItemAPI::class.java)
    }

    fun getApi(): ItemAPI {
        return mApi
    }

    companion object {
        private var sInstance: APIManager? = null

        @Synchronized
        fun getInstance(): APIManager {
            if (sInstance == null) {
                sInstance = APIManager()
            }
            return sInstance as APIManager
        }
    }

}