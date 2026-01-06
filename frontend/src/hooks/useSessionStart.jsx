import { useEffect } from 'react';
import { SESSION_START_ENDPOINT } from '../config';

export default function useSessionStart() {
  useEffect(() => {
    fetch(SESSION_START_ENDPOINT, {
      method: 'GET',
      credentials: 'include'
    }).catch(err => {
      console.warn('Session start failed', err);
    });
  }, []);
}