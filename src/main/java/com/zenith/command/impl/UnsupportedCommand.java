package com.zenith.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.Proxy;
import com.zenith.command.api.*;
import com.zenith.discord.Embed;
import com.zenith.network.client.Authenticator;

import com.zenith.Lang;
import static com.zenith.Globals.CONFIG;
import static com.zenith.command.brigadier.CustomStringArgumentType.getString;
import static com.zenith.command.brigadier.CustomStringArgumentType.wordWithChars;
import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;
import static com.zenith.discord.DiscordBot.escape;
import static com.zenith.util.config.Config.Authentication.AccountType.OFFLINE;

public class UnsupportedCommand extends Command {
    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name("unsupported")
            .category(CommandCategory.MANAGE)
            .description("""
            Unsupported settings that cause critical security issues.
            
            Do not use edit these unless you absolutely understand what you are doing.
            
            No user support will be provided if you modify any of these settings.
            
            All subcommands are only usable from the terminal.
            """)
            .usageLines(
                "whitelist on/off",
                "spectatorWhitelist on/off",
                "allowOfflinePlayers on/off",
                "auth type offline",
                "auth offlineUsername <username>"
            )
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("unsupported")
            .requires(c -> Command.validateCommandSource(c, CommandSources.TERMINAL))
            .then(literal("whitelist").then(argument("toggle", toggle()).executes(c -> {
                CONFIG.server.extra.whitelist.enable = getToggle(c, "toggle");
                c.getSource().getEmbed()
                    .title(Lang.t("Lista Blanca ", "Whitelist ") + toggleStrCaps(CONFIG.server.extra.whitelist.enable));
                return OK;
            })))
            .then(literal("spectatorWhitelist").then(argument("toggle", toggle()).executes(c -> {
                CONFIG.server.spectator.whitelistEnabled = getToggle(c, "toggle");
                c.getSource().getEmbed()
                    .title(Lang.t("Lista Blanca de Espectadores ", "Spectator Whitelist ") + toggleStrCaps(CONFIG.server.spectator.whitelistEnabled));
                return OK;
            })))
            .then(literal("allowOfflinePlayers").then(argument("toggle", toggle()).executes(c -> {
                CONFIG.server.verifyUsers = !getToggle(c, "toggle");
                c.getSource().getEmbed()
                    .title(Lang.t("Permitir Jugadores Offline ", "Allow Offline Players ") + toggleStrCaps(!CONFIG.server.verifyUsers));
                return OK;
            })))
            .then(literal("auth")
                .then(literal("type").then(literal("offline").executes(c -> {
                    CONFIG.authentication.accountType = OFFLINE;
                    c.getSource().getEmbed()
                        .title(Lang.t("Tipo de Autenticación Establecido", "Authentication Type Set"));
                    Proxy.getInstance().cancelLogin();
                    Authenticator.INSTANCE.clearAuthCache();
                })))
                .then(literal("offlineUsername").then(argument("username", wordWithChars()).executes(c -> {
                    CONFIG.authentication.username = getString(c, "username");
                    c.getSource().getEmbed()
                        .title(Lang.t("Nombre de Usuario Offline Establecido", "Offline Username Set"));
                    Proxy.getInstance().cancelLogin();
                    Authenticator.INSTANCE.clearAuthCache();
                    return OK;
                }))));
    }

    @Override
    public void defaultEmbed(Embed builder) {
        builder
            .addField(Lang.t("Lista Blanca", "Whitelist"), toggleStr(CONFIG.server.extra.whitelist.enable))
            .addField(Lang.t("Lista Blanca de Espectadores", "Spectator Whitelist"), toggleStr(CONFIG.server.spectator.whitelistEnabled))
            .addField(Lang.t("Permitir Jugadores Offline", "Allow Offline Players"), toggleStr(!CONFIG.server.verifyUsers))
            .addField(Lang.t("Autenticación Offline", "Offline Authentication"), toggleStr(CONFIG.authentication.accountType == OFFLINE))
            .addField(Lang.t("Nombre de Usuario Offline", "Offline Username"), escape(CONFIG.authentication.username))
            .primaryColor();
    }
}
