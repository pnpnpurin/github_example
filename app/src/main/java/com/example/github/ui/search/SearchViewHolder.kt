package com.example.github.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.example.github.databinding.ViewListLoadingRowBinding
import com.example.github.databinding.ViewUserSearchRowBinding
import com.example.github.ui.ViewHolder

sealed class SearchViewHolder<T : ViewDataBinding>(binding: T) : ViewHolder<T>(binding) {

    class UserSearchRowViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        root: Boolean
    ) : SearchViewHolder<ViewUserSearchRowBinding>(
        ViewUserSearchRowBinding.inflate(inflater, parent, root)
    ) {
        fun bind(item: SearchViewItem.UserSearchRowItem) {
            binding.item = item
            binding.executePendingBindings()
        }
    }

    class LoadingViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        root: Boolean
    ) : SearchViewHolder<ViewListLoadingRowBinding>(
        ViewListLoadingRowBinding.inflate(inflater, parent, root)
    )
}
