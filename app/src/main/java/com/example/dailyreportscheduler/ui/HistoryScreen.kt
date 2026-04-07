package com.example.dailyreportscheduler.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dailyreportscheduler.data.VersionStorage
import com.example.dailyreportscheduler.model.ReportVersion
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onRestore: (ReportVersion) -> Unit
) {
    val context  = LocalContext.current
    var versions by remember { mutableStateOf(VersionStorage.loadVersions(context)) }

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
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp)
        ) {

            // ── Header ───────────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint               = Color.White
                    )
                }
                Spacer(Modifier.width(4.dp))
                Column {
                    Text(
                        text       = "Version History",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                    Text(
                        text  = "${versions.size} saved version${if (versions.size != 1) "s" else ""}",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.45f)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Empty state ──────────────────────────────────────────────────
            if (versions.isEmpty()) {
                Box(
                    modifier          = Modifier.fillMaxSize(),
                    contentAlignment  = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📋", fontSize = 52.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text       = "No saved versions yet",
                            color      = Color.White.copy(alpha = 0.7f),
                            fontSize   = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text     = "Versions are saved automatically\nwhen you exit the app, or tap\n\"Save Version\" manually.",
                            color    = Color.White.copy(alpha = 0.38f),
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                // ── Version list ─────────────────────────────────────────────
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(versions, key = { it.id }) { version ->
                        VersionCard(
                            version   = version,
                            onRestore = {
                                onRestore(version)
                                onBack()
                            },
                            onDelete  = {
                                VersionStorage.deleteVersion(context, version.id)
                                versions = VersionStorage.loadVersions(context)
                            }
                        )
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun VersionCard(
    version: ReportVersion,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    val sdf           = SimpleDateFormat("dd MMM yyyy · hh:mm a", Locale.getDefault())
    val formattedTime = sdf.format(Date(version.savedAt))

    val filledSlots = listOf(version.s1, version.s2, version.s3, version.s4, version.s5)
        .count { it.isNotBlank() }

    Card(
        shape  = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: info block
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text          = formattedTime,
                    fontSize      = 11.sp,
                    color         = Color(0xFFE94560),
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 0.4.sp
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text       = version.batch.ifBlank { "No batch" },
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color.White,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                if (version.date.isNotBlank()) {
                    Text(
                        text     = "${version.date}${if (version.day.isNotBlank()) " · ${version.day}" else ""}",
                        fontSize = 13.sp,
                        color    = Color.White.copy(alpha = 0.55f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                // Schedule slot preview
                val firstFilled = listOf(version.s1, version.s2, version.s3, version.s4, version.s5)
                    .firstOrNull { it.isNotBlank() }
                if (firstFilled != null) {
                    Text(
                        text     = "\"$firstFilled\"",
                        fontSize = 12.sp,
                        color    = Color.White.copy(alpha = 0.35f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                if (filledSlots > 0) {
                    Spacer(Modifier.height(6.dp))
                    Surface(
                        shape  = RoundedCornerShape(6.dp),
                        color  = Color(0xFFE94560).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text     = "$filledSlots slot${if (filledSlots != 1) "s" else ""} filled",
                            fontSize = 10.sp,
                            color    = Color(0xFFE94560),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            // Right: actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick           = onRestore,
                    shape             = RoundedCornerShape(10.dp),
                    contentPadding    = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    colors            = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE94560),
                        contentColor   = Color.White
                    )
                ) {
                    Text("Restore", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(
                    onClick  = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.Delete,
                        contentDescription = "Delete version",
                        tint               = Color.White.copy(alpha = 0.35f),
                        modifier           = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
