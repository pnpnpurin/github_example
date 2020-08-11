package com.example.github.ui.search.user

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.search, menu)

        val searchMenu = menu?.findItem(R.id.search_menu)
        (searchMenu?.actionView as? SearchView)?.also {
            it.queryHint = getString(R.string.search)
            it.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!query.isNullOrEmpty()) {
                        viewModel.setQuery(query)
                        it.clearFocus()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
        }

        return true
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
            adapter.updateUserList(it, false)
        }
        bind(viewModel.error) {
            it?.printStackTrace()
        }
        bind(viewModel.loading) {
            if (it) {
                adapter.updateUserList(listOf(), true)
                Logger.w("loading...")
            }
        }
        bind(viewModel.query) {
            if (!it.isNullOrEmpty()) viewModel.search()
        }
    }
}
