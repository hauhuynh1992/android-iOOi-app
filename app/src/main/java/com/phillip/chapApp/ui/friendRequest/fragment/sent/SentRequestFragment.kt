package com.phillip.chapApp.ui.friendRequest.fragment.sent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.phillip.chapApp.R
import com.phillip.chapApp.model.FriendType
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.BaseFragment
import com.phillip.chapApp.ui.friendRequest.fragment.sent.adatper.SentRequestRVAdapter
import com.phillip.chapApp.utils.setVerticalLayout
import kotlinx.android.synthetic.main.fragment_search_user.*
import kotlinx.android.synthetic.main.toolbar_home_dashboard.shimmerFrameLayout

class SentRequestFragment : BaseFragment(), SentRequestContract.View {

    private lateinit var mChatAdapter: SentRequestRVAdapter
    private lateinit var mPresenter: SentRequestContract.Presenter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mPresenter = SentRequestPresenter(this, requireContext())
        mChatAdapter = SentRequestRVAdapter(requireActivity(), arrayListOf())
        mChatAdapter.setOnSentRequestListener(object : SentRequestRVAdapter.OnSentRequestListener {
            override fun onRetrieveClick(userId: Int) {
                mChatAdapter.removeItem(userId)
            }

            override fun onSentClick(userId: Int) {
                mChatAdapter.removeItem(userId)
            }

            override fun onItemClick(user: User) {
                // Todo
            }

        })
        rvList.setVerticalLayout(false)
        rvList.adapter = mChatAdapter

        mPresenter.getFriendRequest()
    }


    override fun showProgress() {
        shimmerFrameLayout.startShimmerAnimation()
    }

    override fun hideProgress() {
        shimmerFrameLayout.stopShimmerAnimation()
        shimmerFrameLayout.visibility = View.GONE
    }

    override fun showSuccessDialog(title: String?, msg: String, onItemClick: () -> Unit) {
    }

    override fun onPause() {
        shimmerFrameLayout.stopShimmerAnimation()
        super.onPause()
    }

    override fun onGetFriendRequestSuccess(users: ArrayList<User>) {
        rvList.visibility = View.VISIBLE
        mChatAdapter.addItems(users)
    }

    override fun onGetSentFriendRequestSuccess(userId: Int) {
    }

    override fun onGetRetrieveFriendRequestSuccess(userId: Int) {
    }
}