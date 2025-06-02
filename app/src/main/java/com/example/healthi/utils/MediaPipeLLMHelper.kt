package com.example.healthi.utils


import android.content.Context
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.genai.LlmInference
import com.google.mediapipe.tasks.genai.LlmInferenceOptions

/**
 * Supported on-device LLM variants.
 * - GEMM2_2B_2BIT: 2-bit quantized, 2B parameters
 * - GEMM2_2B_4BIT: 4-bit quantized, 2B parameters
 */
enum class ModelType(val assetPath: String) {
    GEMM2_2B_2BIT("models/gemm2_2b_2bit.tflite"),
    GEMM2_2B_4BIT("models/gemm2_2b_4bit.tflite")
}

/**
 * Wraps MediaPipe’s GenAI LLM Inference task with runtime model selection.
 *
 * @param context Android context used to load the .tflite asset.
 * @param modelType Which quantization variant to load.
 */
class MediaPipeLLMHelper(
    context: Context,
    modelType: ModelType = ModelType.GEMM2_2B_2BIT
) {
    private val llmTask: LlmInference

    init {
        // Configure base options with the chosen model asset
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath(modelType.assetPath)
            .build()

        // Build LLM inference options
        val options = LlmInferenceOptions.builder()
            .setBaseOptions(baseOptions)
            .build()

        // Create the inference task
        llmTask = LlmInference.createFromOptions(context, options)
    }

    /**
     * Generates decomposition sub-queries for step-by-step reasoning.
     */
    fun generateSubQueries(input: String): List<String> {
        val result = llmTask.predict(input)
        return result.subQueriesList
    }

    /**
     * Produces the final answer by combining optional context with the user query.
     *
     * @param input Original user question.
     * @param context Optional external or historical context to prepend.
     * @return The model’s textual response.
     */
    fun generateAnswer(input: String, context: String = ""): String {
        val prompt = if (context.isNotBlank()) "$context\n$input" else input
        val result = llmTask.predict(prompt)
        return result.text
    }

    /**
     * (Optional) Summarizes and persists the response for future context.
     */
    fun exportSummary(answer: String) {
        // TODO: implement summary extraction and local storage
    }
}
