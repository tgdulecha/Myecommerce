import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  test: {
    environment: 'happy-dom',
    globals: true,
    // Scoped to src/ so this never picks up the Playwright specs in tests/e2e,
    // which use a different test runner/API and aren't Vitest-compatible.
    include: ['src/**/*.{test,spec}.{js,mjs,cjs}'],
  },
})
