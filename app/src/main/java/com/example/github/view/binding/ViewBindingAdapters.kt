package com.example.github.view.binding

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.BindingAdapter

@BindingAdapter("visibilityGoneIfNullOrEmpty")
fun View.setVisibilityGoneIfNullOrEmpty(text: CharSequence?) {
    visibility = if (text.isNullOrEmpty()) GONE else VISIBLE
}