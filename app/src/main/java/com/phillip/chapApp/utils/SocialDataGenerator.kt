package com.phillip.chapApp.utils

import com.phillip.chapApp.R
import com.phillip.chapApp.model.Media
import com.phillip.chapApp.model.Qr
import com.phillip.chapApp.model.SocialUser
import com.phillip.chapApp.model.chat
import java.util.*

fun addChatData(): ArrayList<SocialUser> {
    val collectionData = ArrayList<SocialUser>()
    collectionData.apply {
        addchatlist { name = "Logan Nesser"; image = R.drawable.social_ic_user1; info = "Hy, How are your?";duration = "7.30 PM" }
        addchatlist { name = "Logan Nesser"; image = R.drawable.social_ic_user2; info = "Haha! You are a great..When you meet? ";duration = "5.30 PM" }
        addchatlist { name = "Logan Nesser"; image = R.drawable.social_ic_user3; info = "See you in LA!";duration = "2.30 PM" }
        return collectionData
    }
}
fun addGroupData(): ArrayList<SocialUser> {
    val collectionData = ArrayList<SocialUser>()
    collectionData.apply {
        addchatlist { name = "AI & Mechanism"; image = R.drawable.social_ic_item1; info = "Albert Dune: Hy,Everyone";duration = "7.30 PM" }
        addchatlist { name = "Weight Loss"; image = R.drawable.social_ic_item2; info = "David Thomas: Haha! You are a great..When you meet? ";duration = "5.30 PM" }
        addchatlist { name = "Trip"; image = R.drawable.social_ic_item3; info = "Albert Dune: See you in LA!";duration = "2.30 PM" }
        return collectionData
    }
}
fun addSeeMoreData(): ArrayList<SocialUser> {
    val collectionData = ArrayList<SocialUser>()
    collectionData.apply {
        addchatlist { name = "Logan Nesser"; image = R.drawable.social_ic_user1; info = "Hy, How are your?";duration = "7.30 PM" }
        addchatlist { name = "AI & Mechanism"; image = R.drawable.social_ic_item1; info = "Albert Dune: Hy,Everyone";duration = "7.30 PM" }
        addchatlist { name = "Logan Nesser"; image = R.drawable.social_ic_user2; info = "Haha! You are a great..When you meet? ";duration = "5.30 PM" }
        addchatlist { name = "Logan Nesser"; image = R.drawable.social_ic_user3; info = "See you in LA!";duration = "2.30 PM" }
        addchatlist { name = "Weight Loss"; image = R.drawable.social_ic_item2; info = "David Thomas: Haha! You are a great..When you meet? ";duration = "5.30 PM" }
        addchatlist { name = "Trip"; image = R.drawable.social_ic_item3; info = "Albert Dune: See you in LA!";duration = "2.30 PM" }
        return collectionData
    }
}

fun addStatusData(): ArrayList<SocialUser> {
    val collectionData = ArrayList<SocialUser>()
    collectionData.apply {
        addchatlist { name = "Logan Nesser"; image = R.drawable.social_ic_user4; info = "30 minutes ago";duration = "7.30 PM" }
        addchatlist { name = "Logan Nesser"; image = R.drawable.social_ic_user2; info = "Today,8:30 PM";duration = "5.30 PM" }
        addchatlist { name = "Logan Nesser"; image = R.drawable.social_ic_user3; info = "Today,8:30 PM";duration = "2.30 PM" }
        return collectionData
    }
}
fun addCallData(): ArrayList<SocialUser> {
    val collectionData = ArrayList<SocialUser>()
    collectionData.apply {
        addchatlist { id = "1";  name = "Logan Nesser"; image = R.drawable.social_ic_user1; info = "(2)15 december, 1:52 pm";duration = "7.30 PM";call="R" }
        addchatlist { id = "2";  name = "Logan Nesser"; image = R.drawable.social_ic_user2; info = "13 december, 7:52 pm";duration = "5.30 PM";call="F" }
        addchatlist { id = "3";  name = "Logan Nesser"; image = R.drawable.social_ic_user3; info = "(3)15 April, 1:52 pm";duration = "2.30 PM";call="A" }
        addchatlist { id = "4";  name = "Logan Nesser"; image = R.drawable.social_ic_user3; info = "(3)15 April, 1:52 pm";duration = "2.30 PM";call="A" }
        addchatlist { id = "5";  name = "Logan Nesser"; image = R.drawable.social_ic_user3; info = "(3)15 April, 1:52 pm";duration = "2.30 PM";call="A" }
        addchatlist { id = "6";  name = "Logan Nesser"; image = R.drawable.social_ic_user3; info = "(3)15 April, 1:52 pm";duration = "2.30 PM";call="A" }
        addchatlist { id = "7";  name = "Logan Nesser"; image = R.drawable.social_ic_user3; info = "(3)15 April, 1:52 pm";duration = "2.30 PM";call="A" }
        addchatlist { id = "8";  name = "Logan Nesser"; image = R.drawable.social_ic_user3; info = "(3)15 April, 1:52 pm";duration = "2.30 PM";call="A" }
        addchatlist { id = "9";  name = "Logan Nesser"; image = R.drawable.social_ic_user3; info = "(3)15 April, 1:52 pm";duration = "2.30 PM";call="A" }
        addchatlist { id = "10";  name = "Logan Nesser"; image = R.drawable.social_ic_user3; info = "(3)15 April, 1:52 pm";duration = "2.30 PM";call="A" }
        return collectionData
    }
}
fun addGroupCallData(): ArrayList<SocialUser> {
    val collectionData = ArrayList<SocialUser>()
    collectionData.apply {
        addchatlist { name = "Logan Nesser"; image = R.drawable.social_ic_item1; info = "(2)15 december, 1:52 pm";duration = "7.30 PM";call="R" }
        addchatlist { name = "Logan Nesser"; image = R.drawable.social_ic_item2; info = "13 december, 7:52 pm";duration = "5.30 PM";call="F" }
        return collectionData
    }
}

fun addMediaData(): ArrayList<Media> {
    val collectionData = ArrayList<Media>()
    collectionData.apply {
        addmedialist { image = R.drawable.social_ic_item1;  }
        addmedialist { image = R.drawable.social_ic_item2;  }
        addmedialist { image = R.drawable.social_ic_item3;  }
        addmedialist { image = R.drawable.social_ic_item4;  }
        addmedialist { image = R.drawable.social_ic_item5;  }
        addmedialist { image = R.drawable.social_ic_item6;  }
        addmedialist { image = R.drawable.social_ic_item3;  }
        addmedialist { image = R.drawable.social_ic_item4;  }
        addmedialist { image = R.drawable.social_ic_item1;  }
        addmedialist { image = R.drawable.social_ic_item2;  }
        addmedialist { image = R.drawable.social_ic_item5;  }
        addmedialist { image = R.drawable.social_ic_item6;  }
        addmedialist { image = R.drawable.social_ic_item4;  }
        addmedialist { image = R.drawable.social_ic_item1;  }
        return collectionData
    }
}
fun addQrData(): ArrayList<Qr> {
    val collectionData = ArrayList<Qr>()
    collectionData.apply {
        addQrCode { code = 3005;  }
        addQrCode { code = 3025;  }
        addQrCode { code = 2408;  }
        addQrCode { code = 2709;  }
        addQrCode { code = 3708;  }
        addQrCode { code = 2458;  }
        addQrCode { code = 1809;  }
        addQrCode { code = 6757;  }
        addQrCode { code = 5247;  }
        addQrCode { code = 5253;  }
        addQrCode { code = 5224;  }
        addQrCode { code = 9642;  }
        addQrCode { code = 4836;  }
        addQrCode { code = 7346;  }
        addQrCode { code = 6972;  }
        addQrCode { code = 7545;  }
        addQrCode { code = 5876;  }
        addQrCode { code = 6988;  }
        addQrCode { code = 8752;  }
        return collectionData
    }
}
fun getUserChats(): ArrayList<chat> {
    val collectionData = ArrayList<chat>()
    collectionData.apply {
        addChat { msg="Hi John, how are you";duration="7.30 PM";type="Message";isSender=true }
        addChat { msg="Hi Matloob, I m fine";type="Message" }
        addChat {type="Voice Message"}
        addChat {type="Media"}
        return collectionData
    }
}
