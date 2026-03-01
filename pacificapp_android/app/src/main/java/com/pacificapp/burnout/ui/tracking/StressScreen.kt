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
import com.pacificapp.burnout.data.model.StressLevel
import com.pacificapp.burnout.ui.theme.*
import com.pacificapp.burnout.viewmodels.StressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StressScreen(
    viewModel: StressViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stress Tracking") },
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
                Icon(Icons.Default.Add, contentDescription = "Add Stress Level")
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
                uiState.currentStressLevel?.let { current ->
                    item {
                        CurrentStressCard(current)
                    }
                }

                uiState.statistics?.let { stats ->
                    item {
                        StressStatisticsCard(stats)
                    }
                }

                item {
                    Text(
                        text = "History",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(uiState.stressLevels) { stressLevel ->
                    StressHistoryItem(
                        stressLevel = stressLevel,
                        onDelete = { viewModel.deleteStressLevel(stressLevel.id) }
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
        AddStressDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { level, notes ->
                viewModel.createStressLevel(level, notes)
                showAddDialog = false
            },
            isSaving = uiState.isSaving
        )
    }
}

@Composable
fun CurrentStressCard(stressLevel: StressLevel) {
    val color = when (stressLevel.category) {
        com.pacificapp.burnout.data.model.StressCategory.LOW -> StressLow
        com.pacificapp.burnout.data.model.StressCategory.MODERATE -> StressMedium
        com.pacificapp.burnout.data.model.StressCategory.HIGH -> StressHigh
        com.pacificapp.burnout.data.model.StressCategory.VERY_HIGH -> StressCritical
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Current Stress Level",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${stressLevel.level}",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stressLevel.category.label,
                style = MaterialTheme.typography.bodyLarge,
                color = color
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { stressLevel.levelPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun StressStatisticsCard(stats: com.pacificapp.burnout.data.model.StressStatistics) {
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
                StatItem("Average", String.format("%.0f", stats.averageLevel))
                StatItem("Min", "${stats.minLevel}")
                StatItem("Max", "${stats.maxLevel}")
                StatItem("Records", "${stats.totalRecords}")
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StressHistoryItem(
    stressLevel: StressLevel,
    onDelete: () -> Unit
) {
    val color = when (stressLevel.category) {
        com.pacificapp.burnout.data.model.StressCategory.LOW -> StressLow
        com.pacificapp.burnout.data.model.StressCategory.MODERATE -> StressMedium
        com.pacificapp.burnout.data.model.StressCategory.HIGH -> StressHigh
        com.pacificapp.burnout.data.model.StressCategory.VERY_HIGH -> StressCritical
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = color.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "${stressLevel.level}",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = stressLevel.category.label,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = stressLevel.createdAt.take(10),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (stressLevel.notes.isNotBlank()) {
                        Text(
                            text = stressLevel.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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

@Composable
fun AddStressDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit,
    isSaving: Boolean
) {
    var level by remember { mutableFloatStateOf(50f) }
    var notes by remember { mutableStateOf("") }

    val color = when {
        level <= 30 -> StressLow
        level <= 60 -> StressMedium
        level <= 80 -> StressHigh
        else -> StressCritical
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Stress Level") },
        text = {
            Column {
                Text(
                    text = "${level.toInt()}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = level,
                    onValueChange = { level = it },
                    valueRange = 1f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = color,
                        activeTrackColor = color
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Low", style = MaterialTheme.typography.bodySmall)
                    Text("High", style = MaterialTheme.typography.bodySmall)
                }

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
                onClick = { onConfirm(level.toInt(), notes) },
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
