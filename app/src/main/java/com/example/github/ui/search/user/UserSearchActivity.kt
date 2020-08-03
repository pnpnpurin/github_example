package com.example.github.ui.search.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.github.R
import com.example.github.databinding.ActivityUserSearchBinding
import com.example.github.ui.bind
import com.orhanobut.logger.Logger
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserSearchBinding
    private lateinit var adapter: UserSearchAdapter

    private val viewModel by viewModel<UserSearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_search)
        binding.lifecycleOwner = this

        initView()
        bindViewModelEvents()
    }

    private fun initView() {
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        adapter = UserSearchAdapter(this)

        binding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
            it.addItemDecoration(itemDecoration)
        }

        binding.refreshLayout.setOnRefreshListener {
            viewModel.reload()
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun bindViewModelEvents() {
        bind(viewModel.data) {
            adapter.updateUserList(it)
        }
        bind(viewModel.error) {
            it?.printStackTrace()
        }
        bind(viewModel.loading) {
            if (it) Logger.w("loading...")
        }
        bind(viewModel.query) {
            if (!it.isNullOrEmpty()) viewModel.search()
        }
        viewModel.setQuery("abc")
    }
}
