package com.example.dailyreportscheduler.data

import android.content.Context
import com.example.dailyreportscheduler.model.ReportVersion

private const val PREFS_NAME = "draft_prefs"
private const val KEY_BATCH  = "batch"
private const val KEY_DATE   = "date"
private const val KEY_DAY    = "day"
private const val KEY_S1     = "s1"
private const val KEY_S2     = "s2"
private const val KEY_S3     = "s3"
private const val KEY_S4     = "s4"
private const val KEY_S5     = "s5"

object DraftPrefs {

    /** Persist all form fields to SharedPreferences (called on every field change). */
    fun saveDraft(
        context: Context,
        batch: String,
        date: String,
        day: String,
        s1: String,
        s2: String,
        s3: String,
        s4: String,
        s5: String
    ) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_BATCH, batch)
            .putString(KEY_DATE, date)
            .putString(KEY_DAY, day)
            .putString(KEY_S1, s1)
            .putString(KEY_S2, s2)
            .putString(KEY_S3, s3)
            .putString(KEY_S4, s4)
            .putString(KEY_S5, s5)
            .apply()
    }

    /**
     * Load the last auto-saved draft.
     * Returns a [ReportVersion] with default values if no draft was ever saved.
     */
    fun loadDraft(context: Context): ReportVersion {
        val p = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return ReportVersion(
            id      = "draft",
            savedAt = System.currentTimeMillis(),
            batch   = p.getString(KEY_BATCH, "Feb 2026") ?: "Feb 2026",
            date    = p.getString(KEY_DATE, "") ?: "",
            day     = p.getString(KEY_DAY, "") ?: "",
            s1      = p.getString(KEY_S1, "") ?: "",
            s2      = p.getString(KEY_S2, "") ?: "",
            s3      = p.getString(KEY_S3, "") ?: "",
            s4      = p.getString(KEY_S4, "") ?: "",
            s5      = p.getString(KEY_S5, "") ?: ""
        )
    }
}
