package com.phillip.chapApp.ui.friendRequest.fragment.receive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.phillip.chapApp.R
import com.phillip.chapApp.model.FriendType
import com.phillip.chapApp.model.User
import com.phillip.chapApp.ui.base.BaseFragment
import com.phillip.chapApp.ui.friendRequest.fragment.receive.adapter.ReceiveRequestRVAdapter
import com.phillip.chapApp.utils.setVerticalLayout
import kotlinx.android.synthetic.main.fragment_search_user.*
import kotlinx.android.synthetic.main.toolbar_home_dashboard.shimmerFrameLayout

class ReceiveRequestFragment : BaseFragment(), ReceiveRequestContract.View {

    private lateinit var mChatAdapter: ReceiveRequestRVAdapter
    private lateinit var mPresenter: ReceiveRequestContract.Presenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_search_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mPresenter = ReceiveRequestPresenter(this, requireContext())
        mChatAdapter = ReceiveRequestRVAdapter(requireActivity(), arrayListOf())
        mChatAdapter.setOnReceiveRequestListener(object :
            ReceiveRequestRVAdapter.OnReceiveRequestListener {
            override fun onAcceptClick(userId: Int) {
                mPresenter.acceptRequest(userId = userId)
                mChatAdapter.removeItem(userId)
            }

            override fun onDenyClick(userId: Int) {
                mPresenter.denyRequest(userId = userId)
                mChatAdapter.removeItem(userId)
            }

            override fun onItemClick(user: User) {
//                var intent = Intent(activity, ChatUSerActivity::class.java)
//                startActivity(intent)
            }
        })
        rvList.setVerticalLayout(false)
        rvList.adapter = mChatAdapter
        var status = ArrayList<Int>()
        status.add(FriendType.PENDING.value)
        mPresenter.getFriendRequestWithStatus()
    }

    override fun onGetFriendRequestWithStatusSuccess(users: ArrayList<User>) {
        rvList.visibility = View.VISIBLE
        mChatAdapter.addItems(users)
    }

    override fun onAcceptRequestSuccess(userId: Int, message: String) {
       Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDenyRequestSuccess(userId: Int, message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
}