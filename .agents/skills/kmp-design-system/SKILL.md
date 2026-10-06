---
name: kmp-design-system
description: The pharmacy app's design system — token layer (colors/spacing/radii/shapes/dimens), Pharm* primitives in :core:ui/designsystem/, and PharmacyTheme. Use when adding or changing a primitive, picking a token, or auditing a feature for raw Material 3 leakage.
---

# kmp-design-system

Three layers, and you never reach below the one you're using:

```
:features:<x> screen
  ├── uses → Pharm* primitives (:core:ui/designsystem/)
  │              └── uses → tokens (:core:ui/theme/DesignTokens.kt)
  └── never reaches Material 3 directly in net-new code
```

Everything lives in `:core:ui` under `app.devper.pharm.ui`.

## 1. Tokens — `theme/DesignTokens.kt`

Five `@Immutable` data classes composed into `PharmTokens`, provided through
`LocalPharmTokens` and read as `pharmTokens` (a `@ReadOnlyComposable` getter):

```kotlin
val t = pharmTokens
t.colors.surface        // PharmColors — semantic only
t.spacing.s4            // PharmSpacing — s0_5 … s10 (2dp … 40dp)
t.radii.lg              // PharmRadii  — sm/md/lg/xl/pill
t.shapes.lg             // PharmShapes — the same ladder as RoundedCornerShape
t.dimens.controlHeight  // PharmDimens
t.fontScale             // 1f unless the user picked a font size
```

`PharmColors` is semantic, never raw. The raw palette (`InkNavy`, `AzureDeep`,
`Gray100`, …) is private to `theme/Color.kt` and only `LightPharmColors` /
`DarkPharmColors` may name it. Groups worth knowing:

| Group | Members |
|---|---|
| Surfaces | `bgPage` `surface` `surfaceRaised` |
| Sidebar | `sidebarBg` `sidebarFg` `sidebarFgMuted` `sidebarItemHover` `sidebarItemActive` |
| Text | `fg1` `fg2` `fg3` `fgMuted` |
| Lines | `border` `borderSubtle` `divider` |
| Interaction | `hoverSurface` `hoverSurfaceRaised` `selectedSurface` `focusRing` |
| Accent | `accent` `accentHover` `accentBgSoft` |
| Actions | `primaryActionBg/Fg` `secondaryActionBg/BgHover/Fg` `dangerActionBg/Fg` |
| Status | `successBg/Fg` `warningBg/Fg` `dangerBg/Fg` `infoBg/Fg` |
| Domain | `price` `discount` `ky9*` … `ky12*` `typePurple*` `typeEmerald*` `typeOrange*` `indigo*` |

Dark is `LightPharmColors.copy(...)`, so a new color added to the light set
inherits the light value in dark until you override it — always add both.

`PharmDimens` carries the layout constants the responsive rules depend on:
`sidebarWidth` 260, `topbarHeight` 56 / `compactTopbarHeight` 52,
`listWorkspaceMaxWidth` = `formContentMaxWidth` = 768, `readingContentMaxWidth`
880, `dashboardContentMaxWidth` 1040, `controlHeight` 48,
`buttonSmHeight` 32 / `buttonMdHeight` 36, `minimumTouchTarget` 36 —
raised to 44 on touch-primary platforms by `PharmTokens.forTouchInput()`.

**Typography is not Material's `Typography` slot.** `theme/Typography.kt`
exposes a `PharmText` object of `TextStyle`s: `body` `bodySm` `meta` `micro`
`thead` `price` `total` `metric` `displayTotal` `buttonMd` `buttonSm` `badge`
`badgeSm` `h1` `h2` `h3`. Numeric columns get `.tabular()` (font feature
`tnum`). Font family is Sarabun.

## 2. Theme — `theme/Theme.kt`

```kotlin
PharmacyTheme(
    darkTheme = …,      // isSystemInDarkTheme() by default
    fontScale = 1f,     // user font-size preference
    touchPrimary = false,
) { content() }
```

It provides `LocalPharmTokens` **and** wraps `MaterialTheme` (M3 color scheme +
`pharmacyTypography()` + `PharmacyShapes`) so M3 internals — ripple, IME
insets, bottom sheet scrims — inherit the palette. Features still render
`Pharm*`, not raw M3.

Runtime theme/density/font-scale state lives in `theme/ThemeController.kt`;
density is a separate `LocalPharmDensity` (`designsystem/PharmDensity.kt`) that
`PharmTable` reads for row height.

## 3. Primitives — `designsystem/`

One file per primitive; each reads `pharmTokens` and uses semantic colors only.

| Primitive | Replaces | Notes |
|---|---|---|
| `PharmButton(label, onClick, variant, size, enabled, loading, leadingIcon)` | `Button` | variants Primary/Secondary/Outline/Ghost/Danger, sizes Sm/Md/Lg; a `content: @Composable` overload exists for non-label buttons |
| `PharmIconButton` / `PharmActionMenu` | `IconButton` / dropdown | menu becomes a bottom sheet under `isCompactContent` |
| `PharmTextField` + `FormField(label, required, hint, error)` | `OutlinedTextField` | no floating label — the label is `FormField`'s |
| `PharmSearchField(value, onValueChange, placeholder, onSearch?, searching, endSlot?)` | — | the only search input |
| `PharmCheckbox` / `PharmToggleSwitch` / `PharmKeypad` | M3 equivalents | |
| `PharmBadge(text, tone, size)` / `PharmStatusBadge` / `KyBadge(form)` / `RoleBadge` / `PriceTierBadge` | — | tones Gray/Green/Red/Amber/Blue/Indigo/Purple/Emerald; `PharmStatus.tone()` + `.label(s)` map domain status → badge |
| `PharmTable(rows, columns, key, onRowClick?, emptyContent?, bottomRow?)` | ad-hoc `LazyColumn` | card mode below `cardModeMaxWidth` (default `PharmBreakpoint.Medium` = 600dp); row height comes from `LocalPharmDensity` unless overridden |
| `PharmStaticTable` / `PharmStickyTotalRow` / `PharmTableSurface` | — | non-lazy table, totals row, the bordered surface frame |
| `PharmListCard` / `DrugCard` / `MetricCard` | — | card row for compact mode, drug tile, dashboard metric |
| `PharmModal` / `PharmBottomSheet` / `ErrorBottomSheet` | `AlertDialog` / `ModalBottomSheet` | `ErrorBottomSheet` lives in `ui/components/` |
| `PharmEmptyState(title, subtitle?, icon?, action?)` / `PharmErrorState(onRetry?)` / `PharmListSkeleton` / `PharmCircularProgress` | — | the three list states; all in `PharmFeedbackState.kt` except `PharmErrorState` |
| `PharmDivider` / `PharmVerticalDivider` | `HorizontalDivider` | never hand-roll a 1dp `Box` |
| `PharmIcons` | `Icons.Default.*` | 45 SVG vectors — no emoji-as-icon |
| `PharmDatePicker` / `PharmCalendar` / `PharmDateRangeField` | M3 `DatePicker` | M3's picker is fully removed; keeps its UTC-millis contract |
| `PharmAvatarCircle` / `PharmBrandMark` / `PharmStamp` / `PharmHelpHint` / `PharmMiniBarChart` | — | |

**Page scaffold** primitives are the ones every screen shares — see
`kmp-layout-pattern` for how they fit together: `PharmListScaffold`,
`PharmListToolbar`, `PharmListResultLine`, `PharmFormCard`, `PharmSaveAction`,
`PharmTabBar`, `PharmFilterChips`, `PharmSidebar`, `PharmTopbar`,
`CollapsibleHeader`.

Shared helpers worth reusing instead of re-deriving: `toggleSurface(active,
hovered, colors)` / `toggleBorder(active, colors)` (`ToggleSurface.kt`) back
every chip, tab and rail row; `PharmMotion` holds the durations and
`LocalReducedMotion` disables them.

**Equal-height rule**: search field, filter chip, Sm button, tabs and the
date-range field all share `heightIn(min = …)` off `PharmDimens` — never
`height(…)`, or two-line content clips.

## 4. What is actually enforced

`auditArchitecture` does **not** check design-system usage. Its rules are
A10/A17/A19/A20/A23–A29 (module boundaries, DTO conventions, platform folders,
no `expect`, typed errors, no Thai literals) — see `kmp-build-logic`.

So these are review-time conventions, checked by `kmp-review` / the
`kmp-reviewer` agent, not by the build:

- a `:features:*` file importing `androidx.compose.material3.{Button,
  OutlinedTextField, Card, Scaffold, FilterChip, TopAppBar, AlertDialog,
  HorizontalDivider}` → use the `Pharm*` primitive. `Icon`, `Text` and
  `Surface` are tolerated (`Text` always with a `PharmText` style).
- a hex literal `Color(0xFF…)` outside `theme/` → add a semantic token.
- a hardcoded `Dp` in a primitive where a `PharmDimens` field exists.
- an emoji used as an icon → add a vector to `PharmIcons`.

## 5. Adding a primitive

1. New file `designsystem/Pharm<X>.kt` — one primitive per file.
2. Read `pharmTokens` at the top (`val t = pharmTokens`); take a `modifier:
   Modifier = Modifier` as the first optional parameter.
3. Copy that goes on screen comes from `pharmStrings`, never a literal (A29).
4. Sizing off `PharmDimens`; if the value is new, add the field rather than
   inlining the number.
5. Add a `@Preview` to `PharmControlsPreviews.kt` or `PharmDisplayPreviews.kt`
   using the `PreviewScaffold` light/dark wrappers.
6. If it has non-trivial pure logic (a fit calculation, a predicate), pull it
   into an `internal fun` and unit-test it in `:core:ui:jvmTest` — that is how
   `fittedTableColumns` and `hidesToolbarTitleForSearch` are covered.
