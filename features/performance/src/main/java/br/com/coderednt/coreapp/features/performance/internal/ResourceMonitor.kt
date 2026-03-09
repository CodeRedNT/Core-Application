package br.com.coderednt.coreapp.features.performance.internal

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import br.com.coderednt.coreapp.core.monitoring.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.monitoring.performance.BatteryMetrics
import br.com.coderednt.coreapp.core.monitoring.performance.MemoryMetrics
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitor de recursos do sistema em tempo real (Memória e Bateria).
 * 
 * Este monitor é sensível ao ciclo de vida do processo (Lifecycle-Aware). Ele inicia a 
 * coleta periódica apenas quando o aplicativo está em primeiro plano, economizando 
 * bateria e CPU quando o app está em background.
 * 
 * Implementa detecção passiva de Garbage Collection (GC) utilizando referências fracas.
 * 
 * @property context Contexto da aplicação injetado pelo Hilt.
 * @property appHealthTracker O tracker para onde as métricas coletadas serão enviadas.
 */
@Singleton
class ResourceMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appHealthTracker: AppHealthTracker
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var isMonitoring = false
    private var collectionJob: Job? = null
    private var isAppInForeground = false

    /**
     * Observador do ciclo de vida do processo para pausar/retomar a coleta.
     */
    private val lifecycleObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                isAppInForeground = true
                startPeriodicCollection()
            }
            Lifecycle.Event.ON_STOP -> {
                isAppInForeground = false
                stopPeriodicCollection()
            }
            else -> {}
        }
    }

    /**
     * Inicia o monitoramento global de recursos.
     * Deve ser chamado uma única vez durante o startup do SDK.
     */
    fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true
        
        Log.d("ResourceMonitor", "Iniciando monitoramento de recursos (Lifecycle-Aware)...")

        // Adiciona o observer ao ciclo de vida do processo (via ProcessLifecycleOwner)
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleObserver)

        // Registra o receptor de eventos de bateria (Mudanças de nível, temperatura e carga)
        try {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            context.registerReceiver(batteryReceiver, filter)
        } catch (e: Exception) {
            Log.e("ResourceMonitor", "Erro ao registrar receiver de bateria", e)
        }
    }

    /**
     * Inicia a coleta periódica de métricas de memória e corrente de bateria.
     */
    private fun startPeriodicCollection() {
        if (collectionJob?.isActive == true) return
        
        collectionJob = scope.launch {
            while (isActive) {
                try {
                    collectMemoryMetrics()
                    collectBatteryLiveMetrics()
                    checkGC()
                } catch (e: Exception) {
                    Log.e("ResourceMonitor", "Erro durante a coleta de métricas", e)
                }
                // Coleta a cada 3 segundos para equilibrar precisão e eficiência energética
                delay(3000) 
            }
        }
    }

    /**
     * Interrompe a coleta periódica quando o aplicativo vai para segundo plano.
     */
    private fun stopPeriodicCollection() {
        collectionJob?.cancel()
        collectionJob = null
        Log.d("ResourceMonitor", "Coleta de recursos suspensa (App em background)")
    }

    /**
     * Coleta métricas detalhadas de uso da heap da JVM e memória do sistema.
     */
    private fun collectMemoryMetrics() {
        val runtime = Runtime.getRuntime()
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as? ActivityManager
        
        val usedHeap = (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0)
        val totalHeap = runtime.maxMemory() / (1024.0 * 1024.0)
        
        var availableSys = 0.0
        var isLowMem = false

        activityManager?.let {
            val memoryInfo = ActivityManager.MemoryInfo()
            it.getMemoryInfo(memoryInfo)
            // Memória disponível no sistema em GB
            availableSys = memoryInfo.availMem / (1024.0 * 1024.0 * 1024.0)
            isLowMem = memoryInfo.lowMemory
        }

        appHealthTracker.trackMemory(
            MemoryMetrics(
                usedHeapMb = usedHeap,
                totalHeapMb = totalHeap,
                availableSystemMemGb = availableSys,
                isLowMemory = isLowMem
            )
        )
    }

    /**
     * Obtém o consumo instantâneo de corrente (mA) da bateria.
     */
    private fun collectBatteryLiveMetrics() {
        val batteryManager = context.getSystemService(BATTERY_SERVICE) as? BatteryManager
        batteryManager?.let {
            val currentNow = it.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000
            val currentMetrics = appHealthTracker.metrics.value.battery
            appHealthTracker.trackBattery(currentMetrics.copy(currentNowMa = currentNow))
        }
    }

    /** Referência fraca para detectar a ocorrência de Garbage Collection. */
    private var gcSentinel = WeakReference(Any())
    
    /**
     * Verifica se o objeto sentinela foi coletado pelo GC.
     * Se sim, notifica o tracker e renova a sentinela.
     */
    private fun checkGC() {
        if (gcSentinel.get() == null) {
            appHealthTracker.notifyGC()
            gcSentinel = WeakReference(Any()) 
        }
    }

    /**
     * Receptor de transmissões do sistema para eventos de bateria.
     */
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10f
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            val batteryPct = if (scale > 0) (level / scale.toFloat() * 100).toInt() else level

            val currentNow = (context.getSystemService(BATTERY_SERVICE) as? BatteryManager)
                ?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) ?: 0

            appHealthTracker.trackBattery(
                BatteryMetrics(
                    level = batteryPct,
                    isCharging = isCharging,
                    temperature = temp,
                    health = getBatteryHealth(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1)),
                    currentNowMa = currentNow / 1000
                )
            )
        }
    }

    /**
     * Converte o código de saúde da bateria em uma string legível.
     */
    private fun getBatteryHealth(health: Int) = when (health) {
        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
        BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
        else -> "Unknown"
    }
}
