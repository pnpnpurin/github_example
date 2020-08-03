package com.example.github.ui

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolder<T : ViewDataBinding>(protected val binding: T) :
    RecyclerView.ViewHolder(binding.root) {
    val view: View
        get() = binding.root
}
