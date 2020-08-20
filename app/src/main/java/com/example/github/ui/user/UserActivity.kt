package com.example.github.ui.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.github.R
import com.example.github.databinding.ActivityUserBinding
import com.example.github.ui.bind
import com.orhanobut.logger.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding

    private val viewModel by viewModel<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user)
        binding.lifecycleOwner = this

        initView()
        bindViewModelEvents()

        intent.getStringExtra(USER_INTENT_KEY)?.let {
            viewModel.setUserName(it)
        } ?: run {
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initView() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun bindViewModelEvents() {
        bind(viewModel.data) {
            binding.user = it
        }
        bind(viewModel.error) {
            it?.printStackTrace()
        }
        bind(viewModel.loading) {
            if (it) Logger.w("loading...")
        }
        bind(viewModel.username) {
            if (!it.isNullOrEmpty()) viewModel.fetch()
        }
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
