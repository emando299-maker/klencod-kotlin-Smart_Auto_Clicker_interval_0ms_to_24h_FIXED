
package com.smartautoclicker.app

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AutoClickService : AccessibilityService() {

    private var lastClickTime = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val now = System.currentTimeMillis()
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val clickDelay = prefs.getLong("interval_ms", 1000L).coerceIn(0L, 86_400_000L)
        if (now - lastClickTime < clickDelay) return

        val word = prefs.getString("word", "") ?: ""

        if (word.isBlank()) return

        val root = rootInActiveWindow ?: return

        if (searchAndClick(root, word)) {
            lastClickTime = now
        }
    }

    private fun searchAndClick(
        node: AccessibilityNodeInfo,
        word: String
    ): Boolean {
        val text = node.text?.toString().orEmpty()
        val description = node.contentDescription?.toString().orEmpty()

        if (
            text.contains(word, ignoreCase = true) ||
            description.contains(word, ignoreCase = true)
        ) {
            var target: AccessibilityNodeInfo? = node
            var depth = 0

            while (target != null && depth < 10) {
                if (target.isClickable) {
                    return target.performAction(
                        AccessibilityNodeInfo.ACTION_CLICK
                    )
                }
                target = target.parent
                depth++
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (searchAndClick(child, word)) return true
        }

        return false
    }

    override fun onInterrupt() {}
}