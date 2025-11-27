package jakarta.agentic.ai.impl;

import jakarta.agentic.ai.impl.example.SimpleAgent;
import jakarta.agentic.ai.impl.example.SimpleAgent.TaskRequest;
import jakarta.agentic.ai.impl.runtime.AgentRuntime;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * Main entry point demonstrating the Jakarta Agentic AI implementation.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  Jakarta Agentic AI - Reference Implementation");
        System.out.println("==============================================\n");

        // Initialize CDI container (Weld SE)
        Weld weld = new Weld();
        try (WeldContainer container = weld.initialize()) {

            // Get runtime and agent from CDI
            AgentRuntime runtime = container.select(AgentRuntime.class).get();
            SimpleAgent agent = container.select(SimpleAgent.class).get();

            // Example 1: High priority task (will be approved)
            System.out.println("\n>>> Example 1: High Priority Task");
            TaskRequest highPriorityTask = new TaskRequest(
                "TASK-001",
                "Fix Critical Security Bug",
                "Urgent security vulnerability needs immediate attention",
                "high"
            );
            runtime.executeWorkflow(agent, highPriorityTask);

            // Example 2: Normal priority task (will be approved)
            System.out.println("\n>>> Example 2: Normal Priority Task");
            TaskRequest normalTask = new TaskRequest(
                "TASK-002",
                "Update Documentation",
                "Documentation needs updating for new API",
                "normal"
            );
            runtime.executeWorkflow(agent, normalTask);

            // Example 3: Low priority task (will be rejected)
            System.out.println("\n>>> Example 3: Low Priority Task");
            TaskRequest lowPriorityTask = new TaskRequest(
                "TASK-003",
                "Refactor Old Code",
                "Non-critical code cleanup",
                "low"
            );
            runtime.executeWorkflow(agent, lowPriorityTask);

        }

        System.out.println("\n==============================================");
        System.out.println("  Demo Complete!");
        System.out.println("==============================================");
    }
}
