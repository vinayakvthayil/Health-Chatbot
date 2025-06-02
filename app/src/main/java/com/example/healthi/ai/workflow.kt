package com.example.healthi.workflow

import android.content.Context
import com.example.healthi.model.MediaPipeLLMHelper
import com.example.healthi.model.OnlineSearchHelper
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.Wearable
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.genai.LlmInference
import com.google.mediapipe.tasks.genai.LlmInferenceOptions
import kotlinx.coroutines.*

class AgenticWorkflowManager(
    private val context: Context,
    private val onDeviceHelper: MediaPipeLLMHelper = MediaPipeLLMHelper(context, ModelType.GEMM2_2B_2BIT),
    private val searchHelper: OnlineSearchHelper = OnlineSearchHelper(context)
) : DataClient.OnDataChangedListener {

    private val dataClient = Wearable.getDataClient(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /** Begin listening for Wear OS health metrics. */
    fun startWearSync() {
        dataClient.addListener(this)
    }

    /** Stop listening for Wear OS data. */
    fun stopWearSync() {
        dataClient.removeListener(this)
    }

    override fun onDataChanged(events: DataEventBuffer) {
        for (event in events) {
            if (event.type == DataEvent.TYPE_CHANGED &&
                event.dataItem.uri.path == "/health_metrics"
            ) {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap
                val heartRate = dataMap.getInt("heart_rate")
                val steps = dataMap.getLong("steps")
                // TODO: forward these metrics to your ViewModel or local store
            }
        }
    }

    /**
     * Run the full agentic workflow:
     * 1. Decompose with on-device GEMM2 → sub-queries
     * 2. Fetch external context via Brave Search if needed
     * 3. Generate final answer with Gemini-2.0-flash-lite (fallback to on-device on error)
     */
    fun processUserQuery(input: String, onResult: (String) -> Unit) {
        scope.launch {
            // 1. On-device decomposition
            val subQueries = onDeviceHelper.generateSubQueries(input)

            // 2. Online search context
            val externalContext = if (subQueries.isNotEmpty()) {
                searchHelper.search(subQueries)
            } else ""

            // 3. Build combined prompt
            val prompt = buildString {
                if (externalContext.isNotBlank()) append(externalContext).append("\n")
                append(input)
            }

            // 4. Main Gemini inference with fallback
            val answer = try {
                val geminiOptions = LlmInferenceOptions.builder()
                    .setBaseOptions(
                        BaseOptions.builder()
                            .setModelAssetPath("models/gemini_2_0_flash_lite.tflite")
                            .build()
                    )
                    .build()
                val geminiTask = LlmInference.createFromOptions(context, geminiOptions)
                geminiTask.predict(prompt).text
            } catch (e: Exception) {
                onDeviceHelper.generateAnswer(input, externalContext)
            }

            withContext(Dispatchers.Main) {
                onResult(answer)
            }
        }
    }

    /** Cancel any in-flight operations. */
    fun cancelAll() {
        scope.coroutineContext.cancelChildren()
    }
}
