package jakarta.agentic.ai.impl.example;

import jakarta.agentic.ai.*;
import jakarta.inject.Inject;

/**
 * A simple agent example demonstrating the Jakarta Agentic AI workflow.
 * This agent processes tasks and decides whether to approve or reject them.
 */
@Agent
public class SimpleAgent {

    @Inject
    LargeLanguageModel<?> languageModel;

    /**
     * TRIGGER: Entry point - receives a task to process.
     */
    @Trigger
    public void receiveTask(TaskRequest task) {
        System.out.println("  Received task: " + task.title());
        System.out.println("  Description: " + task.description());
        System.out.println("  Priority: " + task.priority());
    }

    /**
     * DECISION: Analyzes the task and decides how to proceed.
     * Returns TaskAnalysis if the task should be processed, null otherwise.
     */
    @Decision
    public TaskAnalysis analyzeTask(TaskRequest task) {
        String prompt = "Analyze this task and determine if it should be approved: " + task.title();
        String response = (String) languageModel.invokeLargeLanguageModel(prompt, task);

        // For demo: approve high priority tasks
        if ("high".equalsIgnoreCase(task.priority())) {
            System.out.println("  Decision: APPROVED (high priority)");
            return new TaskAnalysis(true, "High priority task approved", task.priority());
        } else if ("low".equalsIgnoreCase(task.priority())) {
            System.out.println("  Decision: REJECTED (low priority)");
            return null; // Stop workflow
        }

        System.out.println("  Decision: APPROVED (normal priority)");
        return new TaskAnalysis(true, "Task approved for processing", task.priority());
    }

    /**
     * ACTION: Processes the approved task.
     */
    @Action
    public TaskResult processTask(TaskRequest task, TaskAnalysis analysis) {
        System.out.println("  Processing task: " + task.title());
        System.out.println("  Analysis reason: " + analysis.reason());

        // Simulate processing
        return new TaskResult(task.id(), "COMPLETED", "Task processed successfully");
    }

    /**
     * OUTCOME: Reports the final result.
     */
    @Outcome
    public void reportResult(TaskResult result) {
        System.out.println("  Task " + result.taskId() + " finished with status: " + result.status());
        System.out.println("  Message: " + result.message());
    }

    /**
     * EXCEPTION HANDLER: Handles any errors during the workflow.
     */
    @HandleException
    public void handleError(Throwable error, TaskRequest task) {
        System.err.println("  Error processing task " + task.id() + ": " + error.getMessage());
    }

    // --- Inner record types ---

    public record TaskRequest(String id, String title, String description, String priority) {}
    public record TaskAnalysis(boolean approved, String reason, String priority) {}
    public record TaskResult(String taskId, String status, String message) {}
}
