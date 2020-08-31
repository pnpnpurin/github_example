package com.example.github.ui.user

import androidx.annotation.IdRes
import com.example.github.R
import com.example.github.ui.ViewType
import com.example.github.ui.user.UserViewHolder.UserRepoRowViewHolder
import com.example.github.ui.user.UserViewHolder.UserRowViewHolder
import kotlin.reflect.KFunction

sealed class UserViewType(
    @IdRes val id: Int,
    viewHolder: KFunction<UserViewHolder<*>>
) : ViewType<UserViewHolder<*>>(viewHolder) {

    companion object {
        fun of(id: Int): UserViewType {
            return UserViewType::class.nestedClasses.mapNotNull {
                it.objectInstance as? UserViewType
            }.first { it.id == id }
        }
    }

    object UserRow : UserViewType(
        R.id.view_type_user_row,
        ::UserRowViewHolder
    )

    object UserRepoRow : UserViewType(
        R.id.view_type_user_repo_row,
        ::UserRepoRowViewHolder
    )
}
