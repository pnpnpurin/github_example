package com.example.github.ui.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.github.R
import com.example.github.databinding.ActivityUserBinding
import com.example.github.ui.bind
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@FlowPreview
@ExperimentalCoroutinesApi
class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var adapter: UserAdapter

    private val viewModel by viewModel<UserViewModel> { parametersOf(intent.getStringExtra(USER_INTENT_KEY)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user)
        binding.lifecycleOwner = this

        initView()
        bindViewModelEvents()
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

        adapter = UserAdapter(this)
        binding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
        }
    }

    private fun bindViewModelEvents() {
        bind(viewModel.data) {
            binding.progress.visibility = View.GONE
            it?.let { result ->
                adapter.submitList(result.first, result.second)
            }
        }
        bind(viewModel.error) {
            binding.progress.visibility = View.GONE
            it?.let { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                exception.printStackTrace()
            }
        }
        bind(viewModel.loading) {
            if (it) {
                binding.progress.visibility = View.VISIBLE
            }
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
