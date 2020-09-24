package com.example.github.ui.search.user

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.github.R
import com.example.github.api.common.NoResultException
import com.example.github.databinding.ActivityUserSearchBinding
import com.example.github.ui.search.SearchLoadStateAdapter
import com.example.github.ui.user.UserActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
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
            it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
        adapter = UserSearchAdapter(this) {
            val intent = UserActivity.createIntent(this, it.login)
            startActivity(intent)
        }

        binding.recyclerView.also {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter.withLoadStateFooter(SearchLoadStateAdapter(::retry))
            it.addItemDecoration(itemDecoration)
        }

        adapter.addLoadStateListener { loadState ->
            binding.recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
            binding.progress.isVisible = loadState.source.refresh is LoadState.Loading
            binding.retryButton.isVisible = false
            binding.emptyList.isVisible = false

            val initialErrorState = loadState.source.refresh as? LoadState.Error
            initialErrorState?.let {
                when (it.error) {
                    is NoResultException -> binding.emptyList.isVisible = true
                    else -> binding.retryButton.isVisible = true
                }
                it.error.printStackTrace()
            }

            val appendErrorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            appendErrorState?.let {
                Toast.makeText(this, "Error : ${it.error}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.refreshLayout.setOnRefreshListener {
            adapter.refresh()
            binding.refreshLayout.isRefreshing = false
        }

        lifecycleScope.launch {
            adapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect { binding.recyclerView.scrollToPosition(0) }
        }
    }

    private fun bindViewModelEvents() {
        lifecycleScope.launch {
            viewModel.query
                .filter { it.isNotEmpty() }
                .collectLatest { query ->
                    viewModel.search(query).collectLatest { result ->
                        adapter.submitData(result)
                    }
                }
        }
    }

    private fun retry() {
        adapter.retry()
    }
}
