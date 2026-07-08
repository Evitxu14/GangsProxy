package com.zenith.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.*;
import com.zenith.Lang;

import static com.zenith.Globals.CONFIG;
import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;

public class TerminalCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name("terminal")
            .category(CommandCategory.MANAGE)
            .description("""
                Configures the gangsproxy interactive terminal.

                All subcommands only usable from the terminal.
                """)
            .usageLines(
                "autoCompletions on/off",
                "logToDiscord on/off",
                "logChatMessages on/off",
                "logOnlyQueuePositionUpdates on/off"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("terminal").requires(ctx -> Command.validateCommandSource(ctx, CommandSources.TERMINAL))
            .then(literal("autoCompletions").then(argument("toggle", toggle()).executes(c -> {
                CONFIG.interactiveTerminal.alwaysOnCompletions = getToggle(c, "toggle");
                c.getSource().getEmbed()
                    .title(Lang.t("Autocompletados ", "AutoCompletions ") + toggleStrCaps(CONFIG.interactiveTerminal.alwaysOnCompletions))
                    .addField(Lang.t("Información", "Info"), Lang.t("Los cambios surtirán efecto en el próximo `restart`", "Changes will take effect on next `restart`"));
            })))
            .then(literal("logToDiscord").then(argument("toggle", toggle()).executes(c -> {
                CONFIG.interactiveTerminal.logToDiscord = getToggle(c, "toggle");
                c.getSource().getEmbed()
                    .title(Lang.t("Registrar en Discord ", "Log To Discord ") + toggleStrCaps(CONFIG.interactiveTerminal.logToDiscord));
            })))
            .then(literal("logChatMessages").then(argument("toggle", toggle()).executes(c -> {
                CONFIG.client.extra.logChatMessages = getToggle(c, "toggle");
                c.getSource().getEmbed()
                    .title(Lang.t("Registrar Mensajes de Chat ", "Log Chat Messages ") + toggleStrCaps(CONFIG.client.extra.logChatMessages));
            })))
            .then(literal("logOnlyQueuePositionUpdates").then(argument("toggle", toggle()).executes(c -> {
                CONFIG.client.extra.logOnlyQueuePositionUpdates = getToggle(c, "toggle");
                c.getSource().getEmbed()
                    .title(Lang.t("Registrar Solo Actualizaciones de Posición de Cola ", "Log Only Queue Pos Updates ") + toggleStrCaps(CONFIG.client.extra.logOnlyQueuePositionUpdates));
            })));
    }


    @Override
    public void defaultHandler(final CommandContext ctx) {
        ctx.getEmbed()
            .addField(Lang.t("Autocompletados", "AutoCompletions"), toggleStr(CONFIG.interactiveTerminal.alwaysOnCompletions))
            .addField(Lang.t("Registrar en Discord", "Log To Discord"), toggleStr(CONFIG.interactiveTerminal.logToDiscord))
            .addField(Lang.t("Registrar Mensajes de Chat", "Log Chat Messages"), toggleStr(CONFIG.client.extra.logChatMessages))
            .addField(Lang.t("Registrar Solo Actualizaciones de Posición de Cola", "Log Only Queue Pos Updates"), toggleStr(CONFIG.client.extra.logOnlyQueuePositionUpdates))
            .primaryColor();
    }
}
