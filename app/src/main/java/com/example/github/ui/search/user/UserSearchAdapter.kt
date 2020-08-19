package com.example.github.ui.search.user

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.github.entity.User
import com.example.github.ui.search.SearchPresenter
import com.example.github.ui.search.SearchViewHolder
import com.example.github.ui.search.SearchViewItem
import com.example.github.ui.search.SearchViewType
import java.lang.IllegalStateException

class UserSearchAdapter(
    private val context: Context,
    private val tapItemCallback: (User) -> Unit
) : PagingDataAdapter<User, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    private val presenter = object : SearchPresenter {
        override fun onTapItem(position: Int) {
            getItem(position)?.let {
                tapItemCallback.invoke(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchViewType.of(viewType).createViewHolder(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = getItem(position) ?: throw IllegalStateException("user must not be null")
        when (holder) {
            is SearchViewHolder.UserSearchRowViewHolder -> {
                holder.bind(SearchViewItem.UserSearchRowItem(user), position, presenter)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return SearchViewType.UserSearchRow.id
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.login == newItem.login
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }
}