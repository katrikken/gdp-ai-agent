import React, { useState, useRef, useEffect } from 'react';
import { Send, Loader2 } from 'lucide-react';

const API_ENDPOINT = 'http://localhost:8080/chat';

const Message = ({ text, isUser, isThinking }) => (
  <div className={`flex w-full ${isUser ? 'justify-end' : 'justify-start'}`}>
    <div
      className={`max-w-3/4 p-3 my-1 rounded-xl shadow-md transition-all duration-300
                 ${isUser
                    ? 'bg-blue-600 text-white rounded-br-none'
                    : 'bg-gray-100 text-gray-800 rounded-tl-none'}`
                 }
    >
      {isThinking ? (
        <div className="flex items-center space-x-2">
          <Loader2 className="w-4 h-4 animate-spin" />
          <span>Thinking...</span>
        </div>
      ) : (
        <p className="whitespace-pre-wrap">{text}</p>
      )}
    </div>
  </div>
);

const App = () => {
  const [input, setInput] = useState('');
  const [model, setModel] = useState('ollama'); // default model
  const [messages, setMessages] = useState([
    { id: 1, text: "Hello! I'm the GDP/Population AI Agent. Ask me for data, like 'What was the GDP of the USA in 2023?'", isUser: false, isThinking: false }
  ]);
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(scrollToBottom, [messages]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!input.trim() || isLoading) return;

    const userMessage = { id: Date.now(), text: input.trim(), isUser: true, isThinking: false };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setIsLoading(true);

    // Add a placeholder response for the AI while waiting
    const thinkingMessageId = Date.now() + 1;
    setMessages(prev => [...prev, { id: thinkingMessageId, text: '', isUser: false, isThinking: true }]);

    // --- API Integration (Placeholder/Conceptual) ---
    try {
      // NOTE: Replace this with your actual fetch logic to your Spring Boot API
      const response = await fetch(API_ENDPOINT, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ prompt: input.trim(), model: model }), // Send prompt as JSON payload
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Simulating a slow API response with a delay (remove in production)
      await new Promise(resolve => setTimeout(resolve, 1500));

      const aiResponseText = await response.text();

      // Update the thinking message with the real response
      setMessages(prev => prev.map(msg =>
        msg.id === thinkingMessageId
        ? { ...msg, text: aiResponseText || "Sorry, I encountered an error while processing your request.", isThinking: false }
        : msg
      ));

    } catch (error) {
      console.error("Error communicating with AI Agent API:", error);
      // Replace the thinking message with an error message
      setMessages(prev => prev.map(msg =>
        msg.id === thinkingMessageId
        ? { ...msg, text: `Error: Could not connect to agent at ${API_ENDPOINT}`, isThinking: false }
        : msg
      ));
    } finally {
      setIsLoading(false);
    }
    // -------------------------------------------------
  };

  return (
    <div className="flex h-screen w-full antialiased text-gray-800 bg-gray-50 p-4">
      <div className="flex flex-col flex-auto h-full p-4 bg-white rounded-xl shadow-2xl max-w-4xl mx-auto">

        {/* Header */}
        <div className="flex items-center justify-between pb-3 border-b border-gray-200">
            <h1 className="text-2xl font-extrabold text-gray-700 flex items-center">
                <span className="bg-blue-600 w-3 h-3 rounded-full mr-2 animate-pulse"></span>
                GDP-AI Data Agent
            </h1>


            {/* Model selector */}
            <div className="flex items-center space-x-2">
              <label htmlFor="modelSelect" className="text-sm text-gray-600">Model</label>
              <select
                id="modelSelect"
                value={model}
                onChange={(e) => setModel(e.target.value)}
                className="border border-gray-300 rounded-lg p-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                disabled={isLoading}
              >
                <option value="ollama">ollama</option>
                <option value="openai">openai</option>
              </select>
            </div>
        </div>

        {/* Message Area */}
        <div className="flex flex-col flex-auto h-full p-4 overflow-y-auto space-y-4 custom-scrollbar">
          {messages.map((msg) => (
            <Message key={msg.id} {...msg} />
          ))}
          <div ref={messagesEndRef} />
        </div>

        {/* Input Area */}
        <div className="mt-4 pt-4 border-t border-gray-200">
          <form onSubmit={handleSubmit} className="flex flex-row items-center space-x-3">
            <input
              type="text"
              className="flex-grow w-full border border-gray-300 rounded-xl p-3 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors"
              placeholder="Ask a question about GDP, Population, or countries..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              disabled={isLoading}
            />
            <button
              type="submit"
              className={`flex items-center justify-center h-12 w-12 rounded-full text-white transition-all duration-300 shadow-lg
                          ${isLoading ? 'bg-gray-400 cursor-not-allowed' : 'bg-blue-600 hover:bg-blue-700 active:bg-blue-800'}`}
              disabled={isLoading}
            >
              {isLoading ? (
                <Loader2 className="h-5 w-5 animate-spin" />
              ) : (
                <Send className="h-5 w-5" />
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default App;