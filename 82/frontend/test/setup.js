import { vi } from 'vitest'

// Ensure that the "window" object exists (it is automatically available in the jsdom environment, but for safety, we'll add an extra layer).
if (typeof window === 'undefined') {
    global.window = {}
}

// Simulate sessionStorage (to prevent "window is not defined" error in userService.test.js)
if (!window.sessionStorage) {
    window.sessionStorage = {
        getItem: vi.fn(),
        setItem: vi.fn(),
        removeItem: vi.fn(),
        clear: vi.fn(),
    }
}

// Simulate localStorage
if (!window.localStorage) {
    window.localStorage = {
        getItem: vi.fn(),
        setItem: vi.fn(),
        removeItem: vi.fn(),
        clear: vi.fn(),
    }
}

// Start scanning for simulated window.location from the project root directory
if (!window.location) {
    window.location = {
        assign: vi.fn(),
        reload: vi.fn(),
        href: '',
    }
}
