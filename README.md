# Jakarta Agentic AI - Reference Implementation

A reference implementation of the Jakarta Agentic AI specification, demonstrating how to build intelligent agent workflows using Jakarta EE and CDI (Contexts and Dependency Injection).

## Overview

This project provides a working implementation of an agent-based AI system that follows a structured workflow pattern:
- **Trigger**: Entry point that receives data to process
- **Decision**: Analyzes input and determines next steps
- **Action**: Performs operations based on decisions
- **Outcome**: Reports final results

The implementation uses CDI for dependency injection and demonstrates integration with Large Language Models (LLM) through a mock provider.

## Features

- **Agent Workflow Runtime**: Orchestrates agent execution through defined phases
- **CDI Integration**: Full Jakarta CDI support via Weld SE
- **LLM Abstraction**: Pluggable LLM provider interface
- **Annotation-Driven**: Simple annotations to define agent behavior
  - `@Agent` - Marks a class as an agent
  - `@Trigger` - Entry point method
  - `@Decision` - Decision-making method
  - `@Action` - Action execution method
  - `@Outcome` - Result reporting method
  - `@HandleException` - Error handling method
- **Example Agent**: Demonstrates task processing with priority-based decision making

## Architecture

```
┌─────────────────────────────────────────────┐
│            Main Application                 │
└─────────────────┬───────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────┐
│         AgentRuntime (CDI)                  │
│  - Discovers annotated methods              │
│  - Orchestrates workflow phases             │
│  - Injects dependencies                     │
└─────────────────┬───────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────┐
│         SimpleAgent (@Agent)                │
│  ┌─────────────────────────────────────┐   │
│  │ @Trigger → @Decision → @Action      │   │
│  │              ↓           ↓           │   │
│  │          @Outcome    @HandleException│   │
│  └─────────────────────────────────────┘   │
└─────────────────┬───────────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────────┐
│    MockLargeLanguageModel (LLM Provider)    │
│  - Simulates LLM responses                  │
│  - Pluggable interface for real providers   │
└─────────────────────────────────────────────┘
```

## Project Structure

```
agentic-ai-impl/
├── src/main/java/jakarta/agentic/ai/impl/
│   ├── Main.java                      # Application entry point
│   ├── MockLargeLanguageModel.java    # Mock LLM implementation
│   ├── example/
│   │   └── SimpleAgent.java           # Example agent with workflow
│   └── runtime/
│       └── AgentRuntime.java          # Agent execution runtime
├── src/main/resources/
│   └── META-INF/
│       └── beans.xml                  # CDI configuration
└── pom.xml                            # Maven configuration
```

## Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- Jakarta Agentic AI API (dependency)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/luiseufrasio/agentic-ai-impl.git
cd agentic-ai-impl
```

### 2. Build the project

```bash
mvn clean package
```

### 3. Run the demo

```bash
mvn exec:java
```

## Example Output

```
==============================================
  Jakarta Agentic AI - Reference Implementation
==============================================

>>> Example 1: High Priority Task
========================================
Starting Agent Workflow: SimpleAgent
========================================

[PHASE 1] TRIGGER
  Received task: Fix Critical Security Bug
  Description: Urgent security vulnerability needs immediate attention
  Priority: high

[PHASE 2] DECISION
  [LLM] Received prompt: Analyze this task and determine if it should be approved: Fix Critical Security Bug
  Decision: APPROVED (high priority)

[PHASE 3] ACTIONS
  Processing task: Fix Critical Security Bug
  Analysis reason: High priority task approved

[PHASE 4] OUTCOME
  Task TASK-001 finished with status: COMPLETED
  Message: Task processed successfully

========================================
Workflow Completed Successfully
========================================
```

## How It Works

### 1. Define an Agent

```java
@Agent
public class SimpleAgent {

    @Inject
    LargeLanguageModel<?> languageModel;

    @Trigger
    public void receiveTask(TaskRequest task) {
        // Entry point - receives input
    }

    @Decision
    public TaskAnalysis analyzeTask(TaskRequest task) {
        // Analyze and decide whether to continue
        String response = languageModel.invokeLargeLanguageModel(prompt, task);
        // Return null to stop workflow
        return new TaskAnalysis(true, "Approved");
    }

    @Action
    public TaskResult processTask(TaskRequest task, TaskAnalysis analysis) {
        // Perform the actual work
        return new TaskResult("COMPLETED");
    }

    @Outcome
    public void reportResult(TaskResult result) {
        // Report final results
    }

    @HandleException
    public void handleError(Throwable error, TaskRequest task) {
        // Handle any errors
    }
}
```

### 2. Execute the Agent

```java
// Initialize CDI container
Weld weld = new Weld();
try (WeldContainer container = weld.initialize()) {

    // Get runtime and agent from CDI
    AgentRuntime runtime = container.select(AgentRuntime.class).get();
    SimpleAgent agent = container.select(SimpleAgent.class).get();

    // Execute workflow
    TaskRequest task = new TaskRequest("TASK-001", "Title", "Description", "high");
    runtime.executeWorkflow(agent, task);
}
```

## Dependencies

- **Jakarta Agentic AI API** - Core API specification
- **Weld SE** (5.1.2.Final) - CDI implementation
- **Jakarta Annotations API** (2.1.1) - Annotation support

## Use Cases

This reference implementation can be adapted for:
- Automated task processing and approval workflows
- AI-powered decision support systems
- Multi-step agent orchestration
- LLM-integrated enterprise applications
- Intelligent automation pipelines

## Extending the Implementation

### Adding a Real LLM Provider

Replace `MockLargeLanguageModel` with an actual LLM integration:

```java
@ApplicationScoped
public class OpenAILanguageModel implements LargeLanguageModel<String> {

    @Override
    public String invokeLargeLanguageModel(String prompt, Object context) {
        // Call OpenAI API, Anthropic Claude, local LLM, etc.
        return callRealLLM(prompt);
    }
}
```

### Creating Custom Agents

Extend the pattern with your own agents:
- Data processing agents
- Content generation agents
- Analysis and reporting agents
- Multi-agent collaboration systems

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## License

This project is a reference implementation for educational and demonstration purposes.

## Related Projects

- [Jakarta EE](https://jakarta.ee/)
- [Weld CDI](https://weld.cdi-spec.org/)
- Jakarta Agentic AI Specification (under development)

## Contact

For questions or feedback, please open an issue on GitHub.

---

Built with Jakarta EE and powered by CDI
