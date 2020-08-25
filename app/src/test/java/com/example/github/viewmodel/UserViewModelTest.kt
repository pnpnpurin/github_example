package com.example.github.viewmodel

import com.example.github.TestCoroutineRule
import com.example.github.entity.User
import com.example.github.entity.common.Result
import com.example.github.repository.user.ApiUserRepository
import com.example.github.ui.user.UserViewModel
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
class UserViewModelTest {
    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Test
    fun `when call setUserName after subscribe username then it should can get username`() = testCoroutineRule.runBlockingTest {
        val repository = mock<ApiUserRepository>()

        val viewmodel = UserViewModel(repository)
        viewmodel.setUserName("abcde")
        val job = launch {
            viewmodel.username
                .collect {
                    assertThat(it).isEqualTo("abcde")
                }
        }
        job.cancel()
    }

    @Test
    fun `when fetch call successfully then it should user entity is set to Result#Success`() = testCoroutineRule.runBlockingTest {
        val repository = mock<ApiUserRepository> {
            on { fetch("abcde") } doReturn flow { emit(Result.Success(mockUser)) }
        }

        val viewmodel = UserViewModel(repository)
        viewmodel.setUserName("abcde")
        viewmodel.fetch()
        val job = launch {
            viewmodel.data
                .filterNotNull()
                .collect {
                    assertThat(it).isInstanceOf(User::class.java)
                    assertThat(it.login).isEqualTo("octocat")
                }
        }
        job.cancel()
    }

    private val mockUser = User(1, "octocat", "https://github.com/images/error/octocat_happy.gif")
}
