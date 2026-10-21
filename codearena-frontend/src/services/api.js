
import axios from "axios";

const api = axios.create({
  baseURL:
    import.meta.env.VITE_API_BASE_URL ||
    "http://localhost:8080/api",
  timeout: 15000,
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      // Server returned an HTTP error.
      console.error(
        "API error:",
        error.response.status,
        error.response.data
      );
    } else if (error.request) {
      // Request sent, but no response received.
      console.error("Backend is not responding.");
    }

    return Promise.reject(error);
  }
);

export default api;
