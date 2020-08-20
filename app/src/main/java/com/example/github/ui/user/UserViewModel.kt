package com.example.github.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.github.entity.common.Result
import com.example.github.entity.User
import com.example.github.repository.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class UserViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private val _load = BroadcastChannel<Unit>(Channel.BUFFERED)
    private val load = _load.asFlow()

    private val _username = MutableStateFlow("")
    val username: Flow<String> = _username

    private val response = MutableStateFlow<Result<User>>(Result.Ready)
    val loading = response.map { it.isLoading }
    val error = response.map { it.errorOrNull }
    val data = response.map { it.valueOrNull }

    init {
        load.map { _username.value }
            .flatMapConcat { repository.fetch(it) }
            .flowOn(Dispatchers.IO)
            .onEach { response.value = it }
            .launchIn(viewModelScope)
    }

    fun fetch() {
        viewModelScope.launch {
            response.value = Result.Loading
            _load.send(Unit)
        }
    }

    fun setUserName(username: String) {
        viewModelScope.launch {
            _username.value = username
        }
    }
}
