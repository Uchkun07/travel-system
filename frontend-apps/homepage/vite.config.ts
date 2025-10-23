import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import path from "path";
import fs from "fs";

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src/"),
    },
  },
  server: {
    https: {
      key: fs.readFileSync(
        path.resolve(__dirname, "../../localhost+2-key.pem")
      ),
      cert: fs.readFileSync(path.resolve(__dirname, "../../localhost+2.pem")),
    },
  },
});
