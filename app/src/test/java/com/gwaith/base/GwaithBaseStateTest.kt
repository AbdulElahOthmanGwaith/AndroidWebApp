package com.gwaith.base

import com.gwaith.base.ui.WebViewState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * اختبارات WebViewState
 * WebViewState Tests
 */
class WebViewStateTest {

    @Test
    fun `الحالة الافتراضية لها قيم صحيحة`() {
        val state = WebViewState()

        assertEquals("https://example.com", state.url)
        assertEquals("", state.title)
        assertTrue(state.isLoading)
        assertNull(state.error)
        assertTrue(state.notificationsEnabled)
        assertTrue(state.javaScriptEnabled)
        assertTrue(state.domStorageEnabled)
        assertFalse(state.showCloseButton)
    }

    @Test
    fun `نسخ الحالة يعمل بشكل صحيح`() {
        val state = WebViewState()
        val updatedState = state.copy(
            url = "https://new-url.com",
            title = "Test Title",
            isLoading = false
        )

        assertEquals("https://new-url.com", updatedState.url)
        assertEquals("Test Title", updatedState.title)
        assertFalse(updatedState.isLoading)
        assertTrue(updatedState.notificationsEnabled)
    }

    @Test
    fun `حالة الخطأ يتم تعيينها بشكل صحيح`() {
        val state = WebViewState()
        val errorState = state.copy(
            error = "Network error",
            isLoading = false
        )

        assertEquals("Network error", errorState.error)
        assertFalse(errorState.isLoading)
    }

    @Test
    fun `تبديل الإشعارات يعمل`() {
        val state = WebViewState()
        val disabledState = state.copy(notificationsEnabled = false)

        assertFalse(disabledState.notificationsEnabled)
        assertTrue(state.notificationsEnabled)
    }

    @Test
    fun `تبديل JavaScript يعمل`() {
        val state = WebViewState()
        val disabledState = state.copy(javaScriptEnabled = false)

        assertFalse(disabledState.javaScriptEnabled)
        assertTrue(state.javaScriptEnabled)
    }

    @Test
    fun `إظهار زر الإغلاق`() {
        val state = WebViewState()
        val withCloseButton = state.copy(showCloseButton = true)

        assertTrue(withCloseButton.showCloseButton)
        assertFalse(state.showCloseButton)
    }
}
