package com.phillip.chapApp.ui.search.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.search.fragment.channel.SearchUserFragment

class SearchVPAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private var mSearchUserFragment: SearchUserFragment? = null

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> {
                mSearchUserFragment = SearchUserFragment()
                return mSearchUserFragment as SearchUserFragment
            }
        }
        return SearchUserFragment()
    }

    fun updateSearchTerm(searchTerm: String) {
        mSearchUserFragment?.updateSearchString(searchTerm)
    }

    fun clearAll() {
        mSearchUserFragment?.clearAll()
    }

    fun showProgress() {
        mSearchUserFragment?.showProgress()
    }

    fun hideProgress() {
        mSearchUserFragment?.hideProgress()
    }


    companion object {
        private const val NUM_TABS = 1
    }
}
