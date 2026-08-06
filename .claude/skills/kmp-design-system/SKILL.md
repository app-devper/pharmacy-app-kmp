---
name: kmp-design-system
description: Establish or extend a Compose Multiplatform design system — token layer (colors/typography/dimens/shapes), branded primitives (Button/TextField/Card/Badge/Toolbar/Table), and theme provider. Use when starting a KMP UI library or auditing a feature for "raw Material 3 widget" leakage.
---

# kmp-design-system

A design system has three layers, and you NEVER reach below the one you're using:

```
Feature screen
  ├── uses → branded primitives (<Brand>Button, <Brand>TextField, …)
  │              └── uses → tokens (colors / typography / dimens / shapes)
  └── never reaches Material 3 directly in net-new code
```

Tokens are the single source of truth; primitives are how every screen looks the same.

## 1. The token layer — `theme/DesignTokens.kt`

One data class per kind. Inject via `CompositionLocal`. Keep tokens **semantic** ("accent",
"surface", "fgMuted"), not raw ("blue500"). Raw palette stays private.

```kotlin
data class BrandColors(
    val bgPage: Color, val surface: Color, val surfaceRaised: Color,
    val fg1: Color, val fg2: Color, val fg3: Color, val fgMuted: Color,
    val accent: Color, val accentFg: Color,
    val successBg: Color, val successFg: Color,
    val warningBg: Color, val warningFg: Color,
    val dangerBg: Color, val dangerFg: Color,
    val border: Color, val borderSubtle: Color, val divider: Color,
)
data class BrandShapes(val sm: Shape, val md: Shape, val lg: Shape, val pill: Shape)
data class BrandDimens(val controlHeight: Dp = 40.dp, val gutter: Dp = 16.dp, val radiusLg: Dp = 12.dp)

data class BrandTokens(
    val colors: BrandColors,
    val shapes: BrandShapes,
    val dimens: BrandDimens,
)

val LocalBrandTokens = staticCompositionLocalOf<BrandTokens> { LightBrandTokens }
val brandTokens: BrandTokens @Composable get() = LocalBrandTokens.current
```

Typography is a `PharmText`-style object exposing `body`/`bodySm`/`micro`/`meta`/`h1`/`h2`/`h3`/
`price`/`metric`/`badge` etc. as `TextStyle`s — **not** Material's `Typography` slot. Define a
single `TextStyle.tabular()` extension for tabular numerals.

## 2. The theme provider — `theme/BrandTheme.kt`

```kotlin
@Composable
fun BrandTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val tokens = if (darkTheme) DarkBrandTokens else LightBrandTokens
    CompositionLocalProvider(LocalBrandTokens provides tokens) {
        MaterialTheme(colorScheme = tokens.toMaterialScheme()) { content() }
    }
}
```
You still wrap in `MaterialTheme` so M3 internals (ripple, IME insets, BottomSheet) inherit your
colors, but features render `<Brand>*` primitives, not raw M3.

## 3. Branded primitives — `designsystem/`

One file per primitive. Every primitive reads `brandTokens` and uses semantic colors only. No
feature code may `import androidx.compose.material3.Button/Text/Card/OutlinedTextField` in new
files — only `Icon`/`Text` (text style overridden) are tolerated.

Minimum set:

| Primitive | Replaces M3 | Notes |
|---|---|---|
| `BrandButton(label, onClick, variant, size, leadingIcon?)` | `Button` | variants Primary/Secondary/Outline/Ghost/Danger; size Sm/Md/Lg; `heightIn(min = dimens.controlHeight)` |
| `BrandTextField(value, onValueChange, placeholder, keyboardType, ...)` | `OutlinedTextField` | NO floating label slot; pair with `FormField(label) { … }` |
| `FormField(label, required?, hint?, error?) { input }` | — | static label above + helper/error below; pin single-line to `height(56.dp)` |
| `BrandFormCard(title, subtitle?) { content }` | — | rounded surface + 1dp border + h2 + 16dp inner spacing |
| `BrandListToolbar(title, subtitle, onBack?, searchValue?, filters?, actions?)` | — | page header for BOTH list and sub-pages; `onBack` renders back arrow & forces title visible |
| `BrandListResultLine(total, noun, visible?, searching?, trailing?)` | — | "ทั้งหมด N <noun>" band under the toolbar |
| `BrandFilterChip` / `BrandSingleSelectChips` / `BrandTabBar` | `FilterChip`/`TabRow` | matched `heightIn(min = controlHeight)` |
| `BrandDateRangeField` | — | one-line field with quick-period chips |
| `BrandBadge(text, tone, size)` / `BrandStatusBadge(status, label?, size)` | — | semantic tones (Green/Red/Amber/Indigo/Purple/Emerald) |
| `BrandTable(rows, columns, key)` | `LazyColumn` ad-hoc | responsive: **switches to card mode `<600dp`**, h-scroll when columns overflow; per-column `hideInCompact` / `compactTitle` |
| `BrandListCard(title, subtitle?, status?, trailing?, body?)` | — | reusable card for list rows on narrow screens |
| `BrandActionMenu(actions)` / `BrandModal` / `ErrorBottomSheet` | — | confirmation + error surfaces |
| `BrandEmptyState(icon, title, subtitle?)` / `BrandListSkeleton` | — | empty + loading states |
| `BrandIcons` | `Icons.Default.*` | SVG vector wrapper so theme can recolor — no emoji-as-icon |

Equal-height rule: search field, filter chip, button (Sm), tabs, date-range all share
`heightIn(min = dimens.controlHeight)` (default 40dp). Multi-line content expands past it
naturally via `heightIn(min = …)` (not `height(…)`).

## 4. Enforce in audit / review

Add these checks (build-time grep or review skill):

- A feature file imports `androidx.compose.material3.{Button|Text|TextField|Card|OutlinedTextField|Scaffold|FilterChip|TopAppBar}` → fail.
- A feature file uses a hex color literal (`Color(0xFF…)`) outside `theme/` → fail.
- A feature file uses an emoji string for an icon → fail (use `BrandIcons`).
- `BrandButton`/`BrandTextField`/etc. file references a hardcoded Color/Dp not from tokens → fail.

## 5. Adopting on a new KMP project

1. Pick the brand prefix (`Brand`, or `<Project>` — be consistent).
2. Create `core/ui` (or wherever your design layer lives) and put `theme/DesignTokens.kt` +
   `theme/BrandTheme.kt` + an empty `designsystem/` folder.
3. Implement primitives in this order — each one unblocks the next: tokens → `BrandButton` →
   `FormField` + `BrandTextField` → `BrandListToolbar` → `BrandFormCard` + `BrandSaveAction` →
   `BrandTable` → status/empty/skeleton.
4. Wrap your app in `BrandTheme { AppNavHost() }`.
5. Write a `Preview` showcase per primitive (`PreviewScaffold.kt` with `LightPreview`/`DarkPreview` wrappers).

Once steps 1–4 ship, the layout + code-pattern skills can produce screens that all look the same
without any per-screen styling.
