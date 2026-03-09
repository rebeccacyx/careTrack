/// <reference types="vitest" />
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
    plugins: [vue()],
    server: {
        port: 5173,
    },
    resolve: {
        alias: {
            '@': resolve(__dirname, 'src'), // Let @ point to src
        },
    },
    test: {
        root: '.', // Start scanning from the project root directory
        globals: true,
        environment: 'jsdom',
        include: ['test/**/*.test.js'], // Test file directory
        coverage: {
            provider: 'v8',
            reporter: ['text', 'html'],
        },
        setupFiles: ['test/setup.js'], // If you want to add global mocks later.
    },
})
