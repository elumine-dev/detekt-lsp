# ADR 0001 — GraalVM Native Image as primary distribution

**Status:** Accepted (2026-04-30)

## Context

The user-perceived cost of an LSP server is dominated by cold start. JVM cold start with
class-data sharing lands around 600–800 ms; pure Java startup is 1.5+ s; tree-sitter-style
native parsers are <50 ms.

For a linter that must give feedback on the first keystroke, this matters.

## Options considered

| | jlink + JRE | GraalVM Native Image | Bundled fat-jar (system Java) |
|---|---|---|---|
| Cold start | ~800 ms | **~50 ms** | 1.5+ s |
| Distribution size | ~50 MB | ~50 MB | ~30 MB + system Java |
| Reflection compat with `kotlin-compiler-embeddable` | ✅ | ⚠️ requires `reflect-config.json` | ✅ |
| User-side dependency | none | none | **needs JDK 21+** |
| Maintenance per detekt upgrade | ✅ | ⚠️ may break | ✅ |

## Decision

Ship **GraalVM Native Image** as the primary binary, per platform. Keep a **fat-jar fallback**
shipped alongside for two reasons:

1. If GraalVM can't compile a particular detekt rule due to reflection/`Class.forName` at runtime,
   we degrade to the JAR rather than block the platform release.
2. Users with corporate Java mandates can prefer the JAR.

The launcher (`server-launcher.ts`) tries native first, falls back to JAR + JAVA_HOME.

## Consequences

- M7 is dedicated to GraalVM integration (4 weeks). Earlier milestones run JVM mode for
  faster iteration.
- CI must build native image on every platform (Mac/Linux/Windows × arm64/x64). Adds ~10 min
  per build.
- `reflect-config.json` is generated via the GraalVM agent during a corpus run, then curated
  by hand. New detekt versions may require regen.
- Project Leyden (JEP 514/515) is **not** the chosen path. It's preview-status in JDK 25
  and its AOT cache format isn't backward-compatible across minor releases — too fragile for
  Marketplace distribution. Re-evaluate at JDK 26 LTS.

## Reversibility

If GraalVM consistently breaks across detekt versions, fall back to **jlink + CDS**
(Class-Data Sharing): cold start ~400 ms, no native compilation. The launcher already supports
this via the JAR fallback path; we'd just stop publishing native binaries.
