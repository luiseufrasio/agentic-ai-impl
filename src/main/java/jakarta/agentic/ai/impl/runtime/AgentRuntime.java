package jakarta.agentic.ai.impl.runtime;

import jakarta.agentic.ai.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple runtime for executing agent workflows.
 * Discovers and invokes methods annotated with @Trigger, @Decision, @Action, @Outcome.
 */
@ApplicationScoped
public class AgentRuntime {

    @Inject
    private LargeLanguageModel<?> languageModel;

    /**
     * Executes an agent workflow with the given trigger data.
     */
    public <T> void executeWorkflow(Object agent, T triggerData) {
        System.out.println("\n========================================");
        System.out.println("Starting Agent Workflow: " + agent.getClass().getSimpleName());
        System.out.println("========================================\n");

        try {
            // Inject LargeLanguageModel into agent
            injectDependencies(agent);

            // 1. Execute Trigger
            System.out.println("[PHASE 1] TRIGGER");
            invokeTrigger(agent, triggerData);

            // 2. Execute Decision
            System.out.println("\n[PHASE 2] DECISION");
            Object decisionResult = invokeDecision(agent, triggerData);

            if (decisionResult == null) {
                System.out.println("\n[WORKFLOW STOPPED] Decision returned null - no further action needed.");
                return;
            }

            // 3. Execute Actions
            System.out.println("\n[PHASE 3] ACTIONS");
            Object actionResult = invokeActions(agent, triggerData, decisionResult);

            // 4. Execute Outcome
            System.out.println("\n[PHASE 4] OUTCOME");
            invokeOutcome(agent, actionResult != null ? actionResult : decisionResult);

            System.out.println("\n========================================");
            System.out.println("Workflow Completed Successfully");
            System.out.println("========================================\n");

        } catch (Exception e) {
            System.out.println("\n[ERROR] Exception during workflow");
            invokeExceptionHandler(agent, e, triggerData);
        }
    }

    private void injectDependencies(Object agent) throws Exception {
        for (Field field : agent.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class) &&
                LargeLanguageModel.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                field.set(agent, languageModel);
            }
        }
    }

    private <T> void invokeTrigger(Object agent, T triggerData) throws Exception {
        for (Method method : agent.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Trigger.class)) {
                method.setAccessible(true);
                method.invoke(agent, triggerData);
                return;
            }
        }
    }

    private <T> Object invokeDecision(Object agent, T triggerData) throws Exception {
        for (Method method : agent.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Decision.class)) {
                method.setAccessible(true);
                return method.invoke(agent, triggerData);
            }
        }
        return true; // No decision method, continue workflow
    }

    private Object invokeActions(Object agent, Object triggerData, Object decisionResult) throws Exception {
        Object lastResult = decisionResult;
        List<Method> actionMethods = new ArrayList<>();

        for (Method method : agent.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Action.class)) {
                actionMethods.add(method);
            }
        }

        for (Method method : actionMethods) {
            method.setAccessible(true);
            Object[] params = buildParameters(method, triggerData, decisionResult, lastResult);
            Object result = method.invoke(agent, params);
            if (result != null) {
                lastResult = result;
            }
        }

        return lastResult;
    }

    private void invokeOutcome(Object agent, Object data) throws Exception {
        for (Method method : agent.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Outcome.class)) {
                method.setAccessible(true);
                method.invoke(agent, data);
                return;
            }
        }
    }

    private <T> void invokeExceptionHandler(Object agent, Exception error, T triggerData) {
        try {
            for (Method method : agent.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(HandleException.class)) {
                    method.setAccessible(true);
                    method.invoke(agent, error, triggerData);
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Error in exception handler: " + e.getMessage());
        }
        error.printStackTrace();
    }

    private Object[] buildParameters(Method method, Object triggerData, Object decisionResult, Object lastResult) {
        Class<?>[] paramTypes = method.getParameterTypes();
        Object[] params = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            if (paramTypes[i].isInstance(triggerData)) {
                params[i] = triggerData;
            } else if (paramTypes[i].isInstance(decisionResult)) {
                params[i] = decisionResult;
            } else if (paramTypes[i].isInstance(lastResult)) {
                params[i] = lastResult;
            }
        }
        return params;
    }
}
