package com.example.github.repository.search.user

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.github.api.common.ApiConfig
import com.example.github.api.search.user.UserApi
import com.example.github.entity.User
import com.example.github.paging.UserPagingSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Retrofit

class ApiUserRepository(retrofit: Retrofit) : UserRepository {

    private val api = retrofit.create(UserApi::class.java)

    override fun search(query: String): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = ApiConfig.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                UserPagingSource(api, query)
            }
        ).flow
    }
}