---
name: pr
description: Immediately squash-merge the current branch's open pull request (the repo has no branch protection, so any mergeable PR merges on command), then delete the merged branch and sync local main. Use when the user says "auto", "merge it", "/pr", or otherwise asks to land the current PR in /Users/admin/ProjectPos/pharmacy-app/app-kmp.
---

# pr — land the current branch's PR now

This repo (`app-devper/pharmacy-app-kmp`) has **no required status checks / branch
protection**, so a mergeable PR can be squash-merged immediately. PRs land via **squash**
(see #67/#68). This skill merges the PR for the **current branch**, deletes the branch, and
syncs `main`.

## Steps

1. **Find the PR for the current branch**
   ```bash
   gh pr view --json number,state,mergeable,mergeStateStatus,headRefName,title
   ```
   - No PR for this branch → stop and tell the user (offer to open one with `gh pr create`).
   - `state` is `MERGED`/`CLOSED` → report it; nothing to do.

2. **Push any unpushed commits first** (a PR can't include what isn't pushed)
   ```bash
   git push
   ```

3. **Check mergeability.** If `mergeable` is `CONFLICTING`, stop and report the conflict —
   do **not** force. If `mergeable` is `UNKNOWN`, re-run step 1 once (GitHub may still be
   computing) before proceeding.

4. **Squash-merge + delete the remote branch**
   ```bash
   gh pr merge <number> --squash --delete-branch
   ```

5. **Sync local main and clean up**
   ```bash
   git checkout main
   git pull --ff-only origin main
   git branch -D <headRefName>      # delete the now-merged local branch
   git remote prune origin
   ```

6. **Report**: the merged PR number + URL, the squash commit SHA now on `main`
   (`git log --oneline -1`), and confirm the branch was deleted local + remote.

## Notes

- Default strategy is `--squash` to match repo history. Only use `--merge` / `--rebase` if the
  user explicitly asks.
- `--delete-branch` removes both the remote branch and (after the local checkout/pull) the
  local one; step 5's `git branch -D` covers the local copy if it lingers.
- If the user wants the PR to **wait** for CI before landing, that needs a branch-protection
  rule + required checks configured on `main` in GitHub settings first — this skill does not
  set that up; it lands the PR now.
- Run from inside `/Users/admin/ProjectPos/pharmacy-app/app-kmp`.
