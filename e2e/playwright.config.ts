import { defineConfig } from "@playwright/test";

export default defineConfig({
  testDir: "./tests",
  timeout: 120_000,
  retries: process.env.CI ? 1 : 0,
  reporter: process.env.CI ? [["list"], ["html", { open: "never" }]] : "list",
  use: {
    baseURL: "http://127.0.0.1:4317",
    trace: "retain-on-failure",
  },
  webServer: {
    command: "npx http-server ../composeApp/build/dist/wasmJs/productionExecutable -p 4317 -s",
    url: "http://127.0.0.1:4317",
    reuseExistingServer: !process.env.CI,
  },
});
