# detekt-lsp

Fast incremental Kotlin static analysis for VS Code, Neovim, Helix, Zed, and any LSP-compatible editor. Powered by [detekt](https://detekt.dev).

[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)
[![Status: Alpha](https://img.shields.io/badge/Status-Alpha_(M0%2B)-yellow.svg?style=flat-square)](#roadmap)
[![Detekt: 1.x](https://img.shields.io/badge/detekt-1.x-purple.svg?style=flat-square)](https://detekt.dev)

> **Status: 0.1.0-alpha — parse-error diagnostics live.** The LSP server boots, parses every Kotlin buffer with `kotlin-compiler-embeddable`, and publishes one LSP diagnostic per `PsiErrorElement` (syntax error). No detekt rule engine wired in yet — Tier 1 rules land in M2.

## Why detekt-lsp

`detekt` is the de-facto Kotlin linter, with one gap: no editor integration outside IntelliJ. No incremental analysis, no live diagnostics in VS Code, Neovim, Helix, or Zed.

`detekt-lsp` fixes this. It embeds `detekt-core` inside an LSP server with a live PSI cache and per-file invalidation, targeting sub-100 ms diagnostics on every keystroke.

- **Multi-editor by design.** Any LSP client works: VS Code, Cursor, Neovim, Helix, Zed.
- **Incremental.** PSI cache + per-file invalidation keep analysis under 100 ms even on large projects.
- **detekt-native.** Inherits your existing `.detekt.yml` config, baseline, and custom rules.
- **Native binary planned.** GraalVM native-image (M7) eliminates the JVM startup cost.

## How it compares

| Aspect | detekt-lsp | detekt CLI | IntelliJ + detekt plugin | fwcd kotlin-language-server |
|---|:---:|:---:|:---:|:---:|
| Live diagnostics | ✅ Live (M2+) | ❌ Run on save | ✅ Live | ⚠️ Slow |
| Editor support | Any LSP client | Terminal only | JetBrains only | LSP clients |
| Detekt rules | ✅ All | ✅ All | ✅ All | ❌ |
| Code actions | Planned (M5) | ❌ | ✅ | Limited |
| Config inheritance | ✅ `.detekt.yml` | ✅ | ✅ | N/A |
| Baseline support | Planned (M6) | ✅ | ✅ | N/A |

Pair detekt-lsp with [**kotlin-jump**](https://github.com/elumine-dev/kotlin-jump) for fast Kotlin/Java navigation in the same editor. The two are designed as companions.

## Roadmap

| Milestone | Goal | Target |
|---|---|---|
| **M0** ✅ | Skeleton, hello-LSP, CI matrix | Done (Apr 2026) |
| **M0+** ✅ | Tier 0 parse-error diagnostics, full-sync buffer | Done (May 2026, v0.1.0-alpha) |
| **M1** | PSI cache + incremental `didChange` (reparse 1k LOC < 10 ms) | Q2 2026 |
| **M2** | detekt-bridge, syntactic rules push (Tier 1 < 30 ms) | Q2–Q3 2026 |
| **M3** | Pull diagnostics, workspace diagnostics | Q3 2026 |
| **M4** | Type-resolution rules (Tier 2 < 300 ms) | Q3–Q4 2026 |
| **M5** | Code actions / auto-correct | Q4 2026 |
| **M6** | Config + baseline + plugins | Q4 2026 |
| **M7** | GraalVM native image | Q1 2027 |
| **M8** | detekt 2.x migration | Q1–Q2 2027 |
| **M9** | Marketplace launch + upstream PR | Q2 2027 |

Full architecture and trade-offs: [`docs/architecture.md`](docs/architecture.md).

## Repo layout

```
detekt-lsp/
├── server/                   # Kotlin LSP server (Gradle multi-module)
│   ├── lsp-protocol/         # lsp4j wrappers
│   ├── lsp-workspace/        # workspace state, file watching
│   ├── lsp-document/         # PSI cache, didChange handler
│   ├── lsp-analysis/         # K2 Analysis API bridge
│   ├── lsp-detekt-bridge/    # detekt-api → LSP diagnostics
│   ├── lsp-codeactions/      # quick fixes / auto-correct
│   ├── lsp-perf/             # JMH benchmarks
│   └── lsp-server-app/       # main(), wiring, native-image config
├── client/packages/
│   └── vscode-extension/     # TypeScript LSP client
├── distribution/             # native-image config + .vsix packaging
├── benchmarks/               # corpus for perf regression gate
├── docs/                     # architecture, ADRs
└── .github/workflows/        # CI matrix builds
```

## Dev quickstart

### Prerequisites

- JDK 21+ (any distribution; Temurin recommended). The Gradle wrapper handles itself.
- Node 20+
- VS Code

### Server

```bash
cd server
./gradlew :lsp-server-app:shadowJar
```

Smoke test (should echo a JSON response with `"name":"detekt-lsp"`):

```bash
JAR=lsp-server-app/build/libs/detekt-lsp-all.jar
REQ='{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"processId":null,"rootUri":null,"capabilities":{}}}'
( printf 'Content-Length: %d\r\n\r\n%s' "${#REQ}" "$REQ"; sleep 2 ) | java -jar "$JAR"
```

### VS Code extension

```bash
cd client/packages/vscode-extension
npm install
npm run build
```

Open this folder in VS Code and press **F5**. An Extension Development Host launches with `detekt-lsp` connected to the local jar (path wired in `.vscode/launch.json`).

## Contributing

Contributors welcome from M1 onward. Until then, the architecture is being stabilized. If you want to track progress:

- Watch the repo for milestone PRs.
- Read [`docs/architecture.md`](docs/architecture.md) for the full plan.
- Open an issue if you spot a design flaw or want to discuss a milestone target.

Once M1 ships, good first issues will be tagged `good-first-issue`.

## Companion tools

- **detekt-lsp** — this server.
- [**kotlin-jump**](https://github.com/elumine-dev/kotlin-jump) — VS Code Kotlin/Java navigation, no JVM (4.6k+ installs).
- [**SearchDeadCode**](https://github.com/KevinDoremy/SearchDeadCode) — Dead code detection for Android (Rust CLI on Homebrew).

Maintained alongside [elumine-dev](https://github.com/elumine-dev) by [Kevin Doremy](https://kevindoremy.com).

## License

[Apache 2.0](LICENSE) (matching detekt upstream) © Kevin Doremy Laferrière
