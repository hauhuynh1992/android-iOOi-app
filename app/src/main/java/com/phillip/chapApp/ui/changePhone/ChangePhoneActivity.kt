package com.phillip.chapApp.ui.changePhone

import android.os.Bundle
import com.phillip.chapApp.R
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.utils.onClick
import kotlinx.android.synthetic.main.social_toolbar.*

class ChangePhoneActivity : SocialBaseActivity(), ChangePhoneContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_change_phone)
        ivBack.onClick { onBackPressed() }
    }
}
