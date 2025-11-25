import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({

  // Configuration for the development server
  server: {
    port: 3000, // Default Vite port, useful to set explicitly
    proxy: {
      // Proxy rule: When the frontend requests a path starting with /api
      '/api': {
        // Target: Forward the request to the running Spring Boot backend
        target: 'http://localhost:8080',
        // Change the origin header to match the target (required for Spring Boot)
        changeOrigin: true,
        // Rewrite the path (optional, but good practice if needed)
        // rewrite: (path) => path.replace(/^\/api/, '') // uncomment if you need to remove /api from the path sent to the backend
      },
    },
  },

  base: '/',

  plugins: [react()],

  build: {
    outDir: 'dist',
    emptyOutDir: true,
  },
});