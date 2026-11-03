package com.sermonprep.llm;

import com.google.ai.edge.litertlm.Backend;
import com.google.ai.edge.litertlm.Conversation;
import com.google.ai.edge.litertlm.ConversationConfig;
import com.google.ai.edge.litertlm.Engine;
import com.google.ai.edge.litertlm.EngineConfig;

/**
 * Thin Java wrapper around LiteRT-LM using only plain types (String, int).
 *
 * Why Java: LiteRT-LM is compiled with Kotlin 2.4 metadata, which the app's Kotlin 2.0 compiler
 * refuses to read. The Java compiler ignores Kotlin metadata, so this module can call it, and the
 * app only sees this class. Not thread-safe; callers serialise access.
 */
public final class LlmBridge {
    private final Engine engine;

    /** Creates the engine object but does not load the model yet. */
    public LlmBridge(String modelPath, String cacheDir, int maxTokens) {
        EngineConfig config = new EngineConfig(
                modelPath,
                new Backend.CPU(null, null),
                null,   // vision backend
                null,   // audio backend
                maxTokens,
                null,   // max images
                cacheDir);
        engine = new Engine(config);
    }

    /** Loads the model. Slow (10+ seconds) and memory hungry: call off the main thread. */
    public void start() {
        engine.initialize();
    }

    /** One-shot generation. Every call uses a fresh conversation, so earlier prompts never leak in. */
    public String generate(String prompt) {
        Conversation conversation = engine.createConversation(new ConversationConfig());
        try {
            return conversation.sendMessage(prompt).toString();
        } finally {
            conversation.close();
        }
    }

    /** Releases the model. Safe to call more than once. */
    public void close() {
        try {
            engine.close();
        } catch (RuntimeException ignored) {
            // Already closed or never initialised.
        }
    }
}
