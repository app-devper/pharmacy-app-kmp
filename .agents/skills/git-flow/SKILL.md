---
name: git-flow
description: Git flow branching for app-devper/pharmacy-app-kmp — main (production, manual tag + deploy after merge), develop (default, integration), feature/*, release/*, hotfix/*. Use when starting new work, opening a PR, cutting a release, hotfixing production, or deciding which base branch a change targets.
---

# git-flow — branching + release model for this repo

Project override of the user-level `git-flow` skill (`~/.claude/skills/git-flow/`)
— generic recipes live there; everything below is what's specific to
`app-devper/pharmacy-app-kmp`.

The stage-by-stage flow (shape → build → review → release → deploy check)
lives in `dev-flow`; this skill owns the branch rules.

## Branch map

| Branch | Role | PR target | After merge |
|---|---|---|---|
| `main` | production — tag + deploy by hand after every merge | — | back-merge into `develop` |
| `develop` | default branch, integration | — | — |
| `feature/<x>` | new work, fixes for next release | `develop` | delete branch |
| `release/<x.y.z>` | release cut from `develop` | `main` | back-merge `main` → `develop`, delete branch |
| `hotfix/<x>` | urgent production fix from `main` | `main` | back-merge `main` → `develop`, delete branch |

Both `main` and `develop` require the check
"Linux (JVM + Android + WasmJs + audit)". PRs land via **squash**.

## What merging `main` does — nothing automatic

The Cloud Build trigger `deploy-pharm-app` (project `devperpos`,
[cloudbuild.yaml](../../../cloudbuild.yaml)) is **disabled** — its last run
was 2026-08-10 — so merging `main` neither deploys nor tags. After a
release/hotfix lands, tag first, then build and deploy the web app
(https://pharm-app.web.app) and the Android build from the tagged `main`
checkout. The commands are in `dev-flow` stage 8, steps 6–8 — only there,
so the two skills cannot drift.

Every `release/*` or `hotfix/*` PR must **bump `app-version`** and
`app-versionCode` in `gradle/libs.versions.toml` so the version, the tag
and the shipped app agree. If the trigger is re-enabled it also tags `v<app-version>` (skipping
an existing tag) — update this section then.

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
# bump app-version and app-versionCode in gradle/libs.versions.toml, commit
git push -u origin release/<x.y.z>
gh pr create --base main --title "release: v<x.y.z>" --body "..."
```

### Hotfix

```bash
git checkout main && git pull --ff-only
git checkout -b hotfix/<slug>
# failing test, fix + bump app-version (patch) and app-versionCode, commit
git push -u origin hotfix/<slug>
gh pr create --base main --title "fix(...): ..." --body "..."
```

### Back-merge (required after every merge into main)

The back-merge is the only direct push to `develop`, in every app-devper repo.
It needs an account with admin rights: `develop` requires the CI status check
and a pushed merge commit has none, so GitHub answers
`Bypassed rule violations for refs/heads/develop` — expected here. The `check`
workflow still runs on the push; confirm it is green:
`gh run list --branch develop -L 1`. Never squash a back-merge (it would drop
`main` from `develop`'s history), and don't open a PR for it.

```bash
git checkout main && git pull --ff-only
git checkout develop && git pull --ff-only
git merge main            # usually fast-forward; resolve if develop moved
git push origin develop
```

## Guard rails

- Never push directly to `main` or `develop` — always via PR. The one
  exception is the back-merge `main` → `develop`, which is pushed directly
  (see Back-merge).
- Never open a feature PR against `main`; only `release/*` and `hotfix/*`
  target `main`.
- A merge into `main` is a release — tag it, then deploy it by hand from
  the tagged `main` (verify sweep green first).
- Landing PRs (merge + branch cleanup + sync) is the **pr** skill's job —
  invoke `/pr` rather than hand-rolling the merge.
