import React from 'react';
import { Send, Loader2 } from 'lucide-react';

const InputArea = ({ input, setInput, handleSubmit, isLoading }) => (
  <div className="inputArea">
    <form onSubmit={handleSubmit} className="form">
      <input
        type="text"
        className="input"
        placeholder="Ask a question about GDP, Population, or countries..."
        value={input}
        onChange={(e) => setInput(e.target.value)}
        disabled={isLoading}
      />
      <button
        type="submit"
        className={`sendBtn ${isLoading ? 'loading' : 'ready'}`}
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
);

export default InputArea;