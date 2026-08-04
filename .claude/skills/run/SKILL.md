---
name: run
description: Launch and drive this Compose Multiplatform app for real — wasmJs build served by the shared mock-api, headless Edge rendering the WebGL canvas via SwiftShader, driven over CDP with screenshots. Use when asked to run the app, check a change in the real UI, reproduce a visual bug, or walk VISUAL_QA.md.
---

# Running the pharmacy app

The app renders into a WebGL canvas, so there is no DOM to query — but a
canvas still takes mouse and keyboard events. Drive it over CDP by
coordinates and read the result from screenshots.

## Why not the obvious paths

- **`./gradlew :composeApp:run` (desktop)** — fine on a machine with a
  display. In a headless session `screencapture` fails with "could not
  create image from display" and there is nothing to look at.
- **`--headless --disable-gpu`** — launches, screenshots, and gives you a
  **blank frame**. Chromium's software path won't back the WebGL context
  Compose needs. This is the trap; a blank PNG is a failure to launch,
  not an empty screen.
- **Playwright/Selenium selectors** — nothing to select. Coordinates only.

## Setup

```bash
# 1. build the web bundle
./gradlew :composeApp:wasmJsBrowserDevelopmentExecutableDistribution

# 2. serve it together with the mock API (repo: app-devper/mock-api)
MOCK_API_PORT=8088 \
MOCK_API_STATIC_DIR=$PWD/composeApp/build/dist/wasmJs/developmentExecutable \
../../mock-api/.venv/bin/mock-api &
curl -s -X POST http://localhost:8088/__mock/reset   # seed back to known counts

# 3. headless browser that can actually render the canvas
"/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge" \
  --headless=new --use-gl=angle --use-angle=swiftshader \
  --enable-unsafe-swiftshader --no-sandbox \
  --remote-debugging-port=9222 --window-size=1280,900 \
  --user-data-dir=/tmp/pharm-edge \
  "http://localhost:8088/?apiBaseUrl=http://localhost:8088" &
```

`--use-angle=swiftshader` is the whole trick. Chrome/Chromium works with
the same flags if Edge is not installed.

## Driving

`drive.py` next to this file takes a list of steps and needs only the
`websockets` package — the mock-api venv already has it.

```bash
DRIVE_OUT=/tmp/shots ../../mock-api/.venv/bin/python .claude/skills/run/drive.py \
  click:636:398 type:admin click:636:483 type:qa1234 click:636:551 wait:7 shot:home
```

Steps: `click:X:Y` · `type:TEXT` · `wait:SECONDS` · `shot:NAME` ·
`eval:JS`. **Look at every screenshot you take** — that is the check.

A fresh `--user-data-dir` resets theme, density, font size and language,
so set them explicitly when a test depends on them.

## Coordinates at 1280×900

Sign in as `admin` / `qa1234` (`super`, `manager`, `user` — same password).

| Target | x, y |
|---|---|
| Username / password / sign in | 636,398 · 636,483 · 636,551 |
| Sidebar item *n* (1-based) | 130, 78 + 38·(n−1) |
| Settings (sidebar) | 130,648 |
| Settings rail tab *n* | 336, 134 + 38·(n−1) — Display is 6th, 336,325 |
| Theme สว่าง / มืด / อัตโนมัติ | 497,293 · 554,293 · 620,293 |
| Font เล็ก / ปกติ / ใหญ่ / ใหญ่มาก | 492,373 · 549,373 · 608,373 · 678,373 |
| Density สบายตา / กระชับ | 507,453 · 583,453 |
| Close dialog | 1010,71 |

Sidebar entries shift with role — admin-only items are hidden for `user`.

## What to exercise

[VISUAL_QA.md](../../../VISUAL_QA.md) lists every feature with its seeded
row counts and what to look at. Scenarios switch with
`MOCK_API_SCENARIO=empty|error` on the server, or the `X-Mock-Scenario`
header per request.

Combinations that have hidden real bugs before, worth a pass whenever
table or type styling changes:

- **compact density × ใหญ่มาก font** — two-line table cells clipped
  against the sticky header
- **light theme** — `surface` and `bgPage` are both `#FFFFFF`, so
  anything relying on a surface fill alone to separate itself is
  invisible; it only shows up here, never in dark
- **widths 320 / 600 / 840** — the shell tier changes at 840 and the
  content tier at 600

## Cleanup

```bash
pkill -f "remote-debugging-port=9222"; pkill -f mock-api
```
