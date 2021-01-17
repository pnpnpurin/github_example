package com.example.github.ui.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.github.api.common.NoResultException
import com.example.github.entity.Repo
import com.example.github.entity.common.Result
import com.example.github.entity.User
import com.example.github.repository.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class UserViewModel(
    username: String,
    repository: UserRepository
) : ViewModel() {

    private val response = MutableStateFlow<Result<Pair<User, List<Repo>?>>>(Result.Ready)
    val loading = response.map { it.isLoading }
    val error = response.map { it.errorOrNull }
    val data = response.map { it.valueOrNull }

    init {
        repository.fetch(username)
            .zip(repository.repos(username)) { user, repos ->
                user.valueOrNull?.let { u ->
                    Result.Success(Pair(u, repos.valueOrNull))
                } ?: run {
                    val exception = user.errorOrNull ?: NoResultException()
                    Result.Error(exception)
                }
            }
            .flowOn(Dispatchers.IO)
            .onEach { response.value = it }
            .launchIn(viewModelScope)
    }
}
