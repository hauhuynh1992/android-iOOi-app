package com.phillip.chapApp.ui.search.fragment.channel

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.phillip.chapApp.R
import com.phillip.chapApp.model.FriendType
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.BaseFragment
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.chatUser.ChatUSerActivity
import com.phillip.chapApp.ui.search.SearchActivity
import com.phillip.chapApp.ui.search.fragment.channel.adapter.SearchUserRVAdapter
import com.phillip.chapApp.utils.setVerticalLayout
import kotlinx.android.synthetic.main.fragment_search_user.*
import kotlinx.android.synthetic.main.toolbar_home_dashboard.shimmerFrameLayout

class SearchUserFragment : BaseFragment(), SearchUserContract.View {

    private lateinit var mChatAdapter: SearchUserRVAdapter
    private lateinit var mPresenter: SearchUserPresenter
    lateinit var items: ArrayList<User>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mChatAdapter = SearchUserRVAdapter(requireActivity(), arrayListOf())
        mChatAdapter.setListener(object : SearchUserRVAdapter.OnSearchFriendListener {
            override fun onSendMessage(user: User) {
                (requireActivity() as SocialBaseActivity).hideKeyboard()
                val user = User(
                    id = user.id,
                    name = user.name,
                    image = user.image
                )
                val jsonUser = Gson().toJson(user)
                val intent = Intent(requireContext(), ChatUSerActivity::class.java).apply {
                    putExtra("USER", jsonUser)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(intent)
            }

            override fun onSendFriendRequest(user: User) {
                (requireActivity() as SocialBaseActivity).hideKeyboard()
                mPresenter.addFriend(user.id)
            }

        })
        rvList.setVerticalLayout(false)
        rvList.adapter = mChatAdapter
        mPresenter = SearchUserPresenter(this, requireContext())
        rvList.visibility = View.VISIBLE
        shimmerFrameLayout.visibility = View.GONE
    }

    override fun onShowSearchResult(users: ArrayList<User>, status: Int) {
        rvList.visibility = View.VISIBLE
        mChatAdapter.addItems(users, status)
    }

    override fun onSendFriendRequestSuccess(message: String) {
        mChatAdapter.setStatus(FriendType.PENDING.value)
    }

    override fun showProgress() {
        rvList.visibility = View.GONE
        shimmerFrameLayout.visibility = View.VISIBLE
        shimmerFrameLayout.startShimmerAnimation()
    }

    override fun hideProgress() {
        shimmerFrameLayout.visibility = View.GONE
        rvList.visibility = View.VISIBLE
        shimmerFrameLayout.stopShimmerAnimation()
    }

    override fun showSuccessDialog(title: String?, msg: String, onItemClick: () -> Unit) {
    }

    override fun onPause() {
        shimmerFrameLayout.stopShimmerAnimation()
        super.onPause()
    }

    fun updateSearchString(searchTerm: String) {
        mPresenter.search(searchTerm)
    }

    fun clearAll() {
        mChatAdapter.clearAll()
        (requireActivity() as SearchActivity).hideKeyboard()
    }

}