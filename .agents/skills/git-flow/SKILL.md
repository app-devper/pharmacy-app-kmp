---
name: git-flow
description: Git flow branching for app-devper/pharmacy-app-kmp — main (production, deploys + tags on merge), develop (default, integration), feature/*, release/*, hotfix/*. Use when starting new work, opening a PR, cutting a release, hotfixing production, or deciding which base branch a change targets.
---

# git-flow — branching + release model for this repo

Project override of the user-level `git-flow` skill (`~/.Codex/skills/git-flow/`)
— generic recipes live there; everything below is what's specific to
`app-devper/pharmacy-app-kmp`.

## Branch map

| Branch | Role | PR target | After merge |
|---|---|---|---|
| `main` | production — every merge deploys + tags | — | back-merge into `develop` |
| `develop` | default branch, integration | — | — |
| `feature/<x>` | new work, fixes for next release | `develop` | delete branch |
| `release/<x.y.z>` | release cut from `develop` | `main` | back-merge `main` → `develop`, delete branch |
| `hotfix/<x>` | urgent production fix from `main` | `main` | back-merge `main` → `develop`, delete branch |

Both `main` and `develop` require the check
"Linux (JVM + Android + WasmJs + audit)". PRs land via **squash**.

## What merging `main` triggers

Cloud Build trigger `deploy-pharm-app` (project `devperpos`,
[cloudbuild.yaml](../../../cloudbuild.yaml)) runs on every push to `main`:

1. `./gradlew :composeApp:wasmJsBrowserDistribution` (CI-only heap override
   via `GRADLE_USER_HOME` — machine has 8GB)
2. `firebase deploy --only hosting:pharm-app` → https://pharm-app.web.app
3. Tag `v<app-version>` from `gradle/libs.versions.toml` key `app-version` —
   **skipped if the tag already exists**

So every `release/*` or `hotfix/*` PR must **bump `app-version`**
(+ `app-versionCode` for Android) in `gradle/libs.versions.toml`, or the
deploy goes out untagged.

## Recipes

### Feature

```bash
git checkout develop && git pull --ff-only
git checkout -b feature/<slug>
# work, commit
git push -u origin feature/<slug>
gh pr create --base develop --title "..." --body "..."
```

### Release

```bash
git checkout develop && git pull --ff-only
git checkout -b release/<x.y.z>
# bump app-version (and app-versionCode) in gradle/libs.versions.toml
gh pr create --base main --title "release: v<x.y.z>" --body "..."
```

### Hotfix

```bash
git checkout main && git pull --ff-only
git checkout -b hotfix/<slug>
# fix + bump patch version
gh pr create --base main --title "fix(...): ..." --body "..."
```

### Back-merge (required after every merge into main)

```bash
git checkout main && git pull --ff-only
git checkout develop && git pull --ff-only
git merge main            # usually fast-forward; resolve if develop moved
git push origin develop
```

## Guard rails

- Never push directly to `main` or `develop` — always via PR.
- Never open a feature PR against `main`; only `release/*` and `hotfix/*`
  target `main`.
- A merge into `main` is a production deploy — treat release/hotfix PRs
  accordingly (verify sweep green first).
- Landing PRs (merge + branch cleanup + sync) is the **pr** skill's job —
  invoke `/pr` rather than hand-rolling the merge.
