package com.example.github.view.binding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("glide_imageUri", "glide_placeholder", requireAll = false)
fun ImageView.setImageUri(uriString: String?, resource: Drawable) {
    Glide.with(this)
        .load(uriString)
        .placeholder(resource)
        .into(this)
}
