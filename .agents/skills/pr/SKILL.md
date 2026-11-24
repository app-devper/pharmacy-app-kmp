---
name: pr
description: Land the current branch's open pull request in app-devper/pharmacy-app-kmp — wait for the required check, squash-merge, delete the branch, sync the base branch, and back-merge main into develop when the PR targeted main. Use when the user says "pr land", "merge it", "/pr", or otherwise asks to land the current PR.
---

# pr — land the current branch's PR

This repo follows **git flow** (see the `git-flow` skill): feature PRs target
`develop`; release/hotfix PRs target `main`. Both branches require the
"Linux (JVM + Android + WasmJs + audit)" check. PRs land via **squash**.

## Steps

1. **Find the PR for the current branch**
   ```bash
   gh pr view --json number,state,mergeable,mergeStateStatus,baseRefName,headRefName,title
   ```
   - No PR for this branch → stop and tell the user (offer `gh pr create`
     with the base the `git-flow` skill prescribes).
   - `state` is `MERGED`/`CLOSED` → report it; nothing to do.

2. **Push any unpushed commits first**
   ```bash
   git push
   ```

3. **Check mergeability.** `CONFLICTING` → stop and report — do **not**
   force. `UNKNOWN` → re-run step 1 once before proceeding.

4. **Wait for CI, then squash-merge.** Never hand the merge to auto-merge:
   wait for the required check and merge yourself.
   ```bash
   gh pr checks <number> --required --watch --interval 30   # in the background; you're notified when it ends
   gh pr checks <number> --required                         # "Linux (JVM + Android + WasmJs + audit)" pass?
   gh pr merge <number> --squash --delete-branch
   ```
   - Right after a push the first command can exit at once with "no checks
     reported": the run has not registered yet. Run it again after a few
     seconds.
   - Required check failed → stop, do not merge, and show the failing step:
     `gh run list --branch <headRefName> -L 1 --json databaseId -q '.[0].databaseId'`
     gives the run id for `gh run view <id> --log-failed`.

5. **Sync the base branch and clean up**
   ```bash
   git checkout <baseRefName>
   git pull --ff-only origin <baseRefName>
   git branch -D <headRefName>
   git remote prune origin
   ```

6. **If the PR targeted `main`: back-merge into `develop`** (mandatory —
   see `git-flow` skill)
   ```bash
   git checkout develop && git pull --ff-only origin develop
   git merge main && git push origin develop
   ```
   Pushed directly on purpose (admin only; GitHub reports
   "Bypassed rule violations" — expected). Then check
   `gh run list --branch develop -L 1` is green.
   Nothing deploys or tags automatically (`deploy-pharm-app` is disabled) —
   continue with `dev-flow` stage 8 steps 6–8: tag `v<app-version>`, build
   and deploy from the tagged `main`, check.

7. **Report**: merged PR number + URL, the squash commit SHA on the base
   branch (`git log --oneline -1`), branch deleted local + remote, and for
   `main` landings the back-merge result and that tag, deploy and check
   (`dev-flow` stage 8 steps 6–8) are still to do.

## Notes

- Default strategy is `--squash`. Only `--merge` / `--rebase` if the user
  explicitly asks.
- A merge into `main` is a release, tagged and deployed by hand right
  after — confirm the release/hotfix PR bumped `app-version` and
  `app-versionCode` before landing.
- Run from inside `/Users/admin/ProjectPos/Pharmacy/pharmacy-app-kmp`.
