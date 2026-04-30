# detekt-lsp

> Fast incremental Kotlin static analysis for VSCode, powered by [detekt](https://detekt.dev).

**Status: M0 — skeleton only.** Hello-LSP server boots and responds to `initialize`. No real
analysis yet. See `docs/architecture.md` for the full plan.

## Why

`detekt` is the de-facto Kotlin linter, but it has no editor integration outside IntelliJ — no
incremental analysis, no live diagnostics in VSCode/Neovim/Helix/Zed. `detekt-lsp` fixes that
by embedding `detekt-core` inside an LSP server with a live PSI cache and per-file invalidation,
giving sub-100 ms diagnostics on every keystroke.

Performance is the explicit priority. See [the plan](../.claude/plans/staged-wishing-deer.md)
for targets and trade-offs.

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
- VSCode

### Server

```bash
cd server
./gradlew :lsp-server-app:shadowJar
```

Smoke test it (must echo a JSON response with `"name":"detekt-lsp"`):

```bash
JAR=lsp-server-app/build/libs/detekt-lsp-all.jar
REQ='{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"processId":null,"rootUri":null,"capabilities":{}}}'
( printf 'Content-Length: %d\r\n\r\n%s' "${#REQ}" "$REQ"; sleep 2 ) | java -jar "$JAR"
```

### VSCode extension

```bash
cd client/packages/vscode-extension
npm install
npm run build
```

Then open this folder in VSCode and press **F5** — an Extension Development Host launches with
`detekt-lsp` connected to the local jar (path is wired in `.vscode/launch.json`).

## Roadmap

- M0 ✓ — skeleton, hello-LSP, CI
- M1 — PSI cache + incremental `didChange` (target: reparse 1k LOC < 10 ms)
- M2 — detekt-bridge, syntactic rules push (Tier 1 < 30 ms)
- M3 — pull diagnostics, workspace diagnostics
- M4 — type-resolution rules (Tier 2 < 300 ms)
- M5 — code actions / auto-correct
- M6 — config + baseline + plugins
- M7 — GraalVM native image
- M8 — detekt 2.x migration
- M9 — Marketplace launch + upstream PR

Full plan: `docs/architecture.md`.

## License

Apache-2.0 (matching detekt upstream).
