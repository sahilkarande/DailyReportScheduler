package com.example.dailyreportscheduler.model

data class ReportVersion(
    val id: String,
    val savedAt: Long,
    val batch: String,
    val date: String,
    val day: String,
    val s1: String,
    val s2: String,
    val s3: String,
    val s4: String,
    val s5: String
)
