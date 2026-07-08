package com.zenith.terminal;

import com.zenith.Lang;
import com.zenith.Proxy;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandOutputHelper;
import com.zenith.command.api.CommandSources;
import com.zenith.terminal.logback.TerminalConsoleAppender;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.DumbTerminal;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.zenith.Globals.*;

public class TerminalManager {
    private LineReader lineReader;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            Terminal terminal = TerminalConsoleAppender.getTerminal();
            if (terminal == null) {
                TERMINAL_LOG.warn(Lang.t("No se pudo iniciar la terminal interactiva", "Unable to initialize interactive terminal"));
                return;
            }
            if (terminal instanceof DumbTerminal && !CONFIG.interactiveTerminal.allowDumbTerminal) {
                TERMINAL_LOG.warn(Lang.t("Terminal basica inicializada pero desactivada en config", "Dumb terminal initialized but is disabled by config"));
                return;
            }
            TERMINAL_LOG.info(Lang.t("Iniciando Terminal Interactiva...", "Starting Interactive Terminal..."));
            this.lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .appName("Gang'sProxy")
                .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                .option(LineReader.Option.CASE_INSENSITIVE, true)
                .option(LineReader.Option.INSERT_TAB, false)
                .option(LineReader.Option.EMPTY_WORD_OPTIONS, false)
                .completer(new TerminalCommandCompleter())
                .build();
            // always show completions below prompt

            if (!(terminal instanceof DumbTerminal) && CONFIG.interactiveTerminal.alwaysOnCompletions) {
                new TerminalAutoCompletionWidget(lineReader);
            }
            TerminalConsoleAppender.setReader(lineReader);
            var terminalThread = new Thread(this::readTerminal, "gangsproxy Terminal");
            terminalThread.setDaemon(true);
            terminal.handle(Terminal.Signal.INT, signal -> terminalThread.interrupt());
            terminalThread.start();
        }
    }

    private void readTerminal() {
        int eofCount = 0;
        while (true) {
            try {
                String line = lineReader.readLine("> ");
                if (line == null || line.isBlank()) {
                    continue;
                }
                handleTerminalCommand(line);
                eofCount = 0;
            } catch (final EndOfFileException e) {
                if (eofCount++ > 20) {
                    TERMINAL_LOG.warn(Lang.t("Detectada entrada de terminal mal configurada, desactivando terminal interactiva", "Detected misconfigured terminal input, disabling interactive terminal"));
                    return;
                }
            } catch (final UserInterruptException e) {
                TERMINAL_LOG.info(Lang.t("Saliendo...", "Exiting..."));
                EXECUTOR.execute(() -> {
                    Proxy.getInstance().stop(false);
                });
                break;
            } catch (final Exception e) {
                TERMINAL_LOG.error(Lang.t("Error leyendo entrada de terminal", "Error while reading terminal input"), e);
            }
        }
    };

    private void handleTerminalCommand(final String command) {
        switch (command) {
            case "exit" -> {
                TERMINAL_LOG.info(Lang.t("Saliendo...", "Exiting..."));
                Proxy.getInstance().stop(false);
            }
            default -> executeDiscordCommand(command);
        }
    }

    private void executeDiscordCommand(final String command) {
        final var commandContext = CommandContext.create(command, CommandSources.TERMINAL);
        COMMAND.execute(commandContext);
        if (CONFIG.interactiveTerminal.logToDiscord && !commandContext.isSensitiveInput()) CommandOutputHelper.logInputToDiscord(command, CommandSources.TERMINAL, commandContext);
        var embed = commandContext.getEmbed();
        if (CONFIG.interactiveTerminal.logToDiscord && DISCORD.isRunning() && !commandContext.isSensitiveInput()) {
            CommandOutputHelper.logEmbedOutputToDiscord(embed);
            CommandOutputHelper.logMultiLineOutputToDiscord(commandContext.getMultiLineOutput());
        } else {
            CommandOutputHelper.logEmbedOutputToTerminal(embed);
            CommandOutputHelper.logMultiLineOutputToTerminal(commandContext.getMultiLineOutput());
        }
    }
}
