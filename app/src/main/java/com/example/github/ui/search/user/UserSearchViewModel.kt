package com.example.github.ui.search.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.github.entity.User
import com.example.github.repository.search.user.SearchUsersRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class UserSearchViewModel(
    private val repository: SearchUsersRepository
) : ViewModel() {

    private val _query = ConflatedBroadcastChannel("")
    val query: Flow<String> = _query.asFlow()

    fun search(query: String): Flow<PagingData<User>> {
        return repository.fetch(query)
            .cachedIn(viewModelScope)
    }

    fun setQuery(query: String) {
        viewModelScope.launch {
            _query.send(query)
        }
    }
}