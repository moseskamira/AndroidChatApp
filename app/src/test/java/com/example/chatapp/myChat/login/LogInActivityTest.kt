package com.example.chatapp.myChat.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LogInActivityTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var loginActivity: LogInActivity
    private lateinit var emptyString: String

    @Before
    fun setUP() {
        emptyString = ""
        loginActivity = LogInActivity()
    }

    @Test
    fun `empty PhoneNumber field returns false`() {
        val status = loginActivity.startPhoneNumberVerification(emptyString)
        assertThat(status).isFalse()
    }

}