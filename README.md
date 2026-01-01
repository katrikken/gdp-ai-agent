# 📊 AI Data Agent: GDP and Population Query

Welcome to the AI Data Agent! This Spring Boot application serves as an intelligent agent, capable of answering natural language questions about global GDP and Population data stored within its embedded database. It leverages the power of the OpenAI model through Spring AI to provide accurate, data-grounded responses.

## ✨ Capabilities

This agent is designed to bridge the gap between structured data and natural language queries. You can ask questions like:

- "What is the population of the USA in 2023?"
- "Compare the GDP of Japan and Germany."
- "Show me the population trend over the last few years for Canada."

The agent will interpret your request, query the underlying database, and present the findings in a friendly, conversational manner.

## 🏛️ Architecture Overview

The application follows a standard layered architecture, with specific components designed to expose data and analytical capabilities to the Large Language Model (LLM) via Spring AI's Tool mechanism.

1. Data Layer (Database)

The data is persisted in an H2 in-memory database and structured across three primary tables and one calculated view:

```Country``` Table: Stores metadata for countries (e.g., country_code, name).

```Gdp``` Table: Stores the Gross Domestic Product data, linked to the Country table by ID, for specific years and values.

```Population``` Table: Stores population figures, linked to the Country table by ID, for specific years and values.

```GdpPerCapitaView``` View: A calculated view that joins GdpData and PopulationData to provide the GDP per capita metric for all countries and years.

2. Persistence Layer (JPA Repositories)

The application uses Spring Data JPA to abstract database interactions. Dedicated repositories provide basic CRUD and complex query capabilities for each entity:

```CountryRepository```

```GdpRepository```

```PopulationRepository```

```GdpPerCapitaViewRepository``` 

3. Service Layer (AI Tools)

```tool package``` The services are the core logic layer and act as the tools that the AI agent can invoke to execute data queries. These services contain business logic, coordinate data retrieval from repositories, and are automatically registered with the Spring AI framework as callable functions.
```service package``` The services here are loaders and parsers that retrieve data from provided URLs and save them to the database. The loaders have ```@PostConstruct``` annotation to trigger data loading after startup.

4. REST 

```ChatController``` is the entry point for API calls that sends the prompt to the configured ChatModel

## 🚀 Installation & Setup

### Prerequisites

Before building and running the application, ensure you have the following software installed:

- Java Development Kit (JDK): Version 21.
- Apache Maven: Version 3.9.11 or newer.

### Build the Application

1) Use Maven to clean the project and build the executable JAR file.
```
mvn clean install
```

2) Configuration: OpenAI API Key

Since the agent relies on the OpenAI model for natural language processing, you must provide your API key.
Before running the application, create file *src/main/resources/secrets.yml* file and add the following configuration, replacing the placeholder with your actual key:

```
spring.ai.openai.api-key: {your api key}
```
3) Configuration for Ollama

- Install Ollama following official instructions from https://docs.ollama.com/quickstart
- run the Mistral model or the model of choice that supports tools and is set in the application.yml

```
ollama run mistral
```


### 🛠️ Running the Application

Once built and configured, you can start the Spring Boot application directly using the Maven plugin:

```
mvn spring-boot:run
```


The application will start on port 8080.

### 🌐 Access Points

You can interact with the running application using the following interfaces:

| Interface        | URL                              | Description                                                                                  | 
|:-----------------|:---------------------------------|:---------------------------------------------------------------------------------------------|
| Frontend UI      | http://localhost:8080            | Access the simple web interface to chat with the agent.                                      |
| API Endpoint     | http://localhost:8080/chat       | The main REST endpoint for external application access (e.g., via curl or for BI tools).     |
| Database Console | http://localhost:8080/h2-console | Access the embedded H2 database console to view the stored GDP and Population data directly. |

### Example API Usage (cURL)

You can test the chat endpoint from your terminal:

```
curl -X POST http://localhost:8080/chat -H "Content-Type: text/plain" --data-raw "Which country had the highest GDP in 2023?"
```


