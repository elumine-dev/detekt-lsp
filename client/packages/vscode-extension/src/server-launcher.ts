import * as fs from 'fs';
import * as path from 'path';
import * as vscode from 'vscode';

export interface LaunchSpec {
  command: string;
  args: string[];
  cwd?: string;
}

/**
 * Resolution order:
 *   1. `detekt.server.binary` setting          — explicit override
 *   2. Bundled native binary (M7+)             — `<ext>/server-bin/<os>-<arch>/detekt-lsp[.exe]`
 *   3. Bundled fat-jar + JAVA_HOME             — `<ext>/server-bin/detekt-lsp.jar`
 *   4. Dev-mode env `DETEKT_LSP_DEV_JAR=...`   — pointed at locally-built shadowJar
 */
export async function resolveServerCommand(
  context: vscode.ExtensionContext,
  out: vscode.OutputChannel,
): Promise<LaunchSpec | undefined> {
  const cfg = vscode.workspace.getConfiguration('detekt');

  // 1. User override
  const override = cfg.get<string>('server.binary', '').trim();
  if (override) {
    if (!fs.existsSync(override)) {
      out.appendLine(`detekt: configured binary not found at ${override}`);
      return undefined;
    }
    return { command: override, args: [] };
  }

  // 2. Bundled native binary (per platform)
  const nativeName = process.platform === 'win32' ? 'detekt-lsp.exe' : 'detekt-lsp';
  const nativePath = path.join(
    context.extensionPath,
    'server-bin',
    `${process.platform}-${process.arch}`,
    nativeName,
  );
  if (fs.existsSync(nativePath)) {
    out.appendLine(`detekt: using bundled native binary (${process.platform}-${process.arch})`);
    return { command: nativePath, args: [] };
  }

  // 3. Bundled fat-jar
  const bundledJar = path.join(context.extensionPath, 'server-bin', 'detekt-lsp.jar');
  if (fs.existsSync(bundledJar)) {
    return launchSpecForJar(bundledJar, cfg, out);
  }

  // 4. Dev mode: point at a locally-built shadowJar
  const devJar = process.env.DETEKT_LSP_DEV_JAR;
  if (devJar && fs.existsSync(devJar)) {
    out.appendLine(`detekt: dev mode (DETEKT_LSP_DEV_JAR=${devJar})`);
    return launchSpecForJar(devJar, cfg, out);
  }

  out.appendLine(
    'detekt: no server binary found. Either build the server ' +
    '(`cd server && ./gradlew :lsp-server-app:shadowJar` then set DETEKT_LSP_DEV_JAR) ' +
    'or set "detekt.server.binary" in settings.',
  );
  out.show();
  return undefined;
}

function launchSpecForJar(
  jarPath: string,
  cfg: vscode.WorkspaceConfiguration,
  out: vscode.OutputChannel,
): LaunchSpec | undefined {
  const java = resolveJava(out);
  if (!java) return undefined;
  const userJvmArgs = cfg.get<string[]>('jvm.args', []);
  return {
    command: java,
    args: [
      '-XX:+UseG1GC',
      '-XX:MaxGCPauseMillis=50',
      '-Xms256m',
      '-Xmx1g',
      ...userJvmArgs,
      '-jar',
      jarPath,
    ],
  };
}

function resolveJava(out: vscode.OutputChannel): string | undefined {
  const javaBin = process.platform === 'win32' ? 'java.exe' : 'java';
  if (process.env.JAVA_HOME) {
    const candidate = path.join(process.env.JAVA_HOME, 'bin', javaBin);
    if (fs.existsSync(candidate)) return candidate;
  }
  for (const dir of (process.env.PATH ?? '').split(path.delimiter)) {
    if (!dir) continue;
    const candidate = path.join(dir, javaBin);
    if (fs.existsSync(candidate)) return candidate;
  }
  out.appendLine('detekt: no Java found. Set JAVA_HOME (JDK 21+) or install java.');
  return undefined;
}
