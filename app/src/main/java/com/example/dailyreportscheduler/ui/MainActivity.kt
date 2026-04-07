package com.example.dailyreportscheduler.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.dailyreportscheduler.data.DraftPrefs
import com.example.dailyreportscheduler.data.VersionStorage
import com.example.dailyreportscheduler.model.ReportVersion
import com.example.dailyreportscheduler.ui.theme.DailyReportSchedulerTheme
import com.example.dailyreportscheduler.utils.generateReport
import java.text.SimpleDateFormat
import java.util.*

// ── Activity ─────────────────────────────────────────────────────────────────

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyReportSchedulerTheme {
                Surface { AppRoot() }
            }
        }
    }
}

// ── Root composable — holds all shared state ─────────────────────────────────

@Composable
fun AppRoot() {
    val context = LocalContext.current

    // Load the auto-saved draft once on first composition
    val savedDraft = remember { DraftPrefs.loadDraft(context) }

    var batch by remember { mutableStateOf(savedDraft.batch) }
    var date  by remember { mutableStateOf(savedDraft.date) }
    var day   by remember { mutableStateOf(savedDraft.day) }
    var s1    by remember { mutableStateOf(savedDraft.s1) }
    var s2    by remember { mutableStateOf(savedDraft.s2) }
    var s3    by remember { mutableStateOf(savedDraft.s3) }
    var s4    by remember { mutableStateOf(savedDraft.s4) }
    var s5    by remember { mutableStateOf(savedDraft.s5) }

    var showHistory by remember { mutableStateOf(false) }

    // ── Auto-save draft on every field change ─────────────────────────────────
    LaunchedEffect(batch, date, day, s1, s2, s3, s4, s5) {
        DraftPrefs.saveDraft(context, batch, date, day, s1, s2, s3, s4, s5)
    }

    // ── Auto-save a version snapshot when app goes to background (ON_STOP) ───
    val lifecycleOwner = LocalLifecycleOwner.current
    // rememberUpdatedState ensures the observer always sees the latest values
    val latestBatch by rememberUpdatedState(batch)
    val latestDate  by rememberUpdatedState(date)
    val latestDay   by rememberUpdatedState(day)
    val latestS1    by rememberUpdatedState(s1)
    val latestS2    by rememberUpdatedState(s2)
    val latestS3    by rememberUpdatedState(s3)
    val latestS4    by rememberUpdatedState(s4)
    val latestS5    by rememberUpdatedState(s5)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                VersionStorage.saveVersion(
                    context,
                    ReportVersion(
                        id      = UUID.randomUUID().toString(),
                        savedAt = System.currentTimeMillis(),
                        batch   = latestBatch,
                        date    = latestDate,
                        day     = latestDay,
                        s1      = latestS1,
                        s2      = latestS2,
                        s3      = latestS3,
                        s4      = latestS4,
                        s5      = latestS5
                    )
                )
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // ── Manual "Save Version" action ─────────────────────────────────────────
    val onSaveVersionManual: () -> Unit = {
        VersionStorage.saveVersion(
            context,
            ReportVersion(
                id      = UUID.randomUUID().toString(),
                savedAt = System.currentTimeMillis(),
                batch   = batch,
                date    = date,
                day     = day,
                s1      = s1,
                s2      = s2,
                s3      = s3,
                s4      = s4,
                s5      = s5
            )
        )
        Toast.makeText(context, "✅  Version saved!", Toast.LENGTH_SHORT).show()
    }

    // ── Navigation ────────────────────────────────────────────────────────────
    if (showHistory) {
        HistoryScreen(
            onBack    = { showHistory = false },
            onRestore = { v ->
                batch = v.batch
                date  = v.date
                day   = v.day
                s1    = v.s1
                s2    = v.s2
                s3    = v.s3
                s4    = v.s4
                s5    = v.s5
            }
        )
    } else {
        HomeScreen(
            batch         = batch,
            date          = date,
            day           = day,
            s1            = s1,
            s2            = s2,
            s3            = s3,
            s4            = s4,
            s5            = s5,
            onBatchChange = { batch = it },
            onDateChange  = { date = it },
            onDayChange   = { day = it },
            onS1Change    = { s1 = it },
            onS2Change    = { s2 = it },
            onS3Change    = { s3 = it },
            onS4Change    = { s4 = it },
            onS5Change    = { s5 = it },
            onShowHistory = { showHistory = true },
            onSaveVersion = onSaveVersionManual,
            onClear       = {
                batch = ""
                date  = ""
                day   = ""
                s1    = ""
                s2    = ""
                s3    = ""
                s4    = ""
                s5    = ""
            }
        )
    }
}

// ── Home screen ───────────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    batch: String, date: String, day: String,
    s1: String, s2: String, s3: String, s4: String, s5: String,
    onBatchChange: (String) -> Unit,
    onDateChange:  (String) -> Unit,
    onDayChange:   (String) -> Unit,
    onS1Change:    (String) -> Unit,
    onS2Change:    (String) -> Unit,
    onS3Change:    (String) -> Unit,
    onS4Change:    (String) -> Unit,
    onS5Change:    (String) -> Unit,
    onShowHistory: () -> Unit,
    onSaveVersion: () -> Unit,
    onClear:       () -> Unit
) {
    var output  by remember { mutableStateOf("") }
    val context = LocalContext.current

    val bgGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 48.dp)
        ) {

            // ── Header ───────────────────────────────────────────────────────
            Row(
                modifier          = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = "Daily Report",
                        fontSize   = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                    Text(
                        text     = "Fill in your details below",
                        fontSize = 14.sp,
                        color    = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                // History icon button
                IconButton(onClick = onShowHistory) {
                    Icon(
                        imageVector        = Icons.Default.History,
                        contentDescription = "View history",
                        tint               = Color.White.copy(alpha = 0.75f),
                        modifier           = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // ── Card 1: Basic Info ────────────────────────────────────────────
            SectionLabel("Basic Info")
            Spacer(Modifier.height(10.dp))

            Card(
                shape    = RoundedCornerShape(18.dp),
                colors   = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.07f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value         = batch,
                        onValueChange = onBatchChange,
                        label         = { Text("Batch") },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        colors        = fieldColors()
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val cal = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val selected = "$dayOfMonth/${month + 1}/$year"
                                    onDateChange(selected)
                                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    val d   = sdf.parse(selected)
                                    onDayChange(SimpleDateFormat("EEEE", Locale.getDefault()).format(d!!))
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape  = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE94560),
                            contentColor   = Color.White
                        )
                    ) {
                        Text(
                            text       = if (date.isEmpty()) "Select Date" else date,
                            fontWeight = FontWeight.SemiBold,
                            fontSize   = 15.sp
                        )
                    }

                    if (day.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text     = "Day: $day",
                            color    = Color.White.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Card 2: Schedules ─────────────────────────────────────────────
            SectionLabel("Schedules")
            Spacer(Modifier.height(10.dp))

            Card(
                shape    = RoundedCornerShape(18.dp),
                colors   = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.07f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    ScheduleField("Schedule 1  (09:00 – 11:00 AM)", s1, onS1Change)
                    ScheduleField("Schedule 2  (11:15 AM – 01:15 PM)", s2, onS2Change)
                    ScheduleField("Schedule 3  (02:00 – 04:00 PM)", s3, onS3Change)
                    ScheduleField("Schedule 4  (04:30 – 06:30 PM)", s4, onS4Change)
                    ScheduleField("Schedule 5  (06:30 – 07:30 PM)", s5, onS5Change)

                    Spacer(Modifier.height(12.dp))

                    // Generate Report
                    Button(
                        onClick  = { output = generateReport(batch, date, day, s1, s2, s3, s4, s5) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape  = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE94560),
                            contentColor   = Color.White
                        )
                    ) {
                        Text(text = "Generate Report", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                    Spacer(Modifier.height(10.dp))

                    // Save Version + Clear Form
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick  = onSaveVersion,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape  = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE94560)),
                            border = BorderStroke(1.dp, Color(0xFFE94560).copy(alpha = 0.5f))
                        ) {
                            Text("💾  Save Version", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick  = onClear,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape  = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White.copy(alpha = 0.6f)),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                        ) {
                            Text("🗑  Clear Form", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Card 3: Output ────────────────────────────────────────────────
            SectionLabel("Output")
            Spacer(Modifier.height(10.dp))

            Card(
                shape    = RoundedCornerShape(18.dp),
                colors   = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.07f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value         = output,
                        onValueChange = { output = it },
                        modifier      = Modifier.fillMaxWidth(),
                        minLines      = 8,
                        shape         = RoundedCornerShape(12.dp),
                        colors        = fieldColors()
                    )

                    // Live word & char count
                    if (output.isNotBlank()) {
                        val words = output.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
                        val chars = output.length
                        Text(
                            text      = "$words words · $chars characters",
                            fontSize  = 11.sp,
                            color     = Color.White.copy(alpha = 0.32f),
                            textAlign = TextAlign.End,
                            modifier  = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Copy button
                        Button(
                            onClick = {
                                val mgr = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                                    as android.content.ClipboardManager
                                mgr.setPrimaryClip(android.content.ClipData.newPlainText("report", output))
                                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape  = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor   = Color.White
                            )
                        ) {
                            Text("Copy", fontWeight = FontWeight.SemiBold)
                        }

                        // Share button
                        Button(
                            onClick = {
                                val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(android.content.Intent.EXTRA_TEXT, output)
                                }
                                context.startActivity(android.content.Intent.createChooser(intent, "Share via"))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape  = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE94560),
                                contentColor   = Color.White
                            )
                        ) {
                            Text("Share", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Shared UI helpers ─────────────────────────────────────────────────────────

@Composable
fun SectionLabel(text: String) {
    Text(
        text          = text,
        fontSize      = 12.sp,
        fontWeight    = FontWeight.Bold,
        color         = Color.White.copy(alpha = 0.5f),
        letterSpacing = 1.5.sp
    )
}

@Composable
fun ScheduleField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value         = value,
        onValueChange = onChange,
        label         = { Text(label) },
        modifier      = Modifier.fillMaxWidth(),
        shape         = RoundedCornerShape(12.dp),
        colors        = fieldColors()
    )
    Spacer(Modifier.height(10.dp))
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = Color(0xFFE94560),
    unfocusedBorderColor    = Color.White.copy(alpha = 0.2f),
    focusedLabelColor       = Color(0xFFE94560),
    unfocusedLabelColor     = Color.White.copy(alpha = 0.5f),
    focusedTextColor        = Color.White,
    unfocusedTextColor      = Color.White.copy(alpha = 0.85f),
    cursorColor             = Color(0xFFE94560),
    focusedContainerColor   = Color.Transparent,
    unfocusedContainerColor = Color.Transparent
)