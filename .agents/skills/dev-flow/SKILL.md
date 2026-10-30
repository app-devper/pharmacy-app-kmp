---
name: dev-flow
description: End-to-end delivery flow for app-devper/pharmacy-app-kmp — from an idea or bug through shaping, design, test-first build, verify, review, PR into develop, release PR into main, tag, deploy check and back-merge. Says which skill to use at each stage. Use when starting any change, when unsure what comes next, or when asked to ship, release or hotfix.
---

# dev-flow — idea → release for pharmacy-app-kmp

One flow, nine stages. Each stage names the skill that does the work and the
exit check that must hold before moving on. Skip a stage only where it says
so. Branching and PR mechanics are owned by `git-flow` and `pr`; this skill
only sequences them.

## 0. Pick the track

| Track | Starts at | Branch from → PR into |
|---|---|---|
| Feature / change | 1 | `develop` → `develop` (`feature/<slug>`) |
| Bug fix (not urgent) | 1 (short) | `develop` → `develop` (`feature/fix-<slug>`) |
| Refactor | 1 via `improve-codebase-architecture` | `develop` → `develop` |
| Release | 8 | `develop` → `main` (`release/X.Y.Z`) |
| Hotfix (production broken) | 9 | `main` → `main` (`hotfix/<slug>`) |

## 1. Shape — agree on what and why

- New behaviour or unclear requirement → `/grill-with-docs` (runs `grilling`
  + `domain-modeling`). This repo has several contexts: find the one the
  change belongs to in `CONTEXT-MAP.md`, put terms in
  `docs/contexts/<context>/CONTEXT.md`, and record hard-to-reverse decisions
  in `docs/adr/`. A change to how contexts relate goes into `CONTEXT-MAP.md`
  itself. These files are the product-wide vocabulary — pharmacy-api links
  to them.
- Small, well-understood fix → `grilling` only, a few questions, no docs.
- Refactor → `/improve-codebase-architecture` picks the candidate and grills it.

**Exit:** one or two sentences stating the change and how we will know it works.

## 2. Design — decide the shape of the code

- `codebase-design` for module/interface boundaries (deep modules, seams).
- Stack references: `kmp-rules` / `kmp-code-pattern` first; scaffold with `kmp-feature`, `kmp-add-form`, `kmp-screen-split`, `kmp-data-layer`, `kmp-navigation`, `kmp-design-system`; reference `compose-multiplatform-patterns`, `kotlin-coroutines-flows`.

**Exit:** files/modules to touch are known; any new term is in its context's `docs/contexts/<context>/CONTEXT.md`.

## 3. Branch

`git-flow` → `feature/<slug>` from an up-to-date `develop`. One concern per
branch.

## 4. Build

Test first with `kmp-test` (`runVmTest` + `Fake<X>Repository`). Kover is a ratchet: raise the floor in the same PR when coverage goes up. `kmp-test` wins over the generic `tdd-workflow` where they differ.

Commit in small conventional commits: `feat(scope): …`, `fix(scope): …`,
`refactor(scope): …`, `test(scope): …`, `chore(scope): …`.

## 5. Verify locally — same gates as CI (`Linux (JVM + Android + WasmJs + audit)`)

```bash
# the canonical sweep in CLAUDE.md § Test verify, plus the coverage gate
./gradlew :composeApp:auditArchitecture :composeApp:testDebugUnitTest \
  :composeApp:compileTestKotlinWasmJs koverVerify   # + the :core/:features jvmTest list
```

Use the `run` skill: wasmJs build + mock-api + headless browser screenshots.

**Exit:** everything above is green and the change was seen working — not
just compiled.

## 6. Review

`kmp-review` (or the `kmp-reviewer` agent), `kotlin-coding-style`, then `/code-review`. Fix findings before opening the PR.

## 7. PR into develop

```bash
git push -u origin feature/<slug>
gh pr create --base develop --title "feat(scope): …" --body "…"
```

Body: what changed, why, how it was verified (stage 5). Land it with the
`pr` skill (squash, delete branch, sync `develop`). Never push to `develop`
directly.

## 8. Release — develop → main

1. **Pick the version** from what landed since the last tag
   (`git log $(git describe --tags --abbrev=0 --match 'v*' origin/main)..origin/develop --oneline`):
   breaking → major, any `feat` → minor, only `fix` → patch.
2. **Cut the branch:** `git-flow` release recipe → `release/X.Y.Z` from `develop`.
3. **Version:** Bump `app-version` (+ `app-versionCode` for Android) in `gradle/libs.versions.toml`.
4. **PR:** `gh pr create --base main --title "release: vX.Y.Z" --body "<changelog: PR list since last tag>"`.
   CI `Linux (JVM + Android + WasmJs + audit)` must be green.
5. **Land:** `pr` skill (squash into `main`).
6. **Deploy:** **Manual** — Cloud Build trigger `deploy-pharm-app` is disabled (last run 2026-08-10), so merging `main` deploys nothing. Deploy from a clean `main` checkout in step 9.
7. **Tag:** Tag by hand — nothing tags automatically.
   ```bash
   git checkout main && git pull --ff-only
   git tag -a vX.Y.Z -m "vX.Y.Z" HEAD   # HEAD = the "release: vX.Y.Z (#n)" squash commit
   git push origin vX.Y.Z
   ```
8. **Back-merge** `main` → `develop` (`git-flow`; the `pr` skill does it
   after a PR into `main`).
9. **Deploy and check:**
   ```bash
   git checkout main && git pull --ff-only
   ./gradlew :composeApp:wasmJsBrowserDistribution
   firebase deploy --only hosting:pharm-app --project devperpos
   firebase hosting:channel:list --site pharm-app --project devperpos   # live release time
   ```

**Exit:** tag `vX.Y.Z` on `main`, pharm-app.web.app released from that commit, `develop` contains `main`.

## 9. Hotfix — production is broken

`git-flow` hotfix recipe: `hotfix/<slug>` from `main` → stages 4–6 (test
reproducing the bug first) → PR into `main` titled `fix(scope): …` → release
steps 3 and 5–9 with a **patch** bump.

## Guard rails

- No direct pushes to `main` or `develop` except the stage-8 back-merge
  (admin only, see `git-flow`); every other change goes through a PR
  with `Linux (JVM + Android + WasmJs + audit)` green.
- A merge into `main` is a release, deployed by hand right after —
  only `release/*` and `hotfix/*` PRs target `main`.
- Never tag a commit that is not on `main`; never move or delete a pushed tag.
