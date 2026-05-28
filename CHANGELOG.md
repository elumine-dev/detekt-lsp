# Changelog

All notable changes are documented here. Versions follow [Semantic Versioning](https://semver.org/).

## [0.1.0] — 2026-05-28

First alpha cut. The server can be installed and produces live diagnostics, but only
on syntactic parse errors. Not yet a replacement for `detekt` CLI.

### Added
- Full LSP server lifecycle: `initialize`, `initialized`, `shutdown`, `exit`.
- Kotlin parser layer (`lsp-document`) backed by `kotlin-compiler-embeddable`.
- Tier 0 syntactic diagnostics (`lsp-analysis`): every `PsiErrorElement` becomes
  one LSP `Diagnostic` with severity Error, source `detekt-lsp`, and an accurate
  line/column range.
- Full text-document sync: per-buffer reparse and diagnostic publication on
  every `didOpen` / `didChange`.
- VS Code extension client (`detekt-lsp`) bundling the fat-jar server.
- Test suite: 10 tests covering parser, analyzer, and offset-to-position math.

### Known limitations
- No detekt rule engine yet — only parse errors fire. Tier 1 rules land in M2.
- No PSI cache — every change reparses from scratch. Incremental layer lands in M1.
- No code actions, baseline, plugins, or type-resolution rules. Roadmap in README.

### Infrastructure
- Gradle wrapper 8.14 (JDK 24-compatible).
- GitHub Actions release pipeline auto-publishes the VSIX to VS Code Marketplace
  and Open VSX when a `v*.*.*` tag is pushed.
