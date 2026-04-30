import * as vscode from 'vscode';
import {
  LanguageClient,
  LanguageClientOptions,
  ServerOptions,
} from 'vscode-languageclient/node';
import { resolveServerCommand } from './server-launcher';

let client: LanguageClient | undefined;
let outputChannel: vscode.OutputChannel | undefined;

export async function activate(context: vscode.ExtensionContext): Promise<void> {
  outputChannel = vscode.window.createOutputChannel('detekt');
  context.subscriptions.push(outputChannel);

  context.subscriptions.push(
    vscode.commands.registerCommand('detekt.showOutput', () => outputChannel?.show()),
    vscode.commands.registerCommand('detekt.restart', async () => {
      if (!client) {
        outputChannel?.appendLine('detekt: no server running, nothing to restart');
        return;
      }
      outputChannel?.appendLine('detekt: restarting server…');
      await client.restart();
      outputChannel?.appendLine('detekt: server restarted');
    }),
  );

  const launch = await resolveServerCommand(context, outputChannel);
  if (!launch) return;

  const serverOptions: ServerOptions = {
    command: launch.command,
    args: launch.args,
    ...(launch.cwd ? { options: { cwd: launch.cwd } } : {}),
  };

  const clientOptions: LanguageClientOptions = {
    documentSelector: [{ scheme: 'file', language: 'kotlin' }],
    outputChannel,
    traceOutputChannel: outputChannel,
    synchronize: {
      configurationSection: 'detekt',
      fileEvents: [
        vscode.workspace.createFileSystemWatcher('**/detekt.yml'),
        vscode.workspace.createFileSystemWatcher('**/baseline.xml'),
      ],
    },
  };

  client = new LanguageClient('detekt', 'detekt', serverOptions, clientOptions);
  outputChannel.appendLine(`detekt: launching server (${launch.command})`);
  await client.start();
  outputChannel.appendLine('detekt: ready');
}

export async function deactivate(): Promise<void> {
  await client?.stop();
}
