package com.example.github.ui.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.github.R
import com.example.github.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user)
    }

    companion object {
        private const val USER_INTENT_KEY = "USER_INTENT_KEY"

        fun createIntent(context: Context, username: String): Intent {
            return Intent(context, UserActivity::class.java).also {
                it.putExtra(USER_INTENT_KEY, username)
            }
        }
    }
}