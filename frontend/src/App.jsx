import React, { useState } from 'react';
import Header from './components/Header';
import ChatWindow from './components/ChatWindow';
import InputArea from './components/InputArea';
import useSessionStart from './hooks/useSessionStart';
import { API_ENDPOINT } from './config';
import './styles/components.css';

const App = () => {
  useSessionStart();

  const [input, setInput] = useState('');
  const [model, setModel] = useState('ollama');
  const [messages, setMessages] = useState([
    { id: 1, text: "Hello! I'm the GDP/Population AI Agent. Ask me for data, like 'What was the GDP of the USA in 2023?'", isUser: false, isThinking: false }
  ]);
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!input.trim() || isLoading) return;

    const userMessage = { id: Date.now(), text: input.trim(), isUser: true, isThinking: false };
    setMessages(prev => [...prev, userMessage]);
    const thinkingMessageId = Date.now() + 1;
    setMessages(prev => [...prev, { id: thinkingMessageId, text: '', isUser: false, isThinking: true }]);
    setInput('');
    setIsLoading(true);

    try {
      const response = await fetch(API_ENDPOINT, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify({ prompt: userMessage.text, model: model }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // optional simulated delay (remove in production)
      await new Promise(resolve => setTimeout(resolve, 1500));

      const aiResponseText = await response.text();

      setMessages(prev => prev.map(msg =>
        msg.id === thinkingMessageId
        ? { ...msg, text: aiResponseText || "Sorry, I encountered an error while processing your request.", isThinking: false }
        : msg
      ));

    } catch (error) {
      console.error("Error communicating with AI Agent API:", error);
      setMessages(prev => prev.map(msg =>
        msg.id === thinkingMessageId
        ? { ...msg, text: `Error: Could not connect to agent at ${API_ENDPOINT}`, isThinking: false }
        : msg
      ));
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex h-screen w-full antialiased text-gray-800 bg-gray-50 p-4">
      <div className="flex flex-col flex-auto h-full p-4 bg-white rounded-xl shadow-2xl max-w-4xl mx-auto">
        <Header model={model} setModel={setModel} isLoading={isLoading} />
        <ChatWindow messages={messages} />
        <InputArea input={input} setInput={setInput} handleSubmit={handleSubmit} isLoading={isLoading} />
      </div>
    </div>
  );
};

export default App;