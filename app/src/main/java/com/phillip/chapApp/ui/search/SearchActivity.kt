package com.phillip.chapApp.ui.search

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.SearchView
import com.google.android.material.tabs.TabLayoutMediator
import com.phillip.chapApp.R
import com.phillip.chapApp.ui.base.SocialBaseActivity
import com.phillip.chapApp.ui.search.adapter.SearchVPAdapter
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.social_activity_friend_request.*
import kotlinx.android.synthetic.main.social_activity_profie.toolbar
import kotlinx.android.synthetic.main.social_activity_search.*
import kotlinx.android.synthetic.main.social_activity_search.btnActionClock
import java.util.concurrent.TimeUnit


class SearchActivity : SocialBaseActivity(), SearchContract.View {
    private lateinit var mPresenter: SearchPresenter
    private lateinit var mSearchAdapter: SearchVPAdapter

//    var dX = 0f
//    var dY = 0f
//    var lastAction = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_activity_search)

        mPresenter = SearchPresenter(this, this)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(resources.getDrawable(R.drawable.ic_left_arrow, null))
        toolbar.setNavigationOnClickListener { onBackPressed() }

        mSearchAdapter = SearchVPAdapter(supportFragmentManager, lifecycle)
        vb_search.adapter = mSearchAdapter

        TabLayoutMediator(tab_search, vb_search) { tab, position ->
            tab.text = "User"
        }.attach()

        // set hint
        var searchEdit: View = edtSearch.findViewById(R.id.search_plate)
        searchEdit.setBackgroundColor(Color.TRANSPARENT)

        edtSearch.queryHint = resources.getString(R.string.search_hint)
        edtSearch.setIconifiedByDefault(false)
        searchObservable().observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.newThread())
            .subscribe { searhTerm ->
                Log.d("AAA", searhTerm)
                if (!searhTerm.isNullOrEmpty()) {
                    mSearchAdapter.updateSearchTerm(searhTerm)
                } else {
                    mSearchAdapter.clearAll()
                }
            }

//        btnActionClock.setOnTouchListener(object : View.OnTouchListener {
//            override fun onTouch(view: View, event: MotionEvent): Boolean {
//                when (event.getActionMasked()) {
//                    MotionEvent.ACTION_DOWN -> {
//                        dX = view.getX() - event.getRawX()
//                        dY = view.getY() - event.getRawY()
//                        lastAction = MotionEvent.ACTION_DOWN
//                    }
//                    MotionEvent.ACTION_MOVE -> {
//                        view.setY(event.getRawY() + dY)
//                        view.setX(event.getRawX() + dX)
//                        lastAction = MotionEvent.ACTION_MOVE
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        if (lastAction == MotionEvent.ACTION_DOWN)
//                            lockDevice()
//                        return true
//                    }
//                    else -> return false
//                }
//                return true
//            }
//
//        })
    }

    private fun searchObservable(): Observable<String> {
        return Observable.create(ObservableOnSubscribe<String> { emitter ->
            val onQueryTextListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    emitter.onNext(newText)
                    return false
                }
            }
            // set query listener
            edtSearch.setOnQueryTextListener(onQueryTextListener)
            emitter.setCancellable { edtSearch.setOnQueryTextListener(null) }
        }).debounce(1000, TimeUnit.MILLISECONDS)
    }
}
