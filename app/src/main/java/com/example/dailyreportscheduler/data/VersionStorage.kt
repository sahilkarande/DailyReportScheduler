package com.example.dailyreportscheduler.data

import android.content.Context
import android.content.SharedPreferences
import com.example.dailyreportscheduler.model.ReportVersion
import org.json.JSONArray
import org.json.JSONObject

private const val PREFS_NAME   = "version_storage"
private const val KEY_VERSIONS = "versions"
private const val MAX_VERSIONS = 50

object VersionStorage {

    /**
     * Prepend a new [version] snapshot to the stored list.
     * Silently skips saving if all fields are blank (nothing to store).
     * Auto-trims the list to [MAX_VERSIONS] entries.
     */
    fun saveVersion(context: Context, version: ReportVersion) {
        // Don't store a completely empty form
        if (version.batch.isBlank() && version.date.isBlank() &&
            version.s1.isBlank() && version.s2.isBlank()
        ) return

        val prefs    = prefs(context)
        val existing = loadRaw(prefs)

        val newEntry = JSONObject().apply {
            put("id",      version.id)
            put("savedAt", version.savedAt)
            put("batch",   version.batch)
            put("date",    version.date)
            put("day",     version.day)
            put("s1",      version.s1)
            put("s2",      version.s2)
            put("s3",      version.s3)
            put("s4",      version.s4)
            put("s5",      version.s5)
        }

        // Build updated array: new entry first, then up to MAX_VERSIONS-1 old ones
        val updated = JSONArray()
        updated.put(newEntry)
        for (i in 0 until minOf(existing.length(), MAX_VERSIONS - 1)) {
            updated.put(existing.getJSONObject(i))
        }

        prefs.edit().putString(KEY_VERSIONS, updated.toString()).apply()
    }

    /** Return all saved versions in reverse-chronological order (newest first). */
    fun loadVersions(context: Context): List<ReportVersion> {
        val arr  = loadRaw(prefs(context))
        val list = mutableListOf<ReportVersion>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(
                ReportVersion(
                    id      = o.getString("id"),
                    savedAt = o.getLong("savedAt"),
                    batch   = o.optString("batch"),
                    date    = o.optString("date"),
                    day     = o.optString("day"),
                    s1      = o.optString("s1"),
                    s2      = o.optString("s2"),
                    s3      = o.optString("s3"),
                    s4      = o.optString("s4"),
                    s5      = o.optString("s5")
                )
            )
        }
        return list
    }

    /** Remove a single version by its [id]. */
    fun deleteVersion(context: Context, id: String) {
        val prefs   = prefs(context)
        val arr     = loadRaw(prefs)
        val updated = JSONArray()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            if (o.getString("id") != id) updated.put(o)
        }
        prefs.edit().putString(KEY_VERSIONS, updated.toString()).apply()
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun loadRaw(prefs: SharedPreferences): JSONArray {
        val json = prefs.getString(KEY_VERSIONS, "[]") ?: "[]"
        return try { JSONArray(json) } catch (_: Exception) { JSONArray() }
    }
}
