package com.pacificapp.burnout.ui.tracking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pacificapp.burnout.data.model.SleepRecord
import com.pacificapp.burnout.data.model.SleepQualityCategory
import com.pacificapp.burnout.data.model.SleepStatistics
import com.pacificapp.burnout.ui.theme.*
import com.pacificapp.burnout.viewmodels.SleepViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepScreen(
    viewModel: SleepViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sleep Tracking") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Sleep Record")
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                uiState.lastNightSleep?.let { lastNight ->
                    item {
                        LastNightSleepCard(lastNight)
                    }
                }

                uiState.statistics?.let { stats ->
                    item {
                        SleepStatisticsCard(stats)
                    }
                }

                item {
                    Text(
                        text = "History",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(uiState.sleepRecords) { record ->
                    SleepHistoryItem(
                        record = record,
                        onDelete = { viewModel.deleteSleepRecord(record.id) }
                    )
                }
            }
        }

        uiState.error?.let { error ->
            LaunchedEffect(error) {
                // Show snackbar
            }
        }
    }

    if (showAddDialog) {
        AddSleepDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { hours, quality, notes ->
                viewModel.createSleepRecord(
                    date = LocalDate.now(),
                    durationHours = hours,
                    quality = quality,
                    notes = notes
                )
                showAddDialog = false
            },
            isSaving = uiState.isSaving
        )
    }
}

@Composable
fun LastNightSleepCard(record: SleepRecord) {
    val qualityColor = record.qualityCategory?.let { Color(it.color) } ?: MaterialTheme.colorScheme.primary
    val durationColor = if (record.isSufficientSleep) StressLow else StressMedium

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = durationColor.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Last Night",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Bedtime,
                        contentDescription = null,
                        tint = durationColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = record.durationFormatted,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = durationColor
                    )
                    Text(
                        text = "Duration",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                record.quality?.let { quality ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = qualityColor,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "$quality/10",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = qualityColor
                        )
                        Text(
                            text = "Quality",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (record.isSufficientSleep) "Good sleep duration!" else "Try to get more sleep",
                style = MaterialTheme.typography.bodyMedium,
                color = durationColor
            )
        }
    }
}

@Composable
fun SleepStatisticsCard(stats: SleepStatistics) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("Avg Duration", String.format("%.1fh", stats.averageDuration))
                StatItem("Avg Quality", String.format("%.1f", stats.averageQuality))
                StatItem("Records", "${stats.totalRecords}")
            }
        }
    }
}

@Composable
fun SleepHistoryItem(
    record: SleepRecord,
    onDelete: () -> Unit
) {
    val durationColor = if (record.isSufficientSleep) StressLow else StressMedium
    val qualityColor = record.qualityCategory?.let { Color(it.color) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Bedtime,
                    contentDescription = null,
                    tint = durationColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = record.durationFormatted,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = durationColor
                    )
                    Text(
                        text = record.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (record.notes.isNotBlank()) {
                        Text(
                            text = record.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                record.quality?.let { quality ->
                    Surface(
                        color = (qualityColor ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "$quality/10",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = qualityColor ?: MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun AddSleepDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, Int?, String) -> Unit,
    isSaving: Boolean
) {
    var hours by remember { mutableFloatStateOf(7f) }
    var quality by remember { mutableFloatStateOf(7f) }
    var notes by remember { mutableStateOf("") }

    val durationColor = if (hours >= 7) StressLow else StressMedium

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Sleep") },
        text = {
            Column {
                Text(
                    text = "Duration",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = String.format("%.1f hours", hours),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = durationColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Slider(
                    value = hours,
                    onValueChange = { hours = it },
                    valueRange = 0f..12f,
                    steps = 23,
                    colors = SliderDefaults.colors(
                        thumbColor = durationColor,
                        activeTrackColor = durationColor
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Quality",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${quality.toInt()}/10",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Slider(
                    value = quality,
                    onValueChange = { quality = it },
                    valueRange = 1f..10f,
                    steps = 8
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(hours.toDouble(), quality.toInt(), notes) },
                enabled = !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
