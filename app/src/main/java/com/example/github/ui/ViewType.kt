package com.example.github.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import kotlin.reflect.KFunction

abstract class ViewType<VH : ViewHolder<*>>(
    private val viewHolder: KFunction<VH>
) {
    fun createViewHolder(inflater: LayoutInflater, parent: ViewGroup, root: Boolean): VH {
        return viewHolder.parameters.mapNotNull {
            when (it.type.classifier) {
                LayoutInflater::class -> it to inflater
                ViewGroup::class -> it to parent
                Boolean::class -> it to root
                else -> null
            }
        }.toMap()
            .let {
                viewHolder.callBy(it)
            }
    }
}
