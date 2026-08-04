# Visual QA — every feature against the mock API

Manual pass over all 20 features using the shared
[`app-devper/mock-api`](https://github.com/app-devper/mock-api). The mock implements
every pharmacy route this client calls, so no feature is blocked by a missing endpoint.

Automated UI driving is not available: Compose renders into a WebGL canvas with no DOM,
so Playwright and friends cannot click anything. This list is for a human.

## Setup

```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentExecutableDistribution
MOCK_API_PORT=8088 \
MOCK_API_STATIC_DIR=$PWD/composeApp/build/dist/wasmJs/developmentExecutable \
../../mock-api/.venv/bin/mock-api
```

Open `http://localhost:8088/?apiBaseUrl=http://localhost:8088` and sign in as
`admin` / `qa1234` (also `super`, `manager`, `user` — same password, different role).

Mutations live in memory. `curl -X POST http://localhost:8088/__mock/reset` returns
the seed to its original state; do this between runs so the counts below still match.

## Scenarios

The mock serves three data scenarios. Switch per request with the `X-Mock-Scenario`
header, or start the server with `MOCK_API_SCENARIO=empty`:

- `seeded` (default) — the row counts in the table below
- `empty` — every collection returns `[]`, for checking empty states
- `error` — for checking error sheets

## Feature checklist

Row counts are what the seeded mock actually returns (verified against a running
instance), so a mismatch is a real finding, not a stale doc.

| # | Feature | Sidebar | Seeded content | Check visually |
|---|---------|---------|----------------|----------------|
| 1 | Sell | หน้าขายยา | 6 drugs | Search filters cards; Enter on an ambiguous match asks to confirm before adding; added-drug strip appears under the search bar; cart shows the lot/expiry line only for lots inside 90 days |
| 2 | Checkout | (in Sell) | — | Quick banknote buttons match the total; underpaying shows the shortfall, never negative change; receipt preview renders |
| 3 | KY compliance | (in Sell) | — | Adding a KY10–13 drug arms the compliance banner; precapture sheet stores details; changing the cart afterwards shows the invalidation notice |
| 4 | Sales history | ประวัติการขาย | 4 bills | Bill list, per-bill items, return sheet caps quantity at lot-bound units with the hint, void flow asks for a reason |
| 5 | Stock | สต็อกยา | 6 drugs | Type filter chips; Excel exports a CSV with Thai headers; Import navigates to นำเข้าสินค้า; low-stock and expiry metric cards |
| 6 | Drug form | (Stock → add/edit) | — | Validation appears only after an invalid save; alt units and price tiers persist |
| 7 | Lots | (Stock → lots) | per drug | Add a lot — it appears with `remaining` equal to the quantity; delete removes it |
| 8 | Adjustments | (Stock → adjust) | — | A positive/negative delta moves the drug's stock on return to the list |
| 9 | Stock count | ตรวจนับสต็อก | 1 round | Per-row variance vs system; "เติมจากระบบ" fills counts; leaving mid-count and returning restores the draft; submit asks to confirm |
| 10 | Expiry | จัดการวันหมดอายุ | 2 lots | Days-left colouring including already-expired (negative); write-off flow reports failures |
| 11 | Labels | พิมพ์ฉลาก | 6 drugs | Size picker changes the preview; print dialog opens |
| 12 | Movements | ความเคลื่อนไหวสต็อก | 3 entries | Date range + type filters; Excel export |
| 13 | Offline sync | รายการค้างซิงค์ | 0 pending | Badge in the top bar is tappable and triggers a sync; queue empties |
| 14 | Imports | นำเข้าสินค้า | 2 orders | Create an order, confirm it, check the reconcile result; supplier and drug pickers filter |
| 15 | Reorder → PO | (Stock → สั่งซื้อ) | 3 suggestions | Per-row "+" and "เพิ่มทั้งหมด" build the draft; "สร้างใบสั่งซื้อ (N)" opens the import form pre-filled with drug, qty, cost and sell |
| 16 | Suppliers | ซัพพลายเออร์ | 3 suppliers | List, detail, create/edit |
| 17 | Customers | ลูกค้า | 4 customers | List, detail with that customer's sales, create/edit |
| 18 | Reports | รายงาน | dashboard + 4 recent | Dashboard metrics, top/slow drugs, EOD figures; EOD refreshes after switching away and back; closing the day warns about unsynced bills |
| 19 | Profit | กำไร | summary | Date range, sorting, Excel export |
| 20 | KY forms | แบบฟอร์ม ขย. 9–12 | 1 row each | Each of ขย.9/10/11/12 lists, adds and exports |
| 21 | Users | จัดการผู้ใช้งาน | — | Create, edit, role change, status toggle, password reset |
| 22 | Settings | ตั้งค่าระบบ | object | Receipt tab test print; turning KY auto-capture off asks to confirm and the sell screen then shows the warning badge |
| 23 | Profile | (top bar) | — | Language chip switches the whole UI including the calendar, with no restart |
| 24 | Help | คู่มือการใช้งาน | article | Table of contents scrolls to the section and tracks the active one |

## Cross-cutting passes

Worth one sweep each rather than per feature:

- **Empty states** — restart with `MOCK_API_SCENARIO=empty` and walk the list pages; each should show its own empty copy, not a blank panel
- **Error states** — `MOCK_API_SCENARIO=error`; each page should raise the error sheet with feature-specific copy
- **Session expiry** — `POST /__mock/reset` invalidates nothing, so to test this stop the mock mid-session; the next call 401s and the login screen should say the session expired rather than appearing silently
- **Language** — switch to English from the profile chip and re-walk a few pages; no Thai should remain except printed receipts and KY official forms
- **Responsive** — check 320px, 360px, 390px, 430px, 519px, 520px, 599px, 600px and 840px; the full-width mobile drawer and compact top bar stay active below 600px, tables switch to card mode below 600px and nothing scrolls horizontally
- **Roles** — sign in as `user` and confirm admin-only sidebar entries are hidden
