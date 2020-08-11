package com.example.github.ui.search

import com.example.github.entity.User

sealed class SearchViewItem {

    abstract val viewType: SearchViewType

    data class UserSearchRowItem(
        val user: User
    ) : SearchViewItem() {
        val login: String
            get() = user.login
        val imageUrl: String?
            get() = user.imageUrl

        override val viewType: SearchViewType = SearchViewType.UserSearchRow
    }

    object LoadingItem : SearchViewItem() {
        override val viewType: SearchViewType = SearchViewType.LoadingRow
    }
}
