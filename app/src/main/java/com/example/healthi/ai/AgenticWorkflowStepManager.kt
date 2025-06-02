package com.example.healthi.ai

import android.content.Context
import com.example.healthi.model.MediaPipeLLMHelper
import com.example.healthi.model.ModelType
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

/**
 * AgenticWorkflowStepManager
 * - Explicit step-by-step prompts for each model
 * - Logs and exposes every step for transparency (CoT style)
 * - Handles Wear OS data as context if available
 */
class AgenticWorkflowStepManager(
    private val context: Context,
    private val onDeviceHelper2Bit: MediaPipeLLMHelper = MediaPipeLLMHelper(context, ModelType.GEMM2_2B_2BIT),
    private val onDeviceHelper4Bit: MediaPipeLLMHelper = MediaPipeLLMHelper(context, ModelType.GEMM2_2B_4BIT),
    private val searchHelper: OnlineSearchHelper = OnlineSearchHelper(context)
) : DataClient.OnDataChangedListener {

    private val dataClient = Wearable.getDataClient(context)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Store latest wearable health metrics as context
    private var lastWearContext: String = ""

    fun startWearSync() {
        dataClient.addListener(this)
    }

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
                lastWearContext = "Latest health data: Heart rate $heartRate bpm, Steps $steps."
            }
        }
    }

    /**
     * Full, step-by-step agentic workflow with explicit prompts for each model.
     * Returns a list of steps for UI transparency.
     */
    fun processUserQueryStepByStep(
        input: String,
        use4Bit: Boolean = false,
        onStep: (String) -> Unit,
        onFinal: (String) -> Unit
    ) {
        scope.launch {
            // Step 1: Add context from wearables if available
            val contextBlock = if (lastWearContext.isNotBlank()) lastWearContext else ""
            onStep("Step 1: Context from wearables: $contextBlock")

            // Step 2: Decompose user query into sub-queries using on-device GEMM2
            val helper = if (use4Bit) onDeviceHelper4Bit else onDeviceHelper2Bit
            val cotPrompt = "Decompose the following health question into step-by-step sub-questions for reasoning:\n$input"
            onStep("Step 2: Prompt to GEMM2: $cotPrompt")
            val subQueries = helper.generateSubQueries(cotPrompt)
            onStep("Step 2: GEMM2 returned sub-queries: $subQueries")

            // Step 3: Use online search for each sub-query (if any)
            var externalContext = ""
            if (subQueries.isNotEmpty()) {
                onStep("Step 3: Fetching online context for sub-queries...")
                externalContext = searchHelper.search(subQueries)
                onStep("Step 3: Online search context: $externalContext")
            } else {
                onStep("Step 3: No sub-queries generated; skipping online search.")
            }

            // Step 4: Compose a detailed CoT prompt for the main Gemini model
            val mainPrompt = buildString {
                if (contextBlock.isNotBlank()) append(contextBlock).append("\n")
                if (externalContext.isNotBlank()) append("Relevant information from the web:\n").append(externalContext).append("\n")
                append("User question: $input\n")
                append("Step-by-step reasoning and answer:")
            }
            onStep("Step 4: Prompt to Gemini model: $mainPrompt")

            // Step 5: Run Gemini-2.0-flash-lite (on-device or fallback)
            val answer = try {
                val geminiOptions = LlmInferenceOptions.builder()
                    .setBaseOptions(
                        BaseOptions.builder()
                            .setModelAssetPath("models/gemini_2_0_flash_lite.tflite")
                            .build()
                    )
                    .build()
                val geminiTask = LlmInference.createFromOptions(context, geminiOptions)
                val result = geminiTask.predict(mainPrompt)
                onStep("Step 5: Gemini model output: ${result.text}")
                result.text
            } catch (e: Exception) {
                onStep("Step 5: Gemini model unavailable, fallback to on-device GEMM2.")
                val fallbackPrompt = "$mainPrompt\n(Answer as best as possible using available context.)"
                val fallbackResult = helper.generateAnswer(fallbackPrompt)
                onStep("Step 5: GEMM2 fallback output: $fallbackResult")
                fallbackResult
            }

            withContext(Dispatchers.Main) {
                onFinal(answer)
            }
        }
    }

    fun cancelAll() {
        scope.coroutineContext.cancelChildren()
    }
}
