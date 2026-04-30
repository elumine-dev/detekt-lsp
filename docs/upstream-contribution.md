# Upstream contribution plan

The long-term home for this project is [`detekt/detekt`](https://github.com/detekt/detekt) as a
new module `detekt-lsp`. Living outside that monorepo is a means, not an end.

## Why upstream?

- **Adoption.** Existing detekt users (>50k repos on GitHub) get LSP support automatically.
- **Maintenance.** Each detekt release ships LSP support tested against the same code. No
  version-skew bug class.
- **Multi-editor reach.** Officially-blessed LSP unlocks Neovim, Helix, Zed, IntelliJ
  (via LSP4IJ), without us building a client per editor.
- **Trust.** Users will install an extension from `detekt` more readily than from a third party.

## Why not start in the upstream repo?

- Iteration speed. We need to make breaking architectural calls (PSI cache strategy,
  Tier 1/Tier 2 dispatch, K2 Analysis API integration) without going through review on each
  one.
- Risk. detekt 2.x is mid-migration; embedding ourselves in their build now would slow us
  *and* them.
- Unproven viability. The K2 Analysis API + GraalVM native image combo may not work. Better
  to find out in our own repo.

## Migration plan (M9)

The `server/` subtree is structured to drop directly into `detekt/detekt-lsp/`:

```
detekt-lsp/server/lsp-protocol/                 → detekt/detekt-lsp/lsp-protocol/
detekt-lsp/server/lsp-document/                 → detekt/detekt-lsp/lsp-document/
... etc.
```

The convention plugins under `build-logic/` are written to be portable; they don't depend on
any project-root specifics.

The VSCode client stays in our repo (separate publishing cadence, separate license concerns,
detekt org may or may not want to host it).

## What we're committing to

If detekt accepts the contribution, we commit to:

- Maintaining the LSP module against each detekt release (ours or someone else's).
- Keeping the perf regression gate green on the upstream CI.
- Reviewing LSP-related issues in `detekt/detekt`.

If detekt declines or wants a different shape, we keep this repo. The LSP server still works.

## Pre-conditions for the upstream PR

Before opening the PR (M9):

- [ ] All M0–M8 milestones green
- [ ] 1000+ Marketplace installs (proves user demand)
- [ ] 0 issues "wrong findings vs CLI" (proves correctness)
- [ ] Apache-2.0 license already on every file (matches detekt)
- [ ] No commits attributed to AI tooling
- [ ] CONTRIBUTING.md covers the LSP module
- [ ] Pre-discussion in [GitHub Discussions](https://github.com/detekt/detekt/discussions)
      to gauge maintainer interest
