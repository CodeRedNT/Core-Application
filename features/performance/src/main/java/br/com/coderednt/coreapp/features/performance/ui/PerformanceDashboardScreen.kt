package br.com.coderednt.coreapp.features.performance.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

/**
 * Rota de entrada para o Dashboard de Performance.
 * 
 * Gerencia a injeção do ViewModel e observa o estado das métricas de saúde do app.
 */
@Composable
fun PerformanceDashboardRoute(
    viewModel: PerformanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.metricsState.collectAsStateWithLifecycle()
    PerformanceDashboardScreen(uiState = uiState)
}

/**
 * Categorias de métricas exibidas no dashboard.
 */
enum class PerformanceTab(val label: String, val icon: ImageVector) {
    STARTUP("Startup", Icons.Rounded.RocketLaunch),
    MEMORY("Memory", Icons.Rounded.Memory),
    NETWORK("Network", Icons.Rounded.CloudSync),
    BATTERY("Battery", Icons.Rounded.BatteryChargingFull),
    LOGS("Logs", Icons.Rounded.ListAlt)
}

/**
 * Tela principal do Dashboard de Performance.
 * 
 * Exibe métricas detalhadas sobre o estado do aplicativo divididas em abas 
 * interativas, utilizando Material Design 3 e animações de transição.
 * 
 * @param uiState O estado consolidado das métricas obtido do tracker de saúde.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceDashboardScreen(
    uiState: HealthMetrics
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var selectedTab by remember { mutableStateOf(PerformanceTab.STARTUP) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                LargeTopAppBar(
                    title = { Text("Performance Dashboard") },
                    scrollBehavior = scrollBehavior
                )
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    edgePadding = 16.dp,
                    containerColor = MaterialTheme.colorScheme.surface,
                    divider = {}
                ) {
                    PerformanceTab.entries.forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = { Text(tab.label) },
                            icon = { Icon(tab.icon, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    PerformanceTab.STARTUP -> StartupTabContent(uiState)
                    PerformanceTab.MEMORY -> MemoryTabContent(uiState)
                    PerformanceTab.NETWORK -> NetworkTabContent(uiState)
                    PerformanceTab.BATTERY -> BatteryTabContent(uiState)
                    PerformanceTab.LOGS -> LogsTabContent(uiState)
                }
            }
        }
    }
}

/**
 * Conteúdo da aba de Startup.
 * Exibe o TTID (Time to Initial Display), TTFD e os custos de cada fase de inicialização.
 */
@Composable
fun StartupTabContent(uiState: HealthMetrics) {
    val ttfdValue = uiState.ui.renderTimes["MainActivity"] ?: uiState.ui.renderTimes["MainScreen"] ?: 0L
    val ttidRating = getTtidRating(uiState.startup.totalStartupTimeMs)
    val ttfdRating = getTtfdRating(ttfdValue)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MetricCard(
                title = "App Startup (TTID)",
                value = formatDuration(uiState.startup.totalStartupTimeMs),
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
        item {
            val totalPhases = uiState.startup.totalStartupTimeMs
            
            ExpandableSection(title = "Startup Phases", totalTime = formatDuration(totalPhases)) {
                PhaseItem("1. OS Overhead", uiState.startup.osOverheadTimeMs, "Kernel fork to first provider")
                PhaseItem("2. Provider Init", uiState.startup.providerInitTimeMs, "ContentProviders setup")
                PhaseItem("3. DI Optimization", uiState.startup.diInitializationTimeMs, "Hilt Graph construction")
                PhaseItem("4. Application Init", uiState.startup.appOnCreateTimeMs, "Application onCreate cycle")
                PhaseItem("5. Module Sync Costs", uiState.startup.moduleLoadTimes.values.sum(), "Total of all sync modules")
                PhaseItem("6. Activity Launch", uiState.startup.activityLaunchDelayMs, "App to Activity handoff")
                PhaseItem("7. UI Inflation", uiState.startup.uiInflationTimeMs, "Compose setContent")
                if (uiState.startup.splashScreenDurationMs > 0) {
                    PhaseItem("8. Splash Screen", uiState.startup.splashScreenDurationMs, "Animation & Exit")
                }
            }
        }
        
        if (uiState.startup.moduleLoadTimes.isNotEmpty()) {
            item {
                val totalSync = uiState.startup.moduleLoadTimes.values.sum()
                ExpandableSection(
                    title = "Module Costs (Sync)", 
                    totalTime = formatDuration(totalSync)
                ) {
                    uiState.startup.moduleLoadTimes.forEach { entry ->
                        MetricCard(
                            title = entry.key, 
                            value = formatDuration(entry.value), 
                            icon = Icons.Rounded.Timer,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        if (uiState.startup.parallelModuleLoadTimes.isNotEmpty()) {
            item {
                val totalAsync = uiState.startup.parallelModuleLoadTimes.values.sum()
                ExpandableSection(
                    title = "Module Costs (Async)", 
                    totalTime = formatDuration(totalAsync)
                ) {
                    uiState.startup.parallelModuleLoadTimes.forEach { entry ->
                        MetricCard(
                            title = entry.key, 
                            value = formatDuration(entry.value), 
                            icon = Icons.Rounded.Bolt,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Conteúdo da aba de Memória.
 * Exibe o uso da Heap da JVM, contagem de GC e avisos de sistema.
 */
@Composable
fun MemoryTabContent(uiState: HealthMetrics) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MetricCard(
                title = "Initial Memory (Startup)",
                value = "${String.format(Locale.US, "%.1f", uiState.memory.initialMemoryMb)} MB",
                icon = Icons.Rounded.Rocket,
                description = "Memory used at Application.onCreate end",
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        }
        item {
            val usagePct = if (uiState.memory.totalHeapMb > 0) (uiState.memory.usedHeapMb / uiState.memory.totalHeapMb) else 0.0
            MetricCard(
                title = "App Memory Usage (Live)",
                value = "${String.format(Locale.US, "%.1f", uiState.memory.usedHeapMb)} MB",
                icon = Icons.Rounded.Memory,
                description = "Current JVM Heap - Max: ${uiState.memory.totalHeapMb.toInt()} MB",
                containerColor = if (usagePct > 0.8) Color(0xFFF44336).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
            )
        }
        item {
            MetricCard(
                title = "Garbage Collection",
                value = "${uiState.memory.gcCount} runs",
                icon = Icons.Rounded.DeleteSweep,
                description = "Automatic cleanup cycles detected",
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f),
                contentColor = MaterialTheme.colorScheme.tertiary
            )
        }
        if (uiState.memory.activityMemoryUsage.isNotEmpty()) {
            item {
                ExpandableSection(
                    title = "Activity Memory Map",
                    totalTime = "${uiState.memory.activityMemoryUsage.size} Screens",
                    totalLabel = "Tracked"
                ) {
                    uiState.memory.activityMemoryUsage.forEach { entry ->
                        MetricCard(
                            title = entry.key,
                            value = "${String.format(Locale.US, "%.1f", entry.value)} MB",
                            icon = Icons.Rounded.Analytics,
                            description = "Heap used at Resume",
                            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
        item {
            MetricCard(
                title = "System RAM Available",
                value = String.format(Locale.US, "%.2f GB", uiState.memory.availableSystemMemGb),
                icon = Icons.Rounded.Dns,
                description = if (uiState.memory.isLowMemory) "SYSTEM LOW MEMORY WARNING!" else "Health: Optimal"
            )
        }
    }
}

/**
 * Conteúdo da aba de Rede.
 * Exibe as latências das chamadas de API registradas.
 */
@Composable
fun NetworkTabContent(uiState: HealthMetrics) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (uiState.ui.apiLatencies.isEmpty()) {
            item { Text("No API calls tracked yet.", modifier = Modifier.padding(16.dp)) }
        } else {
            uiState.ui.apiLatencies.forEach { entry ->
                item {
                    val time = entry.value
                    MetricCard(
                        title = entry.key,
                        value = formatDuration(time.toDouble()),
                        icon = Icons.Rounded.CloudSync,
                        containerColor = if (time > 1000L) Color(0xFFFFC107).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Conteúdo da aba de Bateria.
 * Exibe o nível do dispositivo, corrente instantânea e temperatura.
 */
@Composable
fun BatteryTabContent(uiState: HealthMetrics) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MetricCard(
                title = "App Battery Consumption",
                value = "${uiState.battery.dropPercentage}%",
                icon = Icons.Rounded.DataUsage,
                description = "Drop since app startup",
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                contentColor = MaterialTheme.colorScheme.error
            )
        }
        item {
            MetricCard(
                title = "Device Battery Level",
                value = "${uiState.battery.level}%",
                icon = if (uiState.battery.isCharging) Icons.Rounded.BatteryChargingFull else Icons.Rounded.BatteryStd,
                description = "Status: ${if (uiState.battery.isCharging) "Charging" else "Discharging"}",
                containerColor = if (uiState.battery.level < 20) Color(0xFFF44336).copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
            )
        }
        item {
            MetricCard(
                title = "Current Consumption (Live)",
                value = "${uiState.battery.currentNowMa} mA",
                icon = Icons.Rounded.ElectricBolt,
                description = "Current flow from/to battery",
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
        item {
            MetricCard(
                title = "Temperature",
                value = "${uiState.battery.temperature} °C",
                icon = Icons.Rounded.DeviceThermostat,
                description = "Health: ${uiState.battery.health}"
            )
        }
    }
}

/**
 * Conteúdo da aba de Logs.
 * Exibe o último erro crítico capturado pelo Logger.e().
 */
@Composable
fun LogsTabContent(uiState: HealthMetrics) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            MetricCard(
                title = "Last System Error",
                value = uiState.lastError ?: "No critical errors",
                icon = Icons.Rounded.ErrorOutline,
                description = "Captured via Logger.e()",
                containerColor = if (uiState.lastError != null) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (uiState.lastError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Classificação de performance baseada em limites de tempo.
 */
enum class PerformanceRating(val label: String, val color: @Composable () -> Color) {
    GOOD("Good", { Color(0xFF4CAF50) }), 
    FAIR("Fair", { Color(0xFFFFC107) }), 
    POOR("Poor", { Color(0xFFF44336) })
}

/** Classifica o tempo de startup (TTID). */
fun getTtidRating(ms: Double): PerformanceRating = when { 
    ms <= 2000.0 -> PerformanceRating.GOOD
    ms <= 5000.0 -> PerformanceRating.FAIR
    else -> PerformanceRating.POOR 
}

/** Classifica o tempo de renderização da tela principal (TTFD). */
fun getTtfdRating(ms: Long): PerformanceRating = when { 
    ms <= 3000 -> PerformanceRating.GOOD
    ms <= 6000 -> PerformanceRating.FAIR
    else -> PerformanceRating.POOR 
}

/** Formata durações em ns, ms ou s com base na magnitude do valor. */
fun formatDuration(ms: Double): String {
    val absMs = kotlin.math.abs(ms)
    return when {
        absMs >= 1000.0 -> String.format(Locale.US, "%.2f s", ms / 1000.0)
        absMs >= 1.0 -> String.format(Locale.US, "%.2f ms", ms)
        else -> String.format(Locale.US, "%.0f ns", ms * 1_000_000.0)
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) { 
                content() 
            } 
        }
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
            Icon(imageVector = icon, contentDescription = null, tint = contentColor); Spacer(modifier = Modifier.padding(8.dp))
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
