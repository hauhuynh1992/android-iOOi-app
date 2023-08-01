package com.phillip.chapApp.ui.newGroup.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.phillip.chapApp.R
import com.phillip.chapApp.helper.Config
import com.phillip.chapApp.model.User
import com.phillip.chapApp.utils.loadImageFromResources
import com.phillip.chapApp.utils.loadImageFromUrl
import com.phillip.chapApp.utils.setSingleClick
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.social_item_add_people.view.*
import kotlinx.android.synthetic.main.social_item_add_people_edittext.view.*
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit


class AddPeopleRVAdapter(
    private val activity: Activity,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mListener: onAddPeopleListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            USER_TYPE -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_add_people, parent, false)
                val holder = UserViewHolder(mView)
                holder.itemView.setSingleClick {
                    val item = list.get(holder.adapterPosition)
                    mListener?.onItemClick(item)
                }
                holder.itemView.cardRemove.setSingleClick {
                    val item = list.get(holder.adapterPosition)
                    mListener?.onRemoveItemClick(item)
                }
                return holder
            }
            else -> {
                val mView: View = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.social_item_add_people_edittext, parent, false)
                mView.viewTreeObserver.addOnPreDrawListener(object :
                    ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        val lp: ViewGroup.LayoutParams = mView.getLayoutParams()
                        if (lp is StaggeredGridLayoutManager.LayoutParams) {
                            val sglp = lp
                            sglp.isFullSpan = true
                            mView.setLayoutParams(sglp)
                            val lm =
                                (parent as RecyclerView).layoutManager as StaggeredGridLayoutManager?
                            lm!!.invalidateSpanAssignments()
                        }
                        mView.getViewTreeObserver().removeOnPreDrawListener(this)
                        return true
                    }

                })
                return EditTextViewHolder(mView)
            }

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val type = holder.itemViewType
        if (type == EDIT_TYPE) {
            // set hint
            var searchEdit: View =
                (holder as EditTextViewHolder).itemView.edtSearch.findViewById(R.id.search_plate)
            searchEdit.setBackgroundColor(Color.TRANSPARENT)

            holder.itemView.edtSearch.queryHint = activity.resources.getString(R.string.search_hint)
            holder.itemView.edtSearch.setIconifiedByDefault(false)
            searchObservable(holder.itemView.edtSearch).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe { searhTerm ->
                    mListener?.onSearchClick(searhTerm)
                }
        } else {
            val item = list.get(position)
            (holder as UserViewHolder).itemView.tvName.text = item.name ?: "Unknown"
            if (item.isSelected) {
                holder.itemView.cardRemove.visibility = View.VISIBLE
                holder.itemView.cardUser.visibility = View.GONE
            } else {
                holder.itemView.cardRemove.visibility = View.GONE
                holder.itemView.cardUser.visibility = View.VISIBLE
            }
            if (item.image != null) {
                holder.itemView.ivUser.loadImageFromUrl(activity, Config.URL_SERVER + item.image!!)
            } else {
                holder.itemView.ivUser.loadImageFromResources(
                    activity,
                    R.drawable.ic_default_avatar
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        if (position == list.size) {
            return EDIT_TYPE
        } else {
            return USER_TYPE
        }
    }

    inner class EditTextViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    fun addItem(item: User) {
        list.add(item)
        notifyDataSetChanged()
    }

    fun updateItem(item: User, isSelected: Boolean) {
        list.indexOfFirst { it.id == item.id }.let { index ->
            if (index != -1) {
                list.get(index).isSelected = isSelected
                notifyDataSetChanged()
            }
        }
    }

    fun getAllUser(): String {
        var listString = ""
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<User>>() {}.type
        listString = gson.toJson(list, listType)
        return listString
    }


    fun removeItem(item: User) {
        list.indexOfFirst { it.id == item.id }.let { index ->
            if (index != -1) {
                list.removeAt(index)
                notifyDataSetChanged()
            }
        }
    }

    fun setOnAddPeopleListener(listener: onAddPeopleListener) {
        this.mListener = listener
    }

    interface onAddPeopleListener {
        fun onItemClick(item: User)
        fun onRemoveItemClick(item: User)
        fun onSearchClick(searchTerm: String)
    }

    private fun searchObservable(edtSearch: SearchView): Observable<String> {
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

    companion object {
        const val USER_TYPE = 1
        const val EDIT_TYPE = 2
    }
}