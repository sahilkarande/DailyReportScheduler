package com.example.dailyreportscheduler.utils

fun generateReport(
    batch: String,
    date: String,
    day: String,
    s1: String,
    s2: String,
    s3: String,
    s4: String,
    s5: String
): String {

    return """
PGCP-BDA Daily Report 
Batch: $batch
$date, $day
---------------------------------
09:00 AM to 11:00 AM: $s1

11:15 AM to 01:15 PM: $s2

02:00 PM to 04:00 PM: $s3 

04:30 PM to 06:30 PM: $s4

06:30 PM to 07:30 PM: $s5
    """.trimIndent()
}