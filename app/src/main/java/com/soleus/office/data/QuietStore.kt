package com.soleus.office.data

import android.content.Context

/** Sessiz saat tercihi (SharedPreferences; tasarım varsayılanı 21:00 -> 08:00, açık). */
data class QuietPrefs(
    val enabled: Boolean = true,
    val startMin: Int = 21 * 60,
    val endMin: Int = 8 * 60
)

object QuietStore {
    private const val DOSYA = "soleus"
    private const val KEY_ENABLED = "quiet_enabled"
    private const val KEY_START = "quiet_start_min"
    private const val KEY_END = "quiet_end_min"

    fun load(context: Context): QuietPrefs {
        val p = context.getSharedPreferences(DOSYA, Context.MODE_PRIVATE)
        return QuietPrefs(
            enabled = p.getBoolean(KEY_ENABLED, true),
            startMin = p.getInt(KEY_START, 21 * 60),
            endMin = p.getInt(KEY_END, 8 * 60)
        )
    }

    fun save(context: Context, prefs: QuietPrefs) {
        context.getSharedPreferences(DOSYA, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_ENABLED, prefs.enabled)
            .putInt(KEY_START, prefs.startMin)
            .putInt(KEY_END, prefs.endMin)
            .apply()
    }
}
