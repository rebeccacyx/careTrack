// src/services/api.js
import axios from "axios";

// Get API base URL from environment variable or fallback to defaults
// Priority: VITE_API_BASE_URL > hostname detection > import.meta.env.MODE > default
const getApiBaseUrl = () => {
    // Check for explicit environment variable (highest priority)
    if (import.meta.env.VITE_API_BASE_URL) {
        let url = import.meta.env.VITE_API_BASE_URL.trim();
        // Remove trailing slash if present
        url = url.replace(/\/+$/, '');
        // Add /api suffix if not already present
        const finalUrl = url.endsWith('/api') ? url : `${url}/api`;
        console.log('🔧 API Base URL from VITE_API_BASE_URL:', finalUrl);
        return finalUrl;
    }
    
    // Detect production by checking if we're on Heroku frontend domain
    // This is more reliable than build-time mode detection
    if (typeof window !== 'undefined') {
        const hostname = window.location.hostname;
        if (hostname.includes('herokuapp.com') || hostname.includes('care-track')) {
            const prodUrl = "https://care-scheduling-app-e8951cd9f9c6.herokuapp.com/api";
            console.log('🔧 API Base URL (detected production from hostname):', prodUrl);
            return prodUrl;
        }
    }
    
    // Fallback to mode-based detection (Vite build-time)
    if (import.meta.env.MODE === "production" || import.meta.env.PROD) {
        const prodUrl = "https://care-scheduling-app-e8951cd9f9c6.herokuapp.com/api";
        console.log('🔧 API Base URL (detected from build mode):', prodUrl);
        return prodUrl;
    }
    
    // Default to localhost for development
    const devUrl = "http://localhost:8081/api";
    console.log('🔧 API Base URL (development default):', devUrl);
    return devUrl;
};

const API_BASE_URL = getApiBaseUrl();

// Export function to get base URL without /api suffix (for file serving)
export const getBaseUrl = () => {
    return API_BASE_URL.replace('/api', '');
};

// Log the final API base URL for debugging
console.log('🌐 Final API Base URL:', API_BASE_URL);

const api = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000, // 10 second timeout
    headers: {
        'Content-Type': 'application/json',
    }
});

// Request interceptor to add auth token and user headers
api.interceptors.request.use(
    (config) => {
        // Log the full URL being requested (for debugging)
        const fullUrl = `${config.baseURL || ''}${config.url}`;
        console.log(`📤 API Request: ${config.method?.toUpperCase()} ${fullUrl}`);
        
        // If data is FormData, delete Content-Type header to let browser set it with boundary
        if (config.data instanceof FormData) {
            delete config.headers['Content-Type'];
        }
        
        // Get token from sessionStorage only
        const token = sessionStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        
        // Add user ID header for backend permission checks
        const userId = sessionStorage.getItem('userId');
        if (userId) {
            config.headers['X-User-Id'] = userId;
        }
        
        // Add organization ID header if available
        const organizationId = sessionStorage.getItem('organizationId');
        if (organizationId) {
            config.headers['X-Organization-Id'] = organizationId;
        }
        
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor to handle common errors
api.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        // Log error details for debugging
        const errorUrl = error.config ? `${error.config.baseURL || ''}${error.config.url}` : 'unknown';
        console.error(`❌ API Error: ${error.response?.status || 'Network Error'} ${error.config?.method?.toUpperCase()} ${errorUrl}`);
        if (error.response) {
            console.error('Error response:', error.response.data);
        } else if (error.request) {
            console.error('Request made but no response received:', error.request);
        } else {
            console.error('Error setting up request:', error.message);
        }
        
        // Handle 401 unauthorized
        if (error.response?.status === 401) {
            // Clear tokens and redirect to login
            sessionStorage.removeItem('token');
            sessionStorage.removeItem('userId');
            sessionStorage.removeItem('inviteSubmitted');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default api;
