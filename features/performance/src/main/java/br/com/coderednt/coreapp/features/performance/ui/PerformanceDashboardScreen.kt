package br.com.coderednt.coreapp.features.performance.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.coderednt.coreapp.core.monitoring.performance.HealthMetrics
import java.util.Locale

@Composable
fun PerformanceDashboardRoute(
    viewModel: PerformanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PerformanceDashboardScreen(uiState = uiState)
}

enum class PerformanceRating(val label: String, val color: @Composable () -> Color) {
    GOOD("Good", { Color(0xFF4CAF50) }),
    FAIR("Fair", { Color(0xFFFFC107) }),
    POOR("Poor", { Color(0xFFF44336) })
}

fun getTtidRating(ms: Double): PerformanceRating = when {
    ms <= 2000.0 -> PerformanceRating.GOOD
    ms <= 5000.0 -> PerformanceRating.FAIR
    else -> PerformanceRating.POOR
}

fun getTtfdRating(ms: Long): PerformanceRating = when {
    ms <= 3000 -> PerformanceRating.GOOD
    ms <= 6000 -> PerformanceRating.FAIR
    else -> PerformanceRating.POOR
}

fun formatDuration(ms: Double): String {
    val absMs = kotlin.math.abs(ms)
    return when {
        absMs >= 1000.0 -> String.format(Locale.US, "%.2f s", ms / 1000.0)
        absMs >= 1.0 -> String.format(Locale.US, "%.2f ms", ms)
        absMs >= 0.001 -> String.format(Locale.US, "%.2f µs", ms * 1000.0)
        else -> String.format(Locale.US, "%.0f ns", ms * 1_000_000.0)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceDashboardScreen(
    uiState: HealthMetrics
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val ttfdValue = uiState.renderTimes["MainActivity"] ?: uiState.renderTimes["MainScreen"] ?: 0L
    
    val ttidRating = getTtidRating(uiState.startupTimeMs)
    val ttfdRating = getTtfdRating(ttfdValue)

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("Performance Dashboard") },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                MetricCard(
                    title = "App Startup (TTID)",
                    value = formatDuration(uiState.startupTimeMs),
                    icon = Icons.Rounded.Speed,
                    description = "Time to Initial Display - Rating: ${ttidRating.label}",
                    containerColor = ttidRating.color().copy(alpha = 0.2f),
                    contentColor = ttidRating.color()
                )
            }

            item {
                MetricCard(
                    title = "Main Screen (TTFD)",
                    value = formatDuration(ttfdValue.toDouble()),
                    icon = Icons.Rounded.Layers,
                    description = "Time to Fully Drawn - Rating: ${ttfdRating.label}",
                    containerColor = ttfdRating.color().copy(alpha = 0.2f),
                    contentColor = ttfdRating.color()
                )
            }

            if (uiState.navigationTimes.isNotEmpty()) {
                item {
                    val avgNav = uiState.navigationTimes.values.average()
                    ExpandableSection(
                        title = "Navigation Vitals",
                        totalTime = formatDuration(avgNav),
                        totalLabel = "Avg Delay"
                    ) {
                        uiState.navigationTimes.forEach { (route, time) ->
                            MetricCard(
                                title = route,
                                value = formatDuration(time.toDouble()),
                                icon = Icons.Rounded.Navigation,
                                description = "Transition Delay",
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f),
                                contentColor = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }

            item {
                val moduleSyncTotal = uiState.moduleLoadTimes.values.sum()
                val totalPhases = uiState.osOverheadTimeMs + uiState.providerInitTimeMs + 
                                uiState.diInitializationTimeMs + uiState.appOnCreateTimeMs + 
                                moduleSyncTotal + uiState.activityLaunchDelayMs + 
                                uiState.uiInflationTimeMs + uiState.splashScreenDurationMs
                
                ExpandableSection(
                    title = "Startup Phases",
                    totalTime = formatDuration(totalPhases)
                ) {
                    PhaseItem("1. OS Overhead", uiState.osOverheadTimeMs, "Kernel fork to first provider")
                    PhaseItem("2. Provider Init", uiState.providerInitTimeMs, "ContentProviders setup")
                    PhaseItem("3. DI Optimization", uiState.diInitializationTimeMs, "Hilt Graph construction")
                    PhaseItem("4. Application Init", uiState.appOnCreateTimeMs, "Application onCreate cycle")
                    PhaseItem("5. Module Sync Costs", moduleSyncTotal, "Total of all sync modules")
                    PhaseItem("6. Activity Launch", uiState.activityLaunchDelayMs, "App to Activity handoff")
                    PhaseItem("7. UI Inflation", uiState.uiInflationTimeMs, "Compose setContent")
                    if (uiState.splashScreenDurationMs > 0) {
                        PhaseItem("8. Splash Screen", uiState.splashScreenDurationMs, "Animation & Exit")
                    }
                }
            }

            item {
                val totalSync = uiState.moduleLoadTimes.values.sum()
                ExpandableSection(
                    title = "Module Costs (Sync)",
                    totalTime = formatDuration(totalSync)
                ) {
                    uiState.moduleLoadTimes.forEach { (module, time) ->
                        MetricCard(title = module, value = formatDuration(time), icon = Icons.Rounded.Timer)
                    }
                }
            }

            item {
                val totalAsync = uiState.parallelModuleLoadTimes.values.sum()
                ExpandableSection(
                    title = "Module Costs (Async)",
                    totalTime = formatDuration(totalAsync)
                ) {
                    uiState.parallelModuleLoadTimes.forEach { (module, time) ->
                        MetricCard(title = module, value = formatDuration(time), icon = Icons.Rounded.Bolt)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                VitalsLegendTable()
            }
        }
    }
}

@Composable
fun PhaseItem(title: String, value: Double, desc: String) {
    MetricCard(
        title = title,
        value = formatDuration(value),
        icon = Icons.Rounded.Category,
        description = desc,
        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
    )
}

@Composable
fun VitalsLegendTable() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Android Vitals Legend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            LegendRow(PerformanceRating.GOOD.label, color = PerformanceRating.GOOD.color.invoke(), "TTID < 2s / TTFD < 3s")
            LegendRow(PerformanceRating.FAIR.label, PerformanceRating.FAIR.color.invoke(), "TTID < 5s / TTFD < 6s")
            LegendRow(PerformanceRating.POOR.label, PerformanceRating.POOR.color.invoke(), "TTID > 5s / TTFD > 6s")
        }
    }
}

@Composable
fun LegendRow(label: String, color: Color, range: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
        Text(text = range, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ExpandableSection(
    title: String, 
    totalTime: String, 
    totalLabel: String = "Total",
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "$totalLabel: $totalTime", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            Icon(imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
        }
        AnimatedVisibility(visible = expanded) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) { content() }
        }
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    description: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = containerColor)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = contentColor)
            Spacer(modifier = Modifier.padding(8.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (description != null) {
                    Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                }
            }
        }
    }
}
