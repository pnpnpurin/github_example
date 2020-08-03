package com.example.github.ui.search.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.github.entity.User
import com.example.github.entity.common.PagingList
import com.example.github.entity.common.Result
import com.example.github.repository.search.user.UserRepository
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class UserSearchViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _load = BroadcastChannel<Unit>(Channel.BUFFERED)
    private val load = _load.asFlow()

    private val _query = MutableStateFlow("")
    val query: Flow<String> = _query

    private val response = MutableStateFlow<Result<PagingList<User>>>(Result.Ready)
    val loading = response.map { it.isLoading }
    val error = response.map { it.errorOrNull }
    val data = response.map { it.valueOrNull?.entities.orEmpty() }

    init {
        load.map { _query.value }
            .flatMapConcat { repository.search(it, 1, 100) }
            .flowOn(Dispatchers.IO)
            .onEach { response.value = it }
            .launchIn(viewModelScope)
    }

    fun search() {
        viewModelScope.launch {
            response.value = Result.Loading
            _load.send(Unit)
        }
    }

    fun reload() {
        viewModelScope.launch {
            response.value = Result.Loading
            _load.send(Unit)
        }
    }

    fun setQuery(query: String) {
        viewModelScope.launch {
            _query.value = query
        }
    }
}