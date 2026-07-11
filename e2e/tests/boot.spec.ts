import { test, expect } from "@playwright/test";

test("boots from splash to the app without page errors", async ({ page }) => {
  const pageErrors: string[] = [];
  page.on("pageerror", (err) => pageErrors.push(String(err)));

  const wasmResponse = page.waitForResponse(
    (res) => res.url().endsWith(".wasm") && res.status() === 200,
  );

  const initialHtml = await (await page.request.get("/")).text();
  expect(initialHtml).toContain('id="app-loading"');

  await page.goto("/");
  await expect(page).toHaveTitle("PharmacyApp");

  await wasmResponse;
  await expect(page.locator("#app-loading")).toBeHidden({ timeout: 90_000 });

  const canvas = page.locator("canvas").first();
  await expect(canvas).toBeVisible();
  const box = await canvas.boundingBox();
  expect(box).not.toBeNull();
  expect(box!.width).toBeGreaterThan(300);
  expect(box!.height).toBeGreaterThan(300);

  expect(pageErrors).toEqual([]);
});

test("renders visible content after boot", async ({ page }) => {
  await page.goto("/");
  await expect(page.locator("#app-loading")).toBeHidden({ timeout: 90_000 });
  await page.waitForTimeout(1_500);

  const shot = await page.screenshot();
  console.log("boot screenshot bytes:", shot.byteLength);
  expect(shot.byteLength).toBeGreaterThan(20_000);
});
