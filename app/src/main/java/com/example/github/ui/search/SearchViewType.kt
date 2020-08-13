package com.example.github.ui.search

import androidx.annotation.IdRes
import com.example.github.R
import com.example.github.ui.ViewType
import com.example.github.ui.search.SearchViewHolder.UserSearchRowViewHolder
import kotlin.reflect.KFunction

sealed class SearchViewType(
    @IdRes val id: Int,
    viewHolder: KFunction<SearchViewHolder<*>>
) : ViewType<SearchViewHolder<*>>(viewHolder) {

    companion object {
        fun of(id: Int): SearchViewType {
            return SearchViewType::class.nestedClasses.mapNotNull {
                it.objectInstance as? SearchViewType
            }.first { it.id == id }
        }
    }

    object UserSearchRow : SearchViewType(
        R.id.view_type_user_search_row,
        ::UserSearchRowViewHolder
    )
}
