package com.phillip.chapApp.ui.friendRequest.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.phillip.chapApp.ui.friendRequest.fragment.receive.ReceiveRequestFragment
import com.phillip.chapApp.ui.friendRequest.fragment.sent.SentRequestFragment
import com.phillip.chapApp.ui.search.fragment.channel.SearchUserFragment

class FriendRequestVPAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {


    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return ReceiveRequestFragment()
        }
        return SentRequestFragment()
    }

    companion object {
        private const val NUM_TABS = 1
    }
}
