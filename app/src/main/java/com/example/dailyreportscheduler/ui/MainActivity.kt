package com.example.dailyreportscheduler.ui

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailyreportscheduler.ui.theme.DailyReportSchedulerTheme
import com.example.dailyreportscheduler.utils.generateReport
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyReportSchedulerTheme {
                Surface {
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    var batch  by remember { mutableStateOf("Feb 2026") }
    var date   by remember { mutableStateOf("") }
    var day    by remember { mutableStateOf("") }
    var s1     by remember { mutableStateOf("") }
    var s2     by remember { mutableStateOf("") }
    var s3     by remember { mutableStateOf("") }
    var s4     by remember { mutableStateOf("") }
    var s5     by remember { mutableStateOf("") }
    var output by remember { mutableStateOf("") }

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
            Text(
                text = "Daily Report",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Fill in your details below",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(28.dp))

            // ── Card 1: Basic Info ────────────────────────────────────────────
            SectionLabel("Basic Info")
            Spacer(Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.07f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = batch,
                        onValueChange = { batch = it },
                        label = { Text("Batch") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors()
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val cal = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val selected = "$dayOfMonth/${month + 1}/$year"
                                    date = selected
                                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    val d = sdf.parse(selected)
                                    day = SimpleDateFormat("EEEE", Locale.getDefault()).format(d!!)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE94560),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (date.isEmpty()) "Select Date" else date,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }

                    if (day.isNotEmpty()) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "Day: $day",
                            color = Color.White.copy(alpha = 0.7f),
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
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.07f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    ScheduleField("Schedule 1", s1) { s1 = it }
                    ScheduleField("Schedule 2", s2) { s2 = it }
                    ScheduleField("Schedule 3", s3) { s3 = it }
                    ScheduleField("Schedule 4", s4) { s4 = it }
                    ScheduleField("Schedule 5", s5) { s5 = it }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            output = generateReport(batch, date, day, s1, s2, s3, s4, s5)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE94560),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Generate Report",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Card 3: Output ────────────────────────────────────────────────
            SectionLabel("Output")
            Spacer(Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.07f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = output,
                        onValueChange = { output = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 8,
                        shape = RoundedCornerShape(12.dp),
                        colors = fieldColors()
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val mgr = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE)
                                        as android.content.ClipboardManager
                                mgr.setPrimaryClip(android.content.ClipData.newPlainText("report", output))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.15f),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Copy", fontWeight = FontWeight.SemiBold)
                        }

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
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE94560),
                                contentColor = Color.White
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

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White.copy(alpha = 0.5f),
        letterSpacing = 1.5.sp
    )
}

@Composable
fun ScheduleField(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = fieldColors()
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