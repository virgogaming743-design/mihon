package eu.kanade.presentation.components

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object GlassSettings {
    private const val PREFS_NAME = "liquid_glass"
    private const val KEY_ENABLED = "enabled"
    private var loaded = false

    var enabled by mutableStateOf(false)
        private set

    fun load(context: Context) {
        if (loaded) return
        loaded = true
        enabled = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, false)
    }

    fun set(context: Context, value: Boolean) {
        loaded = true
        enabled = value
        context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ENABLED, value)
            .apply()
    }
}
