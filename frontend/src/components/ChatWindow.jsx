import React, { useEffect, useRef } from 'react';
import Message from './Message';

const ChatWindow = ({ messages }) => {
  const messagesEndRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  return (
    <div className="chatWindow custom-scrollbar">
      {messages.map((msg) => (
        <Message key={msg.id} {...msg} />
      ))}
      <div ref={messagesEndRef} />
    </div>
  );
};

export default ChatWindow;