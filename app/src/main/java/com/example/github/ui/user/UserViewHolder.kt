package com.example.github.ui.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.example.github.databinding.ViewUserRepoRowBinding
import com.example.github.databinding.ViewUserRowBinding
import com.example.github.ui.ViewHolder

sealed class UserViewHolder<T : ViewDataBinding>(binding: T) : ViewHolder<T>(binding) {

    class UserRowViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        root: Boolean
    ) : UserViewHolder<ViewUserRowBinding>(
        ViewUserRowBinding.inflate(inflater, parent, root)
    ) {
        fun bind(item: UserViewItem.UserRowItem) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    class UserRepoRowViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        root: Boolean
    ) : UserViewHolder<ViewUserRepoRowBinding>(
        ViewUserRepoRowBinding.inflate(inflater, parent, root)
    ) {
        fun bind(item: UserViewItem.UserRepoRowItem) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}
