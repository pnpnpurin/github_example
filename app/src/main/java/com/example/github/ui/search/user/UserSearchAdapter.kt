package com.example.github.ui.search.user

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.github.entity.User
import com.example.github.ui.search.SearchViewHolder
import com.example.github.ui.search.SearchViewItem
import com.example.github.ui.search.SearchViewType

class UserSearchAdapter(
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var users = listOf<SearchViewItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SearchViewType.of(viewType).createViewHolder(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SearchViewHolder.UserSearchRowViewHolder -> {
                val item = users[position] as SearchViewItem.UserSearchRowItem
                holder.bind(item)
            }
            is SearchViewHolder.LoadingViewHolder -> {
                // nop
            }
        }
    }

    override fun getItemCount(): Int = users.size

    override fun getItemViewType(position: Int): Int {
        return users[position].viewType.id
    }

    fun updateUserList(userList: List<User>, isLoading: Boolean) {
        users = mutableListOf<SearchViewItem>().also { list ->
            list.addAll(userList.map { SearchViewItem.UserSearchRowItem(it) })
            if (isLoading) {
                list.add(SearchViewItem.LoadingItem)
            }
        }

        notifyDataSetChanged()
    }
}