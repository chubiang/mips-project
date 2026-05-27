import { defineConfig } from 'vite'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'
import tailwindcss from '@tailwindcss/vite'
import tsconfigPaths from 'vite-tsconfig-paths'


// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
    tsconfigPaths(),
    babel({ presets: [reactCompilerPreset()] })
  ],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        secure: false
      },
    },
    //allowedHosts: true // 모든 호스트 허용 (ngrok 주소가 매번 바뀌어도 프리패스!)
  }
})
