package com.example.chatapp.myChat

import androidx.test.platform.app.InstrumentationRegistry
import com.example.chatapp.R
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class ResourceComparerTest {
    private lateinit var resourceComparer: ResourceComparer

    @Before
    fun setUp() {
        resourceComparer = ResourceComparer()
    }

    @After
    fun tearDown() {
        //TODO
    }

    @Test
    fun resourceComparisonReturnsTrue() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val result = resourceComparer.isEqual(context, R.id.chat_title, "Chat Title")
        assertThat(result).isTrue()
    }
}