#📊 AI Data Agent: GDP and Population Query

Welcome to the AI Data Agent! This Spring Boot application serves as an intelligent agent, capable of answering natural language questions about global GDP and Population data stored within its embedded database. It leverages the power of the OpenAI model through Spring AI to provide accurate, data-grounded responses.

##✨ Capabilities

This agent is designed to bridge the gap between structured data and natural language queries. You can ask questions like:

-"What is the population of the USA in 2023?"
-"Compare the GDP of Japan and Germany."
-"Show me the population trend over the last few years for Canada."

The agent will interpret your request, query the underlying database, and present the findings in a friendly, conversational manner.

##🚀 Installation & Setup

###Prerequisites

Before building and running the application, ensure you have the following software installed:

-Java Development Kit (JDK): Version 21.
-Apache Maven: Version 3.9.11 or newer.

###Build the Application

1) Use Maven to clean the project and build the executable JAR file.
```
mvn clean install
```

2) Configuration: OpenAI API Key

Since the agent relies on the OpenAI model for natural language processing, you must provide your API key.
Before running the application, open your src/main/resources/application.yml file and add the following configuration, replacing the placeholder with your actual key:

```
spring.ai.openai.api-key: {your api key}
```

###🛠️ Running the Application

Once built and configured, you can start the Spring Boot application directly using the Maven plugin:

```
mvn spring-boot:run
```


The application will start on port 8080.

###🌐 Access Points

You can interact with the running application using the following interfaces:

| Interface        | URL                              | Description                                                                                  | 
|:-----------------|:---------------------------------|:---------------------------------------------------------------------------------------------|
| Frontend UI      | http://localhost:8080            | Access the simple web interface to chat with the agent.                                      |
| API Endpoint     | http://localhost:8080/chat       | The main REST endpoint for external application access (e.g., via curl).                     |
| Database Console | http://localhost:8080/h2-console | Access the embedded H2 database console to view the stored GDP and Population data directly. |

###Example API Usage (cURL)

You can test the chat endpoint from your terminal:

```
curl -X POST http://localhost:8080/chat -H "Content-Type: text/plain" --data-raw "Which country had the highest GDP in 2023?"
```


