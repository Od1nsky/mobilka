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
import com.pacificapp.burnout.data.model.WorkActivity
import com.pacificapp.burnout.data.model.WorkStatistics
import com.pacificapp.burnout.ui.theme.*
import com.pacificapp.burnout.viewmodels.WorkViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkScreen(
    viewModel: WorkViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Work Tracking") },
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
                Icon(Icons.Default.Add, contentDescription = "Add Work Activity")
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
                uiState.todayActivity?.let { today ->
                    item {
                        TodayWorkCard(today)
                    }
                }

                uiState.statistics?.let { stats ->
                    item {
                        WorkStatisticsCard(stats)
                    }
                }

                if (uiState.isOverworking) {
                    item {
                        OverworkWarningCard()
                    }
                }

                item {
                    Text(
                        text = "History",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(uiState.workActivities) { activity ->
                    WorkHistoryItem(
                        activity = activity,
                        onDelete = { viewModel.deleteWorkActivity(activity.id) }
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
        AddWorkDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { hours, breaks, breakMinutes, productivity, notes ->
                viewModel.createWorkActivity(
                    date = LocalDate.now(),
                    durationHours = hours,
                    breaksCount = breaks,
                    breaksTotalMinutes = breakMinutes,
                    productivity = productivity,
                    notes = notes
                )
                showAddDialog = false
            },
            isSaving = uiState.isSaving
        )
    }
}

@Composable
fun TodayWorkCard(activity: WorkActivity) {
    val durationColor = if (activity.isOvertime) StressHigh else StressLow
    val productivityColor = activity.productivityCategory?.let { Color(it.color) }
        ?: MaterialTheme.colorScheme.primary

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
                text = "Today's Work",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Work,
                        contentDescription = null,
                        tint = durationColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = activity.durationFormatted,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = durationColor
                    )
                    Text(
                        text = "Duration",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Coffee,
                        contentDescription = null,
                        tint = if (activity.hasAdequateBreaks) StressLow else StressMedium,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "${activity.breaksCount}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Breaks",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                activity.productivity?.let { prod ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = productivityColor,
                            modifier = Modifier.size(28.dp)
                        )
                        Text(
                            text = "$prod/10",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = productivityColor
                        )
                        Text(
                            text = "Productivity",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (activity.isOvertime) {
                Text(
                    text = "Overtime detected - consider taking a break",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StressHigh
                )
            } else if (!activity.hasAdequateBreaks) {
                Text(
                    text = "Remember to take regular breaks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StressMedium
                )
            }
        }
    }
}

@Composable
fun WorkStatisticsCard(stats: WorkStatistics) {
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
                StatItem("Avg Hours", String.format("%.1f", stats.averageDuration))
                StatItem("Avg Prod", String.format("%.1f", stats.averageProductivity))
                StatItem("Avg Breaks", "${stats.averageBreaksCount}")
                StatItem("Records", "${stats.totalRecords}")
            }
        }
    }
}

@Composable
fun OverworkWarningCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = StressHigh.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = StressHigh,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Overworking Detected",
                    style = MaterialTheme.typography.titleMedium,
                    color = StressHigh
                )
                Text(
                    text = "Your average work hours exceed 9 hours. Consider adjusting your schedule.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun WorkHistoryItem(
    activity: WorkActivity,
    onDelete: () -> Unit
) {
    val durationColor = if (activity.isOvertime) StressHigh else StressLow
    val productivityColor = activity.productivityCategory?.let { Color(it.color) }

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
                    Icons.Default.Work,
                    contentDescription = null,
                    tint = durationColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = activity.durationFormatted,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = durationColor
                        )
                        if (activity.isOvertime) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = StressHigh.copy(alpha = 0.2f),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "OT",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StressHigh
                                )
                            }
                        }
                    }
                    Text(
                        text = activity.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${activity.breaksCount} breaks (${activity.breaksTotalMinutes}min)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                activity.productivity?.let { prod ->
                    Surface(
                        color = (productivityColor ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "$prod/10",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = productivityColor ?: MaterialTheme.colorScheme.primary
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
fun AddWorkDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, Int, Int, Int?, String) -> Unit,
    isSaving: Boolean
) {
    var hours by remember { mutableFloatStateOf(8f) }
    var breaks by remember { mutableFloatStateOf(2f) }
    var breakMinutes by remember { mutableFloatStateOf(30f) }
    var productivity by remember { mutableFloatStateOf(7f) }
    var notes by remember { mutableStateOf("") }

    val durationColor = if (hours > 8) StressHigh else StressLow

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Work Activity") },
        text = {
            Column {
                Text(
                    text = "Work Duration",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = String.format("%.1f hours", hours),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = durationColor,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Slider(
                    value = hours,
                    onValueChange = { hours = it },
                    valueRange = 0f..16f,
                    steps = 31,
                    colors = SliderDefaults.colors(
                        thumbColor = durationColor,
                        activeTrackColor = durationColor
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Breaks",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${breaks.toInt()} breaks",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Slider(
                            value = breaks,
                            onValueChange = { breaks = it },
                            valueRange = 0f..10f,
                            steps = 9
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${breakMinutes.toInt()} min",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Slider(
                            value = breakMinutes,
                            onValueChange = { breakMinutes = it },
                            valueRange = 0f..120f,
                            steps = 23
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Productivity",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "${productivity.toInt()}/10",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Slider(
                    value = productivity,
                    onValueChange = { productivity = it },
                    valueRange = 1f..10f,
                    steps = 8
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        hours.toDouble(),
                        breaks.toInt(),
                        breakMinutes.toInt(),
                        productivity.toInt(),
                        notes
                    )
                },
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
