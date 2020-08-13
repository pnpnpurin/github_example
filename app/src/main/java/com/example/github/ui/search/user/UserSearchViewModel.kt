package com.example.github.ui.search.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.github.entity.User
import com.example.github.repository.search.user.UserRepository
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class UserSearchViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _load = BroadcastChannel<Unit>(Channel.BUFFERED)
    private val load = _load.asFlow()

    private val _query = MutableStateFlow("")
    val query: Flow<String> = _query

    private val response = MutableStateFlow<PagingData<User>>(PagingData.empty())
    val data = response.map { it }

    init {
        load.map { _query.value }
            .flatMapConcat { repository.search(it) }
            .flowOn(Dispatchers.IO)
            .onEach { response.value = it }
            .launchIn(viewModelScope)
    }

    fun search() {
        viewModelScope.launch {
            _load.send(Unit)
        }
    }

    fun setQuery(query: String) {
        viewModelScope.launch {
            _query.value = query
        }
    }
}