import React from 'react';
import { Loader2 } from 'lucide-react';

const Message = ({ text, isUser, isThinking }) => (
  <div className={`messageRow ${isUser ? 'user' : 'ai'}`}>
    <div className={`messageBubble ${isUser ? 'user' : 'ai'}`}>
      {isThinking ? (
        <div className="thinking">
          <Loader2 className="w-4 h-4 animate-spin" />
          <span>Thinking...</span>
        </div>
      ) : (
        <p className="messageText">{text}</p>
      )}
    </div>
  </div>
);

export default Message;