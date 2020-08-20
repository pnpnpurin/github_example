package com.example.github.view.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("glide_imageUri")
fun ImageView.setImageUri(uriString: String?) {
    Glide.with(this)
        .load(uriString)
        .into(this)
}
