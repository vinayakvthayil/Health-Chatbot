package com.example.healthi.utils

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import android.content.Context;

public class GeminiManager {
    private static final String SYSTEM_PROMPT = "You are the best supportive health chatbot. " +
    "Provide empathetic, non-judgmental responses. you will try to understand user and will ask for more information about their query/condition to provide better answer" +
    "provide medical advice. " +
    "If user shows signs of crisis, recommend professional help.";

    private final GenerativeModelFutures model;
    private final Context context;

    public GeminiManager(Context context) {
        this.context = context;
        GenerativeModel baseModel = new GenerativeModel(
            "gemini-pro",
            BuildConfig.GEMINI_API_KEY
        );
        this.model = GenerativeModelFutures.from(baseModel);
    }

    public interface ResponseCallback {
        void onResponse(String response);
        void onError(String error);
    }

    public void generateResponse(String userMessage, ResponseCallback callback) {
        try {
            Content content = new Content.Builder()
                .addText(SYSTEM_PROMPT + "\nUser: " + userMessage)
                .build();

            Futures.addCallback(
                model.generateContent(content),
                new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        callback.onResponse(result.getText());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        callback.onError("Error generating response: " + t.getMessage());
                    }
                },
                context.getMainExecutor()
            );
        } catch (Exception e) {
            callback.onError("Error initializing request: " + e.getMessage());
        }
    }
}