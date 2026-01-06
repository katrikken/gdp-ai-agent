import React from 'react';

const Header = ({ model, setModel, isLoading }) => (
  <div className="header">
    <h1 className="headerTitle">
      <span className="pulseDot" />
      GDP-AI Data Agent
    </h1>

    <div className="headerControls">
      <label htmlFor="modelSelect" className="modelLabel">Model</label>
      <select
        id="modelSelect"
        value={model}
        onChange={(e) => setModel(e.target.value)}
        className="modelSelect"
        disabled={isLoading}
      >
        <option value="ollama">ollama</option>
        <option value="openai">openai</option>
      </select>
    </div>
  </div>
);

export default Header;