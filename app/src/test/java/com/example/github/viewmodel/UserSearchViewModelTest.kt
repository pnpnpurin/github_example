package com.example.github.viewmodel

import androidx.paging.*
import com.example.github.TestCoroutineRule
import com.example.github.paging.UserPagingSource
import com.example.github.repository.search.user.ApiSearchUsersRepository
import com.example.github.ui.search.user.UserSearchViewModel
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test

@FlowPreview
@ExperimentalCoroutinesApi
class UserSearchViewModelTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Test
    fun `when search call successfully then it should user entity is set to PagingData`() {
        val source = mock<UserPagingSource>()
        val repository = mock<ApiSearchUsersRepository> {
            on { fetch("abcde") } doReturn Pager(
                config = PagingConfig(pageSize = 1, enablePlaceholders = false),
                pagingSourceFactory = { source }
            ).flow
        }

        val viewmodel = UserSearchViewModel(repository)
        testCoroutineRule.runBlockingTest {
            val result = viewmodel.search("abcde").first()
            assertThat(result).isInstanceOf(PagingData::class.java)
        }
    }

    @Test
    fun `when call setQuery after subscribe query then it should can get query parameter`() {
        val repository = mock<ApiSearchUsersRepository>()
        val viewmodel = UserSearchViewModel(repository)

        testCoroutineRule.runBlockingTest {
            viewmodel.setQuery("abcde")
            val job = launch {
                viewmodel.query
                    .filter { it.isNotEmpty() }
                    .collect {
                        assertThat(it).isEqualTo("abcde")
                    }
            }
            job.cancel()
        }
    }
}
