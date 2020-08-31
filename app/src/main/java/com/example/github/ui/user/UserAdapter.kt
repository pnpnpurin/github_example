package com.example.github.ui.user

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.github.entity.Repo
import com.example.github.entity.User

class UserAdapter(
    private val context: Context
) : ListAdapter<UserViewItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserViewType.of(viewType).createViewHolder(LayoutInflater.from(context), parent, false)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserViewHolder.UserRowViewHolder -> {
                val item = getItem(position) as UserViewItem.UserRowItem
                holder.bind(item)
            }
            is UserViewHolder.UserRepoRowViewHolder -> {
                val item = getItem(position) as UserViewItem.UserRepoRowItem
                holder.bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> UserViewType.UserRow.id
            else -> UserViewType.UserRepoRow.id
        }
    }

    fun submitList(user: User, repos: List<Repo>?) {
        val list = mutableListOf<UserViewItem>(UserViewItem.UserRowItem(user))
        repos?.let {
            list.addAll(it.map { repo -> UserViewItem.UserRepoRowItem(repo) })
        }
        submitList(list)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserViewItem>() {
            override fun areItemsTheSame(oldItem: UserViewItem, newItem: UserViewItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserViewItem, newItem: UserViewItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
