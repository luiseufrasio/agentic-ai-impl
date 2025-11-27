package jakarta.agentic.ai.impl;

import jakarta.agentic.ai.LargeLanguageModel;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mock implementation of LargeLanguageModel for demonstration purposes.
 * In a real implementation, this would integrate with an actual LLM provider
 * (OpenAI, Anthropic, local models, etc.)
 */
@ApplicationScoped
public class MockLargeLanguageModel implements LargeLanguageModel<Object> {

    @Override
    public LargeLanguageModel<Object> getDefaultModel() {
        return this;
    }

    @Override
    public Object invokeLargeLanguageModel(String prompt, Object context) {
        System.out.println("  [LLM] Received prompt: " + truncate(prompt, 100));

        // Simulate LLM response based on prompt content
        if (prompt.toLowerCase().contains("documentation")) {
            return "YES - Documentation is needed for this change.";
        } else if (prompt.toLowerCase().contains("generate")) {
            return "Generated documentation content based on the analysis.";
        } else if (prompt.toLowerCase().contains("analyze")) {
            return "Analysis complete. Proceeding with workflow.";
        }

        return "Mock LLM response for: " + truncate(prompt, 50);
    }

    @Override
    public Object getUnderlyingLlm() {
        return this;
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
