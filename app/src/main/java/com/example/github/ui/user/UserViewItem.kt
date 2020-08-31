package com.example.github.ui.user

import com.example.github.entity.Repo
import com.example.github.entity.User

sealed class UserViewItem {

    abstract val id: Int
    abstract val viewType: UserViewType

    data class UserRowItem(
        val user: User
    ) : UserViewItem() {
        val login: String
            get() = user.login
        val imageUrl: String
            get() = user.imageUrl
        val bio: String?
            get() = user.bio
        val blog: String?
            get() = user.blog
        val followers: Int
            get() = user.followers
        val following: Int
            get() = user.following

        override val id: Int = user.id
        override val viewType: UserViewType = UserViewType.UserRow
    }

    data class UserRepoRowItem(
        val repo: Repo
    ) : UserViewItem() {
        val name: String
            get() = repo.name
        val description: String?
            get() = repo.description

        override val id: Int = repo.id
        override val viewType: UserViewType = UserViewType.UserRepoRow
    }
}
