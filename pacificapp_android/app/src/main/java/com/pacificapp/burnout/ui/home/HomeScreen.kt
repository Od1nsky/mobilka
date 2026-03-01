package com.pacificapp.burnout.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pacificapp.burnout.data.model.*
import com.pacificapp.burnout.ui.theme.*
import com.pacificapp.burnout.viewmodels.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onNavigateToStress: () -> Unit = {},
    onNavigateToSleep: () -> Unit = {},
    onNavigateToWork: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
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
                item {
                    uiState.summary?.let { summary ->
                        BurnoutRiskCard(summary.burnoutRiskLevel)
                    }
                }

                item {
                    uiState.summary?.let { summary ->
                        MetricsRow(summary)
                    }
                }

                item {
                    uiState.summary?.let { summary ->
                        if (summary.weeklyTrend.isNotEmpty()) {
                            WeeklyTrendCard(summary.weeklyTrend)
                        }
                    }
                }

                item {
                    uiState.burnoutRisk?.let { risk ->
                        FactorsCard(risk)
                    }
                }

                item {
                    QuickActionsCard(
                        onCalculateRisk = { viewModel.calculateBurnoutRisk() },
                        isCalculating = uiState.isCalculating,
                        onNavigateToStress = onNavigateToStress,
                        onNavigateToSleep = onNavigateToSleep,
                        onNavigateToWork = onNavigateToWork
                    )
                }

                if (uiState.recommendations.isNotEmpty()) {
                    item {
                        RecommendationsCard(uiState.recommendations)
                    }
                }
            }
        }

        uiState.error?.let { error ->
            LaunchedEffect(error) {
                // Show snackbar or handle error
            }
        }
    }
}

@Composable
fun BurnoutRiskCard(riskLevel: Int) {
    val riskColor = when {
        riskLevel <= 25 -> StressLow
        riskLevel <= 50 -> StressMedium
        riskLevel <= 75 -> StressHigh
        else -> StressCritical
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = riskColor.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Burnout Risk",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$riskLevel%",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = riskColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { riskLevel / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = riskColor,
                trackColor = riskColor.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when {
                    riskLevel <= 25 -> "Low Risk - Keep up the good work!"
                    riskLevel <= 50 -> "Moderate Risk - Consider taking breaks"
                    riskLevel <= 75 -> "High Risk - Time to prioritize rest"
                    else -> "Critical Risk - Immediate action needed"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = riskColor
            )
        }
    }
}

@Composable
fun MetricsRow(summary: DashboardSummary) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricCard(
            title = "Stress",
            value = "${summary.currentStressLevel.toInt()}",
            unit = "/100",
            icon = Icons.Default.Warning,
            color = when {
                summary.currentStressLevel <= 30 -> StressLow
                summary.currentStressLevel <= 60 -> StressMedium
                else -> StressHigh
            },
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            title = "Sleep",
            value = String.format("%.1f", summary.averageSleepHours),
            unit = "hrs",
            icon = Icons.Default.Bedtime,
            color = if (summary.averageSleepHours >= 7) StressLow else StressMedium,
            modifier = Modifier.weight(1f)
        )
        MetricCard(
            title = "Work",
            value = String.format("%.1f", summary.averageWorkHours),
            unit = "hrs",
            icon = Icons.Default.Work,
            color = if (summary.averageWorkHours <= 8) StressLow else StressHigh,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WeeklyTrendCard(weeklyData: List<DailyRiskData>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Weekly Trend",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weeklyData.takeLast(7).forEach { day ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val color = when {
                            day.riskLevel <= 25 -> StressLow
                            day.riskLevel <= 50 -> StressMedium
                            day.riskLevel <= 75 -> StressHigh
                            else -> StressCritical
                        }
                        Box(
                            modifier = Modifier
                                .height(((day.riskLevel / 100f) * 60).dp.coerceAtLeast(8.dp))
                                .width(24.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = color.copy(alpha = 0.7f),
                                shape = MaterialTheme.shapes.small
                            ) {}
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = day.date.takeLast(2),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FactorsCard(burnoutRisk: BurnoutRisk) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Risk Factors",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            FactorItem("Overtime", burnoutRisk.overtimeFactor)
            FactorItem("Workday Duration", burnoutRisk.workdayDurationFactor)
            FactorItem("Stress Level", burnoutRisk.stressFactor)
            FactorItem("Sleep Quality", burnoutRisk.sleepQualityFactor)
            FactorItem("Sleep Deprivation", burnoutRisk.sleepDeprivationFactor)
        }
    }
}

@Composable
fun FactorItem(name: String, level: String) {
    val color = when (level.lowercase()) {
        "low" -> StressLow
        "moderate", "medium" -> StressMedium
        "high" -> StressHigh
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium
        )
        Surface(
            color = color.copy(alpha = 0.2f),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = level.replaceFirstChar { it.uppercase() },
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelMedium,
                color = color
            )
        }
    }
}

@Composable
fun QuickActionsCard(
    onCalculateRisk: () -> Unit,
    isCalculating: Boolean,
    onNavigateToStress: () -> Unit,
    onNavigateToSleep: () -> Unit,
    onNavigateToWork: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onCalculateRisk,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isCalculating
            ) {
                if (isCalculating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Calculate Burnout Risk")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToStress,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Stress", style = MaterialTheme.typography.labelMedium)
                }
                OutlinedButton(
                    onClick = onNavigateToSleep,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Bedtime, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sleep", style = MaterialTheme.typography.labelMedium)
                }
                OutlinedButton(
                    onClick = onNavigateToWork,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Work, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Work", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun RecommendationsCard(recommendations: List<UserRecommendation>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Recommendations",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            recommendations.take(3).forEach { rec ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = rec.content,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
